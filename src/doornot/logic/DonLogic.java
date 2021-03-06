package doornot.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import doornot.parser.DonParser;
import doornot.parser.IDonParser;
import doornot.storage.DonStorage;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;
import doornot.storage.IDonTask.TaskType;
import doornot.util.AbstractDonCommand;
import doornot.util.CalHelper;
import doornot.util.SearchHelper;
import doornot.util.TodayTaskComparator;
import doornot.util.AbstractDonCommand.GeneralCommandType;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * DonLogic - Class for handling the logic of the program
 * (creation/deletion/modification of tasks)
 * 
 */
//@author A0111995Y
public class DonLogic implements IDonLogic {

	private static final String FILE_DONLOGIC_LOG = "donlogic.log";
	private static final String MSG_SAVE_SUCCESSFUL = "Save successful.";
	private static final String MSG_SAVE_FAILED = "Save failed.";
	private static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	private static final String MSG_UNDO_REMINDER = "You can undo your action with the undo command.";
	private static final String MSG_REDO_NO_ACTIONS = "There are no actions to redo!";
	private static final String MSG_EX_COMMAND_CANNOT_BE_NULL = "Command cannot be null";

	private IDonStorage donStorage;
	private IDonParser donParser;
	

	// actionPast contains the actions to undo, actionFuture to redo
	// If a new action that performs modifications is made, actionFuture has to
	// be cleared.
	private Stack<AbstractDonCommand> commandPast, commandFuture;

	private final static Logger log = Logger
			.getLogger(DonLogic.class.getName());
	
	public DonLogic() {
		donStorage = new DonStorage();
		donParser = new DonParser();

		commandPast = new Stack<AbstractDonCommand>();
		commandFuture = new Stack<AbstractDonCommand>();

		donStorage.loadFromDisk();
		initLogger();
	}

	/**
	 * Constructor for dependency injection during testing
	 * 
	 * @param storage
	 *            the storage component
	 * @param parser
	 *            the parser component
	 */
	public DonLogic(IDonStorage storage, IDonParser parser, boolean useLog) {
		donStorage = storage;
		donParser = parser;

		commandPast = new Stack<AbstractDonCommand>();
		commandFuture = new Stack<AbstractDonCommand>();

		donStorage.loadFromDisk();
		if(useLog) {
			initLogger();
		}
	}
	
	public static void setDebug(Level level) {
		log.setLevel(level);
	}

	private static void initLogger() {
		try {
			Handler fileHandler = new FileHandler(FILE_DONLOGIC_LOG);
			fileHandler.setFormatter(new SimpleFormatter());
			log.addHandler(fileHandler);
			Logger.getLogger(DonLogic.class.getName()).setLevel(Level.FINE);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IDonResponse runCommand(String command) {
		if (command == null) {
			throw new IllegalArgumentException(MSG_EX_COMMAND_CANNOT_BE_NULL);
		}
		log.fine(command);
		AbstractDonCommand dCommand = donParser.parseCommand(command);

		AbstractDonCommand.GeneralCommandType genCommandType = dCommand
				.getGeneralType();
		IDonResponse response = null;
		if (genCommandType == GeneralCommandType.UNDO) {
			response = undoLastAction();
		} else if (genCommandType == GeneralCommandType.REDO) {
			response = redoAction();
		} else {
			response = dCommand.executeCommand(donStorage);
			if (dCommand.hasExecuted()) {
				// Executed commands can be undone, add undo message
				response.addMessage(MSG_UNDO_REMINDER);
				commandPast.add(dCommand);
				commandFuture.clear(); // If a change has been made, the redo
										// stack needs to be cleared
			}
		}

		// Perform a save after every command
		saveToDrive();

		return response;
	}

	@Override
	public IDonResponse saveToDrive() {
		boolean saveSuccess = donStorage.saveToDisk();
		IDonResponse response = new DonResponse();
		if (saveSuccess) {
			response.setResponseType(IDonResponse.ResponseType.SAVE_SUCCESS);
			response.addMessage(MSG_SAVE_SUCCESSFUL);
			log.fine(MSG_SAVE_SUCCESSFUL);
		} else {
			response.setResponseType(IDonResponse.ResponseType.SAVE_FAILURE);
			response.addMessage(MSG_SAVE_FAILED);
			log.fine(MSG_SAVE_FAILED);
		}
		return response;
	}

	@Override
	public IDonResponse initialize() {
		return donParser.parseCommand("today").executeCommand(donStorage);
	}

	/**
	 * Undoes the last action
	 * 
	 * @return response stating the status of the undo operation
	 */
	private IDonResponse undoLastAction() {
		IDonResponse response = new DonResponse();

		if (commandPast.size() <= 0) {
			response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
			response.addMessage(MSG_UNDO_NO_ACTIONS);

		} else {
			AbstractDonCommand lastCommand = commandPast.pop();
			assert lastCommand.hasExecuted(); // The lastCommand can only be in
												// the stack if it has run
			response = lastCommand.undoCommand(donStorage);
			if (!lastCommand.hasExecuted()) {
				// Command undone
				commandFuture.add(lastCommand);
			}
		}

		return response;
	}

	/**
	 * Undoes the last action
	 * 
	 * @return response stating the status of the undo operation
	 */
	private IDonResponse redoAction() {
		IDonResponse response = new DonResponse();

		if (commandFuture.size() <= 0) {
			response.setResponseType(IDonResponse.ResponseType.REDO_FAILURE);
			response.addMessage(MSG_REDO_NO_ACTIONS);
		} else {
			AbstractDonCommand nextCommand = commandFuture.pop();
			assert !nextCommand.hasExecuted(); // The lastCommand can only be in
												// the stack if it has run

			response = nextCommand.executeCommand(donStorage);
			if (nextCommand.hasExecuted()) {
				// Command redone
				commandPast.add(nextCommand);
			}

		}
		return response;
	}

	@Override
	public List<IDonTask> getTaskList() {
		List<IDonTask> taskList = new ArrayList<IDonTask>(donStorage.getTaskList());
		Collections.sort(taskList);
		return taskList;
	}

	@Override
	public List<IDonTask> getTodayTasks() {
		List<IDonTask> todayTasks = SearchHelper.findTaskRange(donStorage, CalHelper.getTodayStart(),
				CalHelper.getTodayEnd(), SearchHelper.FIND_INCOMPLETE);
		Collections.sort(todayTasks, new TodayTaskComparator());
		return todayTasks;
	}

	@Override
	public List<IDonTask> getWeekTasks() {
		Calendar start = CalHelper.getTodayStart();
		Calendar end = CalHelper.getDayEnd(CalHelper.getDaysFromNow(7));
		List<IDonTask> weekTasks = SearchHelper.findTaskRange(donStorage, start, end,
				SearchHelper.FIND_INCOMPLETE);
		Collections.sort(weekTasks);
		return weekTasks;
	}

	@Override
	public List<IDonTask> getFutureTasks() {
		Calendar start = CalHelper.getDayEnd(CalHelper.getDaysFromNow(7));
		List<IDonTask> futureTasks = SearchHelper.findTaskRange(donStorage, start, null,
				SearchHelper.FIND_INCOMPLETE);
		Collections.sort(futureTasks);
		return futureTasks;
	}

	@Override
	public List<IDonTask> getFloatingTasks() {
		List<IDonTask> floatingTasks = SearchHelper.findTaskByType(donStorage, TaskType.FLOATING, true, false);
		Collections.sort(floatingTasks);
		return floatingTasks;
	}

	@Override
	public List<IDonTask> getOverdueTasks() {
		List<IDonTask> taskList = SearchHelper.findTaskRange(donStorage, null,
				Calendar.getInstance(), SearchHelper.FIND_INCOMPLETE);
		List<IDonTask> resultList = new ArrayList<IDonTask>();
		for (IDonTask task : taskList) {
			if ((task.getEndDate() != null && CalHelper.dateEqualOrBefore(
					task.getEndDate(), Calendar.getInstance()))
					|| task.getEndDate() == null) {
				resultList.add(task);
			}
		}
		Collections.sort(taskList);
		return resultList;
	}

}

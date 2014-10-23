package doornot.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import doornot.CalHelper;
import doornot.logic.IDonResponse.ResponseType;
import doornot.parser.DonParser;
import doornot.parser.IDonParser;
import doornot.storage.DonStorage;
import doornot.storage.DonTask;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;
import doornot.storage.IDonTask.TaskType;

/**
 * DonLogic - Class for handling the logic of the program
 * (creation/deletion/modification of tasks)
 * 
 */
//@author A0111995Y
public class DonLogic implements IDonLogic {

	private static final String MSG_NO_UNDONE_TASKS = "Congratulations, you have no incomplete tasks!";
	private static final String MSG_COMMAND_WRONG_FORMAT = "The command you entered was of the wrong format!";
	private static final String MSG_COMMAND_WRONG_DATE = "The date you entered was invalid!";
	private static final String MSG_SAVE_SUCCESSFUL = "Save successful.";
	private static final String MSG_SAVE_FAILED = "Save failed.";
	
	private static final String MSG_SEARCH_ID_FAILED = "No tasks with ID of %1$d were found.";
	private static final String MSG_SEARCH_LABEL_FAILED = "No tasks with label '%1$s' were found.";
	private static final String MSG_SEARCH_TITLE_FAILED = "No tasks with a title containing '%1$s' were found.";
	private static final String MSG_SEARCH_DATE_FAILED = "No tasks starting in '%1$s' were found.";
	private static final String MSG_DELETE_SUCCESS = "The above task was deleted successfully.";
	private static final String MSG_DELETE_FAILED = "The above task could not be deleted.";
	private static final String MSG_EDIT_TITLE_SUCCESS = "Task name changed from '%1$s' to '%2$s'.";
	private static final String MSG_EDIT_SINGLE_DATE_SUCCESS = "%1$s changed from %2$s to %3$s.";
	private static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	private static final String MSG_UNDO_SUCCESS = "Last action undone. %1$d change(s) removed.";
	private static final String MSG_REDO_NO_ACTIONS = "There are no actions to redo!";
	private static final String MSG_REDO_SUCCESS = "Redo successful. %1$d change(s) redone.";
	private static final String MSG_TOGGLE_STATUS_ID_SUCCESS = "Task %1$d has been set to '%2$s'";
	private static final String MSG_SEARCH_MORE_THAN_ONE_TASK = "'%1$s' returned more than 1 result. Please specify with the ID.";
	private static final String MSG_UNKNOWN_COMMAND = "You have entered an unknown command";
	private static final String MSG_FREE_EVERYWHERE = "You are free!";

	private static final String MSG_EX_COMMAND_CANNOT_BE_NULL = "Command cannot be null";
	
	private static final String PHRASE_COMPLETE = "complete";
	private static final String PHRASE_INCOMPLETE = "incomplete";
	private static final String PHRASE_END_DATE = "End date";
	private static final String PHRASE_START_DATE = "Start date";

	private static final int FIND_INCOMPLETE = 0;
	private static final int FIND_COMPLETE = 1;
	private static final int FIND_ALL = 2;

	private IDonStorage donStorage;
	private IDonParser donParser;

	//actionPast contains the actions to undo, actionFuture to redo
	//If a new action that performs modifications is made, actionFuture has to be cleared.
	private Stack<DonAction> actionPast, actionFuture;
	private Stack<AbstractDonCommand> commandPast, commandFuture;

	private final static Logger log = Logger
			.getLogger(DonLogic.class.getName());

	public DonLogic() {
		donStorage = new DonStorage();
		donParser = new DonParser();
		actionPast = new Stack<DonAction>();
		actionFuture = new Stack<DonAction>();
		
		commandPast = new Stack<AbstractDonCommand>();
		commandFuture = new Stack<AbstractDonCommand>();

		donStorage.loadFromDisk();

		initLogger();
	}
	
	/**
	 * Constructor for dependency injection during testing
	 * @param storage	the storage component
	 * @param parser	the parser component
	 */
	public DonLogic(IDonStorage storage, IDonParser parser, boolean useLog) {
		donStorage = storage;
		donParser = parser;
		actionPast = new Stack<DonAction>();
		actionFuture = new Stack<DonAction>();
		
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
			Handler fileHandler = new FileHandler("donlogic.log");
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
		if(command==null) {
			throw new IllegalArgumentException(MSG_EX_COMMAND_CANNOT_BE_NULL);
		}
		AbstractDonCommand dCommand = donParser.parseCommand(command);
		
		commandPast.add(dCommand);
		
		IDonResponse response = dCommand.executeCommand(donStorage);
		/*
		AbstractDonCommand.CommandType commandType = dCommand.getType();
		AbstractDonCommand.GeneralCommandType genCommandType = dCommand.getGeneralType();
		IDonResponse response = null;
		if (commandType == AbstractDonCommand.CommandType.ADD_FLOAT) {
			response = createTask(dCommand.getNewName());

		} else if (commandType == AbstractDonCommand.CommandType.ADD_TASK) {
			response = createTask(dCommand.getNewName(),
					dCommand.getNewDeadline(), dCommand.hasUserSetTime());

		} else if (commandType == AbstractDonCommand.CommandType.ADD_EVENT) {
			response = createTask(dCommand.getNewName(),
					dCommand.getNewStartDate(), dCommand.getNewEndDate(), dCommand.hasUserSetTime());

		} else if (commandType == AbstractDonCommand.CommandType.SEARCH_ID) {
			response = findTask(dCommand.getID());

		} else if (commandType == AbstractDonCommand.CommandType.SEARCH_NAME) {
			response = findTask(dCommand.getName());

		} else if (commandType == AbstractDonCommand.CommandType.SEARCH_DATE) {
			response = findTask(dCommand.getDeadline());

		} else if (commandType == AbstractDonCommand.CommandType.SEARCH_AFTDATE) {
			//If given search date has a time, will search after the given time.
			//If given search does not include a time, will search from the day after
			Calendar givenDate = dCommand.getDeadline();
			if (!dCommand.hasUserSetTime()) {
				givenDate = CalHelper.getDayAfter(givenDate);
			}
			response = findTaskRange(givenDate, null, FIND_INCOMPLETE);

		} else if (commandType == AbstractDonCommand.CommandType.SEARCH_FREE) {
			response = findFreeTime();

		} else if (commandType == AbstractDonCommand.CommandType.SEARCH_UNDONE) {
			response = findUndone();

		} else if (commandType == AbstractDonCommand.CommandType.OVERDUE) {
			response = findTaskRange(null, Calendar.getInstance(), FIND_INCOMPLETE);

		} else if (commandType == AbstractDonCommand.CommandType.TODAY) {
			response = getTasksToday();

		} else if (commandType == AbstractDonCommand.CommandType.DELETE_ID) {
			response = deleteTask(dCommand.getID());

		} else if (commandType == AbstractDonCommand.CommandType.DELETE) {
			response = deleteTask(dCommand.getName());

		} else if (commandType == AbstractDonCommand.CommandType.EDIT_ID_NAME) {
			response = editTask(dCommand.getID(), dCommand.getNewName());

		} else if (commandType == AbstractDonCommand.CommandType.EDIT_ID_DATE) {
			// TODO: recognize different single date edit type
			response = editTask(dCommand.getID(), true,
					dCommand.getNewDeadline(), dCommand.hasUserSetTime());

		} else if (commandType == AbstractDonCommand.CommandType.EDIT_ID_EVENT) {
			response = editTask(dCommand.getID(), dCommand.getNewStartDate(),
					dCommand.getNewEndDate(), dCommand.hasUserSetTime());

		} else if (commandType == AbstractDonCommand.CommandType.EDIT_NAME) {
			response = editTask(dCommand.getName(), dCommand.getNewName());

		} else if (commandType == AbstractDonCommand.CommandType.EDIT_DATE) {
			// TODO: recognize different single date edit type
			response = editTask(dCommand.getName(), true,
					dCommand.getNewDeadline(), dCommand.hasUserSetTime());

		} else if (commandType == AbstractDonCommand.CommandType.EDIT_EVENT) {
			response = editTask(dCommand.getName(), dCommand.getNewStartDate(),
					dCommand.getNewEndDate(), dCommand.hasUserSetTime());

		} else if (commandType == AbstractDonCommand.CommandType.MARK_ID) {
			response = toggleStatus(dCommand.getID());

		} else if (commandType == AbstractDonCommand.CommandType.MARK) {
			response = toggleStatus(dCommand.getName());

		} else if (commandType == AbstractDonCommand.CommandType.UNDO) {
			response = undoLastAction();

		} else if (genCommandType == AbstractDonCommand.GeneralCommandType.HELP) {
			response = getHelp(commandType);

		} else if (commandType == AbstractDonCommand.CommandType.INVALID_FORMAT) {
			response = createInvalidFormatResponse();

		} else if (commandType == AbstractDonCommand.CommandType.INVALID_DATE) {
			response = createInvalidDateResponse();

		} else if(commandType == AbstractDonCommand.CommandType.REDO) {
			response = redoAction();
			
		} else if(commandType == AbstractDonCommand.CommandType.LABEL_ID) {
			response = addLabel(dCommand.getID(), dCommand.getLabel());
			
		} else if(commandType == AbstractDonCommand.CommandType.LABEL_NAME) {
			response = addLabel(dCommand.getName(), dCommand.getLabel());
			
		} else if(commandType == AbstractDonCommand.CommandType.DELABEL_ID) {
			response = removeLabel(dCommand.getID(), dCommand.getLabel());
			
		} else if(commandType == AbstractDonCommand.CommandType.DELABEL_NAME) {
			response = removeLabel(dCommand.getName(), dCommand.getLabel());
			
		} else if(commandType == AbstractDonCommand.CommandType.SEARCH_LABEL) {
			response = findLabel(dCommand.getLabel());

		} else {
			// No relevant action could be executed
			response = new DonResponse();
			response.setResponseType(ResponseType.UNKNOWN_COMMAND);
			response.addMessage(MSG_UNKNOWN_COMMAND);
		}
		*/
		log.fine(command);

		// Perform a save after every command
		saveToDrive();

		return response;
	}

	private IDonResponse getTasksToday() {
		IDonResponse response;
		response = findTaskRange(CalHelper.getTodayStart(), CalHelper.getTodayEnd(), FIND_INCOMPLETE);
		return response;
	}

	@Override
	public IDonResponse saveToDrive() {
		boolean saveSuccess = donStorage.saveToDisk();
		IDonResponse response = new DonResponse();
		if (saveSuccess) {
			response.setResponseType(IDonResponse.ResponseType.SAVE_SUCCESS);
			response.addMessage(MSG_SAVE_SUCCESSFUL);
		} else {
			response.setResponseType(IDonResponse.ResponseType.SAVE_FAILURE);
			response.addMessage(MSG_SAVE_FAILED);
		}
		return response;
	}

	@Override
	public IDonResponse initialize() {
		return getTasksToday();
	}

	/**
	 * Creates a response for user entered commands with invalid formatting
	 * 
	 * @return the response
	 */
	private IDonResponse createInvalidFormatResponse() {
		IDonResponse response = new DonResponse();
		response.addMessage(MSG_COMMAND_WRONG_FORMAT);
		response.setResponseType(ResponseType.UNKNOWN_COMMAND);
		return response;
	}

	/**
	 * Creates a response for user entered commands with invalid dates
	 * 
	 * @return the response
	 */
	private IDonResponse createInvalidDateResponse() {
		IDonResponse response = new DonResponse();
		response.addMessage(MSG_COMMAND_WRONG_DATE);
		response.setResponseType(ResponseType.UNKNOWN_COMMAND);
		return response;
	}

	

	/**
	 * Find tasks with the given name
	 * 
	 * @param name
	 *            the name to search for
	 * @return the response containing the tasks
	 */
	private IDonResponse findTask(String name) {
		assert name!=null;
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			// Search for the given name/title without case sensitivity
			if (task.getTitle().toLowerCase().contains(name.toLowerCase())) {
				response.addTask(task);
			}
		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
			log.fine("Search success");
		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_TITLE_FAILED, name));
			log.fine(String.format(MSG_SEARCH_TITLE_FAILED, name));
		}
		return response;
	}

	/**
	 * Find tasks starting/occurring on a given date
	 * 
	 * @param date
	 *            the date to search for
	 * @return the response containing the tasks
	 */
	private IDonResponse findTask(Calendar date) {
		assert date!=null;
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			// Search for the given name/title without case sensitivity
			TaskType taskType = task.getType();
			if (taskType == TaskType.FLOATING) {
				// Floating tasks have no date.
				continue;
			}
			Calendar taskDate = task.getStartDate();
			Calendar taskEndDate = task.getEndDate();
			// If the date falls within the start and end date of an event, the
			// event is returned as well
			if (CalHelper.isSameDay(taskDate, date)
					|| (taskType == TaskType.DURATION && CalHelper.isBetweenDates(date,
							taskDate, taskEndDate))) {
				response.addTask(task);
			}
		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
			log.fine("Search success");
		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
			String dateString = date.get(Calendar.DATE)
					+ " "
					+ date.getDisplayName(Calendar.MONTH, Calendar.LONG,
							Locale.ENGLISH) + " " + date.get(Calendar.YEAR);
			response.addMessage(String.format(MSG_SEARCH_DATE_FAILED,
					dateString));
			log.fine(String.format(MSG_SEARCH_DATE_FAILED, dateString));
		}
		return response;
	}

	/**
	 * Find tasks that begin within the given range of time. Either parameter
	 * can be null to search for tasks before or after a date. For example if
	 * startDate is null and endDate is set to 09102014, the method will return
	 * all tasks from before 9th of October 2014. If startDate is 09102014 and
	 * endDate is null, all tasks beginning after 9th of October 2014 will be
	 * returned.
	 * 
	 * @param startDate
	 *            the date to start searching from (inclusive)
	 * @param endDate
	 *            the latest possible start date of a task (inclusive)
	 * @param completeType
	 *            0 if the tasks found must be incomplete, 1 if it must be
	 *            completed 2 if it can be complete or incomplete
	 * @return the response containing the tasks
	 */
	private IDonResponse findTaskRange(Calendar startDate, Calendar endDate,
			int completeType) {
		assert !(startDate==null && endDate==null);
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();

		for (IDonTask task : taskList) {
			if (task.getType() == TaskType.FLOATING) {
				// Floating tasks have no date.
				continue;
			}
			// Ignore tasks that do not match the completion status to search
			// for
			if ((task.getStatus() && completeType == FIND_INCOMPLETE)
					|| (!task.getStatus() && completeType == FIND_COMPLETE)) {
				continue;
			}
			Calendar taskStart = task.getStartDate();
			Calendar taskEnd = task.getEndDate();
			if (startDate == null) {
				if (CalHelper.dateEqualOrBefore(taskStart, endDate)) {
					response.addTask(task);
				}
			} else if (endDate == null) {
				if (CalHelper.dateEqualOrAfter(taskStart, startDate)) {
					response.addTask(task);
				}
			} else {
				//If task is between date or is ongoing between the dates
				if ((CalHelper.dateEqualOrAfter(taskStart, startDate)
						&& CalHelper.dateEqualOrBefore(taskStart, endDate))
						|| (CalHelper.dateEqualOrAfter(startDate, taskStart) && CalHelper.dateEqualOrBefore(startDate, taskEnd))
						|| (CalHelper.dateEqualOrAfter(endDate, taskStart) && CalHelper.dateEqualOrBefore(endDate, taskEnd))) {
					response.addTask(task);
				}
			}

		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
			log.fine("Search success");
		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
			log.fine("Search empty");
		}
		return response;
	}

	/**
	 * Find tasks given the ID
	 * 
	 * @param id
	 *            the id to search for
	 * @return the response containing the tasks
	 */
	private IDonResponse findTask(int id) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with given ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
			log.fine(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			response.setResponseType(IDonResponse.ResponseType.SEARCH_SUCCESS);
			log.fine("Search success");
			response.addTask(task);
		}
		return response;
	}

	/**
	 * Deletes the task with the given ID
	 * 
	 * @param id
	 *            the id of the task to delete
	 * @return the response containing the deletion status
	 */
	private IDonResponse deleteTask(int id) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			boolean deleteStatus = donStorage.removeTask(id);
			if (deleteStatus) {
				// Deleted
				response.setResponseType(IDonResponse.ResponseType.DEL_SUCCESS);
				response.addMessage(MSG_DELETE_SUCCESS);
				response.addTask(task);

				// Add delete action to history
				ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
				affectedTasks.add(task.clone());
				actionPast.push(new DonAction(
						AbstractDonCommand.CommandType.DELETE, AbstractDonCommand.GeneralCommandType.DELETE, affectedTasks));
				actionFuture.clear();
			} else {
				response.setResponseType(IDonResponse.ResponseType.DEL_FAILURE);
				response.addMessage(MSG_DELETE_FAILED);
			}
		}
		return response;
	}

	/**
	 * Deletes the task with the given title. If more than 1 task is found, the
	 * search results will be returned and nothing will be deleted.
	 * 
	 * @param title
	 *            the title of the task to search for to delete
	 * @return the response containing the deletion status
	 */
	private IDonResponse deleteTask(String title) {
		assert title!=null;
		IDonResponse response = new DonResponse();

		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.DEL_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = deleteTask(task.getID());
		}

		return response;
	}

	/**
	 * Change the title of the task with ID id to the new title
	 * 
	 * @param id
	 *            the id of the task to edit the title of
	 * @param newTitle
	 *            the new title
	 * @return the response
	 */
	private IDonResponse editTask(int id, String newTitle) {
		assert newTitle!=null;
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
			log.fine(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			String oldTitle = task.getTitle();
			task.setTitle(newTitle);
			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_EDIT_TITLE_SUCCESS, oldTitle,
					newTitle));
			log.fine(String.format(MSG_EDIT_TITLE_SUCCESS, oldTitle, newTitle));
			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			actionPast.push(new DonAction(
					AbstractDonCommand.CommandType.EDIT_ID_NAME, AbstractDonCommand.GeneralCommandType.EDIT, affectedTasks));
			actionFuture.clear();
		}
		return response;
	}

	/**
	 * Change the title of the task with a given title to the new title. The
	 * task name being searched for must belong to only one task
	 * 
	 * @param title
	 *            the title of the task to search for
	 * @param newTitle
	 *            the new title
	 * @return the response
	 */
	private IDonResponse editTask(String title, String newTitle) {
		assert newTitle!=null && title!=null;
		IDonResponse response = new DonResponse();
		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			log.fine(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK, title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = editTask(task.getID(), newTitle);
		}

		return response;
	}

	/**
	 * Change the deadline of the task with ID id to the new deadline (or start
	 * date/end date)
	 * 
	 * @param id
	 *            the id of the task to change
	 * @param isStartDate
	 *            true if the date to change is the start date, false otherwise.
	 *            This will be ignored for deadline tasks.
	 * @param newDate
	 *            the new date to be applied to the task
	 * @param timeUsed TODO
	 * @return the success response
	 */
	private IDonResponse editTask(int id, boolean isStartDate, Calendar newDate, boolean timeUsed) {
		assert newDate!=null;
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			Calendar oldDate = null;
			String dateType = "";
			if (task.getType() == IDonTask.TaskType.FLOATING) {
				// TODO: What should we do with floating tasks when the user
				// wants to edit the date?
			} else if (task.getType() == IDonTask.TaskType.DEADLINE) {
				dateType = "Deadline";
				oldDate = task.getStartDate();
				task.setStartDate(newDate);
				task.setTimeUsed(timeUsed);
			} else if (task.getType() == IDonTask.TaskType.DURATION) {
				if (isStartDate) {
					dateType = PHRASE_START_DATE;
					oldDate = task.getStartDate();
					task.setStartDate(newDate);
				} else {
					dateType = PHRASE_END_DATE;
					oldDate = task.getEndDate();
					task.setEndDate(newDate);
				}
				task.setTimeUsed(timeUsed);
			}

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
					dateType, oldDate.getTime().toString(), newDate.getTime()
							.toString()));
			log.fine(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS, dateType,
					oldDate.getTime().toString(), newDate.getTime().toString()));
			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			actionPast.push(new DonAction(
					AbstractDonCommand.CommandType.EDIT_ID_DATE, AbstractDonCommand.GeneralCommandType.EDIT, affectedTasks));
			actionFuture.clear();
		}
		return response;
	}

	/**
	 * Change the start date/end date or deadline of the task with a given title
	 * to the new title. The task name being searched for must belong to only
	 * one task
	 * 
	 * @param title
	 *            the title of the task to search for
	 * @param isStartDate
	 *            true if the date to change is the start date, false otherwise.
	 *            This will be ignored for deadline tasks.
	 * @param newDate
	 *            the new date to be applied to the task
	 * @param timeUsed TODO
	 * @return the response
	 */
	private IDonResponse editTask(String title, boolean isStartDate,
			Calendar newDate, boolean timeUsed) {
		assert title!=null && newDate!=null;
		IDonResponse response = new DonResponse();

		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			log.fine(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK, title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = editTask(task.getID(), isStartDate, newDate, timeUsed);
		}

		return response;
	}

	/**
	 * Change the start and end date of the task with ID id to the new dates
	 * 
	 * @param id
	 *            the id of the task to change
	 * @param newStartDate
	 *            the new start date to be applied to the task
	 * @param newEndDate
	 *            the new end date to be applied to the task
	 * @param timeUsed TODO
	 * @return the success response
	 */
	private IDonResponse editTask(int id, Calendar newStartDate,
			Calendar newEndDate, boolean timeUsed) {
		assert newStartDate!=null && newEndDate!=null;
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
			log.fine(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			Calendar oldStartDate = null, oldEndDate = null;
			String dateType = "";
			if (task.getType() == IDonTask.TaskType.FLOATING) {
				// TODO: What should we do with floating tasks when the user
				// wants to edit the date?
			} else if (task.getType() == IDonTask.TaskType.DEADLINE) {
				// TODO: What should we do with deadline tasks when the user
				// wants to edit the end date?
			} else if (task.getType() == IDonTask.TaskType.DURATION) {
				oldStartDate = task.getStartDate();
				task.setStartDate(newStartDate);

				oldEndDate = task.getEndDate();
				task.setEndDate(newEndDate);
				
				task.setTimeUsed(timeUsed);

			}

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
					PHRASE_START_DATE, oldStartDate.getTime().toString(),
					newStartDate.getTime().toString()));
			response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
					PHRASE_END_DATE, oldEndDate.getTime().toString(),
					newEndDate.getTime().toString()));
			log.fine(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
					PHRASE_START_DATE, oldStartDate.getTime().toString(),
					newStartDate.getTime().toString()));
			log.fine(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
					PHRASE_END_DATE, oldEndDate.getTime().toString(),
					newEndDate.getTime().toString()));

			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			actionPast.push(new DonAction(
					AbstractDonCommand.CommandType.EDIT_ID_EVENT, AbstractDonCommand.GeneralCommandType.EDIT, affectedTasks));
			actionFuture.clear();
		}
		return response;
	}

	/**
	 * Change the start and end date of the task with a title containing the
	 * search string to the new dates. The title of the task must belong to only
	 * 1 task or the search results will be returned instead and no edits will
	 * be made.
	 * 
	 * @param title
	 *            the title of the task to search for
	 * @param newStartDate
	 *            the new start date to be applied to the task
	 * @param newEndDate
	 *            the new end date to be applied to the task
	 * @param timeUsed TODO
	 * @return
	 */
	private IDonResponse editTask(String title, Calendar newStartDate,
			Calendar newEndDate, boolean timeUsed) {
		assert newStartDate!=null && newEndDate!=null;
		IDonResponse response = new DonResponse();
		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			log.fine(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK, title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = editTask(task.getID(), newStartDate, newEndDate, timeUsed);
		}

		return response;
	}

	/**
	 * Undoes the last action
	 * 
	 * @return response stating the status of the undo operation
	 */
	private IDonResponse undoLastAction() {
		IDonResponse response = new DonResponse();
		if (actionPast.size() <= 0) {
			response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
			response.addMessage(MSG_UNDO_NO_ACTIONS);
			log.fine(MSG_UNDO_NO_ACTIONS);
		} else {
			DonAction lastAction = actionPast.pop();
			int changesReversed = 0;
			AbstractDonCommand.GeneralCommandType generalActionType = lastAction.getGeneralType();
			if (generalActionType == AbstractDonCommand.GeneralCommandType.ADD) {
				// Perform a delete (reverse of Add)
				for (IDonTask addedTask : lastAction.getAffectedTasks()) {
					int id = addedTask.getID();
					boolean deleteSuccess = donStorage.removeTask(id);
					if (deleteSuccess) {
						changesReversed++;
					} else {
						response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
						response.addMessage(MSG_UNDO_NO_ACTIONS);
						log.fine(MSG_UNDO_NO_ACTIONS);
						return response;
					}
				}

			} else if (generalActionType == AbstractDonCommand.GeneralCommandType.DELETE) {
				// Perform an add (reverse of Delete)
				for (IDonTask removedTask : lastAction.getAffectedTasks()) {
					int id = donStorage.addTask(removedTask);
					if (id != -1) {
						changesReversed++;
					}
				}
			} else if (generalActionType == AbstractDonCommand.GeneralCommandType.EDIT
					|| generalActionType == AbstractDonCommand.GeneralCommandType.MARK) {
				// Replace the edited tasks with their previous properties
				List<IDonTask> redoTaskList = new ArrayList<IDonTask>();
				for (IDonTask editedTask : lastAction.getAffectedTasks()) {
					int id = editedTask.getID();
					IDonResponse searchResponse = findTask(id);
					//Clone the tasks which will have undo applied on them for redo to work
					IDonTask affectedTask = searchResponse.getTasks().get(0);
					redoTaskList.add(affectedTask.clone());
					affectedTask.copyTaskDetails(editedTask);
					changesReversed++;
					
				}
				lastAction = new DonAction(lastAction.getActionType(), lastAction.getGeneralType(), redoTaskList);
			} else {
				response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
				response.addMessage(MSG_UNDO_NO_ACTIONS);
				log.fine(MSG_UNDO_NO_ACTIONS);
				return response;
			}
			//Add undone action to the future stack for redo to use
			actionFuture.add(lastAction); 

			response.setResponseType(IDonResponse.ResponseType.UNDO_SUCCESS);
			response.addMessage(String
					.format(MSG_UNDO_SUCCESS, changesReversed));
			log.fine(String.format(MSG_UNDO_SUCCESS, changesReversed));
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
		if (actionFuture.size() <= 0) {
			response.setResponseType(IDonResponse.ResponseType.REDO_FAILURE);
			response.addMessage(MSG_REDO_NO_ACTIONS);
			log.fine(MSG_REDO_NO_ACTIONS);
		} else {
			DonAction nextAction = actionFuture.pop();
			int changesReversed = 0;
			AbstractDonCommand.GeneralCommandType generalActionType = nextAction.getGeneralType();
			if (generalActionType == AbstractDonCommand.GeneralCommandType.ADD) {
				// Perform an add
				for (IDonTask addedTask : nextAction.getAffectedTasks()) {
					int id = donStorage.addTask(addedTask);
					if (id != -1) {
						changesReversed++;
					}
				}

			} else if (generalActionType == AbstractDonCommand.GeneralCommandType.DELETE) {
				// Perform a delete
				for (IDonTask deletedTask : nextAction.getAffectedTasks()) {
					int id = deletedTask.getID();
					boolean deleteSuccess = donStorage.removeTask(id);
					if (deleteSuccess) {
						changesReversed++;
					} else {
						response.setResponseType(IDonResponse.ResponseType.REDO_FAILURE);
						response.addMessage(MSG_REDO_NO_ACTIONS);
						log.fine(MSG_REDO_NO_ACTIONS);
						return response;
					}
				}
			} else if (generalActionType == AbstractDonCommand.GeneralCommandType.EDIT
					|| generalActionType == AbstractDonCommand.GeneralCommandType.MARK) {
				// Replace the edited tasks with their previous properties
				List<IDonTask> undoTaskList = new ArrayList<IDonTask>();
				for (IDonTask editedTask : nextAction.getAffectedTasks()) {
					int id = editedTask.getID();
					IDonResponse searchResponse = findTask(id);
					//Clone the tasks which will have redo applied on them for undo to work
					IDonTask affectedTask = searchResponse.getTasks().get(0);
					undoTaskList.add(affectedTask.clone());
					affectedTask.copyTaskDetails(editedTask);
					changesReversed++;
					
				}
				nextAction = new DonAction(nextAction.getActionType(), nextAction.getGeneralType(), undoTaskList);
			} else {
				response.setResponseType(IDonResponse.ResponseType.REDO_FAILURE);
				response.addMessage(MSG_REDO_NO_ACTIONS);
				log.fine(MSG_REDO_NO_ACTIONS);
				return response;
			}
			//Add redone action to the future stack for undo to use
			actionPast.add(nextAction); 

			response.setResponseType(IDonResponse.ResponseType.REDO_SUCCESS);
			response.addMessage(String
					.format(MSG_REDO_SUCCESS, changesReversed));
			log.fine(String.format(MSG_REDO_SUCCESS, changesReversed));
		}
		return response;
	}

	/**
	 * Toggles the "done" status of the task with the given ID
	 * 
	 * @param id
	 *            the id of the task to change
	 * @return the response
	 */
	private IDonResponse toggleStatus(int id) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
			log.fine(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			boolean taskCompleted = !task.getStatus();
			task.setStatus(taskCompleted);

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_TOGGLE_STATUS_ID_SUCCESS, id,
					(taskCompleted ? PHRASE_COMPLETE : PHRASE_INCOMPLETE)));
			log.fine(String.format(MSG_TOGGLE_STATUS_ID_SUCCESS, id,
					(taskCompleted ? PHRASE_COMPLETE : PHRASE_INCOMPLETE)));
			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			actionPast.push(new DonAction(AbstractDonCommand.CommandType.MARK_ID, AbstractDonCommand.GeneralCommandType.MARK,
					affectedTasks));
			actionFuture.clear();
		}
		return response;
	}

	/**
	 * Toggles the "done" status of the task containing the given title
	 * 
	 * @param title
	 *            the title of the task to change
	 * @return the response
	 */
	private IDonResponse toggleStatus(String title) {
		assert title!=null;
		IDonResponse response = new DonResponse();
		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			log.fine(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK, title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = toggleStatus(task.getID());
		}

		return response;
	}

	/**
	 * Find free time in the user's schedule based on existing task and events.
	 * For dates with no time stated, it is assumed that the user means that the
	 * whole day is taken up.
	 * 
	 * @return
	 */
	private IDonResponse findFreeTime() {
		IDonResponse response = new DonResponse();
		// Get all tasks with deadlines or events that end after today
		List<IDonTask> taskList = getTaskByType(IDonTask.TaskType.DEADLINE,
				false, false);
		taskList.addAll(getTaskByType(IDonTask.TaskType.DURATION, false, false));
		Collections.sort(taskList);

		if (taskList.size() <= 0) {
			response.addMessage(MSG_FREE_EVERYWHERE);
			log.fine(MSG_FREE_EVERYWHERE);
			return response;
		}

		// TODO handle the case where there are no tasks
		// Find free period between now and the start time of the earliest task
		// if possible
		Calendar now = Calendar.getInstance();
		if (taskList.get(0).getStartDate().after(now)) {
			DonPeriod free = new DonPeriod(now, taskList.get(0).getStartDate());
			response.addPeriod(free);
			log.fine(free.toString());
		}

		for (int i = 0; i < taskList.size() - 1; i++) {
			IDonTask currentTask = taskList.get(i);
			IDonTask nextTask = taskList.get(i + 1);
			// Check if there is a free period between the end time
			// and the start time of the next event
			if (currentTask.getType() == IDonTask.TaskType.DEADLINE) {
				// A deadline task has no end date
				// If the user did not specify a time in the deadline
				// 0000hr on the given day will be used as the deadline
				if (currentTask.getStartDate().compareTo(
						nextTask.getStartDate()) < 0) {
					// There is a free period
					DonPeriod free = new DonPeriod(currentTask.getStartDate(),
							nextTask.getStartDate());
					response.addPeriod(free);
					log.fine(free.toString());
				}
			} else {
				if (currentTask.getEndDate().compareTo(nextTask.getStartDate()) < 0) {
					// There is a free period
					DonPeriod free = new DonPeriod(currentTask.getEndDate(),
							nextTask.getStartDate());
					response.addPeriod(free);
					log.fine(free.toString());
				}
			}
		}

		return response;
	}
	
	/**
	 * Find all undone/incomplete tasks
	 * @return the response containing incomplete tasks
	 */
	private IDonResponse findUndone() {
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			if (!task.getStatus()) {
				response.addTask(task.clone());
			}
		}
		if(response.hasTasks()) {
			response.setResponseType(IDonResponse.ResponseType.SEARCH_SUCCESS);
		} else {
			response.addMessage(MSG_NO_UNDONE_TASKS);
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
		}
		return response;
	}

	/**
	 * Returns a list of tasks by the given task type
	 * 
	 * @param type
	 *            the type of the task
	 * @param allowOverdue
	 *            true if overdue tasks are allowed.
	 * @param allowFinished
	 *            true if completed tasks are allowed
	 * @return the list of tasks
	 */
	private List<IDonTask> getTaskByType(IDonTask.TaskType type,
			boolean allowOverdue, boolean allowFinished) {
		List<IDonTask> taskList = donStorage.getTaskList();
		List<IDonTask> resultList = new ArrayList<IDonTask>();
		Calendar now = Calendar.getInstance();
		for (IDonTask task : taskList) {
			if (task.getType() == type) {
				if (type == IDonTask.TaskType.DEADLINE) {
					if ((!allowOverdue && task.getStartDate().before(now))
							|| (!allowFinished && task.getStatus())) {
						// is overdue or finished
						continue;
					}
				} else if (type == IDonTask.TaskType.DURATION) {
					if ((!allowOverdue && task.getEndDate().before(now))
							|| (!allowFinished && task.getStatus())) {
						// is overdue or finished
						continue;
					}
				}
				// Clone the task to prevent the original from being edited.
				resultList.add(task.clone());
			}
		}
		return resultList;
	}
	
	/**
	 * Add a label to a task with the given id
	 * @param id the task's id to search for and add a label to
	 * @param labelName the name of the label to add
	 * @return the response containing the affected task
	 */
	private IDonResponse addLabel(int id, String labelName) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
			log.fine(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			List<String> currentLabels = task.getLabels();
			if (currentLabels.contains(labelName)) {
				response.setResponseType(IDonResponse.ResponseType.LABEL_EXISTS);
				response.addMessage(String.format("The label '%1$s' already exists", labelName));
				log.fine(String.format("The label '%1$s' already exists", labelName));
			} else {
				task.addLabel(labelName);
				response.setResponseType(IDonResponse.ResponseType.LABEL_ADDED);
				response.addTask(task);
			}

			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			//TODO change commandtype to label commands
			actionPast.push(new DonAction(
					AbstractDonCommand.CommandType.EDIT_ID_NAME, AbstractDonCommand.GeneralCommandType.EDIT, affectedTasks));
			actionFuture.clear();
		}
		
		return response;
	}
	
	/**
	 * Add a label to a task with the given name
	 * If more than 1 task has the name, it will not add the label
	 * @param id the task's id to search for and add a label to
	 * @param labelName the name of the label to add
	 * @return the response containing the affected task
	 */
	private IDonResponse addLabel(String title, String labelName) {
		IDonResponse response = new DonResponse();
		
		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			log.fine(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK, title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = addLabel(task.getID(), labelName);
		}
		
		return response;
	}
	
	/**
	 * Removes a label from a task with the given id
	 * @param id the task's id to search for and add a label to
	 * @param labelName the name of the label to add
	 * @return the response containing the affected task
	 */
	private IDonResponse removeLabel(int id, String labelName) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
			log.fine(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			List<String> currentLabels = task.getLabels();
			if (currentLabels.remove(labelName)) {
				response.setResponseType(IDonResponse.ResponseType.LABEL_REMOVED);
				response.addMessage(String.format("The label '%1$s' has been removed", labelName));
				log.fine(String.format("The label '%1$s' has been removed", labelName));
				response.addTask(task);
			} else {
				response.setResponseType(IDonResponse.ResponseType.LABEL_NOT_FOUND);
				response.addMessage(String.format("The label '%1$s' does not exist", labelName));
				log.fine(String.format("The label '%1$s' does not exist", labelName));
			}

			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			//TODO change commandtype to label commands
			actionPast.push(new DonAction(
					AbstractDonCommand.CommandType.EDIT_ID_NAME, AbstractDonCommand.GeneralCommandType.EDIT, affectedTasks));
			actionFuture.clear();
		}
		
		return response;
	}
	
	/**
	 * Removes a label from a task with the given name
	 * @param id the task's id to search for and add a label to
	 * @param labelName the name of the label to add
	 * @return the response containing the affected task
	 */
	private IDonResponse removeLabel(String title, String labelName) {
		IDonResponse response = new DonResponse();

		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			log.fine(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK, title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = removeLabel(task.getID(), labelName);
		}
		
		return response;
	}
	
	private IDonResponse findLabel(String labelName) {
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		
		for (IDonTask task : taskList) {
			List<String> labels = task.getLabels();
			for (String label : labels) {
				if (label.equalsIgnoreCase(labelName)) {
					response.addTask(task);
					break;
				}
			}
		}
		if (!response.hasTasks()) {
			// No task with given label found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_LABEL_FAILED, labelName));
			log.fine(String.format(MSG_SEARCH_LABEL_FAILED, labelName));
		} else {
			response.setResponseType(IDonResponse.ResponseType.SEARCH_SUCCESS);
		}
		
		return response;
	}

	/**
	 * Show the user help information
	 * 
	 * @return response containing help messages
	 */
	private IDonResponse getHelp(AbstractDonCommand.CommandType commandType) {
		// TODO: decide the format of the help
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.HELP);

		if (commandType == AbstractDonCommand.CommandType.HELP_GENERAL) {
			// Give info on all commands available
			response.addMessage("Welcome to DoOrNot. These are the available commands:");
			response.addMessage("add / a, edit / ed / e, search / s, del / d, mark / m");
			response.addMessage("Type help command name to learn how to use the command!");
		} else if (commandType == AbstractDonCommand.CommandType.HELP_ADD) {
			// Help for add
			response.addMessage("add / a: Adds a task to the todo list");
			response.addMessage("Command format: add \"Task title\"");
			response.addMessage("Command format: add \"Task title\" @ DD/MM/YYYY HHmm");
			response.addMessage("Command format: add \"Task title\" from DDMMYYYY_HHmm to DD/MM/YYYY HHmm");
			response.addMessage("All dates can either be with time (DD/MM/YYYY HHmm) or without (DD/MM/YYYY)");
			response.addMessage("Examples:");
			response.addMessage("add \"Finish reading Book X\" <-- Adds a floating task");
			response.addMessage("add \"Submit CS9842 assignment\" @ 18/11/2014 <-- Adds a task with a deadline at 18th of November 2014");
			response.addMessage("add \"Talk by person\" from 05/08/2015 1500 to 05/08/2015 1800 <-- Adds an event that lasts from 3pm of 5th August 2015 to 6pm of the same day");
		} else if (commandType == AbstractDonCommand.CommandType.HELP_EDIT) {
			// Help for edit
			response.addMessage("edit / ed / e: Edits a task in the todo list");
			response.addMessage("Command format: edit Task_id to \"New task title\"");
			response.addMessage("Command format: edit \"Part of old Task title\" to \"New task title\"");
			response.addMessage("Command format: edit Task_id to DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit \"Part of old Task title\" to DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit Task_id to from DD/MM/YYYY HHmm to DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit \"Part of old Task title\" to from DD/MM/YYYY HHmm to DD/MM/YYYY HHmm");
			response.addMessage("If multiple tasks are found with the given title, nothing will be edited.");
			response.addMessage("All dates can either be with time (DD/MM/YYYY HHmm) or without (DD/MM/YYYY)");
			response.addMessage("Examples:");
			response.addMessage("edit 22 to \"Do work\" <-- Changes task 22's title to Do work");
			response.addMessage("edit \"Do work\" to 17/01/2015 <-- Changes the deadline of the task containing \"Do work\" as the title to 17th January 2015");
			response.addMessage("edit 14 to from 02/05/2015 to 03/05/2015 <-- Changes the start and end dates of task 14 to 2nd and 3rd of May 2015 respectively");
		} else if (commandType == AbstractDonCommand.CommandType.HELP_DELETE) {
			// Help for delete
			response.addMessage("del / d: Delete a task in the todo list");
			response.addMessage("Command format: del Task_id");
			response.addMessage("Command format: del \"Part of Task title\"");
			response.addMessage("If multiple tasks are found with the given title, nothing will be deleted.");
			response.addMessage("Examples:");
			response.addMessage("del 22 <-- Deletes task 22");
			response.addMessage("del \"Do work\" <-- Deletes the task containing \"Do work\" in the title");
		} else if (commandType == AbstractDonCommand.CommandType.HELP_SEARCH) {
			// Help for search
			response.addMessage("search / s: Finds a task with the given ID, title or date");
			response.addMessage("Command format: search Task_id");
			response.addMessage("Command format: search \"Part of Task title\"");
			response.addMessage("Command format: search 22/01/2016");
			response.addMessage("All dates can either be with time (DD/MM/YYYY HHmm) or without (DD/MM/YYYY)");
			response.addMessage("Examples:");
			response.addMessage("search 22 <-- Searches for task 22");
			response.addMessage("search \"Do work\" <-- Searches for tasks containing \"Do work\" in the title");
			response.addMessage("search 22/01/2016 <-- Searches for tasks starting or occurring on the 22nd of January 2016");
		} else if (commandType == AbstractDonCommand.CommandType.HELP_UNDO || commandType == AbstractDonCommand.CommandType.HELP_REDO) {
			//Help for undo
			response.addMessage("undo : Undoes the previous action");
			response.addMessage("redo : Performs the last undone action");
			response.addMessage("Command format: undo/redo");
			response.addMessage("Examples:");
			response.addMessage("undo");
			response.addMessage("redo");
			response.addMessage("(What were you expecting?)");

		}

		return response;
	}

	/****
	 * Date helper methods
	 ****/

	
	

	/**
	 * Keeps track of an action performed the user for use with the undo command
	 */
	private class DonAction {
		private AbstractDonCommand.GeneralCommandType generalType;
		private AbstractDonCommand.CommandType actionType;
		private List<IDonTask> affectedTasks;

		public DonAction(AbstractDonCommand.CommandType type, AbstractDonCommand.GeneralCommandType genType, List<IDonTask> tasks) {
			actionType = type;
			generalType = genType;
			affectedTasks = tasks;
		}

		public AbstractDonCommand.CommandType getActionType() {
			return actionType;
		}
		
		public AbstractDonCommand.GeneralCommandType getGeneralType() {
			return generalType;
		}

		public List<IDonTask> getAffectedTasks() {
			return affectedTasks;
		}

	}

	@Override
	public List<IDonTask> getTaskList() {
		return donStorage.getTaskList();
	}

}

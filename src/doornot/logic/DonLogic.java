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
import doornot.logic.AbstractDonCommand.GeneralCommandType;
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

	
	private static final String MSG_COMMAND_WRONG_FORMAT = "The command you entered was of the wrong format!";
	private static final String MSG_COMMAND_WRONG_DATE = "The date you entered was invalid!";
	private static final String MSG_SAVE_SUCCESSFUL = "Save successful.";
	private static final String MSG_SAVE_FAILED = "Save failed.";
	
	private static final String MSG_SEARCH_ID_FAILED = "No tasks with ID of %1$d were found.";
	
	
	private static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	private static final String MSG_UNDO_SUCCESS = "Last action undone. %1$d change(s) removed.";
	private static final String MSG_REDO_NO_ACTIONS = "There are no actions to redo!";
	private static final String MSG_REDO_SUCCESS = "Redo successful. %1$d change(s) redone.";	

	private static final String MSG_EX_COMMAND_CANNOT_BE_NULL = "Command cannot be null";

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
		
		AbstractDonCommand.GeneralCommandType genCommandType = dCommand.getGeneralType();
		IDonResponse response = null;
		if (genCommandType == GeneralCommandType.UNDO) {
			response = undoLastAction();
		} else if (genCommandType == GeneralCommandType.REDO) {
			response = redoAction();
		} else if (genCommandType == GeneralCommandType.INVALID) {
			//TODO might have to split it into different invalids within GeneralCommandType
			response = createInvalidFormatResponse();
		} else {
			response = dCommand.executeCommand(donStorage);
			if(dCommand.hasExecuted()) {
				commandPast.add(dCommand);
				commandFuture.clear(); //If a change has been made, the redo stack needs to be cleared
			}
		}
		
		//TODO only add if the command has been successful (do a check)
		
		
		
		/*
		AbstractDonCommand.CommandType commandType = dCommand.getType();
		AbstractDonCommand.GeneralCommandType genCommandType = dCommand.getGeneralType();
		IDonResponse response = null;
		if (commandType == AbstractDonCommand.CommandType.UNDO) {
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
		return donParser.parseCommand("today").executeCommand(donStorage);
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
	 * Undoes the last action
	 * 
	 * @return response stating the status of the undo operation
	 */
	private IDonResponse undoLastAction() {
		IDonResponse response = new DonResponse();
		
		if(commandPast.size()<=0) {
			response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
			response.addMessage(MSG_UNDO_NO_ACTIONS);
			log.fine(MSG_UNDO_NO_ACTIONS);
		} else {
			AbstractDonCommand lastCommand = commandPast.pop();
			assert lastCommand.hasExecuted(); //The lastCommand can only be in the stack if it has run
			IDonResponse undoResponse = lastCommand.undoCommand(donStorage);
			if(!lastCommand.hasExecuted()) {
				//Command undone
				commandFuture.add(lastCommand);
				
				response.setResponseType(IDonResponse.ResponseType.UNDO_SUCCESS);
				response.addMessage(String.format(MSG_UNDO_SUCCESS, 1));
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
			log.fine(MSG_REDO_NO_ACTIONS);
		} else {
			AbstractDonCommand nextCommand = commandFuture.pop();
			assert !nextCommand.hasExecuted(); //The lastCommand can only be in the stack if it has run
			IDonResponse redoResponse = nextCommand.executeCommand(donStorage);
			if(nextCommand.hasExecuted()) {
				//Command redone
				commandPast.add(nextCommand);
				
				response.setResponseType(IDonResponse.ResponseType.REDO_SUCCESS);
				response.addMessage(String.format(MSG_REDO_SUCCESS, 1));
			}
			/*
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
			*/
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

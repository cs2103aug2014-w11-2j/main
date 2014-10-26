package doornot.logic;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import doornot.logic.AbstractDonCommand.GeneralCommandType;
import doornot.logic.IDonResponse.ResponseType;
import doornot.parser.DonParser;
import doornot.parser.IDonParser;
import doornot.storage.DonStorage;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;


/**
 * DonLogic - Class for handling the logic of the program
 * (creation/deletion/modification of tasks)
 * 
 */
//@author A0111995Y
public class DonLogic implements IDonLogic {

	
	private static final String MSG_COMMAND_WRONG_FORMAT = "The command you entered was of the wrong format!";
	private static final String MSG_COMMAND_WRONG_DATE = "The date you entered was invalid!";
	private static final String MSG_UNKNOWN_COMMAND = "You have entered an unknown command";
	private static final String MSG_SAVE_SUCCESSFUL = "Save successful.";
	private static final String MSG_SAVE_FAILED = "Save failed.";

	private static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	private static final String MSG_UNDO_SUCCESS = "Last action undone. %1$d change(s) removed.";
	private static final String MSG_REDO_NO_ACTIONS = "There are no actions to redo!";
	private static final String MSG_REDO_SUCCESS = "Redo successful. %1$d change(s) redone.";	

	private static final String MSG_EX_COMMAND_CANNOT_BE_NULL = "Command cannot be null";

	private IDonStorage donStorage;
	private IDonParser donParser;

	//actionPast contains the actions to undo, actionFuture to redo
	//If a new action that performs modifications is made, actionFuture has to be cleared.
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
	 * @param storage	the storage component
	 * @param parser	the parser component
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
		} else if (genCommandType == GeneralCommandType.INVALID_FORMAT) {
			
			response = createInvalidFormatResponse();
		} else if (genCommandType == GeneralCommandType.INVALID_DATE) {
			response = createInvalidDateResponse();
		} else if (genCommandType == GeneralCommandType.INVALID_COMMAND) {
			response = createInvalidCommandResponse();
		} else {
			response = dCommand.executeCommand(donStorage);
			if(dCommand.hasExecuted()) {
				commandPast.add(dCommand);
				commandFuture.clear(); //If a change has been made, the redo stack needs to be cleared
			}
		}
		
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
	 * Creates a response for unrecognized commands
	 * 
	 * @return the response
	 */
	private IDonResponse createInvalidCommandResponse() {
		IDonResponse response = new DonResponse();
		response.addMessage(MSG_UNKNOWN_COMMAND);
		response.setResponseType(ResponseType.UNKNOWN_COMMAND);
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
			lastCommand.undoCommand(donStorage);
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
			nextCommand.executeCommand(donStorage);
			if(nextCommand.hasExecuted()) {
				//Command redone
				commandPast.add(nextCommand);
				
				response.setResponseType(IDonResponse.ResponseType.REDO_SUCCESS);
				response.addMessage(String.format(MSG_REDO_SUCCESS, 1));
			}

		}
		return response;
	}


	@Override
	public List<IDonTask> getTaskList() {
		return donStorage.getTaskList();
	}

}

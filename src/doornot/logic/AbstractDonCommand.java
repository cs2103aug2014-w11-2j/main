package doornot.logic;

import java.util.Calendar;

import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
/**
 * Interface for DonCommand, a class for keeping track of what type of command and its parameters
 *
 */
//@author A0115503W
public abstract class AbstractDonCommand {
	
	protected static final String MSG_SEARCH_ID_FAILED = "No tasks with ID of %1$d were found.";
	protected static final String MSG_SEARCH_MORE_THAN_ONE_TASK = "'%1$s' returned more than 1 result. Please specify with the ID.";
	protected static final String MSG_SEARCH_TITLE_FAILED = "No tasks with a title containing '%1$s' were found.";
	protected static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	protected static final String MSG_UNDO_SUCCESS = "Last action undone. %1$d change(s) removed.";
	protected static final String MSG_EDIT_TITLE_SUCCESS = "Task name changed from '%1$s' to '%2$s'.";
	protected static final String MSG_EDIT_SINGLE_DATE_SUCCESS = "%1$s changed from %2$s to %3$s.";
	protected static final String MSG_ADD_TASK_FAILURE = "Could not add task '%1$s'";
	protected static final String MSG_ADD_FLOATING_TASK_SUCCESS = "'%1$s' has been added.";
	
	protected static final String PHRASE_END_DATE = "End date";
	protected static final String PHRASE_START_DATE = "Start date";
	
	//TODO Should only contain types denoting validity and global commands like undo/redo
	public static enum CommandType {
		ADD_FLOAT, 
		ADD_TASK, 
		ADD_EVENT, 
		
		EDIT_ID_NAME, 
		EDIT_ID_DATE, 
		EDIT_ID_EVENT,
		EDIT_NAME, 
		EDIT_DATE, 
		EDIT_EVENT, 
		
		DELETE_ID, 
		DELETE, 
		
		SEARCH_NAME, 
		SEARCH_DATE, 
		SEARCH_ID,
		
		SEARCH_LABEL,
		SEARCH_FREE,
		SEARCH_UNDONE,
		SEARCH_ALL,
		SEARCH_AFTDATE,
		TODAY,
		OVERDUE,
		
		MARK_ID, 
		MARK,
		
		HELP_ADD,
		HELP_EDIT,
		HELP_SEARCH,
		HELP_DELETE,
		HELP_MARK,
		HELP_UNDO,
		HELP_REDO,
		// for help with all commands in general. I think undo goes here
		HELP_GENERAL,
		
		LABEL_ID,
		LABEL_NAME,
		DELABEL_ID,
		DELABEL_NAME,
		
		UNDO,
		REDO,
		
		INVALID_COMMAND,
		// when format of command is wrongly typed
		INVALID_FORMAT,
		INVALID_DATE,
		EXIT,
		HELP
	}
	
	public static enum GeneralCommandType {
		ADD, EDIT, DELETE, SEARCH, MARK, UNDO, LABEL, REDO, HELP, EXIT, INVALID
	}
	
	protected CommandType commandType;
	
	protected boolean executed = false;
	
	public abstract IDonResponse executeCommand(IDonStorage donStorage);
	
	public abstract IDonResponse undoCommand(IDonStorage donStorage);
	
	protected IDonResponse createUndoFailureResponse() {
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
		response.addMessage(MSG_UNDO_NO_ACTIONS);
		return response;
	}
	
	protected IDonResponse createUndoSuccessResponse() {
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.UNDO_SUCCESS);
		response.addMessage(MSG_UNDO_SUCCESS);
		return response;
	}
	
	protected IDonResponse createSearchFailedResponse(String searchString) {
		IDonResponse response = new DonResponse();
		response.setResponseType(ResponseType.SEARCH_EMPTY);
		response.addMessage(String.format(MSG_SEARCH_TITLE_FAILED, searchString));
		return response;
	}
	
	public void setType(CommandType type){
		commandType = type;
	}
	
	public CommandType getType() {
		return commandType;
	}
	
}
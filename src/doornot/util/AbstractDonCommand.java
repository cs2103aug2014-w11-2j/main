package doornot.util;


import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
/**
 * Interface for DonCommand, a class for keeping track of what type of command and its parameters
 *
 */
//@author A0115503W
public abstract class AbstractDonCommand {
	
	protected static final String MSG_NO_UNDONE_TASKS = "Congratulations, you have no incomplete tasks!";
	protected static final String MSG_SEARCH_ID_FAILED = "No tasks with ID of %1$d were found.";
	protected static final String MSG_SEARCH_MORE_THAN_ONE_TASK = "'%1$s' returned more than 1 result. Please specify with the ID.";
	protected static final String MSG_SEARCH_TITLE_FAILED = "No tasks with a title containing '%1$s' were found.";
	protected static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	protected static final String MSG_UNDO_SUCCESS = "Last action undone. %1$d change(s) removed.";
	protected static final String MSG_EDIT_TITLE_SUCCESS = "Task name changed from '%1$s' to '%2$s'.";
	protected static final String MSG_EDIT_SINGLE_DATE_SUCCESS = "%1$s changed from %2$s to %3$s.";
	protected static final String MSG_ADD_TASK_FAILURE = "Could not add task '%1$s'";
	protected static final String MSG_ADD_FLOATING_TASK_SUCCESS = "'%1$s' has been added.";
	protected static final String MSG_DELETE_SUCCESS = "The task was deleted successfully.";
	protected static final String MSG_DELETE_FAILED = "The task could not be deleted.";
	protected static final String MSG_SEARCH_LABEL_FAILED = "No tasks with label '%1$s' were found.";
	protected static final String MSG_SEARCH_DATE_FAILED = "No tasks starting in '%1$s' were found.";
	protected static final String MSG_FREE_EVERYWHERE = "You are free!";
	protected static final String MSG_TOGGLE_STATUS_ID_SUCCESS = "Task %1$d has been set to '%2$s'";
	protected static final String MSG_TOGGLE_STATUS_MULTI_SUCCESS = "%1$d tasks' status toggled.";
	protected static final String MSG_LABEL_NAME_REMOVED = "The label '%1$s' has been removed";
	protected static final String MSG_LABEL_STRING_DOES_NOT_EXIST = "The label '%1$s' does not exist";
	protected static final String MSG_LABEL_EXISTS = "The label '%1$s' already exists";
	protected static final String MSG_LABEL_ADDED_ID = "Label '%1$s' added to task %2$d";
	protected static final String MSG_COMMAND_WRONG_FORMAT = "The format of '%1$s' is invalid!";
	protected static final String MSG_COMMAND_WRONG_DATE = "The date you entered was invalid!";
	protected static final String MSG_UNKNOWN_COMMAND = "The command '%1$s' does not exist!";
	protected static final String MSG_NO_UNDONE_OVERDUE = "No undone overdue tasks found!";
	protected static final String MSG_NO_FLOATING = "No floating tasks found!";
	
	protected static final String PHRASE_END_DATE = "End date";
	protected static final String PHRASE_START_DATE = "Start date";
	protected static final String PHRASE_COMPLETE = "complete";
	protected static final String PHRASE_INCOMPLETE = "incomplete";
	
	protected static final int FIND_INCOMPLETE = 0;
	protected static final int FIND_COMPLETE = 1;
	protected static final int FIND_ALL = 2;
	
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
		ADD, EDIT, DELETE, SEARCH, MARK, UNDO, LABEL, REDO, HELP, EXIT, INVALID_COMMAND, INVALID_FORMAT, INVALID_DATE
	}
	
	protected CommandType commandType;
	
	protected GeneralCommandType generalCommandType;
	
	protected boolean executed = false; //Set to true when execution has finished to allow undo to take place
	
	protected boolean error = false;
	
	public abstract IDonResponse executeCommand(IDonStorage donStorage);
	
	public abstract IDonResponse undoCommand(IDonStorage donStorage);
	
	protected IDonResponse createUndoFailureResponse() {
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
		response.addMessage(MSG_UNDO_NO_ACTIONS);
		return response;
	}
	
	protected IDonResponse createUndoSuccessResponse(int num) {
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.UNDO_SUCCESS);
		response.addMessage(String.format(MSG_UNDO_SUCCESS, num));
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
	/*
	public CommandType getType() {
		return commandType;
	}
	*/
	
	public GeneralCommandType getGeneralType() {
		return generalCommandType;
	}
	
	public boolean hasExecuted() {
		return executed;
	}
	
	public boolean hasError() {
		return error;
	}
	
}

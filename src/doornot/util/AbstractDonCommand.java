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
	
	protected static final String MSG_NAME_CONFLICT = "Warning: Another task with '%1$s' already exists.";
	protected static final String MSG_DEADLINE_CONFLICT = "Warning: Another task is happening at the same time.";
	protected static final String MSG_ADD_FLOATING_TASK_SUCCESS = "'%1$s' has been added.";
	protected static final String MSG_ADD_TASK_FAILURE = "Could not add task '%1$s'";
	protected static final String MSG_COMMAND_WRONG_DATE = "The date you entered was invalid!";
	protected static final String MSG_COMMAND_WRONG_FORMAT = "The format of '%1$s' is invalid!";
	protected static final String MSG_DELETE_FAILED = "The task could not be deleted.";
	protected static final String MSG_DELETE_SUCCESS = "The task was deleted successfully.";
	protected static final String MSG_DELETE_ALL_WITH_LABEL_SUCCESS = "All tasks with the label #%1$s deleted.";
	protected static final String MSG_EDIT_SINGLE_DATE_SUCCESS = "%1$s changed from %2$s to %3$s.";
	protected static final String MSG_EDIT_SINGLE_DATE_ADD_SUCCESS = "%1$s changed to %2$s.";
	protected static final String MSG_EDIT_TITLE_SUCCESS = "Task name changed from '%1$s' to '%2$s'.";
	protected static final String MSG_FREE_EVERYWHERE = "You are free!";
	protected static final String MSG_LABEL_ADDED_ID = "Label '%1$s' added to task %2$d.";
	protected static final String MSG_LABEL_EXISTS = "The label '%1$s' already exists.";
	protected static final String MSG_LABEL_NAME_REMOVED = "The label '%1$s' has been removed.";
	protected static final String MSG_LABEL_ALL_REMOVED = "All labels have been removed.";
	protected static final String MSG_LABEL_STRING_DOES_NOT_EXIST = "The label '%1$s' does not exist";
	protected static final String MSG_MATCHING_RESULTS = "Matching results for '%1$s'";
	protected static final String MSG_NO_FLOATING = "No floating tasks found!";
	protected static final String MSG_NO_LABEL_TASKS = "No tasks with the label '%1$s' found!";
	protected static final String MSG_NO_UNDONE_OVERDUE = "No undone overdue tasks found!";
	protected static final String MSG_NO_UNDONE_TASKS = "Congratulations, you have no incomplete tasks!";
	protected static final String MSG_NO_DONE_TASKS = "No completed tasks found!";
	protected static final String MSG_SEARCH_DATE_FAILED = "No tasks starting in '%1$s' were found.";
	protected static final String MSG_SEARCH_FAILED = "No tasks found";
	protected static final String MSG_SEARCH_FOUND = "%1$d task(s) found";
	protected static final String MSG_SEARCH_ID_FAILED = "No tasks with ID of %1$d were found.";
	protected static final String MSG_SEARCH_ID_FOUND = "Task %1$d found";
	protected static final String MSG_SEARCH_RESULT_NAME = "Search results for '%1$s'";
	protected static final String MSG_SEARCH_RESULT_ID = "Search results for ID %1$d";
	protected static final String MSG_SEARCH_RESULT_UNDONE = "Undone tasks";
	protected static final String MSG_SEARCH_RESULT_TODAY = "Today's tasks";
	protected static final String MSG_SEARCH_RESULT_FREE_TIME = "Free time";
	protected static final String MSG_SEARCH_RESULT_ALL = "All tasks";
	protected static final String MSG_SEARCH_RESULT_WEEK = "Tasks in the coming seven days";
	protected static final String MSG_SEARCH_RESULT_FUTURE = "Future tasks";
	protected static final String MSG_SEARCH_RESULT_OVERDUE = "Overdue tasks";
	protected static final String MSG_SEARCH_RESULT_FLOAT = "Floating tasks";
	protected static final String MSG_SEARCH_RESULT_DONE = "Completed tasks";
	protected static final String MSG_SEARCH_LABEL_FAILED = "No tasks with label '%1$s' were found.";
	protected static final String MSG_SEARCH_LABEL_FOUND = "%1$d tasks with label '%2$s' found.";
	protected static final String MSG_SEARCH_MORE_THAN_ONE_TASK = "'%1$s' returned more than 1 result. Please specify with the ID.";
	protected static final String MSG_SEARCH_NAME_FOUND = "%1$d task(s) containing '%2$s' found";
	protected static final String MSG_SEARCH_TITLE_FAILED = "No tasks with a title containing '%1$s' were found.";
	protected static final String MSG_SEARCH_UNDONE_FOUND = "You have %1$d undone tasks.";
	protected static final String MSG_TOGGLE_STATUS_ID_SUCCESS = "Task %1$d has been set to '%2$s'";
	protected static final String MSG_TOGGLE_STATUS_MULTI_SUCCESS = "%1$d tasks' status toggled.";
	protected static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	protected static final String MSG_UNDO_SUCCESS = "Last action undone. %1$d change(s) removed.";
	protected static final String MSG_UNKNOWN_COMMAND = "The command '%1$s' does not exist!";
	protected static final String MSG_LABEL_OVERLOAD = "Labelling failed. A task cannot have more than %1$d labels!";
	
	protected static final String PHRASE_END_DATE = "End date";
	protected static final String PHRASE_START_DATE = "Start date";
	protected static final String PHRASE_COMPLETE = "complete";
	protected static final String PHRASE_INCOMPLETE = "incomplete";
	protected static final String PHRASE_DEADLINE = "Deadline";
	protected static final String PHRASE_FREE_TIME = "Free time";
	
	protected static final int FIND_INCOMPLETE = 0;
	protected static final int FIND_COMPLETE = 1;
	protected static final int FIND_ALL = 2;
	
	public static enum GeneralCommandType {
		ADD, EDIT, DELETE, SEARCH, MARK, UNDO, LABEL, REDO, HELP, EXIT, INVALID_COMMAND, INVALID_FORMAT, INVALID_DATE
	}

	protected GeneralCommandType generalCommandType;
	
	protected boolean executed = false; //Set to true when execution has finished to allow undo to take place
	
	/**
	 * Runs the AbstractDonCommand on tasks stored in donStorage
	 * @param donStorage the storage object containing the user's tasks
	 * @return the response produced by execution of the command
	 */
	public abstract IDonResponse executeCommand(IDonStorage donStorage);
	
	/**
	 * Reverses the action performed by the AbstractDonCommand in executeCommand
	 * @param donStorage the storage object containing the user's tasks
	 * @return the response produced by reversing the command
	 */
	public abstract IDonResponse undoCommand(IDonStorage donStorage);
	
	/**
	 * Creates a standard failure response for the undo function
	 * @return the undo failure response
	 */
	protected IDonResponse createUndoFailureResponse() {
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
		response.addMessage(MSG_UNDO_NO_ACTIONS);
		return response;
	}
	
	/**
	 * Creates a standard success response for the undo function
	 * @param num the number of changes reversed
	 * @return the undo failure response
	 */
	protected IDonResponse createUndoSuccessResponse(int num) {
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.UNDO_SUCCESS);
		response.addMessage(String.format(MSG_UNDO_SUCCESS, num));
		return response;
	}
	
	/**
	 * Creates a standard search failure response
	 * @param searchString the search string which failed the search
	 * @return the search failure response
	 */
	protected IDonResponse createSearchFailedResponse(String searchString) {
		IDonResponse response = new DonResponse();
		response.setResponseType(ResponseType.SEARCH_EMPTY);
		response.addMessage(String.format(MSG_SEARCH_TITLE_FAILED, searchString));
		return response;
	}
	
	/**
	 * Gets the general command type of the AbstractDonCommand
	 * @return the general type
	 */
	public GeneralCommandType getGeneralType() {
		return generalCommandType;
	}
	
	/**
	 * Returns whether the AbstractDonCommand object has been executed
	 * @return
	 */
	public boolean hasExecuted() {
		return executed;
	}
	
}

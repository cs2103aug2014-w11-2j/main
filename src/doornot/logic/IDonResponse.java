package doornot.logic;

import java.util.List;

import doornot.storage.IDonTask;

/**
 * Interface defining the methods that the GUI needs from a IDonResponse object
 * Each command call by the user should result in 1 IDonResponse object
 */
//@author A0111995Y
public interface IDonResponse {

	public enum ResponseType {
		ADD_SUCCESS, ADD_FAILURE, DEL_SUCCESS, DEL_FAILURE, SEARCH_EMPTY, SEARCH_SUCCESS, EDIT_SUCCESS, EDIT_FAILURE, UNDO_SUCCESS, UNDO_FAILURE, REDO_SUCCESS, REDO_FAILURE, SAVE_SUCCESS, SAVE_FAILURE, HELP, UNKNOWN_COMMAND,
		LABEL_EXISTS, LABEL_ADDED, LABEL_REMOVED, LABEL_NOT_FOUND
	}

	public ResponseType getResponseType();

	/**
	 * Returns the response messages as a list. The list will be empty if there
	 * are no messages
	 * 
	 * @return the list of messages
	 */
	public List<String> getMessages();

	/**
	 * Returns the list of tasks enclosed within the response. The list will be
	 * empty if there are no tasks.
	 * 
	 * @return the list of tasks
	 */
	public List<IDonTask> getTasks();
	
	/**
	 * Returns the list of periods enclosed within the response. The list will be
	 * empty if there are no period.
	 * 
	 * @return the list of periods
	 */
	public List<DonPeriod> getPeriods();

	/**
	 * Adds a message to the response
	 * 
	 * @param message
	 *            the message to add
	 * @return true if the addition was successful
	 */
	public boolean addMessage(String message);

	/**
	 * Adds a task to the response
	 * 
	 * @param task
	 *            the task to add
	 * @return true if the addition was successful
	 */
	public boolean addTask(IDonTask task);
	
	/**
	 * Adds a period to the response
	 * 
	 * @param period
	 *            the period to add
	 * @return true if the addition was successful
	 */
	public boolean addPeriod(DonPeriod period);

	/**
	 * Sets the response type of the IDonResponse object
	 * 
	 * @param type
	 *            the new type of response
	 */
	public void setResponseType(ResponseType type);
	
	/**
	 * Sets the list of tasks of the IDonResponse object directly
	 * 
	 * @param task
	 *            the new task list
	 */
	public void setTaskList(List<IDonTask> tasks);

	/**
	 * Returns whether the response has any messages. This is identical to
	 * checking getMessages().size()>0
	 * 
	 * @return true if the response contains one or more messages
	 */
	public boolean hasMessages();

	/**
	 * Returns whether the response has any tasks. This is identical to checking
	 * getTasks().size()>0
	 * 
	 * @return true if the response contains one or more tasks
	 */
	public boolean hasTasks();

	/**
	 * Copies the task from another IDonResponse into this IDonResponse. If the
	 * response already has tasks inside, the tasks from the other response will
	 * be appended to the tasks in the response object.
	 * 
	 * @param response
	 *            the response to copy tasks from
	 */
	public void copyTasks(IDonResponse response);
}

import java.util.List;

/**
 * Interface defining the methods that the GUI needs from a IDonResponse object
 * Each command call by the user should result in 1 IDonResponse object
 * 
 * @author cs2103aug2014-w11-2j
 * 
 */
public interface IDonResponse {

	/**
	 * Returns the response messages as a list. The list will be empty if there
	 * are no messages
	 * 
	 * @return	the list of messages
	 */
	public List<String> getMessages();

	/**
	 * Returns the list of tasks enclosed within the response. The list will be
	 * empty if there are no tasks.
	 * 
	 * @return	the list of tasks
	 */
	public List<IDonTask> getTasks();
	
	/**
	 * Adds a message to the response
	 * @param	message	the message to add
	 * @return	true if the addition was successful
	 */
	public boolean addMessage(String message);
	
	/**
	 * Adds a task to the response
	 * @param	task	the task to add
	 * @return	true if the addition was successful
	 */
	public boolean addTask(IDonTask task);
	
	/**
	 * Returns whether the response has any messages.
	 * This is identical to checking getMessages().size()>0
	 * @return	true if the response contains one or more messages
	 */
	public boolean hasMessages();
	
	/**
	 * Returns whether the response has any tasks.
	 * This is identical to checking getTasks().size()>0
	 * @return	true if the response contains one or more tasks
	 */
	public boolean hasTasks();
}

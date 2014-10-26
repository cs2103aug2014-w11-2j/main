package doornot.storage;

import java.util.List;

/**
 * Interface defining the functionalities of the storage component
 * 
 * @author cs2103aug2014-w11-2j
 * 
 */
public interface IDonStorage {
	
	/**
	 * Return ID of task 
	 * @return  ID of added task
	 */
	public int addTask(IDonTask task);

	/**
	 * Return response
	 * @return successful
	 */
	public boolean removeTask(int taskID);
	
	/**
	 *  Return the next ID for task
	 * @return  ID
	 */
	public int getNextID();
	
	/**
	 *  Return a Task
	 * @return Task
	 */
	public IDonTask getTask(int ID);
	
	/**
	 * Find tasks with the given name
	 * 
	 * @param name
	 *            the name to search for
	 * @return the response containing the tasks
	 */
	public List<IDonTask> getTaskByName(String name);
	
	/**
	 * Return response
	 * @return successful
	 */
	public boolean saveToDisk();
	/**
	 * Return response
	 * @return successful
	 */
	public boolean loadFromDisk();
	/**
	 * Return list
	 * @return List of Tasks
	 */
	public List<IDonTask> getTaskList();

}

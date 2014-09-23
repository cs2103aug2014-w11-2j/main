package doornot.storage;

import doornot.logic.IDonTask;

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
	public List<IDonTask> getTaskList(IDonTask.TaskType type);

}

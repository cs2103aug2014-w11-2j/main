package doornot.storage;

import doornot.logic.IDonTask;

/**
 * Interface defining the functionalities of the storage component
 * 
 * @author cs2103aug2014-w11-2j
 * 
 */
public interface IDonStorage {

	public int addTask(IDonTask task);

	public boolean removeTask(int taskID);
	
	public int getNextID();
	
	public IDonTask getTask(int ID);

}

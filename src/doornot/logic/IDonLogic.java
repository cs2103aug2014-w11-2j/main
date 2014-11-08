package doornot.logic;

import java.util.List;

import doornot.storage.IDonTask;

/**
 * Interface defining the methods required of the logic component
 */
//@author A0111995Y
public interface IDonLogic {

	/**
	 * Runs the given command and returns the result in an IDonResponse object
	 * 
	 * @param command
	 *            the command given by the user
	 * @return the response
	 */
	public IDonResponse runCommand(String command);

	/**
	 * Perform a save of the data in DoOrNot to drive. This is expected to be
	 * called from the GUI on a repeating interval.
	 * 
	 * @return whether the save was successful
	 */
	public IDonResponse saveToDrive();

	/**
	 * Method to get called by DoOrNot at startup of the program to bring up a
	 * list of latest tasks. Gets information required by a welcome "page"
	 * 
	 * @return the response containing relevant tasks and messages
	 */
	public IDonResponse initialize();

	/**
	 * Gets the list of tasks held by the storage component of the logic
	 * component
	 * 
	 * @return the list of tasks
	 */
	public List<IDonTask> getTaskList();
	
	/**
	 * Gets today's tasks
	 * @return the list of tasks today
	 */
	public List<IDonTask> getTodayTasks();
	
	/**
	 * Gets the tasks happening within the week
	 * @return the list of task in the 7 days ahead
	 */
	public List<IDonTask> getWeekTasks();
	
	/**
	 * Gets the tasks happening after 7 days
	 * @return the list of tasks
	 */
	public List<IDonTask> getFutureTasks();
	
	/**
	 * Gets all floating tasks
	 * @return the list of floating tasks
	 */
	public List<IDonTask> getFloatingTasks();
	
	/**
	 * Gets all overdue tasks
	 * @return the list of overdue tasks
	 */
	public List<IDonTask> getOverdueTasks();
}

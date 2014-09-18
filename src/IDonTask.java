import java.util.Calendar;

/**
 * Interface defining the basic requirements of a Task
 * All references to "date" in the class refer to date and time (if present)
 * 
 * @author cs2103aug2014-w11-2j
 *
 */
public interface IDonTask {
	
	public enum TaskType {
		TASK_FLOATING, TASK_DEADLINE, TASK_DURATION
	}
	
	/**
	 * Returns the unique ID of the task
	 * 
	 * @return	the ID of the task
	 */
	public int getID();
	
	/**
	 * Returns the title of the task
	 * 
	 * @return	the title of the task
	 */
	public String getTitle();
	
	/**
	 * Returns the starting date of the task if it is present. Returns null otherwise. 
	 * For tasks with only a deadline, this will return the deadline.
	 * 
	 * @return	the starting date of the task
	 */
	public Calendar getStartDate();
	
	/**
	 * Returns the end date of the task if it is present. Returns null otherwise. 
	 * For tasks with only a deadline, this will return null.
	 * 
	 * @return	the ending date of the task
	 */
	public Calendar getEndDate();
	
	/**
	 * Returns the completion status of the task, with true representing done 
	 * and false representing undone.
	 * 
	 * @return	the status of the task
	 */
	public boolean getStatus();
	
	/**
	 * Returns the type of the task based on the fields used/unused in the Task
	 * @return	the type of the task
	 */
	public TaskType getType();
	
	/**
	 * Sets the title of the task to the given title
	 * 
	 * @param	newTitle the new title of the task
	 */
	public void setTitle(String newTitle);

	/**
	 * Sets the start date or deadline of the task to the given date
	 * Set to null to denote a floating task.
	 * @param	newDate the new date
	 */
	public void setStartDate(Calendar newDate);
	
	/**
	 * Sets the end date of a task that contains a duration.
	 * Set to null to denote either a floating task or a task with only a deadline.
	 * 
	 * @param	newDate the new date
	 */
	public void setEndDate(Calendar newDate);
	
	/**
	 * Sets the completion status of the task.
	 * True represents a completed task.
	 * @param	newStatus the completion status of the task
	 */
	public void setStatus(boolean newStatus);
}

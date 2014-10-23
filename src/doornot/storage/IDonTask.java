package doornot.storage;

import java.util.Calendar;
import java.util.List;

/**
 * Interface defining the basic requirements of a Task
 * All references to "date" in the class refer to date and time (if present)
 * 
 */
//@author A0111995Y
public interface IDonTask extends Comparable<IDonTask>, Cloneable {
	
	public enum TaskType {
		FLOATING, DEADLINE, DURATION
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
	 * Returns the label of the task if it is present. Return null otherwise.
	 * @return	the label of the task
	 */
	public List<String> getLabels();
	
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
	
	/**
	 * Sets the labels of the task.
	 * @param	labels of the task.
	 */
	public void setLabels(List<String> newLabels);
	
	/**
	 * Copies all fields of the given IDonTask into the current IDonTask
	 * @param	sourceTask	the task to copy details from
	 */
	public void copyTaskDetails(IDonTask sourceTask);
	
	/**
	 * Add new labels for a task, duplication checked ignoring case.
	 * @param	label of the task.
	 * @return true for successful add, false if there is duplication
	 */
	public boolean addLabel(String newLabel);
	
	/**
	 * Delete a label for a task.
	 * @param	index of label to be deleted.
	 * @return  true for successful delete, false otherwise
	 */
	public boolean deleteLabel(String labelToDelete);
	
	/**
	 * Sets whether the user has set time for the dates in this task
	 * @param timeUsed true if user has set time, false otherwise
	 */
	public void setTimeUsed(boolean timeUsed);
	
	/**
	 * Returns whether time is used in the date fields of the task
	 * @return true if time is used
	 */
	public boolean isTimeUsed();
	
	public IDonTask clone();
}

package doornot.parser;

import java.util.Calendar;

public interface IDonCommand {
	
	
	public enum CommandType {
		ADD_FLOAT, ADD_TASK, ADD_EVENT, EDIT_ID_NAME, EDIT_ID_DATE, EDIT_ID_EVENT,
		EDIT_NAME, EDIT_DATE, EDIT_EVENT, DELETE_ID, DELETE, SEARCH_NAME, SEARCH_DATE, SEARCH_ID,
		MARK_ID, MARK, UNDO, INVALID, EXIT 
	}
	
	/**
	 * Returns the type of command
	 * 
	 * @return the type of command
	 */
	public CommandType getType();
	
	/**
	 * Gets the ID of the task
	 * @return int ID
	 */
	public int getID();
	
	/**
	 * Gets the name of the task being edited/searched/marked
	 * @return name of edited task
	 */
	public String getName();
	
	/**
	 * Gets the date of task being edited/searched/marked
	 * @return
	 */
	public Calendar getDeadline();
	
	
	/**
	 * Gets the new name of the task
	 * Used for add, edit commands
	 * 
	 * @return new name
	 */
	public String getNewName();
	
	/**
	 * Gets the new start date of the event task
	 * Used for add, edit commands
	 *
	 * @return new start date
	 */
	public Calendar getNewStartDate();
	
	/**
	 * Gets the new end date of the event task
	 * Used for add, edit commands
	 * 
	 * @return new end date
	 */
	public Calendar getNewEndDate();
	
	/**
	 * Gets the new deadline of the task
	 * Used for add, edit commands
	 * 
	 * @return new deadline
	 */
	public Calendar getNewDeadline();
	
}

package doornot.parser;

import java.util.Calendar;
/**
 * Interface for DonCommand, a class for keeping track of what type of command and its parameters
 *
 */
//@author A0115503W
public interface IDonCommand {
	
	
	public static enum CommandType {
		ADD_FLOAT, 
		ADD_TASK, 
		ADD_EVENT, 
		
		EDIT_ID_NAME, 
		EDIT_ID_DATE, 
		EDIT_ID_EVENT,
		EDIT_NAME, 
		EDIT_DATE, 
		EDIT_EVENT, 
		
		DELETE_ID, 
		DELETE, 
		
		SEARCH_NAME, 
		SEARCH_DATE, 
		SEARCH_ID,
		
		SEARCH_FREE,
		SEARCH_UNDONE,
		SEARCH_ALL,
		SEARCH_AFTDATE,
		TODAY,
		OVERDUE,
		
		MARK_ID, 
		MARK,
		
		HELP_ADD,
		HELP_EDIT,
		HELP_SEARCH,
		HELP_DELETE,
		HELP_MARK,
		HELP_UNDO,
		HELP_REDO,
		// for help with all commands in general. I think undo goes here
		HELP_GENERAL,
		
		UNDO,
		REDO,
		
		INVALID_COMMAND,
		// when format of command is wrongly typed
		INVALID_FORMAT,
		INVALID_DATE,
		EXIT,
		HELP
	}
	
	public static enum GeneralCommandType {
		ADD, EDIT, DELETE, SEARCH, MARK, UNDO, REDO, HELP, EXIT, INVALID
	}
	
	/**
	 * Returns the type of command
	 * 
	 * @return the type of command
	 */
	public CommandType getType();
	
	/**
	 * Returns the broader type of a command
	 * 
	 * @return the general type of command
	 */
	public GeneralCommandType getGeneralType();
	
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
	
	/**
	 * Sets the commandType for this command
	 * @param type
	 */
	public void setType(CommandType type);
	
	/**
	 * Sets the ID of task for this command
	 * @param ID
	 */
	public void setID(int ID);
	
	/**
	 * Sets name of task for this command
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * Sets deadline of task for this command
	 * @param deadline
	 */
	public void setDeadline(Calendar deadline);
	
	/**
	 * Sets new name of task for this command
	 * @param newName
	 */
	public void setNewName(String newName);
	
	/**
	 * Sets new start date of task for this command
	 * @param newStart
	 */
	public void setNewStartDate(Calendar newStart);
	
	/**
	 * Sets new end date of task for this command
	 * @param newEnd
	 */
	public void setNewEndDate(Calendar newEnd);
	
	/**
	 * Sets new deadline of task for this command
	 * @param newDeadline
	 */
	public void setNewDeadline(Calendar newDeadline);
	
	
	
}

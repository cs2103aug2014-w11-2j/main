package doornot.logic;

import java.util.Calendar;

import doornot.storage.IDonStorage;
/**
 * Interface for DonCommand, a class for keeping track of what type of command and its parameters
 *
 */
//@author A0115503W
public abstract class AbstractDonCommand {
	
	private boolean executed = false;
	
	public abstract IDonResponse executeCommand(IDonStorage donStorage);
	
	public abstract IDonResponse undoCommand(IDonStorage donStorage);
	
	
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
		
		SEARCH_LABEL,
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
		
		LABEL_ID,
		LABEL_NAME,
		DELABEL_ID,
		DELABEL_NAME,
		
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
		ADD, EDIT, DELETE, SEARCH, MARK, UNDO, LABEL, REDO, HELP, EXIT, INVALID
	}
	
	
}

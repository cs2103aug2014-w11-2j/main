package doornot.parser;

import java.util.Calendar;

/**
 * DonCommand keeps track of command types and parameters
 * 
 */
//@author A0115503W
public class DonCommand implements IDonCommand{
	
	private CommandType commandType;
	private int taskID;
	private String taskName;
	private String taskNewName;
	private Calendar taskDeadline;
	private Calendar taskNewDeadline;
	private Calendar taskNewStartDate;
	private Calendar taskNewEndDate;
	private String taskLabel;
	//default no time set
	private boolean userSetTime = false;
	
	
	public DonCommand() {
		
	}

	@Override
	public void setType(CommandType type){
		commandType = type;
	}
	
	@Override
	public void setID(int ID){
		taskID = ID;
	}
	
	@Override
	public void setName(String name){
		taskName = name;
	}
	
	@Override
	public void setDeadline(Calendar deadline){
		taskDeadline = deadline;
	}
	
	@Override
	public void setNewName(String newName){
		taskNewName = newName;
	}
	
	@Override
	public void setNewStartDate(Calendar newStart){
		taskNewStartDate = newStart;
	}
	
	@Override
	public void setNewEndDate(Calendar newEnd){
		taskNewEndDate = newEnd;
	}
	
	@Override
	public void setNewDeadline(Calendar newDeadline){
		taskNewDeadline = newDeadline;
	}
	
	@Override
	public void setLabel(String label) {
		taskLabel = label;
		
	}
	
	@Override
	public void setHasUserSetTime(boolean bool) {
		userSetTime = bool;

	}
	
	@Override
	public CommandType getType() {
		
		return commandType;
	}
	
	@Override
	public int getID() {
		return taskID;
	}
	
	
	@Override
	public String getName() {
		return taskName;
	}
	
	@Override
	public Calendar getDeadline() {
		return taskDeadline;
	}
	
	@Override
	public String getNewName() {
		return taskNewName;
	}

	@Override
	public Calendar getNewStartDate() {
		return taskNewStartDate;
	}

	@Override
	public Calendar getNewEndDate() {
		return taskNewEndDate;
	}

	@Override
	public Calendar getNewDeadline() {
		return taskNewDeadline;
	}
	
	@Override
	public String getLabel() {
		return taskLabel;
	}
	
	@Override
	public boolean hasUserSetTime() {
		return userSetTime;
	}
	
	@Override
	public GeneralCommandType getGeneralType() {
		if(getType()==CommandType.ADD_EVENT 
			|| getType()== CommandType.ADD_EVENT
			|| getType() == CommandType.ADD_FLOAT) {
			return GeneralCommandType.ADD;
		} else if(getType()==CommandType.EDIT_DATE
				|| getType()==CommandType.EDIT_EVENT
				|| getType()==CommandType.EDIT_ID_DATE
				|| getType()==CommandType.EDIT_ID_EVENT
				|| getType()==CommandType.EDIT_ID_NAME
				|| getType()==CommandType.EDIT_NAME) {
			return GeneralCommandType.EDIT;
		} else if(getType()==CommandType.DELETE
				|| getType()==CommandType.DELETE_ID) {
			return GeneralCommandType.DELETE;
		} else if(getType()==CommandType.MARK
				|| getType()==CommandType.MARK_ID) {
			return GeneralCommandType.MARK;
		} else if(getType()==CommandType.SEARCH_DATE
				|| getType()==CommandType.SEARCH_ID
				|| getType()==CommandType.SEARCH_NAME
				|| getType()==CommandType.SEARCH_UNDONE
				|| getType()==CommandType.SEARCH_ALL
				|| getType()==CommandType.SEARCH_FREE
				|| getType()==CommandType.SEARCH_AFTDATE
				|| getType()==CommandType.TODAY
				|| getType()==CommandType.OVERDUE) {
			return GeneralCommandType.SEARCH;
		} else if(getType()==CommandType.HELP_ADD
					|| getType()==CommandType.HELP_EDIT
					|| getType()==CommandType.HELP_SEARCH
					|| getType()==CommandType.HELP_DELETE
					|| getType()==CommandType.HELP_MARK
					|| getType()==CommandType.HELP_UNDO
					|| getType()==CommandType.HELP_REDO
					|| getType()==CommandType.HELP_GENERAL) {
				return GeneralCommandType.HELP;
		} else if(getType()==CommandType.INVALID_COMMAND
				|| getType()==CommandType.INVALID_DATE
				|| getType()==CommandType.INVALID_FORMAT) {
			return GeneralCommandType.INVALID;
		} else if(getType()==CommandType.UNDO) {
			return GeneralCommandType.UNDO;
		} else if(getType()==CommandType.REDO) {
			return GeneralCommandType.REDO;
		} else if(getType()==CommandType.HELP) {
			return GeneralCommandType.HELP;
		} else if(getType()==CommandType.EXIT) {
			return GeneralCommandType.EXIT;
		}
		return null;
	}

}

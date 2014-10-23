package doornot.logic;

import java.util.Calendar;

import doornot.storage.IDonStorage;

/**
 * DonCommand keeps track of command types and parameters
 * 
 */
//@author A0115503W
public class DonCommand extends AbstractDonCommand {
	
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


	public void setType(CommandType type){
		commandType = type;
	}
	

	public void setID(int ID){
		taskID = ID;
	}
	

	public void setName(String name){
		taskName = name;
	}
	

	public void setDeadline(Calendar deadline){
		taskDeadline = deadline;
	}
	

	public void setNewName(String newName){
		taskNewName = newName;
	}
	

	public void setNewStartDate(Calendar newStart){
		taskNewStartDate = newStart;
	}
	

	public void setNewEndDate(Calendar newEnd){
		taskNewEndDate = newEnd;
	}

	public void setNewDeadline(Calendar newDeadline){
		taskNewDeadline = newDeadline;
	}
	

	public void setLabel(String label) {
		taskLabel = label;
		
	}
	

	public void setHasUserSetTime(boolean bool) {
		userSetTime = bool;

	}
	

	public CommandType getType() {
		
		return commandType;
	}
	

	public int getID() {
		return taskID;
	}
	
	

	public String getName() {
		return taskName;
	}
	

	public Calendar getDeadline() {
		return taskDeadline;
	}
	

	public String getNewName() {
		return taskNewName;
	}


	public Calendar getNewStartDate() {
		return taskNewStartDate;
	}


	public Calendar getNewEndDate() {
		return taskNewEndDate;
	}


	public Calendar getNewDeadline() {
		return taskNewDeadline;
	}
	

	public String getLabel() {
		return taskLabel;
	}
	

	public boolean hasUserSetTime() {
		return userSetTime;
	}
	

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
				|| getType()==CommandType.SEARCH_LABEL
				|| getType()==CommandType.TODAY
				|| getType()==CommandType.OVERDUE) {
			return GeneralCommandType.SEARCH;
		} else if(getType()==CommandType.LABEL_ID
				|| getType()==CommandType.LABEL_NAME
				|| getType()==CommandType.DELABEL_ID
				|| getType()==CommandType.DELABEL_NAME) {
			return GeneralCommandType.LABEL;
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

	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		// TODO Auto-generated method stub
		return null;
	}

}

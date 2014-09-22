package doornot.parser;

import java.util.Calendar;

import doornot.parser.IDonCommand.CommandType;

public class DonCommand implements IDonCommand{
	
	private CommandType commandType;
	private int taskID;
	private String taskName;
	private String taskNewName;
	private Calendar taskDeadline;
	private Calendar taskNewDeadline;
	private Calendar taskNewStartDate;
	private Calendar taskNewEndDate;
	
	
	
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

}

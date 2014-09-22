package doornot.parser;

import java.util.Calendar;

public class DonCommand implements IDonCommand{
	
	private CommandType commandType;
	private int taskID;
	private String taskName;
	private String taskNewName;
	private Calendar taskNewDeadline;
	private Calendar taskNewStartDate;
	private Calendar taskNewEndDate;
	
	
	
	public DonCommand() {
		// TODO Auto-generated constructor stub
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
		
		return taskNewDeadline;
	}

	@Override
	public String getNewName() {
		// TODO Auto-generated method stub
		return taskNewName;
	}

	@Override
	public Calendar getNewStartDate() {
		// TODO Auto-generated method stub
		return taskNewStartDate;
	}

	@Override
	public Calendar getNewEndDate() {
		// TODO Auto-generated method stub
		return taskNewEndDate;
	}

	@Override
	public Calendar getNewDeadline() {
		// TODO Auto-generated method stub
		return taskNewDeadline;
	}

}

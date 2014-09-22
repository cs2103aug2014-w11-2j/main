package doornot.parser;

import doornot.parser.IDonCommand.CommandType;

public class DonParser implements IDonParser{

	public DonParser() {
		// TODO Auto-generated constructor stub
	}
	private String userCommand;
	private CommandType dType;
	private DonCommand dCommand;
	
	@Override
	public DonCommand parseCommand(String command) {
		
		dCommand = new DonCommand();
		userCommand = command;
		dType = getCommandType();
		setDonCommand();
		return dCommand;
		
	}
	
	private CommandType getCommandType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void setDonCommand() {
		// TODO Auto-generated method stub
		
	}



}

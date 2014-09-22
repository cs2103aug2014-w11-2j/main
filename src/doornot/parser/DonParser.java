package doornot.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		setDonCommand();
		return dCommand;
		
	}
	
	private void setDonCommand() {
		String commandWord = getFirstWord(userCommand);
		
		if(commandWord.equalsIgnoreCase("a") || commandWord.equalsIgnoreCase("add")){
			setAddCommand();
		}else if(commandWord.equalsIgnoreCase("e") || commandWord.equalsIgnoreCase("ed") 
				|| commandWord.equalsIgnoreCase("edit")){
			
		}else if(commandWord.equalsIgnoreCase("s") || commandWord.equalsIgnoreCase("search")){

		}else if(commandWord.equalsIgnoreCase("d") || commandWord.equalsIgnoreCase("del")
				|| commandWord.equalsIgnoreCase("delete")){

		}else if(commandWord.equalsIgnoreCase("m") || commandWord.equalsIgnoreCase("mark")){

		}else if(commandWord.equalsIgnoreCase("undo")){
			dCommand.setType(CommandType.UNDO);
		}else if(commandWord.equalsIgnoreCase("exit")){
			dCommand.setType(CommandType.EXIT);
		}else{
			dCommand.setType(CommandType.INVALID);
		}
		
	}


	private void setAddCommand() {
		String parameters = removeFirstWord(userCommand);
		
		Pattern pattern = Pattern.compile("at\\s+[0-9]{8}\\s+$");
		Matcher matcher = pattern.matcher(parameters.toLowerCase());
		if(matcher.find()){
			dCommand.setType(CommandType.ADD_TASK);
		}
		
	}

	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}

	private static String getFirstWord(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}
}

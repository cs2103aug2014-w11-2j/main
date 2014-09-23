package doornot.parser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
	
	
	//List of all the allowed types 
	private String addTaskReg = "\\bat\\s[0-9]{8}$|@\\s[0-9]{8}$";

	
	
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
		
		
		if(isRightCommand(parameters, addTaskReg)){
			dCommand.setType(CommandType.ADD_TASK);
			dCommand.setName(getTaskName(parameters, addTaskReg));
			dCommand.setNewDeadline(getDate(parameters, addTaskReg));
		}
		
	}
	/**
	 * allow 'at DDMMYYYY' and '@ DDMMYYYY'
	 * @param param
	 * @return
	 */
	private boolean isRightCommand(String param, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(param.toLowerCase());
		return matcher.find();
	}
	
	private Calendar getDate(String param, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(param.toLowerCase());
		matcher.find();
		String[] split = matcher.group().split("\\D");
		String date = split[split.length-1];
		
		return createDate(date);
		
	}

	public Calendar createDate(String date) {
		int day = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(2,4));
		int year = Integer.parseInt(date.substring(4,8));
		
		return new GregorianCalendar(year, month, day);
	}
	
	private String getTaskName(String param, String regex){
		return param.split(regex)[0].trim();
	}

	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}

	private static String getFirstWord(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}
	
}

package doornot.parser;

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
	private DonCommand dCommand;
	
	
	//List of all the allowed types 
	
	//allow 'at DDMMYYYY' and '@ DDMMYYYY'
	private String addTaskReg = "\\bat\\s[0-9]{8}$|@\\s[0-9]{8}$";
	
	// allow 'from DDMMYYYY to DDMMYYYY'
	private String addEventReg = "\\bfrom\\s[0-9]{8}\\sto\\s[0-9]{8}$";
	
	private String markIDReg = "[0-9]";
	
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
			setMarkCommand();
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
			dCommand.setNewDeadline(getEndDate(parameters, addTaskReg));
			
		}else if(isRightCommand(parameters, addEventReg)){
			dCommand.setType(CommandType.ADD_EVENT);
			dCommand.setName(getTaskName(parameters, addEventReg));
			dCommand.setNewStartDate(getStartDate(parameters, addEventReg));
			dCommand.setNewEndDate(getEndDate(parameters, addEventReg));
		}else{
			dCommand.setType(CommandType.ADD_FLOAT);
			dCommand.setName(parameters.trim());
		}
		
	}
	
	private void setMarkCommand(){
		String parameters = removeFirstWord(userCommand);
		
		try{
			int ID = Integer.parseInt(parameters);
			dCommand.setType(CommandType.MARK_ID);
			dCommand.setID(ID);
			
		}catch(NumberFormatException e){
			dCommand.setType(CommandType.MARK);
			dCommand.setName(parameters);
		}
		
	}
	
	private boolean isRightCommand(String param, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(param.toLowerCase());
		return matcher.find();
	}
	
	private Calendar getStartDate(String param, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(param.toLowerCase());
		matcher.find();
		String[] split = matcher.group().split("\\D");
		String date = split[split.length-5];
		
		return createDate(date);
		
	}
	
	
	private Calendar getEndDate(String param, String regex) {
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

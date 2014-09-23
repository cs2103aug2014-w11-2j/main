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
	
	//name must be between " "
	private String taskNameReg = "^\".+\"$";
	
	//allow 'at DDMMYYYY' and '@ DDMMYYYY'
	private String addTaskReg = "\\bat\\s[0-9]{8}$|@\\s[0-9]{8}$";
	
	// allow 'from DDMMYYYY to DDMMYYYY'
	private String eventReg = "\\bfrom\\s[0-9]{8}\\sto\\s[0-9]{8}$";
	
	// allow 'to DDMMYYYY'
	private String editDateReg = "\\bto\\s[0-9]{8}$";
	
	
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
			setEditCommand();
		}else if(commandWord.equalsIgnoreCase("s") || commandWord.equalsIgnoreCase("search")){
			setSearchCommand();
		}else if(commandWord.equalsIgnoreCase("d") || commandWord.equalsIgnoreCase("del")
				|| commandWord.equalsIgnoreCase("delete")){
			setDeleteCommand();
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
			String taskName = getTaskName(parameters, addTaskReg);
			if(isTaskName(taskName)){
				dCommand.setType(CommandType.ADD_TASK);
				dCommand.setName(extractName(taskName));
				dCommand.setNewDeadline(getEndDate(parameters, addTaskReg));
			}else{
				dCommand.setType(CommandType.INVALID);
			}

		}else if(isRightCommand(parameters, eventReg)){
			String taskName = getTaskName(parameters, eventReg);
			if(isTaskName(taskName)){
				dCommand.setType(CommandType.ADD_EVENT);
				dCommand.setName(extractName(taskName));
				dCommand.setNewStartDate(getStartDate(parameters, eventReg));
				dCommand.setNewEndDate(getEndDate(parameters, eventReg));
			}else{
				dCommand.setType(CommandType.INVALID);
			}
		}else{
			if(isTaskName(parameters)){
				dCommand.setType(CommandType.ADD_FLOAT);
				dCommand.setName(extractName(parameters));
			}else{
				dCommand.setType(CommandType.INVALID);
			}
		}

	}


	private void setEditCommand(){
		String parameters = removeFirstWord(userCommand);

		if(parameters.startsWith("\"")){


		}else{
		
		}
		
		
	}
	
	
	private void setMarkCommand(){
		String parameters = removeFirstWord(userCommand);
		
		if(isTaskName(parameters)){
			dCommand.setType(CommandType.MARK);
			dCommand.setName(extractName(parameters));
		}else{
			try{
				int ID = Integer.parseInt(parameters);
				dCommand.setType(CommandType.MARK_ID);
				dCommand.setID(ID);
			
			}catch(Exception e){
				dCommand.setType(CommandType.INVALID);
			}
		}
		
	}
	

	private void setDeleteCommand(){
		String parameters = removeFirstWord(userCommand);
		
		if(isTaskName(parameters)){
			dCommand.setType(CommandType.DELETE);
			dCommand.setName(extractName(parameters));
		}else{
			try{
				int ID = Integer.parseInt(parameters);
				dCommand.setType(CommandType.DELETE_ID);
				dCommand.setID(ID);
			
			}catch(Exception e){
				dCommand.setType(CommandType.INVALID);
			}
		}
		
	}
	
	private void setSearchCommand(){
		String parameters = removeFirstWord(userCommand);
		
		
		if(isTaskName(parameters)){
			dCommand.setType(CommandType.SEARCH_NAME);
			dCommand.setName(extractName(parameters));
		}else{
			try{
				int num = Integer.parseInt(parameters);

				if(parameters.length()==8){

					dCommand.setType(CommandType.SEARCH_DATE);
					dCommand.setDeadline(createDate(parameters));
				}else{
					dCommand.setType(CommandType.SEARCH_ID);
					dCommand.setID(num);
				}

			}catch(Exception e){
				dCommand.setType(CommandType.INVALID);
			}
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
	
	private boolean isTaskName(String param) {
		Pattern pattern = Pattern.compile(taskNameReg);
		Matcher matcher = pattern.matcher(param);
		return matcher.find();
	}
	private String extractName(String param){
		return param.substring(1, param.length()-1);
	}

}

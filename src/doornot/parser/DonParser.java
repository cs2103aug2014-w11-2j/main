package doornot.parser;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import doornot.parser.IDonCommand.CommandType;
/**
 * DonParser parses the commands and creates a DonCommand
 * 
 * @author Haritha Ramesh
 * @author A0115503W
 */
public class DonParser implements IDonParser{

	public DonParser() {

	}
	private String userCommand;
	private DonCommand dCommand;
	
	
	//List of all the allowed types 
	
	//date in DDMMYYYY_hhmm format
	private String dateTimeReg = "[0-9]{8}_[0-9]{4}";
	
	//date in DDMMYYYY format
	private String dateReg = "[0-9]{8}";
	
	//name must be between " "
	private String taskNameReg = "^\".+\"$";
	
	//allow 'at DDMMYYYY' and '@ DDMMYYYY' and 'at DDMMYYYY_hhmm' and '@ DDMMYYYY_hhmm'
	private String addTaskReg = "\\bat\\s[0-9]{8}$|@\\s[0-9]{8}$|\\bat\\s[0-9]{8}_[0-9]{4}$|@\\s[0-9]{8}_[0-9]{4}$";
	
	// allow 'from DDMMYYYY to DDMMYYYY' and 'from DDMMYYYY_hhmm to DDMMYYYY_hhmm'
	private String addEventReg = "\\bfrom\\s[0-9]{8}\\sto\\s[0-9]{8}$|\\bfrom\\s[0-9]{8}_[0-9]{4}\\sto\\s[0-9]{8}_[0-9]{4}$";
	
	// allow 'to DDMMYYYY' and 'to DDMMYYYY_hhmm'
	private String editDateReg = "\\bto\\s[0-9]{8}$|\\bto\\s[0-9]{8}_[0-9]{4}$";
	
	// allow 'to " "'
	private String editNameReg = "\\bto\\s\".+\"$";
	
	// allow 'to from DDMMYYYY to DDMMYYYY' and 'to from DDMMYYYY_hhmm to DDMMYYYY_hhmm'
	private String editEventReg = "\\bto\\sfrom\\s[0-9]{8}\\sto\\s[0-9]{8}$|\\bto\\sfrom\\s[0-9]{8}_[0-9]{4}\\sto\\s[0-9]{8}_[0-9]{4}$";
	
	
	@Override
	public DonCommand parseCommand(String command) {
		
		dCommand = new DonCommand();
		userCommand = command;
		setDonCommand();
		return dCommand;
		
	}
	/**
	 * Creates the respective dCommand according to the user input
	 */
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
		}else if(commandWord.equalsIgnoreCase("help")){
			dCommand.setType(CommandType.HELP);
		}else if(commandWord.equalsIgnoreCase("exit")){
			dCommand.setType(CommandType.EXIT);
		}else{
			dCommand.setType(CommandType.INVALID_COMMAND);
		}
		
	}

	/**
	 * Creates the add CommandType 
	 */
	private void setAddCommand() {
		String parameters = removeFirstWord(userCommand);

		if(isRightCommand(parameters, addTaskReg)){
			String taskName = getTaskName(parameters, addTaskReg);
			if(isTaskName(taskName)){
				dCommand.setType(CommandType.ADD_TASK);
				dCommand.setNewName(extractName(taskName));
	
				setDeadlineForCommand(parameters);
				
			}else{
				dCommand.setType(CommandType.INVALID_FORMAT);
			}

		}else if(isRightCommand(parameters, addEventReg)){
			
			String taskName = getTaskName(parameters, addEventReg);
			if(isTaskName(taskName)){
				dCommand.setType(CommandType.ADD_EVENT);
				dCommand.setNewName(extractName(taskName));
				
				setStartAndEndForCommand(parameters);
				
			}else{
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}else{
			if(isTaskName(parameters)){
				dCommand.setType(CommandType.ADD_FLOAT);
				dCommand.setNewName(extractName(parameters));
			}else{
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}

	}

	/**
	 * Creates the edit CommandType 
	 */
	private void setEditCommand(){
		String parameters = removeFirstWord(userCommand);
		
		if(isRightCommand(parameters, editEventReg)){
			String taskName = getTaskName(parameters, editEventReg);
			if(isTaskName(taskName)){
				dCommand.setType(CommandType.EDIT_EVENT);
				dCommand.setName(extractName(taskName));
				
				setStartAndEndForCommand(parameters);
				
			}else{
				try{
					int ID = Integer.parseInt(taskName);
					dCommand.setType(CommandType.EDIT_ID_EVENT);
					dCommand.setID(ID);
					
					setStartAndEndForCommand(parameters);
				
				}catch(Exception e){
					dCommand.setType(CommandType.INVALID_FORMAT);
				}
			}
		}else if(isRightCommand(parameters, editDateReg)){
			String taskName = getTaskName(parameters, editDateReg);
			if(isTaskName(taskName)){
				dCommand.setType(CommandType.EDIT_DATE);
				dCommand.setName(extractName(taskName));
				setDeadlineForCommand(parameters);
				
			}else{
				try{
					int ID = Integer.parseInt(taskName);
					dCommand.setType(CommandType.EDIT_ID_DATE);
					dCommand.setID(ID);
					setDeadlineForCommand(parameters);
				
				}catch(Exception e){
					dCommand.setType(CommandType.INVALID_FORMAT);
				}
			}
			
		}else{
			String taskName = getTaskName(parameters, editNameReg);
			String newName = getNewName(parameters, editNameReg);
			if(isTaskName(taskName)&&isTaskName(newName)){
				dCommand.setType(CommandType.EDIT_NAME);
				dCommand.setName(extractName(taskName));
				dCommand.setNewName(extractName(newName));
				
			}else if(isTaskName(newName)){
				try{
					int ID = Integer.parseInt(taskName);
					dCommand.setType(CommandType.EDIT_ID_NAME);
					dCommand.setID(ID);
					dCommand.setNewName(extractName(newName));
				
				}catch(Exception e){
					dCommand.setType(CommandType.INVALID_FORMAT);
				}
			}else{
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}
		
	}
	
	/**
	 * Creates the mark CommandType 
	 */
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
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}
		
	}
	
	/**
	 * Creates the delete CommandType 
	 */
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
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}
		
	}
	
	/**
	 * Creates the search CommandType 
	 */
	private void setSearchCommand(){
		String parameters = removeFirstWord(userCommand);
		
		
		if(isTaskName(parameters)){
			dCommand.setType(CommandType.SEARCH_NAME);
			dCommand.setName(extractName(parameters));
		}else{
			//if date
			if(isRightCommand(parameters, dateReg)){
				dCommand.setType(CommandType.SEARCH_DATE);
				setDeadlineForCommand(parameters);
			}else{
				try{
					int num = Integer.parseInt(parameters);
					dCommand.setType(CommandType.SEARCH_ID);
					dCommand.setID(num);


				}catch(Exception e){
					dCommand.setType(CommandType.INVALID_DATE);
				}
			}	
		}
	}
	/**
	 * Uses regex and checks if parameter conatins regex
	 */
	private boolean isRightCommand(String param, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		return matcher.find();
	}
	
	/**
	 * Gets the start date from the parameter
	 */
	private Calendar getStartDate(String param) {
		Pattern pattern = Pattern.compile(dateTimeReg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		
		if(matcher.find()){ //if matches time
			String dateTime = matcher.group();
			return createDateTime(dateTime);
		}else{//match date
			pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(param);
			matcher.find();
			String date = matcher.group();
			return createDate(date);
		}
		
	}
	
	/**
	 * Gets the end date from the parameter
	 */
	private Calendar getEndDate(String param) {
		Pattern pattern = Pattern.compile(dateTimeReg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		if(matcher.find()){ //first match
			String dateTime = matcher.group();
			if(matcher.find()){ //has second match
				dateTime = matcher.group();
				return createDateTime(dateTime);
			}else{ //no second match
				return createDateTime(dateTime);
			}
		}else{
			pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(param);
			matcher.find();
			String date = matcher.group();
			if(matcher.find()){ //has second match
				date = matcher.group();
				return createDate(date);
			}else{ //no second match
				return createDate(date);
			}
		}
		
	}
	
	/**
	 * Creates date using the date string
	 */
	public Calendar createDate(String date) {
		Calendar calCheck =  new GregorianCalendar();
		
		int day = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(2,4))-1;
		int year = Integer.parseInt(date.substring(4,8));
		
		calCheck.set(Calendar.YEAR, year);
		calCheck.set(Calendar.MONTH, month);
		calCheck.set(Calendar.DAY_OF_MONTH, 1);
		
		if((day > calCheck.getActualMaximum(Calendar.DAY_OF_MONTH)) || (month>=12)){
			//create an error date ref
			return new GregorianCalendar(0,0,0);
		}else{
			return new GregorianCalendar(year,month,day);
		}
		
		
	}
	
	/**
	 * Creates date and time using the date string
	 */
	private Calendar createDateTime(String dateTime) {
		Calendar calCheck =  new GregorianCalendar();
		
		int day = Integer.parseInt(dateTime.substring(0,2));
		int month = Integer.parseInt(dateTime.substring(2,4))-1;
		int year = Integer.parseInt(dateTime.substring(4,8));
		int hour = Integer.parseInt(dateTime.substring(9,11));
		int min = Integer.parseInt(dateTime.substring(11,13));
		
		calCheck.set(Calendar.YEAR, year);
		calCheck.set(Calendar.MONTH, month);
		calCheck.set(Calendar.DAY_OF_MONTH, 1);
		
		if((day > calCheck.getActualMaximum(Calendar.DAY_OF_MONTH)) || (month>=12)
				||(hour>=24) ||(min>=60)){
			//create an error date ref
			return new GregorianCalendar(0,0,0);
		}else{
			return new GregorianCalendar(year,month,day, hour, min);
		}
		
	}
	
	public void setDeadlineForCommand(String parameters) {
		if(rightDate(getEndDate(parameters))){
			dCommand.setNewDeadline(getEndDate(parameters));
		}else{
			dCommand = new DonCommand();
			dCommand.setType(CommandType.INVALID_DATE);
		}
	}
	
	public void setStartAndEndForCommand(String parameters) {
		if(rightDate(getEndDate(parameters)) && rightDate(getStartDate(parameters))){
			dCommand.setNewStartDate(getStartDate(parameters));
			dCommand.setNewEndDate(getEndDate(parameters));
		}else{
			dCommand = new DonCommand();
			dCommand.setType(CommandType.INVALID_DATE);
		}
	}
	
	/**
	 * Checks whether it's an error date (0,0,0) is used to represent error dates
	 * @param Date
	 * @return
	 */
	private boolean rightDate(Calendar Date) {
		
		Calendar cal = new GregorianCalendar(0,0,0);
		return !(cal.equals(Date));
	}

	/**
	 * Gets the new name from the parameter
	 */
	private String getNewName(String param, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		matcher.find();
		String[] split = matcher.group().split("^to\\s");
		String newName = split[split.length-1];
		
		return newName;
		
	}
	
	/**
	 * Gets the name of task being referred to from the parameter
	 */
	private String getTaskName(String param, String regex){
		return param.split(regex)[0].trim();
	}

	private static String removeFirstWord(String userCommand) {
		return userCommand.replaceFirst(getFirstWord(userCommand), "").trim();
	}

	private static String getFirstWord(String userCommand) {
		return userCommand.trim().split("\\s+")[0];
	}
	
	/**
	 * Checks if the task name is within ""
	 */
	private boolean isTaskName(String param) {
		Pattern pattern = Pattern.compile(taskNameReg);
		Matcher matcher = pattern.matcher(param);
		return matcher.find();
	}
	/**
	 * Removes ""
	 */
	private String extractName(String param){
		return param.substring(1, param.length()-1);
	}

}

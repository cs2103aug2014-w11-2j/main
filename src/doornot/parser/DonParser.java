package doornot.parser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.*;

import doornot.parser.IDonCommand.CommandType;
/**
 * DonParser parses the commands and creates a DonCommand
 * 

 */

//@author A0115503W
 public class DonParser implements IDonParser{

	public DonParser() {

	}
	private String userCommand;
	private DonCommand dCommand;
	
	// for natty parser
	private Parser nattyParser = new Parser();
	private List<DateGroup> groups;
	private List<Date> dates = null;
	
	//List of all the allowed types 
	
	//date in DDMMYYYY_hhmm format
//	private String dateTimeReg = "[0-9]{8}_[0-9]{4}";
	
	//date in DD/MM/YYYY format
	private String dateReg = "\\b[0-9]{2}/[0-9]{2}/[0-9]{4}\\b";
	private String dateNoYearReg = "\\b[0-9]{2}/[0-9]{2}\\b";
	
	//name must be between " "
	private String taskNameReg = "\".+\"";
	
	//allow 'at DDMMYYYY' and '@ DDMMYYYY' and 'at DDMMYYYY_hhmm' and '@ DDMMYYYY_hhmm'
//	private String addTaskReg = "^at\\s[0-9]{8}$|^@\\s[0-9]{8}$|\\bat\\s[0-9]{8}_[0-9]{4}$|@\\s[0-9]{8}_[0-9]{4}$";
	
	// allow "blah" at or "blah" @
	private String addTaskReg = "^\".+\"\\sat\\b|^\".+\"\\s@";
	// check for DD/MM/YYYY or DD/MM
//	private String addTaskDateReg = "^at\\s[0-9]{2}/[0-9]{2}/[0-9]{4}|^@\\s[0-9]{2}/[0-9]{2}/[0-9]{4}|^at\\s[0-9]{2}/[0-9]{2}|^@\\s[0-9]{2}/[0-9]{2}";
	
	// allow 'from DDMMYYYY to DDMMYYYY' and 'from DDMMYYYY_hhmm to DDMMYYYY_hhmm'
//	private String addEventReg = "\\bfrom\\s[0-9]{8}\\sto\\s[0-9]{8}$|\\bfrom\\s[0-9]{8}_[0-9]{4}\\sto\\s[0-9]{8}_[0-9]{4}$";
	
	// allow from
	private String addEventReg = "^from\\b";
	
	// allow 'to DDMMYYYY' and 'to DDMMYYYY_hhmm'
//	private String editDateReg = "\\bto\\s[0-9]{8}$|\\bto\\s[0-9]{8}_[0-9]{4}$";
	
	// allow to
	private String editDateOrEventReg = "^to\\b";
	
	// allow 'to " "'
	private String editNameReg = "\\bto\\s\".+\"$";
	
	// allow 'to from DDMMYYYY to DDMMYYYY' and 'to from DDMMYYYY_hhmm to DDMMYYYY_hhmm'
//	private String editEventReg = "\\bto\\sfrom\\s[0-9]{8}\\sto\\s[0-9]{8}$|\\bto\\sfrom\\s[0-9]{8}_[0-9]{4}\\sto\\s[0-9]{8}_[0-9]{4}$";

	
	// Obtain a suitable logger.
//	 private static Logger logger = Logger. getLogger("Parser");
	 
	@Override
	public DonCommand parseCommand(String command) {
		
//		logger.log(Level.INFO, "going to start parsing" );
		
		dCommand = new DonCommand();
		userCommand = command;
		setDonCommand();
		return dCommand;
		
	}
	
	@Override
	public void setDonCommand() {
		String commandWord = getFirstWord(userCommand);
		
		if(commandWord.equalsIgnoreCase("a") || commandWord.equalsIgnoreCase("add")){
			setAddCommand();
		}else if(commandWord.equalsIgnoreCase("e") || commandWord.equalsIgnoreCase("ed") 
				|| commandWord.equalsIgnoreCase("edit")){
			setEditCommand();
		}else if(commandWord.equalsIgnoreCase("s") || commandWord.equalsIgnoreCase("search")){
			setSearchCommand();
		}else if(commandWord.equalsIgnoreCase("saf")){
			setSearchAfterCommand();
		}else if(commandWord.equalsIgnoreCase("d") || commandWord.equalsIgnoreCase("del")
				|| commandWord.equalsIgnoreCase("delete")){
			setDeleteCommand();
		}else if(commandWord.equalsIgnoreCase("m") || commandWord.equalsIgnoreCase("mark")){
			setMarkCommand();
		}else if(commandWord.equalsIgnoreCase("sud")){
			dCommand.setType(CommandType.SEARCH_UNDONE);
		}else if(commandWord.equalsIgnoreCase("today")){
			dCommand.setType(CommandType.TODAY);
		}else if(commandWord.equalsIgnoreCase("od") || commandWord.equalsIgnoreCase("overdue")){
			dCommand.setType(CommandType.OVERDUE);
		}else if(commandWord.equalsIgnoreCase("undo")){
			dCommand.setType(CommandType.UNDO);
		}else if(commandWord.equalsIgnoreCase("redo")){
			dCommand.setType(CommandType.REDO);
		}else if(commandWord.equalsIgnoreCase("help")){
			setHelpCommand();
		}else if(commandWord.equalsIgnoreCase("exit")){
			dCommand.setType(CommandType.EXIT);
		}else{
//			logger.log(Level.WARNING, "Command word invalid" );
			
			dCommand.setType(CommandType.INVALID_COMMAND);
		}
		
	}



	/**
	 * Creates the add CommandType 
	 */
	private void setAddCommand() {
		String parameters = removeFirstWord(userCommand);

		// is it "blah" at ...
		if(isRightCommand(parameters, addTaskReg)){
			
			// get blah
			String taskName = getTaskName(parameters);
			
			if(isGoodName(taskName)){
				dCommand.setType(CommandType.ADD_TASK);
				dCommand.setNewName(taskName);
				
				// get rid of "blah" at
				String date = parameters.replaceFirst(addTaskReg, "").trim();
				setNewDeadlineForCommand(date);
				
			}else{
//				logger.log(Level.WARNING, "Add task name invalid" );
				dCommand.setType(CommandType.INVALID_FORMAT);
			}

		}else if(isRightCommand(parameters, addEventReg)){
			
			String taskName = getTaskName(parameters);
			if(isTaskName(taskName)){
				dCommand.setType(CommandType.ADD_EVENT);
				dCommand.setNewName(extractName(taskName));
				
				setStartAndEndForCommand(parameters);
				
			}else{
//				logger.log(Level.WARNING, "Add event name invalid" );
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}else{
			if(isTaskName(parameters)){
				dCommand.setType(CommandType.ADD_FLOAT);
				dCommand.setNewName(extractName(parameters));
			}else{
//				logger.log(Level.WARNING, "Add floating task name invalid format" );
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}

	}

	/**
	 * Creates the edit CommandType 
	 */
	private void setEditCommand(){
		String parameters = removeFirstWord(userCommand);
		
		if(isRightCommand(parameters, editDateOrEventReg)){
			String taskName = getTaskName(parameters);
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
		}else if(isRightCommand(parameters, editDateOrEventReg)){
			String taskName = getTaskName(parameters);
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
			String taskName = getTaskName(parameters);
			String newName = getNewName(parameters, editDateOrEventReg);
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

		if(parameters.isEmpty()){
			dCommand.setType(CommandType.SEARCH_ALL);
		}else if(parameters.equalsIgnoreCase("free")){
			dCommand.setType(CommandType.SEARCH_FREE);
		}else if(parameters.equalsIgnoreCase("undone")){
			dCommand.setType(CommandType.SEARCH_UNDONE);
		}else{
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
						dCommand.setType(CommandType.INVALID_FORMAT);
					}
				}	
			}
		}
	}
	/**
	 * Creates the search after CommandType 
	 */
	private void setSearchAfterCommand(){
		String parameters = removeFirstWord(userCommand);

		//if date
		if(isRightCommand(parameters, dateReg)){
			dCommand.setType(CommandType.SEARCH_AFTDATE);
			setDeadlineForCommand(parameters);
		}else{

			dCommand.setType(CommandType.INVALID_FORMAT);

		}	

	}
	/**
	 * Set the help command types
	 */
	private void setHelpCommand() {
		
		String parameters = removeFirstWord(userCommand);
		
		if(parameters.isEmpty()){
			dCommand.setType(CommandType.HELP_GENERAL);
		}else{
			if(parameters.equalsIgnoreCase("add")){
				dCommand.setType(CommandType.HELP_ADD);
			}else if(parameters.equalsIgnoreCase("edit")){
				dCommand.setType(CommandType.HELP_EDIT);
			}else if(parameters.equalsIgnoreCase("search")){
				dCommand.setType(CommandType.HELP_SEARCH);
			}else if(parameters.equalsIgnoreCase("del")
					|| parameters.equalsIgnoreCase("delete")){
				dCommand.setType(CommandType.HELP_DELETE);
			}else if(parameters.equalsIgnoreCase("mark")){
				dCommand.setType(CommandType.HELP_MARK);
			}else if(parameters.equalsIgnoreCase("undo")){
				dCommand.setType(CommandType.HELP_UNDO);
			}else if(parameters.equalsIgnoreCase("redo")){
				dCommand.setType(CommandType.HELP_REDO);
			}else{
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}
		
	}
	/**
	 * Uses regex and checks if parameter contains regex
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
		Pattern pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
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
		Pattern pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
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
	 * @param String date
	 * @return Calendar date
	 */
	private Calendar createDate(String date) {
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
		int month = Integer.parseInt(dateTime.substring(3,5))-1;
		int year = Integer.parseInt(dateTime.substring(6,10));
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
	/**
	 * Checks if the date follows the dd/mm/yyyy format
	 * @param parameters
	 * @return
	 */
	private boolean isFormalDate(String param) {
		// if dd/mm/yyyy
		Pattern pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		if(matcher.find()){
			return true;
		}else{
			// if dd/mm
			Pattern pattern2 = Pattern.compile(dateNoYearReg, Pattern.CASE_INSENSITIVE);
			Matcher matcher2 = pattern2.matcher(param);
			if(matcher2.find()){
				return true;
			}else{
				return false;
			}
		}
	}
	/**
	 * Checks if the date follows the dd/mm/yyyy format
	 * @param parameters
	 * @return
	 * @throws WrongDateException 
	 */
	private Calendar getFormalDate(String param) throws WrongDateException {
		// if dd/mm/yyyy
		Pattern pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		Calendar cal;
		if(matcher.find()){
			cal = createFormalDate(matcher.group());
		}else{
			Pattern pattern2 = Pattern.compile(dateNoYearReg, Pattern.CASE_INSENSITIVE);
			Matcher matcher2 = pattern2.matcher(param);
			cal = createFormalNoYearDate(matcher2.group());
		}
		return cal;
	}
	

	private Calendar createFormalDate(String date) throws WrongDateException {
		Calendar calCheck =  new GregorianCalendar();
		
		int day = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(3,5))-1;
		int year = Integer.parseInt(date.substring(6,10));
		
		calCheck.set(Calendar.YEAR, year);
		calCheck.set(Calendar.MONTH, month);
		calCheck.set(Calendar.DAY_OF_MONTH, 1);
		
		if((day > calCheck.getActualMaximum(Calendar.DAY_OF_MONTH)) || (month>=12)){
			//create an error date ref
			throw new WrongDateException();
		}else{
			return new GregorianCalendar(year,month,day);
		}
	}
	
	private Calendar createFormalNoYearDate(String date) throws WrongDateException {
		Calendar calCheck =  new GregorianCalendar();
		
		int day = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(3,5))-1;
		
		calCheck.set(Calendar.MONTH, month);
		calCheck.set(Calendar.DAY_OF_MONTH, 1);
		
		if((day > calCheck.getActualMaximum(Calendar.DAY_OF_MONTH)) || (month>=12)){
			//create an error date ref
			throw new WrongDateException();
		}else{
			return new GregorianCalendar(calCheck.get(Calendar.YEAR), month,day);
		}
	}
	/**
	 * Sets new deadlines for dCommand
	 * @param parameters
	 */
	private void setNewDeadlineForCommand(String parameters) {

		if(isFormalDate(parameters)){
			
			
			try{
				Calendar date = getFormalDate(parameters);
				Date time = getTimeFromParser(parameters);
				if(isTimeMentioned()){
					dCommand.setNewDeadline(createDateTimeNatty(date, time));
				}else{
					dCommand.setNewDeadline(date);
				}
				
			}catch(Exception e){
				dCommand = new DonCommand();
				dCommand.setType(CommandType.INVALID_DATE);
			}
			
		}else{
			try{
				Date date = getTimeFromParser(parameters);
				Calendar cal = new GregorianCalendar();
				cal.setTime(date);
				
				if(isTimeMentioned()){	
					
					dCommand.setNewDeadline(cal);
				}else{
					
					dCommand.setNewDeadline(createDateNatty(cal));
				}
			}catch(Exception e){
				dCommand = new DonCommand();
				dCommand.setType(CommandType.INVALID_DATE);
			}


		}
	}
	
	private Calendar createDateNatty(Calendar dateCal) {
		
		Calendar newCal = new GregorianCalendar();
		newCal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
		newCal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
		newCal.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH));
		return newCal;
	}
	
	private Calendar createDateTimeNatty(Calendar date, Date time) {
		
		Calendar timeCal = new GregorianCalendar();
		timeCal.setTime(time);
		date.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		date.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		return date;
	}

	/**
	 * check if time is mentioned
	 * @return
	 */
	private boolean isTimeMentioned() {
		return !(groups.get(0).isTimeInferred());
	}
	
	/**
	 * Gets date from parser
	 * @param parameters
	 * @return
	 */
	private Date getTimeFromParser(String parameters) {
		groups = nattyParser.parse(parameters);
		return groups.get(0).getDates().get(0);
	}

	/**
	 * Sets deadlines for dCommand
	 * @param parameters
	 */
	private void setDeadlineForCommand(String parameters) {

		if(isFormalDate(parameters)){
			
			
			try{
				Calendar date = getFormalDate(parameters);
				Date time = getTimeFromParser(parameters);
				if(isTimeMentioned()){
					dCommand.setDeadline(createDateTimeNatty(date, time));
				}else{
					dCommand.setDeadline(date);
				}
				
			}catch(Exception e){
				dCommand = new DonCommand();
				dCommand.setType(CommandType.INVALID_DATE);
			}
			
		}else{
			try{
				Date date = getTimeFromParser(parameters);
				Calendar cal = new GregorianCalendar();
				cal.setTime(date);
				
				if(isTimeMentioned()){	
					
					dCommand.setDeadline(cal);
				}else{
					
					dCommand.setDeadline(createDateNatty(cal));
				}
			}catch(Exception e){
				dCommand = new DonCommand();
				dCommand.setType(CommandType.INVALID_DATE);
			}


		}
	}	
	
	private void setStartAndEndForCommand(String parameters) {
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
	private String getTaskName(String param){
		Pattern pattern = Pattern.compile(taskNameReg);
		Matcher matcher = pattern.matcher(param);
		matcher.find();
		return extractName(matcher.group());
	}
	/**
	 * Checks if the task name does not contain ;
	 */
	private boolean isGoodName(String name) {
		// ensures semi colon not in name
		if(!name.contains(";")){
			return true;
		}else{
			return false;
		}

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
		// ensures semi colon not in name
		if( matcher.find()){
			if(!extractName(param).contains(";")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	/**
	 * Removes ""
	 */
	private String extractName(String param){
		return param.substring(1, param.length()-1);
	}
	
	public class WrongDateException extends Exception{
		
		public WrongDateException() {
			super(); 
			}
	}
	
	public static void main(String[] args){
		DonParser p = new DonParser();
		DonCommand d = p.parseCommand("add \"hello d12\" @ 12/11/1994");
		System.out.println(d.getType());
		System.out.println(d.getNewName());
		System.out.println(d.getNewDeadline().getTime().toString());
	}
}

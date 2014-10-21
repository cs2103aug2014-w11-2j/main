package doornot.parser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
	
	//date in DD/MM/YYYY format
	private String dateReg = "\\b[0-9]{2}/[0-9]{2}/[0-9]{4}\\b";
	private String dateNoYearReg = "\\b[0-9]{2}/[0-9]{2}\\b";
	
	private String dateEventReg = "\\b[0-9]{2}/[0-9]{2}/[0-9]{4}\\s.*to\\s[0-9]{2}/[0-9]{2}/[0-9]{4}\\b";
	private String dateNoYearEventReg = "\\b[0-9]{2}/[0-9]{2}\\s.*to\\s[0-9]{2}/[0-9]{2}\\b";
	//name must be between " "
	private String taskNameReg = "\".+\"";
	
	// allow "blah" at or "blah" @
	private String addTaskReg = "^\".+\"\\sat\\b|^\".+\"\\s@";
		
	// allow from
	private String addEventReg = "^\".+\"\\sfrom\\s";
	
	// allow only name
	private String addFloatReg = "^\".+\"$";

	// allow "blah" to
	private String editNameToDateReg = "^\".+\"\\sto\\b";
	
	// allow id to
	private String editIDToDateReg = "^[0-9]+\\sto\\b";
	
	// number 
	private String getIDReg = "^[0-9]+\\s";
		
	// allow "blah" to from
	private String editNameToEventReg = "^\".+\"\\sto\\sfrom\\b";
	
	// for " to "
	private String editNameSpaceReg = "\"\\sto\\s\"";
	
	// allow id to from
	private String editIDToEventReg = "^[0-9]+\\sto\\sfrom\\b";
	
	// allow "blah" to "blah"
	private String editNameToNameReg = "^\".+\"\\sto\\s\".+\"$";
	
	// allow id to "blah"
	private String editIDToNameReg = "^[0-9]+\\sto\\s\".+\"$";

	// to "blah"
	private String editToNameReg = "\\bto\\s\".+\"$";
	
	// for id only
	private String searchIDReg = "^[0-9]+$";
	
	// allow xx "BLAH" 
	private String labelIDReg = "^[0-9]+\\s\".+\"$";
	
	// allow "blah" "BLAH" 
	private String labelNameReg = "^\".+\"\\s\".+\"$";
	
	// for that space between the names
	private String labelNameSpaceReg = "\"\\s\"";
	
	@Override
	public DonCommand parseCommand(String command) {
		
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
		}else if(commandWord.equalsIgnoreCase("label")){
			setLabelCommand();
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

				dCommand.setType(CommandType.INVALID_FORMAT);
			}

		// is it "blah" from
		}else if(isRightCommand(parameters, addEventReg)){
			
			// get blah
			String taskName = getTaskName(parameters);
			if(isGoodName(taskName)){
				dCommand.setType(CommandType.ADD_EVENT);
				dCommand.setNewName(taskName);
				
				// get rid of "blah" from
				String date = parameters.replaceFirst(addEventReg, "").trim();
				setStartAndEndForCommand(date);
				
			}else{

				dCommand.setType(CommandType.INVALID_FORMAT);
			}
			
		// is it "blah"
		}else if(isRightCommand(parameters, addFloatReg)){
			
			// get blah
			String taskName = getTaskName(parameters);
			
			if(isGoodName(taskName)){
				dCommand.setType(CommandType.ADD_FLOAT);
				dCommand.setNewName(taskName);
			}else{

				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}else{
			dCommand.setType(CommandType.INVALID_FORMAT);
		}

	}

	/**
	 * Creates the edit CommandType 
	 */
	private void setEditCommand(){
		String parameters = removeFirstWord(userCommand);
		
		if(isRightCommand(parameters, editNameToNameReg)){
			
			String[] taskNames = getTaskNameArr(parameters, editNameSpaceReg);

			String oldName = taskNames[0];
			String newName = taskNames[1];
			
			if(isGoodName(oldName)&&isGoodName(newName)){
				dCommand.setType(CommandType.EDIT_NAME);
				dCommand.setName(oldName);
				dCommand.setNewName(newName);
				
			}else{
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
			
		}else if(isRightCommand(parameters, editIDToNameReg)){
			String newName = getTaskName(parameters);
			
			if(isGoodName(newName)){
				// get rid of to "blah"
				String id = parameters.replaceFirst(editToNameReg, "").trim();
				int ID = Integer.parseInt(id);
				dCommand.setType(CommandType.EDIT_ID_NAME);
				dCommand.setID(ID);
				dCommand.setNewName(newName);
			}else{
				dCommand.setType(CommandType.INVALID_FORMAT);
			}	
			
		}else if(isRightCommand(parameters, editNameToEventReg)){
			
			String taskName = getTaskName(parameters);
			
			if(isGoodName(taskName)){
				dCommand.setType(CommandType.EDIT_EVENT);
				dCommand.setName(taskName);
				
				// get rid of "blah" to from
				String date = parameters.replaceFirst(editNameToEventReg, "").trim();
				setStartAndEndForCommand(date);
				
			}else{
				
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
			
		}else if(isRightCommand(parameters, editIDToEventReg)){
			
			String idStr = getID(parameters);
			int ID = Integer.parseInt(idStr);
			dCommand.setType(CommandType.EDIT_ID_EVENT);
			dCommand.setID(ID);
			// get rid of xxx to from
			String date = parameters.replaceFirst(editIDToEventReg, "").trim();	
			setStartAndEndForCommand(date);
			
			
		}else if(isRightCommand(parameters, editNameToDateReg)){
			
			String taskName = getTaskName(parameters);
			
			if(isGoodName(taskName)){
				dCommand.setType(CommandType.EDIT_DATE);
				dCommand.setName(taskName);
				
				// get rid of "blah" to
				String date = parameters.replaceFirst(editNameToDateReg, "").trim();
				setNewDeadlineForCommand(date);
				
			}else{
				
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
			
		}else if(isRightCommand(parameters, editIDToDateReg)){
			
			String idStr = getID(parameters);
			
			int ID = Integer.parseInt(idStr);
			dCommand.setType(CommandType.EDIT_ID_DATE);
			dCommand.setID(ID);
			
			// get rid of xxx to
			String date = parameters.replaceFirst(editIDToDateReg, "").trim();
			setNewDeadlineForCommand(date);
			
		}else{
			dCommand.setType(CommandType.INVALID_FORMAT);
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
				if(isRightCommand(parameters, searchIDReg)){
					int num = Integer.parseInt(parameters);
					dCommand.setType(CommandType.SEARCH_ID);
					dCommand.setID(num);
				}else{
					
					dCommand.setType(CommandType.SEARCH_DATE);
					setDeadlineForCommand(parameters);
				}
			}
		}
	}
	/**
	 * Creates the search after CommandType 
	 */
	private void setSearchAfterCommand(){
		String parameters = removeFirstWord(userCommand);

			dCommand.setType(CommandType.SEARCH_AFTDATE);
			setDeadlineForCommand(parameters);

	}
	/**
	 * Creates the label CommandType
	 */
	private void setLabelCommand() {
		String parameters = removeFirstWord(userCommand);
		
		if(isRightCommand(parameters, labelIDReg)){
			
			String idStr = getID(parameters);
			int ID = Integer.parseInt(idStr);
			
			// get rid of xxx
			String labelName = parameters.replaceFirst(getIDReg, "").trim();
			dCommand.setType(CommandType.LABEL_ID);
			dCommand.setID(ID);
			dCommand.setLabel(labelName);
			
		}else if(isRightCommand(parameters, labelNameReg)){
			String[] names = getTaskNameArr(parameters, labelNameSpaceReg);

			String taskName = names[0];
			String labelName = names[1];
			
			if(isGoodName(taskName)&&isGoodName(labelName)){
				dCommand.setType(CommandType.LABEL_NAME);
				dCommand.setName(taskName);
				dCommand.setLabel(labelName);
				
			}else{
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
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
	private String removeFormalDate(String param, String regex, String regex2) {
		// if dd/mm/yyyy
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		if(matcher.find()){
			return param.replaceAll(dateReg, " today ");
		}else{
			// if dd/mm
			Pattern pattern2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
			Matcher matcher2 = pattern2.matcher(param);
			if(matcher2.find()){
				return param.replaceAll(dateNoYearReg, " today ");
			}else{
				return "";
			}
		}
	}
	/**
	 * gets the date following the dd/mm/yyyy format
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
			matcher2.find();
			cal = createFormalNoYearDate(matcher2.group());
		}
		return cal;
	}
	
	/**
	 * gets the event dates following the dd/mm/yyyy format
	 * @param parameters
	 * @return
	 * @throws WrongDateException 
	 */
	private Calendar[] getFormalEventDates(String param) throws WrongDateException {
		// if dd/mm/yyyy
		Pattern pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		Calendar cal;
		Calendar[] calArr = new Calendar[2];
		if(matcher.find()){
			cal = createFormalDate(matcher.group());
			calArr[0] = cal;
			matcher.find();
			cal = createFormalDate(matcher.group());
			calArr[1] = cal;
		}else{// is dd/mm
			Pattern pattern2 = Pattern.compile(dateNoYearReg, Pattern.CASE_INSENSITIVE);
			Matcher matcher2 = pattern2.matcher(param);
			matcher2.find();
			cal = createFormalNoYearDate(matcher2.group());
			calArr[0] = cal;
			matcher2.find();
			cal = createFormalNoYearDate(matcher2.group());
			calArr[1] = cal;
		}
		return calArr;
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

		if(!removeFormalDate(parameters, dateReg, dateNoYearReg).equals("")){
			
			
			try{
				Calendar date = getFormalDate(parameters);
				
				String param = removeFormalDate(parameters, dateReg, dateNoYearReg);
				Date time = getTimeFromParser(param);
				
				if(isTimeMentioned()){
					dCommand.setHasUserSetTime(true);
					dCommand.setNewDeadline(createDateTimeNatty(date, time));
				}else{
					dCommand.setNewDeadline(createDateNatty(date));
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
					dCommand.setHasUserSetTime(true);
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
		
		int year = dateCal.get(Calendar.YEAR);
		int month = dateCal.get(Calendar.MONTH);
		int day = dateCal.get(Calendar.DAY_OF_MONTH);

		return new GregorianCalendar(year, month, day, 23, 59);
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
	 * Gets dates from parser
	 * @param parameters
	 * @return
	 */
	private Date[] getTimingsFromParser(String parameters) {
		groups = nattyParser.parse(parameters);
		Date[] dates = new Date[2];
		dates[0] = groups.get(0).getDates().get(0);
		dates[1] = groups.get(0).getDates().get(1);
		return dates;
	}
	/**
	 * Sets deadlines for dCommand
	 * @param parameters
	 */
	private void setDeadlineForCommand(String parameters) {

		if(!removeFormalDate(parameters, dateReg, dateNoYearReg).equals("")){
			
			try{
				Calendar date = getFormalDate(parameters);
				
				String param = removeFormalDate(parameters, dateReg, dateNoYearReg);
				
				Date time = getTimeFromParser(param );
				if(isTimeMentioned()){
					dCommand.setHasUserSetTime(true);
					dCommand.setDeadline(createDateTimeNatty(date, time));
				}else{
					dCommand.setDeadline(createDateNatty(date));
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
					dCommand.setHasUserSetTime(true);
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
		if(!removeFormalDate(parameters, dateEventReg, dateNoYearEventReg).equals("")){
			
			try{
				Calendar[] dates = getFormalEventDates(parameters);
				String param =  removeFormalDate(parameters, dateEventReg, dateNoYearEventReg);
				Date[] timings = getTimingsFromParser(param);
				
				if(isTimeMentioned()){
					dCommand.setHasUserSetTime(true);
					dCommand.setNewStartDate(createDateTimeNatty(dates[0], timings[0]));
					dCommand.setNewEndDate(createDateTimeNatty(dates[1], timings[1]));
				}else{
					dCommand.setNewStartDate(createDateNatty(dates[0]));
					dCommand.setNewEndDate(createDateNatty(dates[1]));
				}
				
			}catch(Exception e){
				dCommand = new DonCommand();
				dCommand.setType(CommandType.INVALID_DATE);
			}
		}else{
			try{
				
				Date[] dates = getTimingsFromParser(parameters);
				Calendar[] calArr = new Calendar[2];
				Calendar cal = new GregorianCalendar();
				cal.setTime(dates[0]);
				calArr[0] = cal;
				Calendar cal2 = new GregorianCalendar();
				cal2.setTime(dates[1]);
				calArr[1] = cal2;
				
				if(isTimeMentioned()){	
					dCommand.setHasUserSetTime(true);
					dCommand.setNewStartDate(calArr[0]);
					dCommand.setNewEndDate(calArr[1]);
				}else{
					
					dCommand.setNewStartDate(createDateNatty(calArr[0]));
					dCommand.setNewEndDate(createDateNatty(calArr[1]));
				}
			}catch(Exception e){
				dCommand = new DonCommand();
				dCommand.setType(CommandType.INVALID_DATE);
			}
		}
		
		
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
	 * Gets the id string
	 * @param param
	 * @return id string
	 */
	private String getID(String param){
		Pattern pattern = Pattern.compile(getIDReg);
		Matcher matcher = pattern.matcher(param);
		matcher.find();
		return matcher.group().trim();
	}
	/**
	 * Gets the array of task names being referred to from the parameter
	 */
	private String[] getTaskNameArr(String param, String regex) {
		String [] nameArr = new String[2];
		nameArr  = param.split(regex);
		nameArr[0] = extractName(nameArr[0].trim()+"\"");
		nameArr[1] = extractName("\""+nameArr[1].trim());
		return nameArr;
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

}

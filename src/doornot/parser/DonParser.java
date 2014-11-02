package doornot.parser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.*;

import doornot.util.AbstractDonCommand;
import doornot.util.CalHelper;
import doornot.util.DonAddLabelCommand;
import doornot.util.DonCreateCommand;
import doornot.util.DonDelabelCommand;
import doornot.util.DonDeleteCommand;
import doornot.util.DonEditCommand;
import doornot.util.DonFindCommand;
import doornot.util.DonGeneralCommand;
import doornot.util.DonHelpCommand;
import doornot.util.DonInvalidCommand;
import doornot.util.DonMarkCommand;
import doornot.util.AbstractDonCommand.GeneralCommandType;
import doornot.util.DonDeleteCommand.DeleteType;
import doornot.util.DonFindCommand.SearchType;
import doornot.util.DonHelpCommand.HelpType;
import doornot.util.DonInvalidCommand.InvalidType;
import doornot.util.DonMarkCommand.MarkType;

/**
 * DonParser parses the commands and creates a DonCommand
 * 
 */

// @author A0115503W
public class DonParser implements IDonParser {

	public DonParser() {

	}

	private String userCommand;
	private AbstractDonCommand dCommand;
	private String commandWord;
	
	// for natty parser
	private Parser nattyParser = new Parser();
	private List<DateGroup> groups;

	// List of all the allowed types

	// name must be between " "
	private String taskNameReg = "\".+\"";

	// allow "blah" by 
	private String deadlineTaskReg = ".+(by\\s.+){1}";

	// allow "blah" from
	private String eventTaskReg = ".+(from\\s.+){1}";
	
	// allow "blah"/ID to
	private String editToNameReg = ".+(to\\s\".+\"){1}";

	// for to "
	private String editNameSpaceReg = "to \"";
	// number
	private String getIDReg = "^[0-9]+$";
	
	// allow xx "BLAH"
	private String labelReg = "#.+$";

	// allow "blah" "BLAH"
	private String labelNameReg = "^\".+\"\\s\".+\"$";

	// for that space between the names
	private String labelNameSpaceReg = "\"\\s\"";

	// allow only name
	private String labelNameAloneReg = "^\".+\"$";

	@Override
	public AbstractDonCommand parseCommand(String command) {

		dCommand = null;
		userCommand = command;
		setDonCommand();
		return dCommand;

	}

	@Override
	public void setDonCommand() {
		commandWord = getFirstWord(userCommand);

		if (commandWord.equalsIgnoreCase("a")
				|| commandWord.equalsIgnoreCase("add")) {
			setAddCommand();
		} else if (commandWord.equalsIgnoreCase("addf")
				|| commandWord.equalsIgnoreCase("af")) {
			setAddFloatCommand();
		}else if (commandWord.equalsIgnoreCase("e")
				|| commandWord.equalsIgnoreCase("ed")
				|| commandWord.equalsIgnoreCase("edit")) {
			setEditCommand();
		} else if (commandWord.equalsIgnoreCase("s")
				|| commandWord.equalsIgnoreCase("search")) {
			setSearchCommand();
		} else if (commandWord.equalsIgnoreCase("saf")) {
			setSearchAfterCommand();
		} else if (commandWord.equalsIgnoreCase("d")
				|| commandWord.equalsIgnoreCase("del")
				|| commandWord.equalsIgnoreCase("delete")) {
			setDeleteCommand();
		} else if (commandWord.equalsIgnoreCase("m")
				|| commandWord.equalsIgnoreCase("mark")) {
			setMarkCommand();
		} else if (commandWord.equalsIgnoreCase("label")) {
			setLabelCommand();
		} else if (commandWord.equalsIgnoreCase("delabel")) {
			setDelabelCommand();
		} else if (commandWord.equalsIgnoreCase("slabel")
				|| commandWord.equalsIgnoreCase("sl")) {
			setSlabelCommand();
		} else if (commandWord.equalsIgnoreCase("sud")) {
			dCommand = new DonFindCommand(SearchType.SEARCH_UNDONE);
		} else if (commandWord.equalsIgnoreCase("today")) {
			dCommand = new DonFindCommand(SearchType.TODAY);
		} else if (commandWord.equalsIgnoreCase("od")
				|| commandWord.equalsIgnoreCase("overdue")) {
			dCommand = new DonFindCommand(SearchType.OVERDUE);
		}else if (commandWord.equalsIgnoreCase("week")) {
			dCommand = new DonFindCommand(SearchType.SEVEN_DAYS);
		}else if (commandWord.equalsIgnoreCase("future")) {
			dCommand = new DonFindCommand(SearchType.FUTURE);
		}else if (commandWord.equalsIgnoreCase("float")) {
			dCommand = new DonFindCommand(SearchType.FLOAT);
		} else if (commandWord.equalsIgnoreCase("undo")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.UNDO);
		} else if (commandWord.equalsIgnoreCase("redo")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.REDO);
		} else if (commandWord.equalsIgnoreCase("help")) {
			setHelpCommand();
		} else if (commandWord.equalsIgnoreCase("exit")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.EXIT);
		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_COMMAND, commandWord);
		}

	}

	/**
	 * Creates the add CommandType
	 */
	private void setAddCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, deadlineTaskReg) && isRightCommand(parameters, eventTaskReg)) {
			//check which is later.
			int byIndex = parameters.lastIndexOf(" by ");
			int fromIndex = parameters.lastIndexOf(" from ");
			
			if(byIndex > fromIndex){
				createAddDeadlineCommand(parameters);
			}else{
				createAddEventCommand(parameters);
			}
		}else if (isRightCommand(parameters, deadlineTaskReg)){
			createAddDeadlineCommand(parameters);
			
		}else if (isRightCommand(parameters, eventTaskReg)){
			createAddEventCommand(parameters);
			
		} else{
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
		}
	}
			
	private void createAddDeadlineCommand(String param) {
		int byIndex = param.lastIndexOf(" by ");
		String taskDate = param.substring(byIndex+1);
		String taskName = param.substring(0, byIndex+1).trim();
		
		if (isGoodName(taskName)) {
			
			Calendar deadline = Calendar.getInstance();
			boolean hasSetTime = setNewDeadlineForCommand(taskDate, deadline);
			//if dCommand is not null setNewDeadlineForCommand must have set INVALID_DATE
			if(dCommand==null) {
				dCommand = new DonCreateCommand(taskName, deadline, hasSetTime);					
			}

		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
		}

	}
	
	private void createAddEventCommand(String param) {
		int fromIndex = param.lastIndexOf(" from ");
		String taskDates = param.substring(fromIndex+1);
		String taskName = param.substring(0, fromIndex+1).trim();
		
		if (isGoodName(taskName)) {
			
			Calendar startDate = Calendar.getInstance(), endDate = Calendar
					.getInstance();
			boolean hasSetTime = setStartAndEndForCommand(taskDates, startDate,
					endDate);
			
			if(dCommand==null) {
				dCommand = new DonCreateCommand(taskName, startDate, endDate,
					hasSetTime);
			}
			
		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
		}
		
		
	}
	/**
	 * Creates the add floating command. 
	 * 
	 */
	private void setAddFloatCommand() {
		String parameters = removeFirstWord(userCommand);


		if (isGoodName(parameters)) {
			dCommand = new DonCreateCommand(parameters);
		} else{
			// TODO maybe here invalid task name?
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
		}
	}

	/**
	 * Creates the edit CommandType
	 */
	private void setEditCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, editToNameReg)) {
			
			String[] taskNames = getTaskNameArr(parameters, editNameSpaceReg);

			String oldName = taskNames[0];
			String newName = taskNames[1];

			if (isGoodName(oldName) && isGoodName(newName)) {
				dCommand = new DonEditCommand(oldName, newName);
				
			}else if (isGoodName(newName)){
				try {
					
					int ID = Integer.parseInt(oldName);
					dCommand = new DonEditCommand(ID, newName);
					
				} catch (Exception e){
					dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
				}

			} else {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
			}

		} else if (isRightCommand(parameters, deadlineTaskReg) && isRightCommand(parameters, eventTaskReg)) {
			//check which is later.
			int byIndex = parameters.lastIndexOf(" by ");
			int fromIndex = parameters.lastIndexOf(" from ");
			
			if(byIndex > fromIndex){
				createEditDeadlineCommand(parameters);
			}else{
				createEditEventCommand(parameters);
			}
			
		}else if (isRightCommand(parameters, deadlineTaskReg)){
			createEditDeadlineCommand(parameters);
			
		}else if (isRightCommand(parameters, eventTaskReg)){
			createEditEventCommand(parameters);
			
		} else{
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
		}
			
	}

	private void createEditDeadlineCommand(String param) {
		int byIndex = param.lastIndexOf(" by ");
		String taskDate = param.substring(byIndex+1);
		String taskName = param.substring(0, byIndex+1).trim();
		
		if (isGoodName(taskName)) {
			
			Calendar deadline = Calendar.getInstance();
			boolean hasSetTime = setNewDeadlineForCommand(taskDate, deadline);
			
			if(dCommand==null) {
				dCommand = new DonEditCommand(taskName, deadline, hasSetTime);					
			}

		} else {
			try {

				int ID = Integer.parseInt(taskName);
				Calendar deadline = Calendar.getInstance();
				boolean hasSetTime = setNewDeadlineForCommand(taskDate, deadline);
				
				if(dCommand==null) {
					dCommand = new DonEditCommand(ID, deadline, hasSetTime);					
				}
			} catch (Exception e){
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
			}
		}

	}

	private void createEditEventCommand(String param) {
		int fromIndex = param.lastIndexOf(" from ");
		String taskDates = param.substring(fromIndex+1);
		String taskName = param.substring(0, fromIndex+1).trim();
		
		if (isGoodName(taskName)) {
			
			Calendar startDate = Calendar.getInstance(), endDate = Calendar
					.getInstance();
			boolean hasSetTime = setStartAndEndForCommand(taskDates, startDate,
					endDate);
			
			if(dCommand==null) {
				dCommand = new DonEditCommand(taskName, startDate, endDate,
					hasSetTime);
			}
			
		} else {
			try {
				int ID = Integer.parseInt(taskName);
				Calendar startDate = Calendar.getInstance(), endDate = Calendar
						.getInstance();
				boolean hasSetTime = setStartAndEndForCommand(taskDates, startDate,
						endDate);
				
				if(dCommand==null) {
					dCommand = new DonEditCommand(ID, startDate, endDate, hasSetTime);					
				}
			} catch (Exception e){
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
			}
		}
	}

	/**
	 * Creates the mark CommandType
	 */
	private void setMarkCommand() {
		String parameters = removeFirstWord(userCommand);
		
		if(parameters.equalsIgnoreCase("overdue")
				|| parameters.equalsIgnoreCase("od")){
			dCommand = new DonMarkCommand(MarkType.MARK_OVERDUE);
			
		} else if(parameters.equalsIgnoreCase("float")
				|| parameters.equalsIgnoreCase("fl")
				|| parameters.equalsIgnoreCase("floating")){
			dCommand = new DonMarkCommand(MarkType.MARK_FLOAT);
			
		} else if (isGoodName(parameters)) {
			dCommand = new DonMarkCommand(parameters);
			
		} else {
			
			try {
				int ID = Integer.parseInt(parameters);
				dCommand = new DonMarkCommand(ID);
				

			} catch (Exception e) {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
			}
		}

	}

	/**
	 * Creates the delete CommandType
	 */
	private void setDeleteCommand() {
		String parameters = removeFirstWord(userCommand);
		
		if(parameters.equalsIgnoreCase("overdue")
				|| parameters.equalsIgnoreCase("od")){
			dCommand = new DonDeleteCommand(DeleteType.DELETE_OVERDUE);
			
		} else if(parameters.equalsIgnoreCase("float")
				|| parameters.equalsIgnoreCase("fl")
				|| parameters.equalsIgnoreCase("floating")){
			dCommand = new DonDeleteCommand(DeleteType.DELETE_FLOAT);
			
		} else if (isRightCommand(parameters, labelReg)) {
			dCommand = new DonDeleteCommand(extractLabelName(parameters), DeleteType.DELETE_LABEL);
			
		}else if (isGoodName(parameters)) {
			dCommand = new DonDeleteCommand(parameters, DeleteType.DELETE_TITLE);
			
		} else {
			try {
				int ID = Integer.parseInt(parameters);
				dCommand = new DonDeleteCommand(ID);
				

			} catch (Exception e) {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
			}
		}

	}

	/**
	 * Creates the search CommandType
	 */
	private void setSearchCommand() {
		String parameters = removeFirstWord(userCommand);

		if (parameters.isEmpty()) {
			dCommand = new DonFindCommand(SearchType.SEARCH_ALL);

		} else if (parameters.equalsIgnoreCase("free")) {
			dCommand = new DonFindCommand(SearchType.SEARCH_FREE);

		} else if (parameters.equalsIgnoreCase("undone")) {
			dCommand = new DonFindCommand(SearchType.SEARCH_UNDONE);

		} else {
			if (isTaskName(parameters)) {
				dCommand = new DonFindCommand(extractName(parameters),
						SearchType.SEARCH_NAME);

			} else {

				// if date
				if (isRightCommand(parameters, labelReg)) {
					int num = Integer.parseInt(parameters);
					dCommand = new DonFindCommand(num);
					
				} else {

					Calendar searchDate = Calendar.getInstance();
					boolean hasSetTime = setNewDeadlineForCommand(parameters,
							searchDate);
					if(dCommand==null) {
						dCommand = new DonFindCommand(searchDate, hasSetTime,
							SearchType.SEARCH_DATE);
					}
					
				}
			}
		}
	}

	/**
	 * Creates the search after CommandType
	 */
	private void setSearchAfterCommand() {
		String parameters = removeFirstWord(userCommand);

		
		Calendar searchDate = Calendar.getInstance();
		boolean hasSetTime = setNewDeadlineForCommand(parameters, searchDate);
		if(dCommand==null) {
			dCommand = new DonFindCommand(searchDate, hasSetTime,
				SearchType.SEARCH_AFTDATE);
		}

	}
	/**
	 * Creates the search label CommandType
	 */
	private void setSlabelCommand() {
		String parameters = removeFirstWord(userCommand);
		if (isRightCommand(parameters, labelNameAloneReg)) {
			dCommand = new DonFindCommand(extractName(parameters),
					SearchType.SEARCH_LABEL);
			
		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
		}

	}

	/**
	 * Creates the remove label CommandType
	 */
	private void setDelabelCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, labelReg)) {

			String idStr = getID(parameters);
			int ID = Integer.parseInt(idStr);

			// get rid of xxx
			String labelName = parameters.replaceFirst(getIDReg, "").trim();
			dCommand = new DonDelabelCommand(ID, extractName(labelName));
			

		} else if (isRightCommand(parameters, labelNameReg)) {
			String[] names = getTaskNameArr(parameters, labelNameSpaceReg);

			String taskName = names[0];
			String labelName = names[1];

			if (isGoodName(taskName) && isGoodName(labelName)) {
				dCommand = new DonDelabelCommand(taskName, labelName);
				

			} else {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
			}
		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
		}

	}

	/**
	 * Creates the label CommandType
	 */
	private void setLabelCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, labelReg)) {

			String idStr = getID(parameters);
			int ID = Integer.parseInt(idStr);

			// get rid of xxx
			String labelName = parameters.replaceFirst(getIDReg, "").trim();
			dCommand = new DonAddLabelCommand(ID, extractName(labelName));
			

		} else if (isRightCommand(parameters, labelNameReg)) {
			String[] names = getTaskNameArr(parameters, labelNameSpaceReg);

			String taskName = names[0];
			String labelName = names[1];

			if (isGoodName(taskName) && isGoodName(labelName)) {
				dCommand = new DonAddLabelCommand(taskName, labelName);
				

			} else {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
			}
		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
		}

	}

	/**
	 * Set the help command types
	 */
	private void setHelpCommand() {

		String parameters = removeFirstWord(userCommand);

		if (parameters.isEmpty()) {
			dCommand = new DonHelpCommand(HelpType.HELP_GENERAL);
		} else {
			if (parameters.equalsIgnoreCase("add")) {
				dCommand = new DonHelpCommand(HelpType.HELP_ADD);
			} else if (parameters.equalsIgnoreCase("edit")) {
				dCommand = new DonHelpCommand(HelpType.HELP_EDIT);
			} else if (parameters.equalsIgnoreCase("search")) {
				dCommand = new DonHelpCommand(HelpType.HELP_SEARCH);
			} else if (parameters.equalsIgnoreCase("del")
					|| parameters.equalsIgnoreCase("delete")) {
				dCommand = new DonHelpCommand(HelpType.HELP_DELETE);
			} else if (parameters.equalsIgnoreCase("label")) {
				dCommand = new DonHelpCommand(HelpType.HELP_LABEL);
			} else if (parameters.equalsIgnoreCase("mark")) {
				dCommand = new DonHelpCommand(HelpType.HELP_MARK);
			} else if (parameters.equalsIgnoreCase("undo")) {
				dCommand = new DonHelpCommand(HelpType.HELP_UNDO);
			} else if (parameters.equalsIgnoreCase("redo")) {
				dCommand = new DonHelpCommand(HelpType.HELP_REDO);
			} else {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT, commandWord);
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
	 * Sets new deadlines for dCommand
	 * 
	 * @param parameters
	 * @param deadlineOut
	 *            Deadline to modify
	 * @return true if user has set time
	 */
	private boolean setNewDeadlineForCommand(String parameters,
			Calendar deadlineOut) {
		boolean hasSetTime = false;


		try {
			Date date = getTimeFromParser(parameters);
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);

			if (isTimeMentioned()) {
				hasSetTime = true;
				CalHelper.copyCalendar(cal, deadlineOut);
			} else {
				CalHelper.copyCalendar(createDateNatty(cal), deadlineOut);
			}
		} catch (Exception e) {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_DATE);
		}
		
		return hasSetTime;
	}

	private Calendar createDateNatty(Calendar dateCal) {

		int year = dateCal.get(Calendar.YEAR);
		int month = dateCal.get(Calendar.MONTH);
		int day = dateCal.get(Calendar.DAY_OF_MONTH);

		return new GregorianCalendar(year, month, day, 23, 59);
	}

	/**
	 * check if time is mentioned
	 * 
	 * @return
	 */
	private boolean isTimeMentioned() {
		return !(groups.get(0).isTimeInferred());
	}

	/**
	 * Gets date from parser
	 * 
	 * @param parameters
	 * @return
	 * @throws WrongDateException 
	 */
	private Date getTimeFromParser(String parameters) throws WrongDateException {
		groups = nattyParser.parse(parameters);
		
		Date date = groups.get(0).getDates().get(0);
		
		if (groups.get(0).getDates().size() != 1
				|| groups.get(0).isRecurring()){
			throw new WrongDateException();
			
		} else {
			return date;
		}

	}

	/**
	 * Gets dates from parser
	 * 
	 * @param parameters
	 * @return
	 * @throws WrongDateException 
	 */
	private Date[] getTimingsFromParser(String parameters) throws WrongDateException {
		groups = nattyParser.parse(parameters);
		
		if (groups.get(0).getDates().size() != 2
				|| groups.get(0).isRecurring()){
			
			throw new WrongDateException();
			
		} else {
			Date[] dates = new Date[2];
			dates[0] = groups.get(0).getDates().get(0);
			dates[1] = groups.get(0).getDates().get(1);
			return dates;
		}
	}

	private boolean setStartAndEndForCommand(String parameters,
			Calendar startDate, Calendar endDate) {
		boolean hasSetTime = false;

		try {
			Date[] dates = getTimingsFromParser(parameters);
			Calendar[] calArr = new Calendar[2];
			Calendar cal = new GregorianCalendar();
			
			cal.setTime(dates[0]);
			calArr[0] = cal;
			Calendar cal2 = new GregorianCalendar();
			cal2.setTime(dates[1]);
			calArr[1] = cal2;

			if (isTimeMentioned()) {
				hasSetTime = true;
				CalHelper.copyCalendar(calArr[0], startDate);
				CalHelper.copyCalendar(calArr[1], endDate);
			} else {
				CalHelper.copyCalendar(createDateNatty(calArr[0]),
						startDate);
				CalHelper.copyCalendar(createDateNatty(calArr[1]), endDate);
			}
		} catch (Exception e) {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_DATE);
		}
		
		return hasSetTime;
	}

	/**
	 * Gets the id string
	 * 
	 * @param param
	 * @return id string
	 */
	private String getID(String param) {
		Pattern pattern = Pattern.compile(getIDReg);
		Matcher matcher = pattern.matcher(param);
		matcher.find();
		return matcher.group().trim();
	}

	/**
	 * Gets the array of task names being referred to from the parameter
	 */
	private String[] getTaskNameArr(String param, String regex) {
		String[] nameArr = new String[2];
		nameArr = param.split(regex);
		nameArr[0] = nameArr[0].trim();
		nameArr[1] = extractName("\"" + nameArr[1].trim());
		return nameArr;
	}

	/**
	 * Gets the name of task being referred to from the parameter
	 */
	private String getTaskName(String param) {
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
		if (!name.contains(";") 
				&& !name.contains("#") 
				&& !name.matches("^overdue$")
				&& !name.matches("^od$")
				&& !name.matches("^fl$")
				&& !name.matches("^floating$")
				&& !name.matches("^float$")
				&& !name.matches("^done$")
				&& !name.matches("^undone$")
				&& !name.matches("^[0-9]+$")) {
			
			return true;
		} else {
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
		if (matcher.find()) {
			if (!extractName(param).contains(";")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Removes ""
	 */
	private String extractName(String param) {
		return param.substring(1, param.length() - 1).trim();
	}
	/**
	 * Removes #
	 */
	private String extractLabelName(String param) {
		return param.substring(1, param.length()).trim();
	}
	public class WrongDateException extends Exception {

		public WrongDateException() {
			super();
		}
	}

}

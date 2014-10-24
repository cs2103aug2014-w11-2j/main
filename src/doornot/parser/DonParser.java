package doornot.parser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.*;

import doornot.CalHelper;
import doornot.logic.AbstractDonCommand;
import doornot.logic.AbstractDonCommand.CommandType;
import doornot.logic.AbstractDonCommand.GeneralCommandType;
import doornot.logic.DonDelabelCommand;
import doornot.logic.DonDeleteCommand;
import doornot.logic.DonFindCommand;
import doornot.logic.DonFindCommand.SearchType;
import doornot.logic.DonAddLabelCommand;
import doornot.logic.DonCreateCommand;
import doornot.logic.DonEditCommand;
import doornot.logic.DonGeneralCommand;
import doornot.logic.DonHelpCommand;
import doornot.logic.DonHelpCommand.HelpType;
import doornot.logic.DonMarkCommand;

/**
 * DonParser parses the commands and creates a DonCommand
 * 
 */
// TODO remove all unnecessary setType

// @author A0115503W
public class DonParser implements IDonParser {

	public DonParser() {

	}

	private String userCommand;
	private AbstractDonCommand dCommand;

	// for natty parser
	private Parser nattyParser = new Parser();
	private List<DateGroup> groups;

	// List of all the allowed types

	// date in DD/MM/YYYY format
	private String dateReg = "\\b[0-9]{2}/[0-9]{2}/[0-9]{4}\\b";
	private String dateNoYearReg = "\\b[0-9]{2}/[0-9]{2}\\b";

	private String dateEventReg = "\\b[0-9]{2}/[0-9]{2}/[0-9]{4}\\s.*to\\s[0-9]{2}/[0-9]{2}/[0-9]{4}\\b";
	private String dateNoYearEventReg = "\\b[0-9]{2}/[0-9]{2}\\s.*to\\s[0-9]{2}/[0-9]{2}\\b";
	// name must be between " "
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
		String commandWord = getFirstWord(userCommand);

		if (commandWord.equalsIgnoreCase("a")
				|| commandWord.equalsIgnoreCase("add")) {
			setAddCommand();
		} else if (commandWord.equalsIgnoreCase("e")
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
			// dCommand.setType(CommandType.SEARCH_UNDONE);
		} else if (commandWord.equalsIgnoreCase("today")) {
			dCommand = new DonFindCommand(SearchType.TODAY);
			// dCommand.setType(CommandType.TODAY);
		} else if (commandWord.equalsIgnoreCase("od")
				|| commandWord.equalsIgnoreCase("overdue")) {
			dCommand = new DonFindCommand(SearchType.OVERDUE);
			//dCommand.setType(CommandType.OVERDUE);
		} else if (commandWord.equalsIgnoreCase("undo")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.UNDO);
		} else if (commandWord.equalsIgnoreCase("redo")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.REDO);
		} else if (commandWord.equalsIgnoreCase("help")) {
			setHelpCommand();
		} else if (commandWord.equalsIgnoreCase("exit")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.EXIT);
		} else {
			dCommand = new DonGeneralCommand(GeneralCommandType.INVALID);
		}

	}

	/**
	 * Creates the add CommandType
	 */
	private void setAddCommand() {
		String parameters = removeFirstWord(userCommand);

		// is it "blah" at ...
		if (isRightCommand(parameters, addTaskReg)) {

			// get blah
			String taskName = getTaskName(parameters);

			if (isGoodName(taskName)) {
				// Floating task
				// get rid of "blah" at
				String date = parameters.replaceFirst(addTaskReg, "").trim();
				Calendar deadline = Calendar.getInstance();
				boolean hasSetTime = setNewDeadlineForCommand(date, deadline);
				dCommand = new DonCreateCommand(taskName, deadline, hasSetTime);
				dCommand.setType(CommandType.ADD_TASK);
			} else {

				dCommand.setType(CommandType.INVALID_FORMAT);
			}

			// is it "blah" from
		} else if (isRightCommand(parameters, addEventReg)) {

			// get blah
			String taskName = getTaskName(parameters);
			if (isGoodName(taskName)) {
				// get rid of "blah" from
				String date = parameters.replaceFirst(addEventReg, "").trim();
				Calendar startDate = Calendar.getInstance(), endDate = Calendar
						.getInstance();
				boolean hasSetTime = setStartAndEndForCommand(date, startDate,
						endDate);

				dCommand = new DonCreateCommand(taskName, startDate, endDate,
						hasSetTime);
				dCommand.setType(CommandType.ADD_EVENT);
			} else {

				dCommand.setType(CommandType.INVALID_FORMAT);
			}

			// is it "blah"
		} else if (isRightCommand(parameters, addFloatReg)) {

			// get blah
			String taskName = getTaskName(parameters);

			if (isGoodName(taskName)) {
				dCommand = new DonCreateCommand(taskName);
				dCommand.setType(CommandType.ADD_FLOAT);
			} else {
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		} else {
			dCommand.setType(CommandType.INVALID_FORMAT);
		}

	}

	/**
	 * Creates the edit CommandType
	 */
	private void setEditCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, editNameToNameReg)) {

			String[] taskNames = getTaskNameArr(parameters, editNameSpaceReg);

			String oldName = taskNames[0];
			String newName = taskNames[1];

			if (isGoodName(oldName) && isGoodName(newName)) {
				dCommand = new DonEditCommand(oldName, newName);
				dCommand.setType(CommandType.EDIT_NAME);

			} else {
				dCommand.setType(CommandType.INVALID_FORMAT);
			}

		} else if (isRightCommand(parameters, editIDToNameReg)) {
			String newName = getTaskName(parameters);

			if (isGoodName(newName)) {
				// get rid of to "blah"
				String id = parameters.replaceFirst(editToNameReg, "").trim();
				int ID = Integer.parseInt(id);
				dCommand = new DonEditCommand(ID, newName);
				dCommand.setType(CommandType.EDIT_ID_NAME);

			} else {
				dCommand.setType(CommandType.INVALID_FORMAT);
			}

		} else if (isRightCommand(parameters, editNameToEventReg)) {

			String taskName = getTaskName(parameters);

			if (isGoodName(taskName)) {

				// get rid of "blah" to from
				String date = parameters.replaceFirst(editNameToEventReg, "")
						.trim();
				Calendar startDate = Calendar.getInstance(), endDate = Calendar
						.getInstance();
				boolean hasSetTime = setStartAndEndForCommand(date, startDate,
						endDate);

				dCommand = new DonEditCommand(taskName, startDate, endDate,
						hasSetTime);
				dCommand.setType(CommandType.EDIT_EVENT);

			} else {

				dCommand.setType(CommandType.INVALID_FORMAT);
			}

		} else if (isRightCommand(parameters, editIDToEventReg)) {

			String idStr = getID(parameters);
			int ID = Integer.parseInt(idStr);
			// get rid of xxx to from
			String date = parameters.replaceFirst(editIDToEventReg, "").trim();
			Calendar startDate = Calendar.getInstance(), endDate = Calendar
					.getInstance();
			boolean hasSetTime = setStartAndEndForCommand(date, startDate,
					endDate);

			dCommand = new DonEditCommand(ID, startDate, endDate, hasSetTime);
			dCommand.setType(CommandType.EDIT_ID_EVENT);

		} else if (isRightCommand(parameters, editNameToDateReg)) {

			String taskName = getTaskName(parameters);

			if (isGoodName(taskName)) {
				// get rid of "blah" to
				String date = parameters.replaceFirst(editNameToDateReg, "")
						.trim();
				Calendar newDeadline = Calendar.getInstance();
				boolean hasSetTime = setNewDeadlineForCommand(date, newDeadline);

				dCommand = new DonEditCommand(taskName, newDeadline, hasSetTime);
				dCommand.setType(CommandType.EDIT_DATE);

			} else {

				dCommand.setType(CommandType.INVALID_FORMAT);
			}

		} else if (isRightCommand(parameters, editIDToDateReg)) {

			String idStr = getID(parameters);

			int ID = Integer.parseInt(idStr);

			// get rid of xxx to
			String date = parameters.replaceFirst(editIDToDateReg, "").trim();
			Calendar newDeadline = Calendar.getInstance();
			boolean hasSetTime = setNewDeadlineForCommand(date, newDeadline);

			dCommand = new DonEditCommand(ID, newDeadline, hasSetTime);
			dCommand.setType(CommandType.EDIT_ID_DATE);

		} else {
			dCommand.setType(CommandType.INVALID_FORMAT);
		}
	}

	/**
	 * Creates the mark CommandType
	 */
	private void setMarkCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isTaskName(parameters)) {
			dCommand = new DonMarkCommand(extractName(parameters));
			dCommand.setType(CommandType.MARK);
		} else {
			try {
				int ID = Integer.parseInt(parameters);
				dCommand = new DonMarkCommand(ID);
				dCommand.setType(CommandType.MARK_ID);

			} catch (Exception e) {
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		}

	}

	/**
	 * Creates the delete CommandType
	 */
	private void setDeleteCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isTaskName(parameters)) {
			dCommand = new DonDeleteCommand(extractName(parameters));
			dCommand.setType(CommandType.DELETE);
		} else {
			try {
				int ID = Integer.parseInt(parameters);
				dCommand = new DonDeleteCommand(ID);
				dCommand.setType(CommandType.DELETE_ID);

			} catch (Exception e) {
				dCommand.setType(CommandType.INVALID_FORMAT);
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
				if (isRightCommand(parameters, searchIDReg)) {
					int num = Integer.parseInt(parameters);
					dCommand = new DonFindCommand(num);
					dCommand.setType(CommandType.SEARCH_ID);
				} else {

					Calendar searchDate = Calendar.getInstance();
					boolean hasSetTime = setNewDeadlineForCommand(parameters,
							searchDate);
					dCommand = new DonFindCommand(searchDate, hasSetTime,
							SearchType.SEARCH_DATE);
					dCommand.setType(CommandType.SEARCH_DATE);
				}
			}
		}
	}

	/**
	 * Creates the search after CommandType
	 */
	private void setSearchAfterCommand() {
		String parameters = removeFirstWord(userCommand);

		dCommand.setType(CommandType.SEARCH_AFTDATE);
		Calendar searchDate = Calendar.getInstance();
		boolean hasSetTime = setNewDeadlineForCommand(parameters, searchDate);
		dCommand = new DonFindCommand(searchDate, hasSetTime,
				SearchType.SEARCH_AFTDATE);

	}

	/**
	 * Creates the search label CommandType
	 */
	private void setSlabelCommand() {
		String parameters = removeFirstWord(userCommand);
		if (isRightCommand(parameters, labelNameAloneReg)) {
			dCommand = new DonFindCommand(extractName(parameters),
					SearchType.SEARCH_LABEL);
			dCommand.setType(CommandType.SEARCH_LABEL);
		} else {
			dCommand.setType(CommandType.INVALID_FORMAT);
		}

	}

	/**
	 * Creates the remove label CommandType
	 */
	private void setDelabelCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, labelIDReg)) {

			String idStr = getID(parameters);
			int ID = Integer.parseInt(idStr);

			// get rid of xxx
			String labelName = parameters.replaceFirst(getIDReg, "").trim();
			dCommand = new DonDelabelCommand(ID, extractName(labelName));
			dCommand.setType(CommandType.DELABEL_ID);

		} else if (isRightCommand(parameters, labelNameReg)) {
			String[] names = getTaskNameArr(parameters, labelNameSpaceReg);

			String taskName = names[0];
			String labelName = names[1];

			if (isGoodName(taskName) && isGoodName(labelName)) {
				dCommand = new DonDelabelCommand(taskName, labelName);
				dCommand.setType(CommandType.DELABEL_NAME);

			} else {
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		} else {
			dCommand.setType(CommandType.INVALID_FORMAT);
		}

	}

	/**
	 * Creates the label CommandType
	 */
	private void setLabelCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, labelIDReg)) {

			String idStr = getID(parameters);
			int ID = Integer.parseInt(idStr);

			// get rid of xxx
			String labelName = parameters.replaceFirst(getIDReg, "").trim();
			dCommand = new DonAddLabelCommand(ID, extractName(labelName));
			dCommand.setType(CommandType.LABEL_ID);

		} else if (isRightCommand(parameters, labelNameReg)) {
			String[] names = getTaskNameArr(parameters, labelNameSpaceReg);

			String taskName = names[0];
			String labelName = names[1];

			if (isGoodName(taskName) && isGoodName(labelName)) {
				dCommand = new DonAddLabelCommand(taskName, labelName);
				dCommand.setType(CommandType.LABEL_NAME);

			} else {
				dCommand.setType(CommandType.INVALID_FORMAT);
			}
		} else {
			dCommand.setType(CommandType.INVALID_FORMAT);
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
			} else if (parameters.equalsIgnoreCase("mark")) {
				dCommand = new DonHelpCommand(HelpType.HELP_MARK);
			} else if (parameters.equalsIgnoreCase("undo")) {
				dCommand = new DonHelpCommand(HelpType.HELP_UNDO);
			} else if (parameters.equalsIgnoreCase("redo")) {
				dCommand = new DonHelpCommand(HelpType.HELP_REDO);
			} else {
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
	 * Checks if the date follows the dd/mm/yyyy format
	 * 
	 * @param parameters
	 * @return
	 */
	private String removeFormalDate(String param, String regex, String regex2) {
		// if dd/mm/yyyy
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		if (matcher.find()) {
			return param.replaceAll(dateReg, " today ");
		} else {
			// if dd/mm
			Pattern pattern2 = Pattern
					.compile(regex2, Pattern.CASE_INSENSITIVE);
			Matcher matcher2 = pattern2.matcher(param);
			if (matcher2.find()) {
				return param.replaceAll(dateNoYearReg, " today ");
			} else {
				return "";
			}
		}
	}

	/**
	 * gets the date following the dd/mm/yyyy format
	 * 
	 * @param parameters
	 * @return
	 * @throws WrongDateException
	 */
	private Calendar getFormalDate(String param) throws WrongDateException {
		// if dd/mm/yyyy
		Pattern pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		Calendar cal;
		if (matcher.find()) {
			cal = createFormalDate(matcher.group());
		} else {
			Pattern pattern2 = Pattern.compile(dateNoYearReg,
					Pattern.CASE_INSENSITIVE);
			Matcher matcher2 = pattern2.matcher(param);
			matcher2.find();
			cal = createFormalNoYearDate(matcher2.group());
		}
		return cal;
	}

	/**
	 * gets the event dates following the dd/mm/yyyy format
	 * 
	 * @param parameters
	 * @return
	 * @throws WrongDateException
	 */
	private Calendar[] getFormalEventDates(String param)
			throws WrongDateException {
		// if dd/mm/yyyy
		Pattern pattern = Pattern.compile(dateReg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(param);
		Calendar cal;
		Calendar[] calArr = new Calendar[2];
		if (matcher.find()) {
			cal = createFormalDate(matcher.group());
			calArr[0] = cal;
			matcher.find();
			cal = createFormalDate(matcher.group());
			calArr[1] = cal;
		} else {// is dd/mm
			Pattern pattern2 = Pattern.compile(dateNoYearReg,
					Pattern.CASE_INSENSITIVE);
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
		Calendar calCheck = new GregorianCalendar();

		int day = Integer.parseInt(date.substring(0, 2));
		int month = Integer.parseInt(date.substring(3, 5)) - 1;
		int year = Integer.parseInt(date.substring(6, 10));

		calCheck.set(Calendar.YEAR, year);
		calCheck.set(Calendar.MONTH, month);
		calCheck.set(Calendar.DAY_OF_MONTH, 1);

		if ((day > calCheck.getActualMaximum(Calendar.DAY_OF_MONTH))
				|| (month >= 12)) {
			// create an error date ref
			throw new WrongDateException();
		} else {
			return new GregorianCalendar(year, month, day);
		}
	}

	private Calendar createFormalNoYearDate(String date)
			throws WrongDateException {
		Calendar calCheck = new GregorianCalendar();

		int day = Integer.parseInt(date.substring(0, 2));
		int month = Integer.parseInt(date.substring(3, 5)) - 1;

		calCheck.set(Calendar.MONTH, month);
		calCheck.set(Calendar.DAY_OF_MONTH, 1);

		if ((day > calCheck.getActualMaximum(Calendar.DAY_OF_MONTH))
				|| (month >= 12)) {
			// create an error date ref
			throw new WrongDateException();
		} else {
			return new GregorianCalendar(calCheck.get(Calendar.YEAR), month,
					day);
		}
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
		if (!removeFormalDate(parameters, dateReg, dateNoYearReg).equals("")) {

			try {
				Calendar date = getFormalDate(parameters);

				String param = removeFormalDate(parameters, dateReg,
						dateNoYearReg);
				Date time = getTimeFromParser(param);

				if (isTimeMentioned()) {
					hasSetTime = true;
					CalHelper.copyCalendar(createDateTimeNatty(date, time),
							deadlineOut);
				} else {
					CalHelper.copyCalendar(createDateNatty(date), deadlineOut);
				}

			} catch (Exception e) {
				dCommand.setType(CommandType.INVALID_DATE);
			}

		} else {
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
				dCommand.setType(CommandType.INVALID_DATE);
			}
		}

		return hasSetTime;
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
	 */
	private Date getTimeFromParser(String parameters) {
		groups = nattyParser.parse(parameters);
		return groups.get(0).getDates().get(0);
	}

	/**
	 * Gets dates from parser
	 * 
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
	 * 
	 * @param parameters
	 */
	/*
	 * private void setDeadlineForCommand(String parameters) {
	 * 
	 * if(!removeFormalDate(parameters, dateReg, dateNoYearReg).equals("")){
	 * 
	 * try{ Calendar date = getFormalDate(parameters);
	 * 
	 * String param = removeFormalDate(parameters, dateReg, dateNoYearReg);
	 * 
	 * Date time = getTimeFromParser(param ); if(isTimeMentioned()){
	 * dCommand.setHasUserSetTime(true);
	 * dCommand.setDeadline(createDateTimeNatty(date, time)); }else{
	 * dCommand.setDeadline(createDateNatty(date)); }
	 * 
	 * }catch(Exception e){ dCommand = new DonCommand();
	 * dCommand.setType(CommandType.INVALID_DATE); }
	 * 
	 * }else{ try{ Date date = getTimeFromParser(parameters); Calendar cal = new
	 * GregorianCalendar(); cal.setTime(date);
	 * 
	 * if(isTimeMentioned()){ dCommand.setHasUserSetTime(true);
	 * dCommand.setDeadline(cal); }else{
	 * 
	 * dCommand.setDeadline(createDateNatty(cal)); } }catch(Exception e){
	 * dCommand = new DonCommand(); dCommand.setType(CommandType.INVALID_DATE);
	 * }
	 * 
	 * 
	 * } }
	 */

	private boolean setStartAndEndForCommand(String parameters,
			Calendar startDate, Calendar endDate) {
		boolean hasSetTime = false;
		if (!removeFormalDate(parameters, dateEventReg, dateNoYearEventReg)
				.equals("")) {

			try {
				Calendar[] dates = getFormalEventDates(parameters);
				String param = removeFormalDate(parameters, dateEventReg,
						dateNoYearEventReg);
				Date[] timings = getTimingsFromParser(param);

				if (isTimeMentioned()) {
					hasSetTime = true;
					CalHelper.copyCalendar(
							createDateTimeNatty(dates[0], timings[0]),
							startDate);
					CalHelper.copyCalendar(
							createDateTimeNatty(dates[1], timings[1]), endDate);
				} else {
					CalHelper
							.copyCalendar(createDateNatty(dates[0]), startDate);
					CalHelper.copyCalendar(createDateNatty(dates[1]), endDate);
				}

			} catch (Exception e) {
				dCommand.setType(CommandType.INVALID_DATE);
			}
		} else {
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
				dCommand.setType(CommandType.INVALID_DATE);
			}
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
		nameArr[0] = extractName(nameArr[0].trim() + "\"");
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
		if (!name.contains(";")) {
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
		return param.substring(1, param.length() - 1);
	}

	public class WrongDateException extends Exception {

		public WrongDateException() {
			super();
		}
	}

}

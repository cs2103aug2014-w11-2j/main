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
//@author A0115503W
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
	private String taskNameReg = "^\".+\"$";

	// allow "blah" by
	private String deadlineTaskReg = ".+(by\\s.+){1}";

	// allow "blah" from
	private String eventTaskReg = ".+(from\\s.+){1}";

	// allow "blah"/ID to
	private String editToNameReg = ".+(to\\s\".+\"){1}";

	// for to "
	private String editNameSpaceReg = "to \"";

	// allow xx "BLAH"
	private String labelNameReg = "^#.+$";

	// allow "blah" "BLAH"
	private String labelReg = ".+ #.+$";

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
		} else if (commandWord.equalsIgnoreCase("e")
				|| commandWord.equalsIgnoreCase("ed")
				|| commandWord.equalsIgnoreCase("edit")) {
			setEditCommand();
		} else if (commandWord.equalsIgnoreCase("s")
				|| commandWord.equalsIgnoreCase("search")) {
			setSearchCommand();
		} else if (commandWord.equalsIgnoreCase("saf")) {
			setSearchDatesCommand(SearchType.SEARCH_AFTDATE);
		} else if (commandWord.equalsIgnoreCase("son")) {
			setSearchDatesCommand(SearchType.SEARCH_DATE);
		} else if (commandWord.equalsIgnoreCase("all")) {
			dCommand = new DonFindCommand(SearchType.SEARCH_ALL);
		} else if (commandWord.equalsIgnoreCase("d")
				|| commandWord.equalsIgnoreCase("del")
				|| commandWord.equalsIgnoreCase("delete")) {
			setDeleteCommand();
		} else if (commandWord.equalsIgnoreCase("m")
				|| commandWord.equalsIgnoreCase("mark")) {
			setMarkCommand();
		} else if (commandWord.equalsIgnoreCase("label")
				|| commandWord.equalsIgnoreCase("l")) {
			setLabelCommand();
		} else if (commandWord.equalsIgnoreCase("delabel")
				|| commandWord.equalsIgnoreCase("dl")) {
			setDelabelCommand();
		} else if (commandWord.equalsIgnoreCase("slabel")
				|| commandWord.equalsIgnoreCase("sl")) {
			setSlabelCommand();
		} else if (commandWord.equalsIgnoreCase("sud")
				|| commandWord.equalsIgnoreCase("undone")) {
			dCommand = new DonFindCommand(SearchType.SEARCH_UNDONE);
		} else if (commandWord.equalsIgnoreCase("sd")
				|| commandWord.equalsIgnoreCase("done")) {
			dCommand = new DonFindCommand(SearchType.SEARCH_DONE);
		} else if (commandWord.equalsIgnoreCase("free")) {
			dCommand = new DonFindCommand(SearchType.SEARCH_FREE);
		} else if (commandWord.equalsIgnoreCase("today")
				|| commandWord.equalsIgnoreCase("t")) {
			dCommand = new DonFindCommand(SearchType.TODAY);
		} else if (commandWord.equalsIgnoreCase("od")
				|| commandWord.equalsIgnoreCase("overdue")
				|| commandWord.equalsIgnoreCase("o")) {
			dCommand = new DonFindCommand(SearchType.OVERDUE);
		} else if (commandWord.equalsIgnoreCase("week")
				|| commandWord.equalsIgnoreCase("w")) {
			dCommand = new DonFindCommand(SearchType.SEVEN_DAYS);
		} else if (commandWord.equalsIgnoreCase("future")
				|| commandWord.equalsIgnoreCase("u")) {
			dCommand = new DonFindCommand(SearchType.FUTURE);
		} else if (commandWord.equalsIgnoreCase("c")
				|| commandWord.equalsIgnoreCase("console")) {
			dCommand = new DonFindCommand(SearchType.CONSOLE);
		}else if (commandWord.equalsIgnoreCase("float")
				|| commandWord.equalsIgnoreCase("fl")
				|| commandWord.equalsIgnoreCase("floating")
				|| commandWord.equalsIgnoreCase("f")) {
			dCommand = new DonFindCommand(SearchType.FLOAT);
		} else if (commandWord.equalsIgnoreCase("results")
				|| commandWord.equalsIgnoreCase("result")
				|| commandWord.equalsIgnoreCase("r")) {
			dCommand = new DonFindCommand(SearchType.RESULTS);
		} else if (commandWord.equalsIgnoreCase("undo")
				|| commandWord.equalsIgnoreCase("un")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.UNDO);
		} else if (commandWord.equalsIgnoreCase("redo")
				|| commandWord.equalsIgnoreCase("re")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.REDO);
		} else if (commandWord.equalsIgnoreCase("help")) {
			setHelpCommand();
		} else if (commandWord.equalsIgnoreCase("exit")) {
			dCommand = new DonGeneralCommand(GeneralCommandType.EXIT);
		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_COMMAND,
					commandWord);
		}

	}

	/**
	 * Creates the add CommandType
	 */
	private void setAddCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, deadlineTaskReg)
				&& isRightCommand(parameters, eventTaskReg)) {
			// check which is later.
			int byIndex = parameters.lastIndexOf(" by ");
			int fromIndex = parameters.lastIndexOf(" from ");

			if (byIndex > fromIndex) {
				createAddDeadlineCommand(parameters);
			} else {
				createAddEventCommand(parameters);
			}
		} else if (isRightCommand(parameters, deadlineTaskReg)) {
			createAddDeadlineCommand(parameters);

		} else if (isRightCommand(parameters, eventTaskReg)) {
			createAddEventCommand(parameters);

		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
					commandWord);
		}
	}

	private void createAddDeadlineCommand(String param) {
		int byIndex = param.lastIndexOf(" by ");
		String taskDate = param.substring(byIndex + 1);
		String taskName = param.substring(0, byIndex + 1).trim();

		if (isWithinInvertedCommas(taskName)) {
			taskName = extractName(taskName);
		}

		if (isGoodName(taskName)) {

			Calendar deadline = Calendar.getInstance();
			boolean hasSetTime = setNewDeadlineForCommand(taskDate, deadline);
			// if dCommand is not null setNewDeadlineForCommand must have set
			// INVALID_DATE
			if (dCommand == null) {
				dCommand = new DonCreateCommand(taskName, deadline, hasSetTime);
			}

		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
					commandWord);
		}

	}

	private void createAddEventCommand(String param) {
		int fromIndex = param.lastIndexOf(" from ");
		String taskDates = param.substring(fromIndex + 1);
		String taskName = param.substring(0, fromIndex + 1).trim();

		if (isWithinInvertedCommas(taskName)) {
			taskName = extractName(taskName);
		}

		if (isGoodName(taskName)) {

			Calendar startDate = Calendar.getInstance(), endDate = Calendar
					.getInstance();
			boolean hasSetTime = setStartAndEndForCommand(taskDates, startDate,
					endDate);

			if (dCommand == null) {
				dCommand = new DonCreateCommand(taskName, startDate, endDate,
						hasSetTime);
			}

		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
					commandWord);
		}

	}

	/**
	 * Creates the add floating command.
	 * 
	 */
	private void setAddFloatCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isWithinInvertedCommas(parameters)) {
			parameters = extractName(parameters);
		}

		if (isGoodName(parameters)) {
			dCommand = new DonCreateCommand(parameters);
		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
					commandWord);
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

			if (isWithinInvertedCommas(oldName)) {
				oldName = extractName(oldName);
			}

			if (isGoodName(oldName) && isGoodName(newName)) {
				dCommand = new DonEditCommand(oldName, newName);

			} else if (isGoodName(newName)) {
				try {

					int ID = Integer.parseInt(oldName);
					dCommand = new DonEditCommand(ID, newName);

				} catch (Exception e) {
					dCommand = new DonInvalidCommand(
							InvalidType.INVALID_FORMAT, commandWord);
				}

			} else {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
						commandWord);
			}

		} else if (isRightCommand(parameters, deadlineTaskReg)
				&& isRightCommand(parameters, eventTaskReg)) {
			// check which is later.
			int byIndex = parameters.lastIndexOf(" by ");
			int fromIndex = parameters.lastIndexOf(" from ");

			if (byIndex > fromIndex) {
				createEditDeadlineCommand(parameters);
			} else {
				createEditEventCommand(parameters);
			}

		} else if (isRightCommand(parameters, deadlineTaskReg)) {
			createEditDeadlineCommand(parameters);

		} else if (isRightCommand(parameters, eventTaskReg)) {
			createEditEventCommand(parameters);

		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
					commandWord);
		}

	}

	private void createEditDeadlineCommand(String param) {
		int byIndex = param.lastIndexOf(" by ");
		String taskDate = param.substring(byIndex + 1);
		String taskName = param.substring(0, byIndex + 1).trim();

		if (isWithinInvertedCommas(taskName)) {
			taskName = extractName(taskName);
		}

		if (isGoodName(taskName)) {

			Calendar deadline = Calendar.getInstance();
			boolean hasSetTime = setNewDeadlineForCommand(taskDate, deadline);

			if (dCommand == null) {
				dCommand = new DonEditCommand(taskName, deadline, hasSetTime);
			}

		} else {
			try {

				int ID = Integer.parseInt(taskName);
				Calendar deadline = Calendar.getInstance();
				boolean hasSetTime = setNewDeadlineForCommand(taskDate,
						deadline);

				if (dCommand == null) {
					dCommand = new DonEditCommand(ID, deadline, hasSetTime);
				}
			} catch (Exception e) {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
						commandWord);
			}
		}

	}

	private void createEditEventCommand(String param) {
		int fromIndex = param.lastIndexOf(" from ");
		String taskDates = param.substring(fromIndex + 1);
		String taskName = param.substring(0, fromIndex + 1).trim();

		if (isWithinInvertedCommas(taskName)) {
			taskName = extractName(taskName);
		}

		if (isGoodName(taskName)) {

			Calendar startDate = Calendar.getInstance(), endDate = Calendar
					.getInstance();
			boolean hasSetTime = setStartAndEndForCommand(taskDates, startDate,
					endDate);

			if (dCommand == null) {
				dCommand = new DonEditCommand(taskName, startDate, endDate,
						hasSetTime);
			}

		} else {
			try {
				int ID = Integer.parseInt(taskName);
				Calendar startDate = Calendar.getInstance(), endDate = Calendar
						.getInstance();
				boolean hasSetTime = setStartAndEndForCommand(taskDates,
						startDate, endDate);

				if (dCommand == null) {
					dCommand = new DonEditCommand(ID, startDate, endDate,
							hasSetTime);
				}
			} catch (Exception e) {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
						commandWord);
			}
		}
	}

	/**
	 * Creates the mark CommandType
	 */
	private void setMarkCommand() {
		String parameters = removeFirstWord(userCommand);

		if (parameters.equalsIgnoreCase("overdue")
				|| parameters.equalsIgnoreCase("od")) {
			dCommand = new DonMarkCommand(MarkType.MARK_OVERDUE);

		} else if (parameters.equalsIgnoreCase("float")
				|| parameters.equalsIgnoreCase("fl")
				|| parameters.equalsIgnoreCase("floating")) {
			dCommand = new DonMarkCommand(MarkType.MARK_FLOAT);

		} else if (isGoodName(parameters)) {

			if (isWithinInvertedCommas(parameters)) {
				parameters = extractName(parameters);
			}

			dCommand = new DonMarkCommand(parameters);

		} else {

			try {
				int ID = Integer.parseInt(parameters);
				dCommand = new DonMarkCommand(ID);

			} catch (Exception e) {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
						commandWord);
			}
		}

	}

	/**
	 * Creates the delete CommandType
	 */
	private void setDeleteCommand() {
		String parameters = removeFirstWord(userCommand);

		if (parameters.equalsIgnoreCase("overdue")
				|| parameters.equalsIgnoreCase("od")) {
			dCommand = new DonDeleteCommand(DeleteType.DELETE_OVERDUE);

		} else if (parameters.equalsIgnoreCase("float")
				|| parameters.equalsIgnoreCase("fl")
				|| parameters.equalsIgnoreCase("floating")) {
			dCommand = new DonDeleteCommand(DeleteType.DELETE_FLOAT);

		} else if (parameters.equalsIgnoreCase("done")) {
			dCommand = new DonDeleteCommand(DeleteType.DELETE_DONE);

		} else if (isRightCommand(parameters, labelNameReg)) {
			dCommand = new DonDeleteCommand(extractLabelName(parameters),
					DeleteType.DELETE_LABEL);

		} else if (isGoodName(parameters)) {
			if (isWithinInvertedCommas(parameters)) {
				parameters = extractName(parameters);
			}
			dCommand = new DonDeleteCommand(parameters, DeleteType.DELETE_TITLE);

		} else {
			try {
				int ID = Integer.parseInt(parameters);
				dCommand = new DonDeleteCommand(ID);

			} catch (Exception e) {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
						commandWord);
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

		} else {
			if (isGoodName(parameters)) {
				if (isWithinInvertedCommas(parameters)) {
					parameters = extractName(parameters);
				}
				dCommand = new DonFindCommand(parameters,
						SearchType.SEARCH_NAME);

			} else {
				try {
					int ID = Integer.parseInt(parameters);
					dCommand = new DonFindCommand(ID);

				} catch (Exception e) {
					dCommand = new DonInvalidCommand(
							InvalidType.INVALID_FORMAT, commandWord);
				}
			}
		}
	}

	/**
	 * Creates the search after CommandType and search on CommandType
	 */
	private void setSearchDatesCommand(SearchType type) {
		String parameters = removeFirstWord(userCommand);

		Calendar searchDate = Calendar.getInstance();
		boolean hasSetTime = setNewDeadlineForCommand(parameters, searchDate);
		if (dCommand == null) {
			dCommand = new DonFindCommand(searchDate, hasSetTime, type);
		}

	}

	/**
	 * Creates the search label CommandType
	 */
	private void setSlabelCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, labelNameReg)) {
			dCommand = new DonFindCommand(extractLabelName(parameters),
					SearchType.SEARCH_LABEL);

		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
					commandWord);
		}

	}

	/**
	 * Creates the remove label CommandType
	 */
	private void setDelabelCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, labelReg)) {
			int byIndex = parameters.lastIndexOf(" #");
			String labelName = parameters.substring(byIndex + 1);
			String taskName = parameters.substring(0, byIndex + 1).trim();

			if (isWithinInvertedCommas(taskName)) {
				taskName = extractName(taskName);
			}

			if (isGoodName(taskName)) {
				dCommand = new DonDelabelCommand(taskName,
						extractLabelName(labelName));
			} else {
				try {
					int ID = Integer.parseInt(taskName);
					dCommand = new DonDelabelCommand(ID,
							extractLabelName(labelName));

				} catch (Exception e) {
					dCommand = new DonInvalidCommand(
							InvalidType.INVALID_FORMAT, commandWord);
				}
			}

		} else {
			if (isGoodName(parameters)) {
				if (isWithinInvertedCommas(parameters)) {
					parameters = extractName(parameters);
				}
				dCommand = new DonDelabelCommand(parameters);
			} else {
				try {
					int ID = Integer.parseInt(parameters);
					dCommand = new DonDelabelCommand(ID);

				} catch (Exception e) {
					dCommand = new DonInvalidCommand(
							InvalidType.INVALID_FORMAT, commandWord);
				}
			}
		}

	}

	/**
	 * Creates the label CommandType
	 */
	private void setLabelCommand() {
		String parameters = removeFirstWord(userCommand);

		if (isRightCommand(parameters, labelReg)) {
			int byIndex = parameters.lastIndexOf(" #");
			String labelName = parameters.substring(byIndex + 1);
			String taskName = parameters.substring(0, byIndex + 1).trim();

			if (isWithinInvertedCommas(taskName)) {
				taskName = extractName(taskName);
			}

			if (isGoodName(taskName)) {
				dCommand = new DonAddLabelCommand(taskName,
						extractLabelName(labelName));
			} else {
				try {
					int ID = Integer.parseInt(taskName);
					dCommand = new DonAddLabelCommand(ID,
							extractLabelName(labelName));

				} catch (Exception e) {
					dCommand = new DonInvalidCommand(
							InvalidType.INVALID_FORMAT, commandWord);
				}
			}

		} else {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
					commandWord);
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
			if (parameters.equalsIgnoreCase("add")
					|| parameters.equalsIgnoreCase("a")
					|| parameters.equalsIgnoreCase("af")
					|| parameters.equalsIgnoreCase("addf")) {
				dCommand = new DonHelpCommand(HelpType.HELP_ADD);
			} else if (parameters.equalsIgnoreCase("edit")
					|| parameters.equalsIgnoreCase("ed")
					|| parameters.equalsIgnoreCase("e")) {
				dCommand = new DonHelpCommand(HelpType.HELP_EDIT);
			} else if (parameters.equalsIgnoreCase("search")
					|| parameters.equalsIgnoreCase("s")
					|| parameters.equalsIgnoreCase("saf")
					|| parameters.equalsIgnoreCase("son")
					|| parameters.equalsIgnoreCase("sd")
					|| parameters.equalsIgnoreCase("sud")) {
				dCommand = new DonHelpCommand(HelpType.HELP_SEARCH);
			} else if (parameters.equalsIgnoreCase("del")
					|| parameters.equalsIgnoreCase("delete")
					|| parameters.equalsIgnoreCase("d")) {
				dCommand = new DonHelpCommand(HelpType.HELP_DELETE);
			} else if (parameters.equalsIgnoreCase("label")
					|| parameters.equalsIgnoreCase("delabel")
					|| parameters.equalsIgnoreCase("slabel")
					|| parameters.equalsIgnoreCase("dl")
					|| parameters.equalsIgnoreCase("sl")
					|| parameters.equalsIgnoreCase("l")) {
				dCommand = new DonHelpCommand(HelpType.HELP_LABEL);
			} else if (parameters.equalsIgnoreCase("mark")
					|| parameters.equalsIgnoreCase("m")) {
				dCommand = new DonHelpCommand(HelpType.HELP_MARK);
			} else if (parameters.equalsIgnoreCase("undo")) {
				dCommand = new DonHelpCommand(HelpType.HELP_UNDO);
			} else if (parameters.equalsIgnoreCase("redo")) {
				dCommand = new DonHelpCommand(HelpType.HELP_REDO);
			} else {
				dCommand = new DonInvalidCommand(InvalidType.INVALID_FORMAT,
						commandWord);
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

		if (groups.get(0).getDates().size() != 1 || groups.get(0).isRecurring()) {
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
	private Date[] getTimingsFromParser(String parameters)
			throws WrongDateException {
		groups = nattyParser.parse(parameters);

		if (groups.get(0).getDates().size() != 2 || groups.get(0).isRecurring()) {

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
				CalHelper.copyCalendar(createDateNatty(calArr[0]), startDate);
				CalHelper.copyCalendar(createDateNatty(calArr[1]), endDate);
			}
		} catch (Exception e) {
			dCommand = new DonInvalidCommand(InvalidType.INVALID_DATE);
		}

		return hasSetTime;
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
	private boolean isWithinInvertedCommas(String param) {
		Pattern pattern = Pattern.compile(taskNameReg);
		Matcher matcher = pattern.matcher(param);
		return matcher.find();
	}

	/**
	 * Checks if the task name does not contain ;
	 */
	private boolean isGoodName(String name) {
		// ensures semi colon not in name
		if (!name.contains(";") && !name.contains("#")
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

		private static final long serialVersionUID = 95503042885547853L;

		public WrongDateException() {
			super();
		}
	}

}

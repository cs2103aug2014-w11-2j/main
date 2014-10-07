package doornot.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import doornot.logic.IDonResponse.ResponseType;
import doornot.logic.IDonTask.TaskType;
import doornot.parser.DonParser;
import doornot.parser.IDonCommand;
import doornot.parser.IDonParser;
import doornot.storage.DonStorage;
import doornot.storage.IDonStorage;

/**
 * DonLogic - Class for handling the logic of the program
 * (creation/deletion/modification of tasks)
 * 
 */
//@author A0111995Y
public class DonLogic implements IDonLogic {

	private static final String MSG_COMMAND_WRONG_FORMAT = "The command you entered was of the wrong format!";
	private static final String MSG_COMMAND_WRONG_DATE = "The date you entered was invalid!";
	private static final String MSG_SAVE_SUCCESSFUL = "Save successful.";
	private static final String MSG_SAVE_FAILED = "Save failed.";
	private static final String MSG_ADD_TASK_FAILURE = "Could not add task '%1$s'";
	private static final String MSG_ADD_FLOATING_TASK_SUCCESS = "'%1$s' has been added.";
	private static final String MSG_SEARCH_ID_FAILED = "No tasks with ID of %1$d was found.";
	private static final String MSG_SEARCH_TITLE_FAILED = "No tasks with a title containing '%1$s' was found.";
	private static final String MSG_SEARCH_DATE_FAILED = "No tasks starting in %1$s were found.";
	private static final String MSG_DELETE_SUCCESS = "The above task was deleted successfully.";
	private static final String MSG_DELETE_FAILED = "The above task could not be deleted.";
	private static final String MSG_EDIT_TITLE_SUCCESS = "Task name changed from '%1$s' to '%2$s'.";
	private static final String MSG_EDIT_SINGLE_DATE_SUCCESS = "%1$s changed from %2$s to %3$s.";
	private static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	private static final String MSG_UNDO_SUCCESS = "Last action undone. %1$d change(s) removed.";
	private static final String MSG_TOGGLE_STATUS_ID_SUCCESS = "Task %1$d has been set to '%2$s'";
	private static final String MSG_SEARCH_MORE_THAN_ONE_TASK = "'%1$s' returned more than 1 result. Please specify with the ID.";
	private static final String MSG_UNKNOWN_COMMAND = "You have entered an unknown command";
	private static final String MSG_FREE_EVERYWHERE = "You are free!";

	private static final String MSG_EX_NO_RANGE_GIVEN = "Task range was not specified";

	private static final String PHRASE_COMPLETE = "complete";
	private static final String PHRASE_INCOMPLETE = "incomplete";
	private static final String PHRASE_END_DATE = "End date";
	private static final String PHRASE_START_DATE = "Start date";

	private static final int FAILURE = -1;
	private static final int FIND_INCOMPLETE = 0;
	private static final int FIND_COMPLETE = 1;
	private static final int FIND_ALL = 2;

	private IDonStorage donStorage;
	private IDonParser donParser;

	private Stack<DonAction> actionHistory;

	public DonLogic() {
		donStorage = new DonStorage();
		donParser = new DonParser();
		actionHistory = new Stack<DonAction>();

		donStorage.loadFromDisk();
	}

	@Override
	public IDonResponse runCommand(String command) {
		IDonCommand dCommand = donParser.parseCommand(command);
		IDonCommand.CommandType commandType = dCommand.getType();
		IDonResponse response = null;
		if (commandType == IDonCommand.CommandType.ADD_FLOAT) {
			response = createTask(dCommand.getNewName());

		} else if (commandType == IDonCommand.CommandType.ADD_TASK) {
			response = createTask(dCommand.getNewName(),
					dCommand.getNewDeadline());

		} else if (commandType == IDonCommand.CommandType.ADD_EVENT) {
			response = createTask(dCommand.getNewName(),
					dCommand.getNewStartDate(), dCommand.getNewEndDate());

		} else if (commandType == IDonCommand.CommandType.SEARCH_ID) {
			response = findTask(dCommand.getID());

		} else if (commandType == IDonCommand.CommandType.SEARCH_NAME) {
			response = findTask(dCommand.getName());

		} else if (commandType == IDonCommand.CommandType.SEARCH_DATE) {
			response = findTask(dCommand.getDeadline());

		} else if (commandType == IDonCommand.CommandType.DELETE_ID) {
			response = deleteTask(dCommand.getID());

		} else if (commandType == IDonCommand.CommandType.DELETE) {
			response = deleteTask(dCommand.getName());

		} else if (commandType == IDonCommand.CommandType.EDIT_ID_NAME) {
			response = editTask(dCommand.getID(), dCommand.getNewName());

		} else if (commandType == IDonCommand.CommandType.EDIT_ID_DATE) {
			// TODO: recognize different single date edit type
			response = editTask(dCommand.getID(), true,
					dCommand.getNewDeadline());

		} else if (commandType == IDonCommand.CommandType.EDIT_ID_EVENT) {
			response = editTask(dCommand.getID(), dCommand.getNewStartDate(),
					dCommand.getNewEndDate());

		} else if (commandType == IDonCommand.CommandType.EDIT_NAME) {
			response = editTask(dCommand.getName(), dCommand.getNewName());

		} else if (commandType == IDonCommand.CommandType.EDIT_DATE) {
			// TODO: recognize different single date edit type
			response = editTask(dCommand.getName(), true,
					dCommand.getNewDeadline());

		} else if (commandType == IDonCommand.CommandType.EDIT_EVENT) {
			response = editTask(dCommand.getName(), dCommand.getNewStartDate(),
					dCommand.getNewEndDate());

		} else if (commandType == IDonCommand.CommandType.MARK_ID) {
			response = toggleStatus(dCommand.getID());

		} else if (commandType == IDonCommand.CommandType.MARK) {
			response = toggleStatus(dCommand.getName());

		} else if (commandType == IDonCommand.CommandType.UNDO) {
			response = undoLastAction();
			
		} else if (commandType == IDonCommand.CommandType.HELP) {
			//TODO allow commands to be passed in
			response = getHelp("");
			
		} else if (commandType == IDonCommand.CommandType.INVALID_FORMAT) {
			response = createInvalidFormatResponse();
			
		} else if (commandType == IDonCommand.CommandType.INVALID_DATE) {
			response = createInvalidDateResponse();
			
		} else {
			// No relevant action could be executed
			response = new DonResponse();
			response.setResponseType(ResponseType.UNKNOWN_COMMAND);
			response.addMessage(MSG_UNKNOWN_COMMAND);
		}
		
		//Perform a save after every command
		saveToDrive();

		return response;
	}

	@Override
	public IDonResponse saveToDrive() {
		boolean saveSuccess = donStorage.saveToDisk();
		IDonResponse response = new DonResponse();
		if (saveSuccess) {
			response.setResponseType(IDonResponse.ResponseType.SAVE_SUCCESS);
			response.addMessage(MSG_SAVE_SUCCESSFUL);
		} else {
			response.setResponseType(IDonResponse.ResponseType.SAVE_FAILURE);
			response.addMessage(MSG_SAVE_FAILED);
		}
		return response;
	}

	@Override
	public IDonResponse initialize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Creates a response for user entered commands with invalid formatting
	 * @return	the response
	 */
	private IDonResponse createInvalidFormatResponse() {
		IDonResponse response = new DonResponse();
		response.addMessage(MSG_COMMAND_WRONG_FORMAT);
		response.setResponseType(ResponseType.UNKNOWN_COMMAND);
		return response;
	}
	
	/**
	 * Creates a response for user entered commands with invalid dates
	 * @return	the response
	 */
	private IDonResponse createInvalidDateResponse() {
		IDonResponse response = new DonResponse();
		response.addMessage(MSG_COMMAND_WRONG_DATE);
		response.setResponseType(ResponseType.UNKNOWN_COMMAND);
		return response;
	}

	/**
	 * Creates a floating task
	 * 
	 * @param title
	 *            the title of the task
	 * @return the response
	 */
	private IDonResponse createTask(String title) {
		IDonTask task = new DonTask(title, donStorage.getNextID());
		int addResult = donStorage.addTask(task);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, title));
		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					title));
			response.addTask(task);

			// Add add action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(task.clone());
			actionHistory.push(new DonAction(IDonCommand.CommandType.ADD_FLOAT,
					affectedTasks));
		}
		return response;
	}

	/**
	 * Creates a deadline task
	 * 
	 * @param title
	 *            the title of the task
	 * @param deadline
	 *            the deadline of the task
	 * @return the response
	 */
	private IDonResponse createTask(String title, Calendar deadline) {
		IDonTask task = new DonTask(title, deadline, donStorage.getNextID());
		int addResult = donStorage.addTask(task);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, title));
		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					title));
			response.addTask(task);

			// Add add action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(task.clone());
			actionHistory.push(new DonAction(IDonCommand.CommandType.ADD_TASK,
					affectedTasks));
		}
		return response;
	}

	/**
	 * Creates a task with a duration
	 * 
	 * @param title
	 *            the title of the task
	 * @param startDate
	 *            the start date of the task
	 * @param endDate
	 *            the end date of the task
	 * @return the response
	 */
	private IDonResponse createTask(String title, Calendar startDate,
			Calendar endDate) {
		IDonTask task = new DonTask(title, startDate, endDate,
				donStorage.getNextID());
		int addResult = donStorage.addTask(task);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, title));
		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					title));
			response.addTask(task);

			// Add add action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(task.clone());
			actionHistory.push(new DonAction(IDonCommand.CommandType.ADD_EVENT,
					affectedTasks));
		}
		return response;
	}

	/**
	 * Find tasks with the given name
	 * 
	 * @param name
	 *            the name to search for
	 * @return the response containing the tasks
	 */
	private IDonResponse findTask(String name) {
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			// Search for the given name/title without case sensitivity
			if (task.getTitle().toLowerCase().contains(name.toLowerCase())) {
				response.addTask(task);
			}
		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_TITLE_FAILED, name));
		}
		return response;
	}

	/**
	 * Find tasks starting/occurring on a given date
	 * 
	 * @param date
	 *            the date to search for
	 * @return the response containing the tasks
	 */
	private IDonResponse findTask(Calendar date) {
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			// Search for the given name/title without case sensitivity
			TaskType taskType = task.getType();
			if (taskType == TaskType.FLOATING) {
				// Floating tasks have no date.
				continue;
			}
			Calendar taskDate = task.getStartDate();
			Calendar taskEndDate = task.getEndDate();
			// If the date falls within the start and end date of an event, the
			// event is returned as well
			if (isSameDay(taskDate, date)
					|| (taskType == TaskType.DURATION && isBetweenDates(date,
							taskDate, taskEndDate))) {
				response.addTask(task);
			}
		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
			String dateString = date.get(Calendar.DATE)
					+ " "
					+ date.getDisplayName(Calendar.MONTH, Calendar.LONG,
							Locale.ENGLISH) + date.get(Calendar.YEAR);
			response.addMessage(String.format(MSG_SEARCH_DATE_FAILED,
					dateString));
		}
		return response;
	}

	/**
	 * Find tasks that begin within the given range of time. Either parameter
	 * can be null to search for tasks before or after a date. For example if
	 * startDate is null and endDate is set to 09102014, the method will return
	 * all tasks from before 9th of October 2014. If startDate is 09102014 and
	 * endDate is null, all tasks beginning after 9th of October 2014 will be
	 * returned.
	 * 
	 * @param startDate
	 *            the date to start searching from (inclusive)
	 * @param endDate
	 *            the latest possible start date of a task (inclusive)
	 * @param completeType
	 *            0 if the tasks found must be incomplete, 1 if it must be
	 *            completed 2 if it can be complete or incomplete
	 * @return the response containing the tasks
	 */
	private IDonResponse findTaskRange(Calendar startDate, Calendar endDate,
			int completeType) {
		DonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		if (startDate == null && endDate == null) {
			throw new IllegalArgumentException(MSG_EX_NO_RANGE_GIVEN);
		}
		for (IDonTask task : taskList) {
			if (task.getType() == TaskType.FLOATING) {
				// Floating tasks have no date.
				continue;
			}
			// Ignore tasks that do not match the completion status to search
			// for
			if ((task.getStatus() && completeType == FIND_INCOMPLETE)
					|| (!task.getStatus() && completeType == FIND_COMPLETE)) {
				continue;
			}
			Calendar taskStart = task.getStartDate();
			if (startDate == null) {
				if (dateEqualOrBefore(taskStart, endDate)) {
					response.addTask(task);
				}
			} else if (endDate == null) {
				if (dateEqualOrAfter(taskStart, startDate)) {
					response.addTask(task);
				}
			} else {
				if (dateEqualOrAfter(taskStart, startDate)
						&& dateEqualOrBefore(taskStart, endDate)) {
					response.addTask(task);
				}
			}

		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
		}
		return response;
	}

	/**
	 * Find tasks given the ID
	 * 
	 * @param id
	 *            the id to search for
	 * @return the response containing the tasks
	 */
	private IDonResponse findTask(int id) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with given ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			response.setResponseType(IDonResponse.ResponseType.SEARCH_SUCCESS);
			response.addTask(task);
		}
		return response;
	}

	/**
	 * Deletes the task with the given ID
	 * 
	 * @param id
	 *            the id of the task to delete
	 * @return the response containing the deletion status
	 */
	private IDonResponse deleteTask(int id) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			boolean deleteStatus = donStorage.removeTask(id);
			if (deleteStatus) {
				// Deleted
				response.setResponseType(IDonResponse.ResponseType.DEL_SUCCESS);
				response.addMessage(MSG_DELETE_SUCCESS);
				response.addTask(task);

				// Add delete action to history
				ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
				affectedTasks.add(task.clone());
				actionHistory.push(new DonAction(
						IDonCommand.CommandType.DELETE, affectedTasks));
			} else {
				response.setResponseType(IDonResponse.ResponseType.DEL_FAILURE);
				response.addMessage(MSG_DELETE_FAILED);
			}
		}
		return response;
	}

	/**
	 * Deletes the task with the given title. If more than 1 task is found, the
	 * search results will be returned and nothing will be deleted.
	 * 
	 * @param title
	 *            the title of the task to search for to delete
	 * @return the response containing the deletion status
	 */
	private IDonResponse deleteTask(String title) {
		IDonResponse response = new DonResponse();

		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.DEL_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = deleteTask(task.getID());
		}

		return response;
	}

	/**
	 * Change the title of the task with ID id to the new title
	 * 
	 * @param id
	 *            the id of the task to edit the title of
	 * @param newTitle
	 *            the new title
	 * @return the response
	 */
	private IDonResponse editTask(int id, String newTitle) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			String oldTitle = task.getTitle();
			task.setTitle(newTitle);
			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_EDIT_TITLE_SUCCESS, oldTitle,
					newTitle));

			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			actionHistory.push(new DonAction(
					IDonCommand.CommandType.EDIT_ID_NAME, affectedTasks));
		}
		return response;
	}

	/**
	 * Change the title of the task with a given title to the new title. The
	 * task name being searched for must belong to only one task
	 * 
	 * @param title
	 *            the title of the task to search for
	 * @param newTitle
	 *            the new title
	 * @return the response
	 */
	private IDonResponse editTask(String title, String newTitle) {
		IDonResponse response = new DonResponse();

		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = editTask(task.getID(), newTitle);
		}

		return response;
	}

	/**
	 * Change the deadline of the task with ID id to the new deadline (or start
	 * date/end date)
	 * 
	 * @param id
	 *            the id of the task to change
	 * @param isStartDate
	 *            true if the date to change is the start date, false otherwise.
	 *            This will be ignored for deadline tasks.
	 * @param newDate
	 *            the new date to be applied to the task
	 * @return the success response
	 */
	private IDonResponse editTask(int id, boolean isStartDate, Calendar newDate) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			Calendar oldDate = null;
			String dateType = "";
			if (task.getType() == IDonTask.TaskType.FLOATING) {
				// TODO: What should we do with floating tasks when the user
				// wants to edit the date?
			} else if (task.getType() == IDonTask.TaskType.DEADLINE) {
				dateType = "Deadline";
				oldDate = task.getStartDate();
				task.setStartDate(newDate);
			} else if (task.getType() == IDonTask.TaskType.DURATION) {
				if (isStartDate) {
					dateType = PHRASE_START_DATE;
					oldDate = task.getStartDate();
					task.setStartDate(newDate);
				} else {
					dateType = PHRASE_END_DATE;
					oldDate = task.getEndDate();
					task.setEndDate(newDate);
				}
			}

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
					dateType, oldDate.getTime().toString(), newDate.getTime()
							.toString()));

			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			actionHistory.push(new DonAction(
					IDonCommand.CommandType.EDIT_ID_DATE, affectedTasks));
		}
		return response;
	}

	/**
	 * Change the start date/end date or deadline of the task with a given title
	 * to the new title. The task name being searched for must belong to only
	 * one task
	 * 
	 * @param title
	 *            the title of the task to search for
	 * @param isStartDate
	 *            true if the date to change is the start date, false otherwise.
	 *            This will be ignored for deadline tasks.
	 * @param newDate
	 *            the new date to be applied to the task
	 * @return the response
	 */
	private IDonResponse editTask(String title, boolean isStartDate,
			Calendar newDate) {
		IDonResponse response = new DonResponse();

		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = editTask(task.getID(), isStartDate, newDate);
		}

		return response;
	}

	/**
	 * Change the start and end date of the task with ID id to the new dates
	 * 
	 * @param id
	 *            the id of the task to change
	 * @param newStartDate
	 *            the new start date to be applied to the task
	 * @param newEndDate
	 *            the new end date to be applied to the task
	 * @return the success response
	 */
	private IDonResponse editTask(int id, Calendar newStartDate,
			Calendar newEndDate) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			Calendar oldStartDate = null, oldEndDate = null;
			String dateType = "";
			if (task.getType() == IDonTask.TaskType.FLOATING) {
				// TODO: What should we do with floating tasks when the user
				// wants to edit the date?
			} else if (task.getType() == IDonTask.TaskType.DEADLINE) {
				// TODO: What should we do with deadline tasks when the user
				// wants to edit the end date?
			} else if (task.getType() == IDonTask.TaskType.DURATION) {
				oldStartDate = task.getStartDate();
				task.setStartDate(newStartDate);

				oldEndDate = task.getEndDate();
				task.setEndDate(newEndDate);

			}

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
					PHRASE_START_DATE, oldStartDate.getTime().toString(),
					newStartDate.getTime().toString()));
			response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
					PHRASE_END_DATE, oldEndDate.getTime().toString(),
					newEndDate.getTime().toString()));

			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			actionHistory.push(new DonAction(
					IDonCommand.CommandType.EDIT_ID_EVENT, affectedTasks));
		}
		return response;
	}

	/**
	 * Change the start and end date of the task with a title containing the
	 * search string to the new dates. The title of the task must belong to only
	 * 1 task or the search results will be returned instead and no edits will
	 * be made.
	 * 
	 * @param title
	 *            the title of the task to search for
	 * @param newStartDate
	 *            the new start date to be applied to the task
	 * @param newEndDate
	 *            the new end date to be applied to the task
	 * @return
	 */
	private IDonResponse editTask(String title, Calendar newStartDate,
			Calendar newEndDate) {
		IDonResponse response = new DonResponse();

		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = editTask(task.getID(), newStartDate, newEndDate);
		}

		return response;
	}

	/**
	 * Undoes the last action
	 * 
	 * @return response stating the status of the undo operation
	 */
	private IDonResponse undoLastAction() {
		IDonResponse response = new DonResponse();
		if (actionHistory.size() <= 0) {
			response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
			response.addMessage(MSG_UNDO_NO_ACTIONS);
		} else {
			DonAction lastAction = actionHistory.pop();
			int changesReversed = 0;
			IDonCommand.CommandType lastActionType = lastAction.getActionType();
			if (lastActionType == IDonCommand.CommandType.ADD_TASK
					|| lastActionType == IDonCommand.CommandType.ADD_EVENT
					|| lastActionType == IDonCommand.CommandType.ADD_FLOAT) {
				// Perform a delete (reverse of Add)
				for (IDonTask addedTask : lastAction.getAffectedTasks()) {
					int id = addedTask.getID();
					boolean deleteSuccess = donStorage.removeTask(id);
					if (deleteSuccess) {
						changesReversed++;
					} else {
						response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
						response.addMessage(MSG_UNDO_NO_ACTIONS);
						return response;
					}
				}

			} else if (lastActionType == IDonCommand.CommandType.DELETE) {
				// Perform an add (reverse of Delete)
				for (IDonTask removedTask : lastAction.getAffectedTasks()) {
					int id = donStorage.addTask(removedTask);
					if (id != -1) {
						changesReversed++;
					}
				}
			} else if (lastActionType == IDonCommand.CommandType.EDIT_DATE
					|| lastActionType == IDonCommand.CommandType.EDIT_EVENT
					|| lastActionType == IDonCommand.CommandType.EDIT_ID_DATE
					|| lastActionType == IDonCommand.CommandType.EDIT_ID_EVENT
					|| lastActionType == IDonCommand.CommandType.EDIT_ID_NAME
					|| lastActionType == IDonCommand.CommandType.EDIT_NAME
					|| lastActionType == IDonCommand.CommandType.MARK
					|| lastActionType == IDonCommand.CommandType.MARK_ID) {
				// Replace the edited tasks with their previous properties
				for (IDonTask editedTask : lastAction.getAffectedTasks()) {
					int id = editedTask.getID();
					IDonResponse searchResponse = findTask(id);
					searchResponse.getTasks().get(0)
							.copyTaskDetails(editedTask);
					changesReversed++;
				}
			} else {
				response.setResponseType(IDonResponse.ResponseType.UNDO_FAILURE);
				response.addMessage(MSG_UNDO_NO_ACTIONS);
				return response;
			}

			response.setResponseType(IDonResponse.ResponseType.UNDO_SUCCESS);
			response.addMessage(String.format(MSG_UNDO_SUCCESS,
					changesReversed));
		}
		return response;
	}

	/**
	 * Toggles the "done" status of the task with the given ID
	 * 
	 * @param id
	 *            the id of the task to change
	 * @return the response
	 */
	private IDonResponse toggleStatus(int id) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			IDonTask unchangedTask = task.clone();
			boolean taskCompleted = !task.getStatus();
			task.setStatus(taskCompleted);

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_TOGGLE_STATUS_ID_SUCCESS, id,
					(taskCompleted ? PHRASE_COMPLETE : PHRASE_INCOMPLETE)));

			// Add edit action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(unchangedTask);
			actionHistory.push(new DonAction(IDonCommand.CommandType.MARK_ID,
					affectedTasks));
		}
		return response;
	}

	/**
	 * Toggles the "done" status of the task containing the given title
	 * 
	 * @param title
	 *            the title of the task to change
	 * @return the response
	 */
	private IDonResponse toggleStatus(String title) {
		IDonResponse response = new DonResponse();

		IDonResponse searchResponse = findTask(title);

		if (searchResponse.getTasks().size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					title));
			response.copyTasks(searchResponse);
		} else if (!searchResponse.hasTasks()) {
			// No task with the name found, return the response of the search
			response = searchResponse;
		} else {
			// 1 task was found
			IDonTask task = searchResponse.getTasks().get(0);
			response = toggleStatus(task.getID());
		}

		return response;
	}
	
	/**
	 * Find free time in the user's schedule based on existing task and events.
	 * For dates with no time stated, it is assumed that the user means that the whole
	 * day is taken up.
	 * @return
	 */
	private IDonResponse findFreeTime() {
		IDonResponse response = new DonResponse();
		//Get all tasks with deadlines or events that end after today
		List<IDonTask> taskList = getTaskByType(IDonTask.TaskType.DEADLINE, false, false);
		taskList.addAll(getTaskByType(IDonTask.TaskType.DURATION, false, false));
		Collections.sort(taskList);
		
		if(taskList.size()<=0) {
			response.addMessage(MSG_FREE_EVERYWHERE);
			return response;
		}
		
		//TODO handle the case where there are no tasks
		//Find free period between now and the start time of the earliest task if possible
		Calendar now = Calendar.getInstance();
		if(taskList.get(0).getStartDate().after(now)) {
			DonPeriod free = new DonPeriod(now, taskList.get(0).getStartDate());
			response.addPeriod(free);
		}
		
		for(int i=0; i<taskList.size()-1; i++) {
			IDonTask currentTask = taskList.get(i);
			IDonTask nextTask = taskList.get(i+1);
			//Check if there is a free period between the end time
			//and the start time of the next event
			if(currentTask.getType()==IDonTask.TaskType.DEADLINE) {
				//A deadline task has no end date
				//If the user did not specify a time in the deadline
				//0000hr on the given day will be used as the deadline
				if(currentTask.getStartDate().compareTo(nextTask.getStartDate())<0) {
					//There is a free period
					DonPeriod free = new DonPeriod(currentTask.getStartDate(), nextTask.getStartDate());
					response.addPeriod(free);
				}
			} else {
				if(currentTask.getEndDate().compareTo(nextTask.getStartDate())<0) {
					//There is a free period
					DonPeriod free = new DonPeriod(currentTask.getEndDate(), nextTask.getStartDate());
					response.addPeriod(free);
				}
			}
		}
		
		return response;
	}
	
	/**
	 * Returns a list of tasks by the given task type
	 * @param type	the type of the task
	 * @param allowOverdue true if overdue tasks are allowed.
	 * @param allowFinished true if completed tasks are allowed
	 * @return	the list of tasks
	 */
	private List<IDonTask> getTaskByType(IDonTask.TaskType type, boolean allowOverdue, boolean allowFinished) {
		List<IDonTask> taskList = donStorage.getTaskList();
		List<IDonTask> resultList = new ArrayList<IDonTask>();
		Calendar now = Calendar.getInstance();
		for(IDonTask task : taskList) {
			if(task.getType()==type) {
				if (type==IDonTask.TaskType.DEADLINE) {
					if((!allowOverdue && task.getStartDate().before(now))
							|| (!allowFinished && task.getStatus())) {
						//is overdue or finished
						continue;
					}
				} else if (type==IDonTask.TaskType.DURATION) {
					if((!allowOverdue && task.getEndDate().before(now))
							|| (!allowFinished && task.getStatus())) {
						//is overdue or finished
						continue;
					}
				}
				//Clone the task to prevent the original from being edited.
				resultList.add(task.clone());
			}
		}
		return resultList;
	}

	/**
	 * Show the user help information
	 * 
	 * @return response containing help messages
	 */
	private IDonResponse getHelp(String command) {
		// TODO: decide the format of the help
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.HELP);
		IDonCommand pCommand = donParser.parseCommand(command);
		if (command.equals("")) {
			// Give info on all commands available
			response.addMessage("Welcome to DoOrNot. These are the available commands:");
			response.addMessage("add / a, edit / ed / e, search / s, del / d, mark / m");
			response.addMessage("Type help command name to learn how to use the command!");
		} else if (pCommand.getGeneralType() == IDonCommand.GeneralCommandType.ADD) {
			// Help for add
			response.addMessage("add / a: Adds a task to the todo list");
			response.addMessage("Command format: add \"Task title\"");
			response.addMessage("Command format: add \"Task title\" @ DDMMYYYY_HHmm");
			response.addMessage("Command format: add \"Task title\" from DDMMYYYY_HHmm to DDMMYYYY_HHmm");
			response.addMessage("All dates can either be with time (DDMMYYYY_HHmm) or without (DDMMYYYY)");
			response.addMessage("Examples:");
			response.addMessage("add \"Finish reading Book X\" <-- Adds a floating task");
			response.addMessage("add \"Submit CS9842 assignment\" @ 18112014 <-- Adds a task with a deadline at 18th of November 2014");
			response.addMessage("add \"Talk by person\" from 05082015_1500 to 05082015_1800 <-- Adds an event that lasts from 3pm of 5th August 2015 to 6pm of the same day");
		} else if (pCommand.getGeneralType() == IDonCommand.GeneralCommandType.EDIT) {
			// Help for edit
			response.addMessage("edit / ed / e: Edits a task in the todo list");
			response.addMessage("Command format: edit Task_id to \"New task title\"");
			response.addMessage("Command format: edit \"Part of old Task title\" to \"New task title\"");
			response.addMessage("Command format: edit Task_id to DDMMYYYY_HHmm");
			response.addMessage("Command format: edit \"Part of old Task title\" to DDMMYYYY_HHmm");
			response.addMessage("Command format: edit Task_id to from DDMMYYYY_HHmm to DDMMYYYY_HHmm");
			response.addMessage("Command format: edit \"Part of old Task title\" to from DDMMYYYY_HHmm to DDMMYYYY_HHmm");
			response.addMessage("If multiple tasks are found with the given title, nothing will be edited.");
			response.addMessage("All dates can either be with time (DDMMYYYY_HHmm) or without (DDMMYYYY)");
			response.addMessage("Examples:");
			response.addMessage("edit 22 to \"Do work\" <-- Changes task 22's title to Do work");
			response.addMessage("edit \"Do work\" to 17012015 <-- Changes the deadline of the task containing \"Do work\" as the title to 17th January 2015");
			response.addMessage("edit 14 to from 02052015 to 03052015 <-- Changes the start and end dates of task 14 to 2nd and 3rd of May 2015 respectively");
		} else if (pCommand.getGeneralType() == IDonCommand.GeneralCommandType.DELETE) {
			// Help for delete
			response.addMessage("del / d: Delete a task in the todo list");
			response.addMessage("Command format: del Task_id");
			response.addMessage("Command format: del \"Part of Task title\"");
			response.addMessage("If multiple tasks are found with the given title, nothing will be deleted.");
			response.addMessage("Examples:");
			response.addMessage("del 22 <-- Deletes task 22");
			response.addMessage("del \"Do work\" <-- Deletes the task containing \"Do work\" in the title");
		} else if (pCommand.getGeneralType() == IDonCommand.GeneralCommandType.SEARCH) {
			// Help for search
			response.addMessage("search / s: Finds a task with the given ID, title or date");
			response.addMessage("Command format: search Task_id");
			response.addMessage("Command format: search \"Part of Task title\"");
			response.addMessage("Command format: search 22012016");
			response.addMessage("All dates can either be with time (DDMMYYYY_HHmm) or without (DDMMYYYY)");
			response.addMessage("Examples:");
			response.addMessage("search 22 <-- Searches for task 22");
			response.addMessage("search \"Do work\" <-- Searches for tasks containing \"Do work\" in the title");
			response.addMessage("search 22012016 <-- Searches for tasks starting or occurring on the 22nd of January 2016");
		} else if (pCommand.getGeneralType() == IDonCommand.GeneralCommandType.UNDO) {
			// Help for undo
			response.addMessage("undo : Undoes the previous action");
			response.addMessage("Command format: undo");
			response.addMessage("Examples:");
			response.addMessage("undo");
			response.addMessage("(What were you expecting?)");

		}

		return response;
	}

	/****
	 * Date helper methods
	 ****/

	/**
	 * Determines if date is the same as or after the base date.
	 * 
	 * @param date
	 *            the date to compare
	 * @param baseDate
	 *            the base date to check against
	 * @return true if date is >= baseDate
	 */
	private boolean dateEqualOrAfter(Calendar date, Calendar baseDate) {
		if (date.after(baseDate) || date.equals(baseDate)) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if date is the same as or before the base date.
	 * 
	 * @param date
	 *            the date to compare
	 * @param baseDate
	 *            the base date to check against
	 * @return true if date is <= baseDate
	 */
	private boolean dateEqualOrBefore(Calendar date, Calendar baseDate) {
		if (date.before(baseDate) || date.equals(baseDate)) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if date is on the same day as baseDate
	 * 
	 * @param date
	 *            the date to compare
	 * @param baseDate
	 *            the base date to check against
	 * @return true if date is on the same DAY as baseDate
	 */
	private boolean isSameDay(Calendar date, Calendar baseDate) {
		if (date.get(Calendar.DATE) == baseDate.get(Calendar.DATE)
				&& date.get(Calendar.MONTH) == baseDate.get(Calendar.MONTH)
				&& date.get(Calendar.YEAR) == baseDate.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if date is between minDate and maxDate
	 * 
	 * @param date
	 *            the date to check
	 * @param minDate
	 *            the earlier date
	 * @param maxDate
	 *            the later date
	 * @return true if date is between minDate and maxDate
	 */
	private boolean isBetweenDates(Calendar date, Calendar minDate,
			Calendar maxDate) {
		if (dateEqualOrAfter(date, minDate) && dateEqualOrBefore(date, maxDate)) {
			return true;
		}
		return false;
	}

	/**
	 * Keeps track of an action performed the user for use with the undo command
	 */
	private class DonAction {
		private IDonCommand.CommandType actionType;
		private List<IDonTask> affectedTasks;

		public DonAction(IDonCommand.CommandType type, List<IDonTask> tasks) {
			actionType = type;
			affectedTasks = tasks;
		}

		public IDonCommand.CommandType getActionType() {
			return actionType;
		}

		public List<IDonTask> getAffectedTasks() {
			return affectedTasks;
		}

	}

	@Override
	public List<IDonTask> getTaskList() {
		return donStorage.getTaskList();
	}

}

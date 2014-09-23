package doornot.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import doornot.logic.IDonResponse.ResponseType;
import doornot.logic.IDonTask.TaskType;
import doornot.parser.DonParser;
import doornot.parser.IDonCommand;
import doornot.parser.IDonParser;
import doornot.DonStorageTMP;
import doornot.storage.IDonStorage;

/**
 * DonLogic - Class for handling the logic of the program
 * (creation/deletion/modification of tasks)
 * 
 */
//@author A0111995Y
public class DonLogic implements IDonLogic {

	private static final String MSG_SAVE_SUCCESSFUL = "Save successful.";
	private static final String MSG_SAVE_FAILED = "Save failed.";
	private static final String MSG_ADD_TASK_FAILURE = "Could not add task '%1$s'";
	private static final String MSG_ADD_FLOATING_TASK_SUCCESS = "'%1$s' has been added.";
	private static final String MSG_SEARCH_ID_FAILED = "No task with ID of %1$d was found.";
	private static final String MSG_SEARCH_TITLE_FAILED = "No task with a title containing '%1$s' was found.";
	private static final String MSG_DELETE_SUCCESS = "The above task was deleted successfully.";
	private static final String MSG_DELETE_FAILED = "The above task could not be deleted.";
	private static final String MSG_EDIT_TITLE_SUCCESS = "Task name changed from '%1$s' to '%2$s'.";
	private static final String MSG_EDIT_SINGLE_DATE_SUCCESS = "%1$s changed from %2$s to %3$s.";
	private static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	private static final String MSG_UNDO_ADD_SUCCESS = "Last action undone. %1$d addition(s) removed.";
	private static final String MSG_TOGGLE_STATUS_ID_SUCCESS = "Task %1$d has been set to '%2$s'";
	private static final String MSG_SEARCH_MORE_THAN_ONE_TASK = "'%1$s' returned more than 1 result. Please specify with the ID.";

	private static final String MSG_EX_NO_RANGE_GIVEN = "Task range was not specified";

	private static final String PHRASE_COMPLETE = "complete";
	private static final String PHRASE_INCOMPLETE = "incomplete";
	private static final String PHRASE_END_DATE = "End date";
	private static final String PHRASE_START_DATE = "Start date";

	private static final int FAILURE = -1;

	private IDonStorage donStorage;
	private IDonParser donParser;

	private Stack<DonAction> actionHistory;

	public DonLogic() {
		donStorage = new DonStorageTMP();
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

		} else if (commandType == IDonCommand.CommandType.DELETE_ID) {
			response = deleteTask(dCommand.getID());

		} else if (commandType == IDonCommand.CommandType.EDIT_ID_NAME) {
			response = editTask(dCommand.getID(), dCommand.getNewName());

		} else if (commandType == IDonCommand.CommandType.EDIT_ID_DATE) {
			// TODO: recognize different single date edit type
			response = editTask(dCommand.getID(), true,
					dCommand.getNewDeadline());

		} else if (commandType == IDonCommand.CommandType.EDIT_ID_EVENT) {
			response = editTask(dCommand.getID(), dCommand.getNewStartDate(),
					dCommand.getNewEndDate());

		} else if (commandType == IDonCommand.CommandType.MARK_ID) {
			response = toggleStatus(dCommand.getID());

		} else if (commandType == IDonCommand.CommandType.UNDO) {
			response = undoLastAction();
		}

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
		List<IDonTask> taskList = donStorage.getTaskList(TaskType.FLOATING);
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
	 * @return the response containing the tasks
	 */
	private IDonResponse findTaskRange(Calendar startDate, Calendar endDate) {
		DonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList(TaskType.FLOATING);
		if (startDate == null && endDate == null) {
			throw new IllegalArgumentException(MSG_EX_NO_RANGE_GIVEN);
		}
		for (IDonTask task : taskList) {
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
	 * Deletes the task with the given title.
	 * If more than 1 task is found, the search results will be returned and nothing will be deleted.
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
	 * Change the title of the task with a given title to the new title. The task
	 * name being searched for must belong to only one task
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
	 * to the new title. The task name being searched for must belong to only one
	 * task
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
					IDonResponse temporaryResponse = deleteTask(id);
					if (temporaryResponse.getResponseType() == IDonResponse.ResponseType.DEL_SUCCESS) {
						changesReversed++;
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
			}

			response.setResponseType(IDonResponse.ResponseType.UNDO_SUCCESS);
			response.addMessage(String.format(MSG_UNDO_ADD_SUCCESS,
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
	 * Show the user help information
	 * 
	 * @return response containing help messages
	 */
	private IDonResponse getHelp(String command) {
		// TODO: decide the format of the help
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.HELP);
		response.addMessage("SOME MESSAGE HERE");
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
	 * Keeps track of an action performed the user for use with the undo command
	 */
	private class DonAction {
		private IDonCommand.CommandType actionType;
		private List<IDonTask> affectedTasks;

		public DonAction(IDonCommand.CommandType type, List<IDonTask> tasks) {
			actionType = type;
			tasks = affectedTasks;
		}

		public IDonCommand.CommandType getActionType() {
			return actionType;
		}

		public List<IDonTask> getAffectedTasks() {
			return affectedTasks;
		}

	}

}

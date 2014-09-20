package doornot.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import doornot.DonCommand;
import doornot.DonParser;
import doornot.DonStorageTMP;
import doornot.storage.IDonStorage;

/**
 * DonLogic - Class for handling the logic of the program (creation/deletion of
 * tasks)
 * 
 * @author cs2103aug2014-w11-2j
 * 
 */
public class DonLogic implements IDonLogic {

	private static final String MSG_ADD_TASK_FAILURE = "Could not add task '%1$s'";
	private static final String MSG_ADD_FLOATING_TASK_SUCCESS = "'%1$s' has been added.";
	private static final String MSG_SEARCH_ID_FAILED = "No task with ID of %1$d was found.";
	private static final String MSG_DELETE_SUCCESS = "The above task was deleted successfully.";
	private static final String MSG_DELETE_FAILED = "The above task could not be deleted.";
	private static final String MSG_EDIT_TITLE_SUCCESS = "Task name changed from '%1$s' to '%2$s'.";
	private static final String MSG_UNDO_NO_ACTIONS = "There are no actions to undo!";
	private static final String MSG_UNDO_ADD_SUCCESS = "Last action undone. %1$d addition(s) removed.";

	private static final int FAILURE = -1;

	private IDonStorage donStorage;

	private Stack<DonAction> actionHistory;

	public DonLogic() {
		donStorage = new DonStorageTMP();
		actionHistory = new Stack<DonAction>();
	}

	@Override
	public IDonResponse runCommand(String command) {
		DonCommand dCommand = DonParser.parseCommand(command);
		DonResponse response = null;
		if (dCommand.getType() == DonCommand.Command.ADD) {

		} else if (dCommand.getType() == DonCommand.Command.SEARCH) {

		}

		return response;
	}

	@Override
	public IDonResponse saveToDrive() {
		// TODO Auto-generated method stub
		return null;
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
			actionHistory.push(new DonAction(DonCommand.Command.ADD,
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
			actionHistory.push(new DonAction(DonCommand.Command.ADD,
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
			actionHistory.push(new DonAction(DonCommand.Command.ADD,
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
		DonResponse response = new DonResponse();
		//TODO: Undone
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
				actionHistory.push(new DonAction(DonCommand.Command.DELETE,
						affectedTasks));
			} else {
				response.setResponseType(IDonResponse.ResponseType.DEL_FAILURE);
				response.addMessage(MSG_DELETE_FAILED);
			}
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
			actionHistory.push(new DonAction(DonCommand.Command.EDIT,
					affectedTasks));
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
			if (lastAction.getActionType() == DonCommand.Command.ADD) {
				// Perform a delete (reverse of Add)
				for (IDonTask addedTask : lastAction.getAffectedTasks()) {
					int id = addedTask.getID();
					IDonResponse temporaryResponse = deleteTask(id);
					if (temporaryResponse.getResponseType() == IDonResponse.ResponseType.DEL_SUCCESS) {
						changesReversed++;
					}
				}

			} else if (lastAction.getActionType() == DonCommand.Command.DELETE) {
				// Perform an add (reverse of Delete)
				for (IDonTask removedTask : lastAction.getAffectedTasks()) {
					int id = donStorage.addTask(removedTask);
					if (id != -1) {
						changesReversed++;
					}
				}
			} else if (lastAction.getActionType() == DonCommand.Command.EDIT) {
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
	 * Keeps track of an action performed the user for use with the undo command
	 */
	private class DonAction {
		private DonCommand.Command actionType;
		private List<IDonTask> affectedTasks;

		public DonAction(DonCommand.Command type, List<IDonTask> tasks) {
			actionType = type;
			tasks = affectedTasks;
		}

		public DonCommand.Command getActionType() {
			return actionType;
		}

		public List<IDonTask> getAffectedTasks() {
			return affectedTasks;
		}

	}

}

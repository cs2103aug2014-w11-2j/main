package doornot.logic;

import java.util.Calendar;

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
	
	private static final int FAILURE = -1;

	private IDonStorage donStorage;

	public DonLogic() {
		donStorage = new DonStorageTMP();
	}

	@Override
	public IDonResponse runCommand(String command) {
		DonCommand dCommand = DonParser.parseCommand(command);
		DonResponse response = null;
		if (dCommand.getType() == DonCommand.Command.ADD) {

		} else if(dCommand.getType() == DonCommand.Command.SEARCH) {
			
		}

		return response;
	}

	@Override
	public IDonResponse saveToDrive() {
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
		DonTask task = new DonTask(title,
				donStorage.getNextID(IDonTask.TaskType.FLOATING));
		int addResult = donStorage.addTask(task, IDonTask.TaskType.FLOATING);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, title));
		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					title));
			response.addTask(task);
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
		DonTask task = new DonTask(title, deadline,
				donStorage.getNextID(IDonTask.TaskType.DEADLINE));
		int addResult = donStorage.addTask(task, IDonTask.TaskType.DEADLINE);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, title));
		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					title));
			response.addTask(task);
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
		DonTask task = new DonTask(title, startDate, endDate,
				donStorage.getNextID(IDonTask.TaskType.DURATION));
		int addResult = donStorage.addTask(task, IDonTask.TaskType.DURATION);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, title));
		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					title));
			response.addTask(task);
		}
		return response;
	}
	
	/**
	 * Find tasks with the given name
	 * @param	name the name to search for
	 * @return	the response containing the tasks
	 */
	private IDonResponse findTask(String name) {
		DonResponse response = new DonResponse();
		
		return response;
	}
	
	/**
	 * Find tasks given the ID
	 * @param	id	the id to search for
	 * @return	the response containing the tasks
	 */
	private IDonResponse findTask(int id) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if(task==null) {
			//No task with given ID found
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
	 * @param	id	the id of the task to delete
	 * @return	the response containing the deletion status
	 */
	private IDonResponse deleteTask(int id) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(id);
		if(task==null) {
			//No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, id));
		} else {
			boolean deleteStatus = donStorage.removeTask(id);
			if(deleteStatus) {
				//Deleted
				response.setResponseType(IDonResponse.ResponseType.DEL_SUCCESS);
				response.addMessage(MSG_DELETE_SUCCESS);
				response.addTask(task);
			} else {
				response.setResponseType(IDonResponse.ResponseType.DEL_FAILURE);
				response.addMessage(MSG_DELETE_FAILED);
			}
		}
		return response;
	}

}

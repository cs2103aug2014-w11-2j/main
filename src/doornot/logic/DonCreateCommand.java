package doornot.logic;

import java.util.ArrayList;
import java.util.Calendar;


import doornot.storage.DonTask;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

public class DonCreateCommand extends AbstractDonCommand {
	
	enum AddType {
		FLOATING, DEADLINE, EVENT
	}
	
	
	private static final int FAILURE = -1;
	
	private AddType type;
	private String taskTitle;
	private Calendar startDate, endDate;
	private boolean timeUsed = false;
	private IDonTask createdTask = null; //To be used only after command has been executed.
	
	public DonCreateCommand(String title) {
		type = AddType.FLOATING;
		taskTitle = title;
	}

	public DonCreateCommand(String title, Calendar deadline, boolean timeUsed) {
		type = AddType.DEADLINE;
		taskTitle = title;
		startDate = deadline;
		this.timeUsed = timeUsed;
	}
	
	public DonCreateCommand(String title, Calendar startDate, Calendar endDate, boolean timeUsed) {
		type = AddType.EVENT;
		taskTitle = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.timeUsed = timeUsed;
	}
	
	/**
	 * Creates a floating task
	 * 
	 * @param donStorage the storage to add tasks to
	 * @return the response
	 */
	private IDonResponse createFloatingTask(IDonStorage donStorage) {
		assert taskTitle != null;
		IDonTask task = new DonTask(taskTitle, donStorage.getNextID());
		int addResult = donStorage.addTask(task);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, taskTitle));

		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					taskTitle));
			response.addTask(task);
			// Add add action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(task.clone());
			createdTask = task.clone();
		}
		return response;
	}

	/**
	 * Creates a deadline task
	 * 
	 * @param donStorage the storage to add tasks to
	 * @return the response
	 */
	private IDonResponse createDeadlineTask(IDonStorage donStorage) {
		assert taskTitle != null && startDate != null; // This method should only be
													// called when both
													// parameters are present
		IDonTask task = new DonTask(taskTitle, startDate, donStorage.getNextID());
		task.setTimeUsed(timeUsed);
		int addResult = donStorage.addTask(task);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, taskTitle));

		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					taskTitle));

			// Add add action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(task.clone());

		}
		return response;
	}

	/**
	 * Creates a task with a duration
	 * 
	 * @param donStorage the storage to add tasks to
	 * @return the response
	 */
	private IDonResponse createEventTask(IDonStorage donStorage) {
		assert taskTitle != null && startDate != null && endDate!=null;
		IDonTask task = new DonTask(taskTitle, startDate, endDate,
				donStorage.getNextID());
		task.setTimeUsed(timeUsed);
		int addResult = donStorage.addTask(task);

		DonResponse response = new DonResponse();
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, taskTitle));

		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					taskTitle));
			response.addTask(task);

			// Add add action to history
			ArrayList<IDonTask> affectedTasks = new ArrayList<IDonTask>();
			affectedTasks.add(task.clone());

		}
		return response;
	}
	
	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = null;
		if (type == AddType.FLOATING) {
			response = createFloatingTask(donStorage);
		} else if (type == AddType.DEADLINE) {
			response = createDeadlineTask(donStorage);
		} else if (type == AddType.EVENT) {
			response = createEventTask(donStorage);
		}
		
		executed = true;
		return response;
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		//Perform a delete
		if(!executed) {
			//Cannot be run until executeCommand has been called.
			return null;
		}
		
		boolean deleteSuccess = donStorage.removeTask(createdTask.getID());
		IDonResponse response = null;
		if(deleteSuccess) {
			response = createUndoSuccessResponse();
			executed = false;
		} else {
			response = createUndoFailureResponse();
		}
		
		return response;
	}


}

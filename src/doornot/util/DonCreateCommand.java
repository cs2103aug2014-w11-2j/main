package doornot.util;

import java.util.Calendar;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.DonTask;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

public class DonCreateCommand extends AbstractDonCommand {
	
	public enum AddType {
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
		generalCommandType = GeneralCommandType.ADD;
	}

	public DonCreateCommand(String title, Calendar deadline, boolean timeUsed) {
		type = AddType.DEADLINE;
		taskTitle = title;
		startDate = deadline;
		this.timeUsed = timeUsed;
		generalCommandType = GeneralCommandType.ADD;
	}
	
	public DonCreateCommand(String title, Calendar startDate, Calendar endDate, boolean timeUsed) {
		type = AddType.EVENT;
		taskTitle = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.timeUsed = timeUsed;
		generalCommandType = GeneralCommandType.ADD;
	}
	
	public String getTaskTitle() {
		return taskTitle;
	}
	
	public Calendar getStartDate() {
		return startDate;
	}
	
	public Calendar getEndDate() {
		return endDate;
	}
	
	public AddType getType() {
		return type;
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
			if (SearchHelper.findTaskByExactName(donStorage, taskTitle).size()>0) {
				response.addMessage(String.format(MSG_NAME_CONFLICT, taskTitle));
			}
			response.addTask(task);
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
			if (SearchHelper.findTaskByDate(donStorage, startDate).size()>0) {
				response.addMessage(MSG_DEADLINE_CONFLICT);
			} else if (SearchHelper.findTaskByExactName(donStorage, taskTitle).size()>0) {
				response.addMessage(String.format(MSG_NAME_CONFLICT, taskTitle));
			}
			response.addTask(task);
			createdTask = task.clone();
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
			if (SearchHelper.findTaskRange(donStorage, startDate, endDate, FIND_INCOMPLETE).size()>0) {
				response.addMessage(MSG_DEADLINE_CONFLICT);
			} else if (SearchHelper.findTaskByExactName(donStorage, taskTitle).size()>0) {
				response.addMessage(String.format(MSG_NAME_CONFLICT, taskTitle));
			}
			response.addTask(task);
			createdTask = task.clone();
		}
		return response;
	}
	
	private IDonResponse recreateTask(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		int taskID = donStorage.addTask(createdTask);
		if(taskID != -1) {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addTask(createdTask);
		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
		}
		return response;
	}
	
	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		assert !executed;
		IDonResponse response = null;
		if (createdTask != null) {
			//The task has previously been created. Execute is being run in the context
			//of a redo. Simply readd the createdTask
			response = recreateTask(donStorage);
		} else if (type == AddType.FLOATING) {
			response = createFloatingTask(donStorage);
		} else if (type == AddType.DEADLINE) {
			response = createDeadlineTask(donStorage);
		} else if (type == AddType.EVENT) {
			response = createEventTask(donStorage);
		}
		
		if (response.getResponseType() == ResponseType.ADD_SUCCESS) {
			executed = true;
		}
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
			response = createUndoSuccessResponse(1);
			executed = false;
		} else {
			response = createUndoFailureResponse();
		}
		
		return response;
	}


}

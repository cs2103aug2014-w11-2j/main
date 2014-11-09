package doornot.util;

import java.util.Calendar;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.DonTask;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

//@author A0111995Y
public class DonCreateCommand extends AbstractDonCommand {

	public enum AddType {
		FLOATING, DEADLINE, EVENT
	}

	private static final int FAILURE = -1;

	private AddType type;
	private String taskTitle;
	private Calendar startDate, endDate;
	private boolean timeUsed = false;
	private IDonTask createdTask = null; // To be used only after command has
											// been executed.

	/**
	 * Creates a CreateCommand that adds a floating task
	 * 
	 * @param title
	 *            the title of the new task
	 */
	public DonCreateCommand(String title) {
		type = AddType.FLOATING;
		taskTitle = title;
		generalCommandType = GeneralCommandType.ADD;
	}

	/**
	 * Creates a CreateCommand that adds a deadline task
	 * 
	 * @param title
	 *            the title of the new task
	 * @param deadline
	 *            the deadline of the task
	 * @param timeUsed
	 *            whether a time is specified
	 */
	public DonCreateCommand(String title, Calendar deadline, boolean timeUsed) {
		type = AddType.DEADLINE;
		taskTitle = title;
		startDate = deadline;
		this.timeUsed = timeUsed;
		generalCommandType = GeneralCommandType.ADD;
	}

	/**
	 * Creates a CreateCommand that adds an event task
	 * 
	 * @param title
	 *            the title of the new task
	 * @param startDate
	 *            the start date of the task
	 * @param endDate
	 *            the end date of a task
	 * @param timeUsed
	 *            whether a time is specified
	 */
	public DonCreateCommand(String title, Calendar startDate, Calendar endDate,
			boolean timeUsed) {
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
	 * @param donStorage
	 *            the storage to add tasks to
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
			if (SearchHelper.findTaskByExactName(donStorage, taskTitle).size() > 1) {
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
	 * @param donStorage
	 *            the storage to add tasks to
	 * @return the response
	 */
	private IDonResponse createDeadlineTask(IDonStorage donStorage) {
		assert taskTitle != null && startDate != null; // This method should
														// only be
														// called when both
														// parameters are
														// present
		IDonTask task = new DonTask(taskTitle, startDate,
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
			if (SearchHelper.findTaskByDate(donStorage, startDate, true).size() > 1) {
				response.addMessage(MSG_DEADLINE_CONFLICT);
			} else if (SearchHelper.findTaskByExactName(donStorage, taskTitle)
					.size() > 1) {
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
	 * @param donStorage
	 *            the storage to add tasks to
	 * @return the response
	 */
	private IDonResponse createEventTask(IDonStorage donStorage) {
		assert taskTitle != null && startDate != null && endDate != null;
		IDonResponse response = new DonResponse();
		if (CalHelper.dateEqualOrBefore(endDate, startDate)) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(MSG_COMMAND_WRONG_DATE);
			return response;
		}
		IDonTask task = new DonTask(taskTitle, startDate, endDate,
				donStorage.getNextID());
		task.setTimeUsed(timeUsed);
		int addResult = donStorage.addTask(task);

		
		if (addResult == FAILURE) {
			response.setResponseType(IDonResponse.ResponseType.ADD_FAILURE);
			response.addMessage(String.format(MSG_ADD_TASK_FAILURE, taskTitle));

		} else {
			response.setResponseType(IDonResponse.ResponseType.ADD_SUCCESS);
			response.addMessage(String.format(MSG_ADD_FLOATING_TASK_SUCCESS,
					taskTitle));
			if (SearchHelper.findTaskRange(donStorage, startDate, endDate,
					SearchHelper.FIND_INCOMPLETE).size() > 1) {
				response.addMessage(MSG_DEADLINE_CONFLICT);
			} else if (SearchHelper.findTaskByExactName(donStorage, taskTitle)
					.size() > 1) {
				response.addMessage(String.format(MSG_NAME_CONFLICT, taskTitle));
			}
			response.addTask(task);
			createdTask = task.clone();
		}
		return response;
	}

	/**
	 * Recreates the task after it has been added and removed through
	 * executeCommand and undoCommand
	 * 
	 * @param donStorage
	 *            the storage to add tasks to
	 * @return the response after re-adding the task
	 */
	private IDonResponse recreateTask(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		int taskID = donStorage.addTask(createdTask);
		if (taskID != -1) {
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
			// The task has previously been created. Execute is being run in the
			// context
			// of a redo. Simply readd the createdTask
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
		// Perform a delete
		if (!executed) {
			// Cannot be run until executeCommand has been called.
			return null;
		}

		boolean deleteSuccess = donStorage.removeTask(createdTask.getID());
		IDonResponse response = null;
		if (deleteSuccess) {
			response = new DonResponse();
			response = createUndoSuccessResponse(1);
			response.addTask(createdTask);
			response.setResponseType(ResponseType.DEL_SUCCESS);
			executed = false;
		} else {
			response = createUndoFailureResponse();
		}

		return response;
	}

}

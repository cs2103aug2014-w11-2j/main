package doornot.util;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

//@author A0111995Y
public class DonEditCommand extends AbstractDonCommand {

	public enum EditType {
		ID_NAME, ID_DATE, ID_EVENT, NAME_NAME, NAME_DATE, NAME_EVENT,
	}

	private EditType type;

	protected int searchID;
	protected String searchTitle;
	private String newTitle;
	private Calendar newDeadline, newStartDate, newEndDate;
	private boolean isTimeUsed, isStartDate = true;
	protected List<IDonTask> unchangedTask = new ArrayList<IDonTask>();

	protected DonEditCommand() {
		generalCommandType = GeneralCommandType.EDIT;
	}

	/**
	 * Edit title by ID
	 * 
	 * @param id
	 *            the id of the task
	 * @param newTitle
	 *            the new title
	 */
	public DonEditCommand(int id, String newTitle) {
		type = EditType.ID_NAME;
		searchID = id;
		this.newTitle = newTitle;
		generalCommandType = GeneralCommandType.EDIT;
	}

	/**
	 * Edit title by searching for the title
	 * 
	 * @param title
	 *            the title of the task to edit
	 * @param newTitle
	 *            the new title
	 */
	public DonEditCommand(String title, String newTitle) {
		type = EditType.NAME_NAME;
		searchTitle = title;
		this.newTitle = newTitle;
		generalCommandType = GeneralCommandType.EDIT;
	}

	/**
	 * Edit the deadline of the task by id
	 * 
	 * @param id
	 *            the id of the task
	 * @param newDeadline
	 *            the new deadline
	 */
	public DonEditCommand(int id, Calendar newDeadline, boolean isTimeUsed) {
		type = EditType.ID_DATE;
		searchID = id;
		this.newDeadline = newDeadline;
		this.isTimeUsed = isTimeUsed;
		generalCommandType = GeneralCommandType.EDIT;
	}

	/**
	 * Edit the deadline of the task by task name
	 * 
	 * @param title
	 *            the title of the task to edit
	 * @param newDeadline
	 *            the new deadline
	 */
	public DonEditCommand(String title, Calendar newDeadline, boolean isTimeUsed) {
		type = EditType.NAME_DATE;
		searchTitle = title;
		this.newDeadline = newDeadline;
		this.isTimeUsed = isTimeUsed;
		generalCommandType = GeneralCommandType.EDIT;
	}

	/**
	 * Edit the start and end date of the task by ID
	 * 
	 * @param id
	 *            the id of the task
	 * @param newStartDate
	 *            the new start date
	 * @param newEndDate
	 *            the new end date
	 */
	public DonEditCommand(int id, Calendar newStartDate, Calendar newEndDate,
			boolean isTimeUsed) {
		type = EditType.ID_EVENT;
		searchID = id;
		this.newStartDate = newStartDate;
		this.newEndDate = newEndDate;
		this.isTimeUsed = isTimeUsed;
		generalCommandType = GeneralCommandType.EDIT;
	}

	/**
	 * Edit the start and end date of the task by name
	 * 
	 * @param title
	 *            the title of the task to edit
	 * @param newStartDate
	 *            the new start date
	 * @param newEndDate
	 *            the new end date
	 */
	public DonEditCommand(String title, Calendar newStartDate,
			Calendar newEndDate, boolean isTimeUsed) {
		type = EditType.NAME_EVENT;
		searchTitle = title;
		this.newStartDate = newStartDate;
		this.newEndDate = newEndDate;
		this.isTimeUsed = isTimeUsed;
		generalCommandType = GeneralCommandType.EDIT;
	}

	public String getNewTitle() {
		return newTitle;
	}

	public int getSearchID() {
		return searchID;
	}

	public String getSearchTitle() {
		return searchTitle;
	}

	public Calendar getNewDeadline() {
		return newDeadline;
	}

	public Calendar getNewStartDate() {
		return newStartDate;
	}

	public Calendar getNewEndDate() {
		return newEndDate;
	}

	public EditType getType() {
		return type;
	}

	/**
	 * Change the title of the task with ID id to the new title
	 * 
	 * @param donStorage
	 *            where the tasks are stored
	 * 
	 * @return the response
	 */
	private IDonResponse editTitleByID(IDonStorage donStorage) {
		assert newTitle != null;
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));
		} else {
			unchangedTask.add(task.clone()); // For undo to use
			String oldTitle = task.getTitle();
			task.setTitle(newTitle);
			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_EDIT_TITLE_SUCCESS, oldTitle,
					newTitle));

		}
		return response;
	}

	/**
	 * Change the title of the task with a given title to the new title. The
	 * task name being searched for must belong to only one task
	 * 
	 * @param donStorage
	 *            where the tasks are stored
	 * 
	 * @return the response
	 */
	private IDonResponse editTitleByTitle(IDonStorage donStorage) {
		assert newTitle != null && searchTitle != null;
		IDonResponse response = new DonResponse();
		List<IDonTask> foundTasks = donStorage.getTaskByName(searchTitle);

		if (foundTasks.size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String
					.format(MSG_MATCHING_RESULTS, searchTitle));
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					searchTitle));

			response.setTaskList(foundTasks);
		} else if (foundTasks.isEmpty()) {
			// No task with the name found, return the response of the search
			response = createSearchFailedResponse(searchTitle);
		} else {
			// 1 task was found
			IDonTask task = foundTasks.get(0);
			searchID = task.getID();
			response = editTitleByID(donStorage);
		}

		return response;
	}

	/**
	 * Change the deadline of the task with ID id to the new deadline (or start
	 * date/end date)
	 * 
	 * @param donStorage
	 *            where the tasks are stored
	 * 
	 * @return the success response
	 */
	private IDonResponse editDeadlineByID(IDonStorage donStorage) {
		assert newDeadline != null;
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));
		} else {
			unchangedTask.add(task.clone());
			Calendar oldDate = null;
			String dateType = "";
			if (task.getType() == IDonTask.TaskType.FLOATING) {
				// If a user edits the (non-existent) deadline of a floating
				// task, the deadline is added
				// This can be undone but no command exists to remove the
				// deadline to turn it back to a
				// floating task
				dateType = PHRASE_DEADLINE;
				task.setStartDate(newDeadline);
				task.setTimeUsed(isTimeUsed);
				response.addMessage(String.format(
						MSG_EDIT_SINGLE_DATE_ADD_SUCCESS, dateType, newDeadline
								.getTime().toString()));
			} else if (task.getType() == IDonTask.TaskType.DEADLINE) {
				dateType = PHRASE_DEADLINE;
				oldDate = task.getStartDate();
				task.setStartDate(newDeadline);
				task.setTimeUsed(isTimeUsed);
				response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
						dateType, oldDate.getTime().toString(), newDeadline
								.getTime().toString()));
			} else if (task.getType() == IDonTask.TaskType.DURATION) {
				if (isStartDate) {
					dateType = PHRASE_START_DATE;
					oldDate = task.getStartDate();
					task.setStartDate(newDeadline);
				} else {
					dateType = PHRASE_END_DATE;
					oldDate = task.getEndDate();
					task.setEndDate(newDeadline);
				}
				task.setTimeUsed(isTimeUsed);
				response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
						dateType, oldDate.getTime().toString(), newDeadline
								.getTime().toString()));
			}

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
		}
		return response;
	}

	/**
	 * Change the start date/end date or deadline of the task with a given title
	 * to the new title. The task name being searched for must belong to only
	 * one task
	 * 
	 * @param donStorage
	 *            where the tasks are stored
	 * 
	 * @return the response
	 */
	private IDonResponse editDeadlineByTitle(IDonStorage donStorage) {
		assert searchTitle != null && newDeadline != null;
		IDonResponse response = new DonResponse();

		List<IDonTask> foundTasks = donStorage.getTaskByName(searchTitle);

		if (foundTasks.size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String
					.format(MSG_MATCHING_RESULTS, searchTitle));
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					searchTitle));
			response.setTaskList(foundTasks);
		} else if (foundTasks.isEmpty()) {
			// No task with the name found, return the response of the search
			response = createSearchFailedResponse(searchTitle);
		} else {
			// 1 task was found
			IDonTask task = foundTasks.get(0);
			searchID = task.getID();
			response = editDeadlineByID(donStorage);
		}

		return response;
	}

	/**
	 * Change the start and end date of the task with ID id to the new dates
	 * 
	 * @param donStorage
	 *            where the tasks are stored
	 * 
	 * @return the success response
	 */
	private IDonResponse editStartEndByID(IDonStorage donStorage) {
		assert newStartDate != null && newEndDate != null;
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));
		} else {
			unchangedTask.add(task.clone());
			Calendar oldStartDate = null, oldEndDate = null;
			if (task.getType() == IDonTask.TaskType.FLOATING
					|| task.getType() == IDonTask.TaskType.DEADLINE) {
				// If a user edits the (non-existent) start+end date of a
				// floating/deadline task, they are added
				// This can be undone but no command exists to remove the dates
				// to turn it back to a
				// floating task
				task.setStartDate(newStartDate);
				task.setEndDate(newEndDate);
				task.setTimeUsed(isTimeUsed);
				response.addMessage(String.format(
						MSG_EDIT_SINGLE_DATE_ADD_SUCCESS, PHRASE_START_DATE,
						newStartDate.getTime().toString()));
				response.addMessage(String.format(
						MSG_EDIT_SINGLE_DATE_ADD_SUCCESS, PHRASE_END_DATE,
						newEndDate.getTime().toString()));
			} else if (task.getType() == IDonTask.TaskType.DURATION) {
				oldStartDate = task.getStartDate();
				task.setStartDate(newStartDate);

				oldEndDate = task.getEndDate();
				task.setEndDate(newEndDate);

				task.setTimeUsed(isTimeUsed);

				response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
						PHRASE_START_DATE, oldStartDate.getTime().toString(),
						newStartDate.getTime().toString()));
				response.addMessage(String.format(MSG_EDIT_SINGLE_DATE_SUCCESS,
						PHRASE_END_DATE, oldEndDate.getTime().toString(),
						newEndDate.getTime().toString()));
			}

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);

		}
		return response;
	}

	/**
	 * Change the start and end date of the task with a title containing the
	 * search string to the new dates. The title of the task must belong to only
	 * 1 task or the search results will be returned instead and no edits will
	 * be made.
	 * 
	 * @param donStorage
	 *            where the tasks are stored
	 * @return
	 */
	private IDonResponse editStartEndByTitle(IDonStorage donStorage) {
		assert newStartDate != null && newEndDate != null;
		IDonResponse response = new DonResponse();
		List<IDonTask> foundTasks = donStorage.getTaskByName(searchTitle);

		if (foundTasks.size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String
					.format(MSG_MATCHING_RESULTS, searchTitle));
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					searchTitle));
			response.setTaskList(foundTasks);
		} else if (foundTasks.isEmpty()) {
			// No task with the name found, return the response of the search
			response = createSearchFailedResponse(searchTitle);
		} else {
			// 1 task was found
			IDonTask task = foundTasks.get(0);
			searchID = task.getID();
			response = editStartEndByID(donStorage);
		}

		return response;
	}

	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = null;
		if (type == EditType.ID_NAME) {
			response = editTitleByID(donStorage);
		} else if (type == EditType.NAME_NAME) {
			response = editTitleByTitle(donStorage);
		} else if (type == EditType.ID_DATE) {
			response = editDeadlineByID(donStorage);
		} else if (type == EditType.NAME_DATE) {
			response = editDeadlineByTitle(donStorage);
		} else if (type == EditType.ID_EVENT) {
			response = editStartEndByID(donStorage);
		} else if (type == EditType.NAME_EVENT) {
			response = editStartEndByTitle(donStorage);
		}

		if (response.getResponseType() == ResponseType.EDIT_SUCCESS) {
			executed = true;
		}
		return response;
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		// Replace the affected task with the old task found in unchangedTask
		if (!executed) {
			// Cannot be run until executeCommand has been called.
			return null;
		}
		IDonResponse response = null;

		int count = 0;
		for (IDonTask task : unchangedTask) {
			int id = task.getID();
			IDonTask changedTask = donStorage.getTask(id);
			if (changedTask == null) {

			} else {
				changedTask.copyTaskDetails(task);
				count++;
			}
		}
		if (count != unchangedTask.size()) {
			// Could not find for some reason.
			response = createUndoFailureResponse();
		} else {
			response = createUndoSuccessResponse(count);
			executed = false;
			unchangedTask.clear();
		}

		return response;
	}

}

package doornot.logic;

import java.util.List;

import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

public class DonMarkCommand extends DonEditCommand {

	public enum MarkType {
		MARK_ID,
		MARK_STRING
	}
	private MarkType type;
	
	/**
	 * Mark a task with the given id
	 * @param id the id of the task
	 */
	public DonMarkCommand(int id) {
		searchID = id;
		type = MarkType.MARK_ID;
	}
	
	/**
	 * Mark a task with the given title
	 * @param string the title of the task
	 */
	public DonMarkCommand(String string) {
		searchTitle = string;
		type = MarkType.MARK_STRING;
	}
	
	/**
	 * Toggles the "done" status of the task with the given ID
	 * 
	 * @param id
	 *            the id of the task to change
	 * @return the response
	 */
	private IDonResponse toggleStatusByID(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));

		} else {
			unchangedTask = task.clone();
			boolean taskCompleted = !task.getStatus();
			task.setStatus(taskCompleted);

			response.setResponseType(IDonResponse.ResponseType.EDIT_SUCCESS);
			response.addTask(task);
			response.addMessage(String.format(MSG_TOGGLE_STATUS_ID_SUCCESS, searchID,
					(taskCompleted ? PHRASE_COMPLETE : PHRASE_INCOMPLETE)));
			
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
	private IDonResponse toggleStatusByTitle(IDonStorage donStorage) {
		assert searchTitle!=null;
		IDonResponse response = new DonResponse();
		List<IDonTask> foundTasks = donStorage.getTaskByName(searchTitle);

		if (foundTasks.size() > 1) {
			response.setResponseType(ResponseType.EDIT_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					searchTitle));
			response.setTaskList(foundTasks);
		} else if (foundTasks.isEmpty()) {
			// No task with the name found, return the response of the search
			response = createSearchFailedResponse(searchTitle);
		} else {
			// 1 task was found
			searchID = foundTasks.get(0).getID();
			response = toggleStatusByID(donStorage);
		}

		return response;
	}
	
	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = null;
		if (type == MarkType.MARK_ID) {
			response = toggleStatusByID(donStorage);
		} else if (type == MarkType.MARK_STRING) {
			response = toggleStatusByTitle(donStorage);
		}
		
		if(response.getResponseType() == ResponseType.EDIT_SUCCESS) {
			executed = true;
		}
		
		return response;
	}

}

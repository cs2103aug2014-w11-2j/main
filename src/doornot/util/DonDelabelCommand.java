package doornot.util;

import java.util.List;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

//@author A0111995Y
public class DonDelabelCommand extends DonEditCommand {

	public enum DelabelType {
		LABEL_ID, LABEL_NAME, LABEL_ALL_NAME, LABEL_ALL_ID
	}

	private DelabelType type;
	private String searchLabel;

	/**
	 * Creates a DelabelCommand that removes the given label from a task with an
	 * ID
	 * 
	 * @param id
	 *            the ID of the task
	 * @param label
	 *            the label to remove
	 */
	public DonDelabelCommand(int id, String label) {
		searchID = id;
		searchLabel = label;
		type = DelabelType.LABEL_ID;
		generalCommandType = GeneralCommandType.LABEL;
	}

	/**
	 * Creates a DelabelCommand that removes the given label from a task
	 * containing the title
	 * 
	 * @param title
	 *            the title of the task to remove the label from
	 * @param label
	 *            the label to remove
	 */
	public DonDelabelCommand(String title, String label) {
		searchTitle = title;
		searchLabel = label;
		type = DelabelType.LABEL_NAME;
		generalCommandType = GeneralCommandType.LABEL;
	}

	/**
	 * Removes all labels from a task with a title containing the search title
	 * 
	 * @param title
	 *            the title of the task
	 */
	public DonDelabelCommand(String title) {
		searchTitle = title;
		type = DelabelType.LABEL_ALL_NAME;
		generalCommandType = GeneralCommandType.LABEL;
	}

	/**
	 * Removes all labels from the task with the given ID
	 * 
	 * @param ID
	 *            the ID of the task
	 */
	public DonDelabelCommand(int ID) {
		searchID = ID;
		type = DelabelType.LABEL_ALL_ID;
		generalCommandType = GeneralCommandType.LABEL;
	}

	public DelabelType getDelabelType() {
		return type;
	}

	public String getSearchLabel() {
		return searchLabel;
	}

	/**
	 * Removes a label from a task with the given id
	 * 
	 * @param id
	 *            the task's id to search for and remove the label from
	 * @return the response containing the affected task
	 */
	private IDonResponse removeLabelByID(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));
		} else {
			unchangedTask.add(task.clone());
			List<String> currentLabels = task.getLabels();
			if (currentLabels.remove(searchLabel)) {
				response.setResponseType(IDonResponse.ResponseType.LABEL_REMOVED);
				response.addMessage(String.format(MSG_LABEL_NAME_REMOVED,
						searchLabel));
				response.addTask(task);
			} else {
				response.setResponseType(IDonResponse.ResponseType.LABEL_NOT_FOUND);
				response.addMessage(String.format(
						MSG_LABEL_STRING_DOES_NOT_EXIST, searchLabel));
			}

		}

		return response;
	}

	/**
	 * Removes a label from a task with the given name
	 * 
	 * @param id
	 *            the task's id to search for and remove the label from
	 * @return the response containing the affected task
	 */
	private IDonResponse removeLabelByTitle(IDonStorage donStorage) {
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
			response = removeLabelByID(donStorage);
		}

		return response;
	}

	/**
	 * Removes all labels from a task with the given id
	 * 
	 * @param id
	 *            the task's id to search for remove all labels from
	 * @return the response containing the affected task
	 */
	private IDonResponse removeAllLabelsByID(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));
		} else {
			unchangedTask.add(task.clone());
			task.getLabels().clear();
			response.setResponseType(IDonResponse.ResponseType.LABEL_REMOVED);
			response.addMessage(MSG_LABEL_ALL_REMOVED);
			response.addTask(task);
		}

		return response;
	}

	/**
	 * Removes all labels from a task with the given name
	 * 
	 * @param id
	 *            the task's id to search for and add a label to
	 * @return the response containing the affected task
	 */
	private IDonResponse removeAllLabelsByTitle(IDonStorage donStorage) {
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
			response = removeAllLabelsByID(donStorage);
		}

		return response;
	}

	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = null;
		if (type == DelabelType.LABEL_ID) {
			response = removeLabelByID(donStorage);
		} else if (type == DelabelType.LABEL_NAME) {
			response = removeLabelByTitle(donStorage);
		} else if (type == DelabelType.LABEL_ALL_ID) {
			response = removeAllLabelsByID(donStorage);
		} else if (type == DelabelType.LABEL_ALL_NAME) {
			response = removeAllLabelsByTitle(donStorage);
		}

		if (response.getResponseType() == ResponseType.LABEL_REMOVED) {
			response.sortTask();
			executed = true;
		}

		return response;
	}

}

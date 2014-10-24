package doornot.logic;

import java.util.List;

import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

public class DonDelabelCommand extends DonEditCommand {
	

	public enum DelabelType {
		LABEL_ID, LABEL_NAME
	}
	
	private DelabelType type;
	private String newLabel;
	
	public DonDelabelCommand(int id, String label) {
		searchID = id;
		newLabel = label;
		type = DelabelType.LABEL_ID;
	}
	
	public DonDelabelCommand(String title, String label) {
		searchTitle = title;
		newLabel = label;
		type = DelabelType.LABEL_NAME;
	}
	
	/**
	 * Removes a label from a task with the given id
	 * @param id the task's id to search for and add a label to
	 * @param labelName the name of the label to add
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
			unchangedTask = task.clone();
			List<String> currentLabels = task.getLabels();
			if (currentLabels.remove(newLabel)) {
				response.setResponseType(IDonResponse.ResponseType.LABEL_REMOVED);
				response.addMessage(String.format(MSG_LABEL_NAME_REMOVED, newLabel));			
				response.addTask(task);
			} else {
				response.setResponseType(IDonResponse.ResponseType.LABEL_NOT_FOUND);
				response.addMessage(String.format(MSG_LABEL_STRING_DOES_NOT_EXIST, newLabel));
			}

		}
		
		return response;
	}
	
	/**
	 * Removes a label from a task with the given name
	 * @param id the task's id to search for and add a label to
	 * @param labelName the name of the label to add
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

	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = null;
		if (type == DelabelType.LABEL_ID) {
			response = removeLabelByID(donStorage);
		} else if (type == DelabelType.LABEL_NAME) {
			response = removeLabelByTitle(donStorage);
		}
		
		if (response.getResponseType() == ResponseType.LABEL_REMOVED) {
			executed = true;
		}
		
		return response;
	}


}

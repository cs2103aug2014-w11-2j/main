package doornot.util;

import java.util.List;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

public class DonAddLabelCommand extends DonEditCommand {
	
	public enum AddLabelType {
		LABEL_ID, LABEL_NAME
	}
	
	private AddLabelType type;
	private String newLabel;
	
	public DonAddLabelCommand(int id, String label) {
		searchID = id;
		newLabel = label;
		type = AddLabelType.LABEL_ID;
		generalCommandType = GeneralCommandType.LABEL;
	}
	
	public DonAddLabelCommand(String title, String label) {
		searchTitle = title;
		newLabel = label;
		type = AddLabelType.LABEL_NAME;
		generalCommandType = GeneralCommandType.LABEL;
	}
	
	public AddLabelType getAddLabelType() {
		return type;
	}
	
	public String getNewLabel() {
		return newLabel;
	}
	
	/**
	 * Add a label to a task with the given id
	 * @param id the task's id to search for and add a label to
	 * @param labelName the name of the label to add
	 * @return the response containing the affected task
	 */
	private IDonResponse addLabelByID(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));
		} else {
			unchangedTask = task.clone();
			List<String> currentLabels = task.getLabels();
			if (currentLabels.contains(newLabel)) {
				response.setResponseType(IDonResponse.ResponseType.LABEL_EXISTS);
				response.addMessage(String.format("The label '%1$s' already exists", newLabel));
			} else {
				task.addLabel(newLabel);
				response.setResponseType(IDonResponse.ResponseType.LABEL_ADDED);
				response.addTask(task);
			}

		}
		
		return response;
	}
	
	/**
	 * Add a label to a task with the given name
	 * If more than 1 task has the name, it will not add the label
	 * @param id the task's id to search for and add a label to
	 * @param labelName the name of the label to add
	 * @return the response containing the affected task
	 */
	private IDonResponse addLabelByTitle(IDonStorage donStorage) {
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
			response = addLabelByID(donStorage);
		}
		
		return response;
	}

	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = null;
		if (type == AddLabelType.LABEL_ID) {
			response = addLabelByID(donStorage);
		} else if (type == AddLabelType.LABEL_NAME) {
			response = addLabelByTitle(donStorage);
		}
		
		if (response.getResponseType() == ResponseType.LABEL_ADDED) {
			executed = true;
		}
		
		return response;
	}


}

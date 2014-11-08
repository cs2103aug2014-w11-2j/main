package doornot.util;

import java.util.List;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

//@author A0111995Y
public class DonAddLabelCommand extends DonEditCommand {

	public enum AddLabelType {
		LABEL_ID, LABEL_NAME
	}
	
	private AddLabelType type;
	private String newLabel;
	private static int MAX_LABEL = 3;
	
	/**
	 * Creates an add label command that adds to the task with the given ID
	 * @param id the id of the task
	 * @param label the label to add
	 */
	public DonAddLabelCommand(int id, String label) {
		searchID = id;
		newLabel = label;
		type = AddLabelType.LABEL_ID;
		generalCommandType = GeneralCommandType.LABEL;
	}
	
	/**
	 * Creates an add label command that adds to a task with the given title
	 * @param title
	 * @param label
	 */
	public DonAddLabelCommand(String title, String label) {
		searchTitle = title;
		newLabel = label;
		type = AddLabelType.LABEL_NAME;
		generalCommandType = GeneralCommandType.LABEL;
	}
	
	/**
	 * Gets the specific type of command this AddLabelCommand is
	 * @return the type of this command
	 */
	public AddLabelType getAddLabelType() {
		return type;
	}
	
	/**
	 * Gets the label to be added to the task
	 * @return 
	 */
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
			List<String> currentLabels = task.getLabels();
			if (currentLabels.size() >= MAX_LABEL) {
				response.setResponseType(IDonResponse.ResponseType.LABEL_FAILED);
				response.addMessage(String.format(MSG_LABEL_OVERLOAD, MAX_LABEL));
			} else {
				unchangedTask.add(task.clone());
				if (currentLabels.contains(newLabel)) {
					response.setResponseType(IDonResponse.ResponseType.LABEL_EXISTS);
					response.addMessage(String.format(MSG_LABEL_EXISTS, newLabel));
				} else {
					task.addLabel(newLabel);
					response.setResponseType(IDonResponse.ResponseType.LABEL_ADDED);
					response.addMessage(String.format(MSG_LABEL_ADDED_ID, newLabel, searchID));
					response.addTask(task);
				}
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

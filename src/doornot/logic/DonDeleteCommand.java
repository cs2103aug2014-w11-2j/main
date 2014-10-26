package doornot.logic;

import java.util.List;

import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

public class DonDeleteCommand extends AbstractDonCommand {
	
	public enum DeleteType {
		DELETE_ID,
		DELETE_TITLE
	}
	
	private DeleteType type;
	private int searchID;
	private String searchTitle;
	private IDonTask deletedTask;
	
	/**
	 * Set a delete command by id
	 * @param id the id of the task to delete
	 */
	public DonDeleteCommand(int id) {
		searchID = id;
		type = DeleteType.DELETE_ID;
		generalCommandType = GeneralCommandType.DELETE;
	}
	
	public DonDeleteCommand(String title) {
		searchTitle = title;
		type = DeleteType.DELETE_TITLE;
		generalCommandType = GeneralCommandType.DELETE;
	}
	
	public DeleteType getType() {
		return type;
	}
	
	public int getSearchID() {
		return searchID;
	}
	
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * Deletes the task with the given ID
	 * 
	 * @param id
	 *            the id of the task to delete
	 * @return the response containing the deletion status
	 */
	private IDonResponse deleteTaskByID(IDonStorage donStorage) {
		DonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));
		} else {
			boolean deleteStatus = donStorage.removeTask(searchID);
			if (deleteStatus) {
				// Deleted
				response.setResponseType(IDonResponse.ResponseType.DEL_SUCCESS);
				response.addMessage(MSG_DELETE_SUCCESS);
				response.addTask(task);
				deletedTask = task.clone();

			} else {
				response.setResponseType(IDonResponse.ResponseType.DEL_FAILURE);
				response.addMessage(MSG_DELETE_FAILED);
			}
		}
		return response;
	}

	/**
	 * Deletes the task with the given title. If more than 1 task is found, the
	 * search results will be returned and nothing will be deleted.
	 * 
	 * @param title
	 *            the title of the task to search for to delete
	 * @return the response containing the deletion status
	 */
	private IDonResponse deleteTaskByTitle(IDonStorage donStorage) {
		assert searchTitle!=null;
		IDonResponse response = new DonResponse();

		List<IDonTask> foundList = donStorage.getTaskByName(searchTitle);

		if (foundList.size() > 1) {
			response.setResponseType(ResponseType.DEL_FAILURE);
			response.addMessage(String.format(MSG_SEARCH_MORE_THAN_ONE_TASK,
					searchTitle));
			response.setTaskList(foundList);
		} else if (foundList.isEmpty()) {
			// No task with the name found, return the response of the search
			response = createSearchFailedResponse(searchTitle);
		} else {
			// 1 task was found
			searchID = foundList.get(0).getID();
			response = deleteTaskByID(donStorage);
		}

		return response;
	}
	
	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = null;
		if (type == DeleteType.DELETE_ID) {
			response = deleteTaskByID(donStorage);
		} else if (type == DeleteType.DELETE_TITLE) {
			response = deleteTaskByTitle(donStorage);
		}
		
		if (response.getResponseType() == ResponseType.DEL_SUCCESS) {
			executed = true;
		}
		return response;
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		//Perform an add
		assert deletedTask!=null;
		int id = donStorage.addTask(deletedTask);
		IDonResponse response = null;
		if(id != -1) {
			response = createUndoSuccessResponse();
			executed = false;
		} else {
			response = createUndoFailureResponse();
		}
		return response;
	}

}

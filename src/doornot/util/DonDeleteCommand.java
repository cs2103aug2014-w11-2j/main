package doornot.util;

import java.util.ArrayList;
import java.util.List;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;
import doornot.storage.IDonTask.TaskType;

//@author A0111995Y
public class DonDeleteCommand extends AbstractDonCommand {

	public enum DeleteType {
		DELETE_ID, DELETE_TITLE, DELETE_OVERDUE, DELETE_FLOAT, DELETE_LABEL, DELETE_DONE
	}

	private DeleteType type;
	private int searchID;
	private String searchTitle;
	private List<IDonTask> deletedTasks = new ArrayList<IDonTask>();

	/**
	 * Creates a DeleteCommand that removes the task with the id
	 * 
	 * @param id
	 *            the id of the task to delete
	 */
	public DonDeleteCommand(int id) {
		searchID = id;
		type = DeleteType.DELETE_ID;
		generalCommandType = GeneralCommandType.DELETE;
	}

	/**
	 * Creates a DeleteCommand that removes the task with the search title in
	 * its name of label
	 * 
	 * @param title
	 *            the name of the title or label to search for
	 * @param delType
	 *            the type of deletion to perform. This should be either
	 *            DeleteType.DELETE_TITLE or DeleteType.DELETE_LABEL
	 */
	public DonDeleteCommand(String title, DeleteType delType) {
		searchTitle = title;
		type = delType;
		generalCommandType = GeneralCommandType.DELETE;
	}

	/**
	 * Creates a DeleteCommand that removes multiple tasks that fit the given
	 * DeleteType
	 * 
	 * @param deltype
	 *            the type of deletion to perform.
	 */
	public DonDeleteCommand(DeleteType deltype) {
		type = deltype;
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
	 * @param donStorage
	 *            the storage object containing the tasks
	 * @return the response containing the deletion status and deleted task
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
				deletedTasks.add(task.clone());

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
	 * @param donStorage
	 *            the storage object containing the tasks
	 * @return the response containing the deletion status and deleted task
	 */
	private IDonResponse deleteTaskByTitle(IDonStorage donStorage) {
		assert searchTitle != null;
		IDonResponse response = new DonResponse();

		List<IDonTask> foundList = donStorage.getTaskByName(searchTitle);

		if (foundList.size() > 1) {
			response.setResponseType(ResponseType.DEL_FAILURE);
			response.addMessage(String
					.format(MSG_MATCHING_RESULTS, searchTitle));
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

	/**
	 * Deletes all overdue tasks
	 * 
	 * @param donStorage
	 *            the storage object containing the tasks
	 * @return the response containing the deletion status and deleted tasks
	 */
	private IDonResponse deleteOverdueTasks(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		List<IDonTask> foundList = SearchHelper.findOverdue(donStorage);

		if (foundList.isEmpty()) {
			// No overdue tasks
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(MSG_NO_UNDONE_OVERDUE);
		} else {
			// >=1 task found
			boolean success = true;
			for (IDonTask task : foundList) {
				deletedTasks.add(task.clone());
				response.addTask(task);
				boolean deleted = donStorage.removeTask(task.getID());
				if (!deleted) {
					// Was likely not found
					response.setResponseType(ResponseType.DEL_FAILURE);
					response.addMessage(MSG_DELETE_FAILED);
					success = false;
					break;
				}
			}
			if (success) {
				response.setResponseType(IDonResponse.ResponseType.DEL_SUCCESS);
				response.addMessage(MSG_DELETE_SUCCESS);
			}

		}

		return response;
	}

	/**
	 * Deletes all floating tasks
	 * 
	 * @param donStorage
	 *            the storage object containing the tasks
	 * @return the response containing the deletion status and deleted tasks
	 */
	private IDonResponse deleteFloatingTasks(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		List<IDonTask> foundList = SearchHelper.findTaskByType(donStorage,
				TaskType.FLOATING, true, true);

		if (foundList.isEmpty()) {
			// No floating tasks
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(MSG_NO_FLOATING);
		} else {
			// >=1 task found
			boolean success = true;
			for (IDonTask task : foundList) {
				deletedTasks.add(task.clone());
				response.addTask(task);
				boolean deleted = donStorage.removeTask(task.getID());
				if (!deleted) {
					// Was likely not found
					response.setResponseType(ResponseType.DEL_FAILURE);
					response.addMessage(MSG_DELETE_FAILED);
					success = false;
					break;
				}
			}
			if (success) {
				response.setResponseType(IDonResponse.ResponseType.DEL_SUCCESS);
				response.addMessage(MSG_DELETE_SUCCESS);
			}

		}

		return response;
	}

	/**
	 * Deletes all tasks with the given label
	 * 
	 * @param donStorage
	 *            the storage object containing the tasks
	 * @return the response containing the deletion status and deleted tasks
	 */
	private IDonResponse deleteLabelTasks(IDonStorage donStorage) {
		assert searchTitle != null;
		IDonResponse response = new DonResponse();
		List<IDonTask> foundList = donStorage.getTaskList();
		List<IDonTask> deleteList = new ArrayList<IDonTask>();

		for (IDonTask task : foundList) {
			if (task.getLabels().contains(searchTitle)) {
				deleteList.add(task);
			}
		}

		if (deleteList.isEmpty()) {
			// No tasks with the given label
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_NO_LABEL_TASKS, searchTitle));
		} else {
			// >=1 task found
			boolean success = true;
			for (IDonTask task : deleteList) {
				deletedTasks.add(task.clone());
				response.addTask(task);
				boolean deleted = donStorage.removeTask(task.getID());
				if (!deleted) {
					// Was likely not found
					response.setResponseType(ResponseType.DEL_FAILURE);
					response.addMessage(MSG_DELETE_FAILED);
					success = false;
					break;
				}
			}
			if (success) {
				response.setResponseType(IDonResponse.ResponseType.DEL_SUCCESS);
				response.addMessage(String.format(
						MSG_DELETE_ALL_WITH_LABEL_SUCCESS, searchTitle));
			}

		}

		return response;
	}

	/**
	 * Delete all completed tasks (marked as done)
	 * 
	 * @param donStorage
	 *            the storage containing the tasks
	 * @return response containing success/failure of the operation
	 */
	private IDonResponse deleteCompletedTasks(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		List<IDonTask> foundList = SearchHelper.findDone(donStorage);

		if (foundList.isEmpty()) {
			// No done tasks
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(MSG_NO_DONE_TASKS);
		} else {
			// >=1 task found
			boolean success = true;
			for (IDonTask task : foundList) {
				deletedTasks.add(task.clone());
				response.addTask(task);
				boolean deleted = donStorage.removeTask(task.getID());
				if (!deleted) {
					// Was likely not found
					response.setResponseType(ResponseType.DEL_FAILURE);
					response.addMessage(MSG_DELETE_FAILED);
					success = false;
					break;
				}
			}
			if (success) {
				response.setResponseType(IDonResponse.ResponseType.DEL_SUCCESS);
				response.addMessage(MSG_DELETE_SUCCESS);
			}

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
		} else if (type == DeleteType.DELETE_OVERDUE) {
			response = deleteOverdueTasks(donStorage);
		} else if (type == DeleteType.DELETE_FLOAT) {
			response = deleteFloatingTasks(donStorage);
		} else if (type == DeleteType.DELETE_LABEL) {
			response = deleteLabelTasks(donStorage);
		} else if (type == DeleteType.DELETE_DONE) {
			response = deleteCompletedTasks(donStorage);
		}

		if (response.getResponseType() == ResponseType.DEL_SUCCESS) {
			response.sortTask();
			executed = true;
		}
		return response;
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		// Perform an add
		assert deletedTasks != null;
		IDonResponse response = null;
		int count = 0;
		for (IDonTask task : deletedTasks) {
			int id = donStorage.addTask(task);
			if (id != -1) {
				count++;
			}
		}

		if (count != deletedTasks.size()) {
			response = createUndoFailureResponse();
		} else {
			response = createUndoSuccessResponse(count);
			executed = false;
			deletedTasks.clear();
		}

		return response;
	}

}

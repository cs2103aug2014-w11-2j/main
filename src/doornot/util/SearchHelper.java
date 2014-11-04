package doornot.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;
import doornot.storage.IDonTask.TaskType;

public class SearchHelper {
	
	protected static final int FIND_INCOMPLETE = 0;
	protected static final int FIND_COMPLETE = 1;
	protected static final int FIND_ALL = 2;
	
	/**
	 * Find tasks with the given name
	 * 
	 * @param name
	 *            the name to search for
	 * @return the response containing the tasks
	 */
	public static List<IDonTask> findTaskByName(IDonStorage donStorage, String searchTitle) {
		assert searchTitle != null;
		List<IDonTask> response = new ArrayList<IDonTask>();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			// Search for the given name/title without case sensitivity
			if (task.getTitle().toLowerCase()
					.contains(searchTitle.toLowerCase())) {
				response.add(task);
			}
		}
		return response;
	}

	/**
	 * Find tasks with the exact given name
	 * 
	 * @param donStorage the storage containing the tasks
	 * @param searchTitle
	 *            the name to search for
	 * @return the response containing the tasks
	 */
	public static List<IDonTask> findTaskByExactName(IDonStorage donStorage, String searchTitle) {
		assert searchTitle != null;
		List<IDonTask> response = new ArrayList<IDonTask>();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			// Search for the given name/title without case sensitivity
			if (task.getTitle().equals(searchTitle)) {
				response.add(task);
			}
		}
		return response;
	}
	
	/**
	 * Find tasks starting/occurring on a given date
	 * 
	 * @param date
	 *            the date to search for
	 * @return the response containing the tasks
	 */
	public static List<IDonTask> findTaskByDate(IDonStorage donStorage, Calendar searchStartDate) {
		assert searchStartDate != null;
		List<IDonTask> response = new ArrayList<IDonTask>();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			// Search for the given name/title without case sensitivity
			TaskType taskType = task.getType();
			if (taskType == TaskType.FLOATING) {
				// Floating tasks have no date.
				continue;
			}
			Calendar taskDate = task.getStartDate();
			Calendar taskEndDate = task.getEndDate();
			// If the date falls within the start and end date of an event, the
			// event is returned as well
			if (CalHelper.isSameDay(taskDate, searchStartDate)
					|| (taskType == TaskType.DURATION && CalHelper
							.isBetweenDates(searchStartDate, taskDate,
									taskEndDate))) {
				response.add(task);
			}
		}
		return response;
	}

	/**
	 * Find tasks that begin within the given range of time. Either parameter
	 * can be null to search for tasks before or after a date. For example if
	 * startDate is null and endDate is set to 09102014, the method will return
	 * all tasks from before 9th of October 2014. If startDate is 09102014 and
	 * endDate is null, all tasks beginning after 9th of October 2014 will be
	 * returned.
	 * 
	 * @param startDate
	 *            the date to start searching from (inclusive)
	 * @param endDate
	 *            the latest possible start date of a task (inclusive)
	 * @param completeType
	 *            0 if the tasks found must be incomplete, 1 if it must be
	 *            completed 2 if it can be complete or incomplete
	 * @return the response containing the tasks
	 */
	public static List<IDonTask> findTaskRange(IDonStorage donStorage,
			Calendar startDate, Calendar endDate, int completeType) {
		assert !(startDate == null && endDate == null);
		List<IDonTask> response = new ArrayList<IDonTask>();
		List<IDonTask> taskList = donStorage.getTaskList();

		for (IDonTask task : taskList) {
			if (task.getType() == TaskType.FLOATING) {
				// Floating tasks have no date.
				continue;
			}
			// Ignore tasks that do not match the completion status to search
			// for
			if ((task.getStatus() && completeType == FIND_INCOMPLETE)
					|| (!task.getStatus() && completeType == FIND_COMPLETE)) {
				continue;
			}
			Calendar taskStart = task.getStartDate();
			Calendar taskEnd = task.getEndDate();
			if (startDate == null) {
				if (CalHelper.dateEqualOrBefore(taskStart, endDate)) {
					response.add(task);
				}
			} else if (endDate == null) {
				if (CalHelper.dateEqualOrAfter(taskStart, startDate)) {
					response.add(task);
				}
			} else {
				// If task is between date or is ongoing between the dates
				if ((CalHelper.dateEqualOrAfter(taskStart, startDate) && CalHelper
						.dateEqualOrBefore(taskStart, endDate))
						|| (CalHelper.dateEqualOrAfter(startDate, taskStart) && CalHelper
								.dateEqualOrBefore(startDate, taskEnd))
						|| (CalHelper.dateEqualOrAfter(endDate, taskStart) && CalHelper
								.dateEqualOrBefore(endDate, taskEnd))) {
					response.add(task);
				}
			}

		}
		return response;
	}

	/**
	 * Find all undone/incomplete tasks
	 * 
	 * @return the response containing incomplete tasks
	 */
	public static List<IDonTask> findUndone(IDonStorage donStorage) {
		List<IDonTask> response = new ArrayList<IDonTask>();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			if (!task.getStatus()) {
				response.add(task.clone());
			}
		}
		
		return response;
	}

	/**
	 * Search for tasks with the given label name
	 * 
	 * @param donStorage
	 *            the storage in which the tasks are located
	 * @param labelName
	 *            the label to search for
	 * @return the response containing tasks with the given label
	 */
	public static List<IDonTask> findLabel(IDonStorage donStorage, String searchTitle) {
		List<IDonTask> response = new ArrayList<IDonTask>();
		List<IDonTask> taskList = donStorage.getTaskList();

		for (IDonTask task : taskList) {
			List<String> labels = task.getLabels();
			for (String label : labels) {
				if (label.equalsIgnoreCase(searchTitle)) {
					response.add(task);
					break;
				}
			}
		}

		return response;
	}

	/**
	 * Returns a list of tasks by the given task type
	 * 
	 * @param donStorage
	 *            the storage in which the tasks are located
	 * @param type
	 *            the type of the task
	 * @param allowOverdue
	 *            true if overdue tasks are allowed.
	 * @param allowFinished
	 *            true if completed tasks are allowed
	 * @return the list of tasks
	 */
	public static List<IDonTask> getTaskByType(IDonStorage donStorage,
			IDonTask.TaskType type, boolean allowOverdue, boolean allowFinished) {
		List<IDonTask> taskList = donStorage.getTaskList();
		List<IDonTask> resultList = new ArrayList<IDonTask>();
		Calendar now = Calendar.getInstance();
		for (IDonTask task : taskList) {
			if (task.getType() == type) {
				if (type == IDonTask.TaskType.DEADLINE) {
					if ((!allowOverdue && task.getStartDate().before(now))
							|| (!allowFinished && task.getStatus())) {
						// is overdue or finished
						continue;
					}
				} else if (type == IDonTask.TaskType.DURATION) {
					if ((!allowOverdue && task.getEndDate().before(now))
							|| (!allowFinished && task.getStatus())) {
						// is overdue or finished
						continue;
					}
				}
				// Clone the task to prevent the original from being edited.
				resultList.add(task);
			}
		}
		return resultList;
	}

	/**
	 * Get all overdue tasks
	 * @param donStorage the storage in which the tasks are located
	 * @return the list of overdue tasks
	 */
	public static List<IDonTask> findOverdue(IDonStorage donStorage) {
		List<IDonTask> response = findTaskRange(donStorage, null,
				Calendar.getInstance(), FIND_ALL);
		List<IDonTask> taskList = new ArrayList<IDonTask>();
		for (IDonTask task : response) {
			if ((task.getEndDate() != null
					&& CalHelper.dateEqualOrBefore(task.getEndDate(),
							Calendar.getInstance())) || task.getEndDate()==null) {
				taskList.add(task);
			} 
		}

		return taskList;
	}
	
	/**
	 * Get the list of all completed tasks
	 * @param donStorage the storage in which the tasks are located
	 * @return the list of completed tasks
	 */
	public static List<IDonTask> findDone(IDonStorage donStorage) {
		List<IDonTask> taskList = donStorage.getTaskList();
		List<IDonTask> outputList = new ArrayList<IDonTask>();
		for (IDonTask task : taskList) {
			if(task.getStatus()) {
				outputList.add(task);
			}
		}

		return outputList;
	}
}

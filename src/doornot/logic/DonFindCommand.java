package doornot.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.joestelmach.natty.Parser;

import doornot.CalHelper;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;
import doornot.storage.IDonTask.TaskType;

public class DonFindCommand extends AbstractDonCommand {

	public enum SearchType {
		SEARCH_NAME, SEARCH_DATE, SEARCH_ID, SEARCH_LABEL, SEARCH_FREE, SEARCH_UNDONE, SEARCH_ALL, SEARCH_AFTDATE, TODAY, OVERDUE, SEVEN_DAYS, FUTURE;
	}

	private SearchType type;
	private int searchID;
	private String searchTitle;
	private Calendar searchStartDate, searchEndDate;
	private boolean isTimeUsed;

	/**
	 * Constructor for find commands without parameters
	 * 
	 * @param type
	 */
	public DonFindCommand(SearchType type) {
		this.type = type;
		generalCommandType = GeneralCommandType.SEARCH;
	}

	/**
	 * Search by id of the task
	 * 
	 * @param id
	 *            the task's id
	 */
	public DonFindCommand(int id) {
		searchID = id;
		type = SearchType.SEARCH_ID;
		generalCommandType = GeneralCommandType.SEARCH;
	}

	/**
	 * Search with a title/name
	 * 
	 * @param title
	 *            the title of the task to search for
	 * @param stringType
	 *            the type of string given. Can be task title or label
	 */
	public DonFindCommand(String title, SearchType stringType) {
		searchTitle = title;
		type = stringType;
		generalCommandType = GeneralCommandType.SEARCH;
	}

	/**
	 * Search with a date. The dateType argument should be a SearchType that is
	 * related to date
	 * 
	 * @param date
	 *            the date to search for
	 * @param dateType
	 *            the type of the date search (before, on, after etc)
	 */
	public DonFindCommand(Calendar date, boolean isTimeUsed, SearchType dateType) {
		searchStartDate = date;
		type = dateType;
		this.isTimeUsed = isTimeUsed;
		if (dateType == SearchType.SEARCH_AFTDATE) {
			if (!isTimeUsed) {
				// If given search date has a time, will search after the given
				// time.
				// If given search does not include a time, will search from the
				// day after
				searchStartDate = CalHelper.getDayAfter(searchStartDate);
			}
		}
		generalCommandType = GeneralCommandType.SEARCH;

	}

	public SearchType getType() {
		return type;
	}

	public int getSearchID() {
		return searchID;
	}

	public String getSearchTitle() {
		return searchTitle;
	}

	public Calendar getSearchStartDate() {
		return searchStartDate;
	}

	public Calendar getSearchEndDate() {
		return searchEndDate;
	}

	/**
	 * Find tasks with the given name
	 * 
	 * @param name
	 *            the name to search for
	 * @return the response containing the tasks
	 */
	private IDonResponse findTaskByName(IDonStorage donStorage) {
		assert searchTitle != null;
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			// Search for the given name/title without case sensitivity
			if (task.getTitle().toLowerCase()
					.contains(searchTitle.toLowerCase())) {
				response.addTask(task);
			}
		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);

		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_TITLE_FAILED,
					searchTitle));

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
	private IDonResponse findTaskByDate(IDonStorage donStorage) {
		assert searchStartDate != null;
		IDonResponse response = new DonResponse();
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
				response.addTask(task);
			}
		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
			String dateString = searchStartDate.get(Calendar.DATE)
					+ " "
					+ searchStartDate.getDisplayName(Calendar.MONTH,
							Calendar.LONG, Locale.ENGLISH) + " "
					+ searchStartDate.get(Calendar.YEAR);
			response.addMessage(String.format(MSG_SEARCH_DATE_FAILED,
					dateString));
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
	private IDonResponse findTaskRange(IDonStorage donStorage,
			Calendar startDate, Calendar endDate, int completeType) {
		assert !(startDate == null && endDate == null);
		IDonResponse response = new DonResponse();
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
					response.addTask(task);
				}
			} else if (endDate == null) {
				if (CalHelper.dateEqualOrAfter(taskStart, startDate)) {
					response.addTask(task);
				}
			} else {
				// If task is between date or is ongoing between the dates
				if ((CalHelper.dateEqualOrAfter(taskStart, startDate) && CalHelper
						.dateEqualOrBefore(taskStart, endDate))
						|| (CalHelper.dateEqualOrAfter(startDate, taskStart) && CalHelper
								.dateEqualOrBefore(startDate, taskEnd))
						|| (CalHelper.dateEqualOrAfter(endDate, taskStart) && CalHelper
								.dateEqualOrBefore(endDate, taskEnd))) {
					response.addTask(task);
				}
			}

		}
		if (response.getTasks().size() > 0) {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
		} else {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
		}
		return response;
	}

	/**
	 * Find tasks given the ID
	 * 
	 * @param id
	 *            the id to search for
	 * @return the response containing the tasks
	 */
	private IDonResponse findTaskByID(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		IDonTask task = donStorage.getTask(searchID);
		if (task == null) {
			// No task with given ID found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_ID_FAILED, searchID));
		} else {
			response.setResponseType(IDonResponse.ResponseType.SEARCH_SUCCESS);
			response.addTask(task);
		}
		return response;
	}

	/**
	 * Find all undone/incomplete tasks
	 * 
	 * @return the response containing incomplete tasks
	 */
	private IDonResponse findUndone(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();
		for (IDonTask task : taskList) {
			if (!task.getStatus()) {
				response.addTask(task.clone());
			}
		}
		if (response.hasTasks()) {
			response.setResponseType(IDonResponse.ResponseType.SEARCH_SUCCESS);
		} else {
			response.addMessage(MSG_NO_UNDONE_TASKS);
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
		}
		return response;
	}

	/**
	 * Get tasks occurring on the current day or already taking place in the
	 * current day
	 * 
	 * @param donStorage
	 *            the storage in which the tasks are located
	 * @return the response containing today's tasks
	 */
	private IDonResponse getTasksToday(IDonStorage donStorage) {
		IDonResponse response;
		response = findTaskRange(donStorage, CalHelper.getTodayStart(),
				CalHelper.getTodayEnd(), FIND_INCOMPLETE);
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
	private IDonResponse findLabel(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		List<IDonTask> taskList = donStorage.getTaskList();

		for (IDonTask task : taskList) {
			List<String> labels = task.getLabels();
			for (String label : labels) {
				if (label.equalsIgnoreCase(searchTitle)) {
					response.addTask(task);
					break;
				}
			}
		}
		if (!response.hasTasks()) {
			// No task with given label found
			response.setResponseType(IDonResponse.ResponseType.SEARCH_EMPTY);
			response.addMessage(String.format(MSG_SEARCH_LABEL_FAILED,
					searchTitle));
		} else {
			response.setResponseType(IDonResponse.ResponseType.SEARCH_SUCCESS);
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
	private List<IDonTask> getTaskByType(IDonStorage donStorage,
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
				resultList.add(task.clone());
			}
		}
		return resultList;
	}

	/**
	 * Find free time in the user's schedule based on existing task and events.
	 * For dates with no time stated, it is assumed that the user means that the
	 * whole day is taken up.
	 * 
	 * @param donStorage
	 *            the storage in which the tasks are located
	 * @return the response containing the free time.
	 */
	private IDonResponse findFreeTime(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		// Get all tasks with deadlines or events that end after today
		List<IDonTask> taskList = getTaskByType(donStorage,
				IDonTask.TaskType.DEADLINE, false, false);
		taskList.addAll(getTaskByType(donStorage, IDonTask.TaskType.DURATION,
				false, false));
		Collections.sort(taskList);

		if (taskList.size() <= 0) {
			response.addMessage(MSG_FREE_EVERYWHERE);
			return response;
		}

		// TODO handle the case where there are no tasks
		// Find free period between now and the start time of the earliest task
		// if possible
		Calendar now = Calendar.getInstance();
		if (taskList.get(0).getStartDate().after(now)) {
			DonPeriod free = new DonPeriod(now, taskList.get(0).getStartDate());
			response.addPeriod(free);
		}

		for (int i = 0; i < taskList.size() - 1; i++) {
			IDonTask currentTask = taskList.get(i);
			IDonTask nextTask = taskList.get(i + 1);
			// Check if there is a free period between the end time
			// and the start time of the next event
			if (currentTask.getType() == IDonTask.TaskType.DEADLINE) {
				// A deadline task has no end date
				// If the user did not specify a time in the deadline
				// 0000hr on the given day will be used as the deadline
				if (currentTask.getStartDate().compareTo(
						nextTask.getStartDate()) < 0) {
					// There is a free period
					DonPeriod free = new DonPeriod(currentTask.getStartDate(),
							nextTask.getStartDate());
					response.addPeriod(free);
				}
			} else {
				if (currentTask.getEndDate().compareTo(nextTask.getStartDate()) < 0) {
					// There is a free period
					DonPeriod free = new DonPeriod(currentTask.getEndDate(),
							nextTask.getStartDate());
					response.addPeriod(free);
				}
			}
		}

		return response;
	}

	/**
	 * Return all the tasks
	 * 
	 * @param donStorage
	 *            the storage in which the tasks are located
	 * @return
	 */
	private IDonResponse findAll(IDonStorage donStorage) {
		IDonResponse response = new DonResponse();
		response.setTaskList(donStorage.getTaskList());
		if (donStorage.getTaskList().isEmpty()) {
			response.setResponseType(ResponseType.SEARCH_EMPTY);
		} else {
			response.setResponseType(ResponseType.SEARCH_SUCCESS);
		}

		return response;
	}

	/**
	 * Get tasks that occur within 7 days
	 * 
	 * @param donStorage
	 *            the storage in which the tasks are located
	 * @return
	 */
	private IDonResponse findSevenDays(IDonStorage donStorage) {
		Calendar start = CalHelper.getTodayStart();
		Calendar end = CalHelper.getDayEnd(CalHelper.getDaysFromNow(7));
		IDonResponse response = findTaskRange(donStorage, start, end,
				FIND_INCOMPLETE);
		return response;
	}

	/**
	 * Get tasks that occur after 7 days
	 * 
	 * @param donStorage
	 *            the storage in which the tasks are located
	 * @return
	 */
	private IDonResponse findFuture(IDonStorage donStorage) {
		Calendar start = CalHelper.getDayEnd(CalHelper.getDaysFromNow(7));
		IDonResponse response = findTaskRange(donStorage, start, null,
				FIND_INCOMPLETE);
		return response;
	}

	private IDonResponse findOverdue(IDonStorage donStorage) {
		IDonResponse response = findTaskRange(donStorage, null,
				Calendar.getInstance(), FIND_INCOMPLETE);
		List<IDonTask> taskList = new ArrayList<IDonTask>();
		for (IDonTask task : response.getTasks()) {
			if ((task.getEndDate() != null
					&& CalHelper.dateEqualOrBefore(task.getEndDate(),
							Calendar.getInstance())) || task.getEndDate()==null) {
				taskList.add(task);
			} 
		}
		response.setTaskList(taskList);
		return response;
	}

	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = null;
		if (type == SearchType.SEARCH_ID) {
			response = findTaskByID(donStorage);
		} else if (type == SearchType.SEARCH_NAME) {
			response = findTaskByName(donStorage);
		} else if (type == SearchType.SEARCH_AFTDATE) {
			response = findTaskRange(donStorage, searchStartDate, null,
					FIND_INCOMPLETE);
		} else if (type == SearchType.SEARCH_DATE) {
			response = findTaskByDate(donStorage);
		} else if (type == SearchType.SEARCH_UNDONE) {
			response = findUndone(donStorage);
		} else if (type == SearchType.SEARCH_LABEL) {
			response = findLabel(donStorage);
		} else if (type == SearchType.SEARCH_FREE) {
			response = findFreeTime(donStorage);
		} else if (type == SearchType.SEARCH_ALL) {
			response = findAll(donStorage);
		} else if (type == SearchType.OVERDUE) {
			response = findOverdue(donStorage);
		} else if (type == SearchType.TODAY) {
			response = getTasksToday(donStorage);
		} else if (type == SearchType.SEVEN_DAYS) {
			response = findSevenDays(donStorage);
		} else if (type == SearchType.FUTURE) {
			response = findFuture(donStorage);
		}
		return response;
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		// Find cannot be undone
		return null;
	}

}

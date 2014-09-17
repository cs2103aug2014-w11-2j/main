import java.util.Calendar;

/**
 * Class containing the properties of a task
 * 
 * @author cs2103aug2014-w11-2j
 * 
 */
public class DonTask implements IDonTask {

	private String taskTitle;
	private Calendar startDate, endDate;
	private boolean status;

	/**
	 * Creates an empty task
	 */
	public DonTask() {
		taskTitle = null;
		startDate = null;
		endDate = null;
		status = false;
	}

	public DonTask(String title) {
		taskTitle = title;
		startDate = null;
		endDate = null;
		status = false;
	}

	public DonTask(String title, Calendar deadline) {
		taskTitle = title;
		startDate = deadline;
		endDate = null;
		status = false;
	}

	public DonTask(String title, Calendar startDate, Calendar endDate) {
		taskTitle = title;
		this.startDate = startDate;
		this.endDate = endDate;
		status = false;
	}

	@Override
	public String getTitle() {
		return taskTitle;
	}

	@Override
	public Calendar getStartDate() {
		return startDate;
	}

	@Override
	public Calendar getEndDate() {
		return endDate;
	}

	@Override
	public boolean getStatus() {
		return status;
	}

	@Override
	public void setTitle(String newTitle) {
		taskTitle = newTitle;
	}

	@Override
	public void setStartDate(Calendar newDate) {
		startDate = newDate;
	}

	@Override
	public void setEndDate(Calendar newDate) {
		endDate = newDate;
	}

	@Override
	public void setStatus(boolean newStatus) {
		status = newStatus;
	}

	@Override
	public TaskType getType() {
		if (startDate == null) {
			return TaskType.TASK_FLOATING;
		} else if (endDate == null) {
			return TaskType.TASK_DEADLINE;
		} else {
			return TaskType.TASK_DURATION;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof IDonTask)) {
			return false;
		}

		IDonTask otherTask = (IDonTask) other;
		if (this.getTitle() == null || otherTask.getTitle() == null) {
			// We treat tasks with null titles as incomparable
			return false;
		}

		if (this.getType() != otherTask.getType()) {
			return false;
		}

		if (this.getType() == TaskType.TASK_FLOATING) {
			// A floating task only needs to have its title compared
			if (this.getTitle() == otherTask.getTitle()) {
				return true;
			}
		} else if (this.getType() == TaskType.TASK_DEADLINE) {
			if (this.getTitle() == otherTask.getTitle()
					&& this.getStartDate().equals(otherTask.getStartDate())) {
				return true;
			}
		} else if(this.getType() == TaskType.TASK_DURATION) {
			if (this.getTitle() == otherTask.getTitle()
					&& this.getStartDate().equals(otherTask.getStartDate())
					&& this.getEndDate().equals(otherTask.getEndDate())) {
				return true;
			}
		}
		return false;

	}
}

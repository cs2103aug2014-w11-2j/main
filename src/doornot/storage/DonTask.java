package doornot.storage;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Class containing the properties of a task
 */
//@author A0111995Y
public class DonTask implements IDonTask {

	private String taskTitle;
	private Calendar startDate, endDate;
	private boolean status;
	private int taskID;

	public DonTask(String title, int ID) {
		taskTitle = title;
		startDate = null;
		endDate = null;
		status = false;
		taskID = ID;
	}

	public DonTask(String title, Calendar deadline, int ID) {
		taskTitle = title;
		startDate = deadline;
		endDate = null;
		status = false;
		taskID = ID;
	}

	public DonTask(String title, Calendar startDate, Calendar endDate, int ID) {
		taskTitle = title;
		this.startDate = startDate;
		this.endDate = endDate;
		status = false;
		taskID = ID;
	}

	@Override
	public int getID() {

		return taskID;
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
			return TaskType.FLOATING;
		} else if (endDate == null) {
			return TaskType.DEADLINE;
		} else {
			return TaskType.DURATION;
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

		if (this.getType() == TaskType.FLOATING) {
			// A floating task only needs to have its title compared
			if (this.getTitle() == otherTask.getTitle()) {
				return true;
			}
		} else if (this.getType() == TaskType.DEADLINE) {
			if (this.getTitle() == otherTask.getTitle()
					&& this.getStartDate().equals(otherTask.getStartDate())) {
				return true;
			}
		} else if (this.getType() == TaskType.DURATION) {
			if (this.getTitle() == otherTask.getTitle()
					&& this.getStartDate().equals(otherTask.getStartDate())
					&& this.getEndDate().equals(otherTask.getEndDate())) {
				return true;
			}
		}
		return false;

	}

	@Override
	public int compareTo(IDonTask otherTask) {
		if (this.getType() == TaskType.FLOATING) {
			// For floating tasks the title will be compared based on
			// lexicographic ordering
			return compareTitle(otherTask);
		} else if (this.getType() == TaskType.DEADLINE) {
			// For deadline tasks the deadline will be compared first
			// with an earlier deadline being "less than" a later deadline
			if (otherTask.getType()==TaskType.FLOATING) {
				//If a deadline task is being compared to a floating one,
				//only the title can be compared.
				return compareTitle(otherTask);
			}
			int startDateComp = compareStartDate(otherTask);
			if (startDateComp == 0) {
				return compareTitle(otherTask);
			}
			return startDateComp;
			

		} else if (this.getType() == TaskType.DURATION) {
			// For tasks with a duration the start date will be compared first
			// with an earlier start date being "less than" the later one.
			// If they are equal the end date will be compared with the earlier
			// one being "less than" the later one.
			// If both start and end dates are equivalent the title will be
			// compared.
			if (otherTask.getType()==TaskType.FLOATING) {
				//If a duration task is being compared to a floating one,
				//only the title can be compared.
				return compareTitle(otherTask);
			} else if (otherTask.getType()==TaskType.DEADLINE) {
				//If a duration task is being compared to a deadline task,
				//only the start date and title can be compared.
				int startDateComp = compareStartDate(otherTask);
				if (startDateComp == 0) {
					return compareTitle(otherTask);
				}
				return startDateComp;
			} else {
				int startDateComp = compareStartDate(otherTask);
				int endDateComp = compareEndDate(otherTask);
				if (startDateComp == 0 && endDateComp != 0) {
					return endDateComp;
				} else if (startDateComp == 0 && endDateComp == 0) {
					return compareTitle(otherTask);
				}

				return startDateComp;
			}
			
		}
		return 0;
	}
	
	/**
	 * Helper method for compareTo to utilize.
	 * Compares the title of the current and another task. Assumes title exists
	 * @param otherTask	the task to compare with
	 * @return -1 if the title is smaller, 0 if they are the same, 1 if the other task is larger
	 */
	private int compareTitle(IDonTask otherTask) {
		return this.getTitle().compareTo(otherTask.getTitle());
	}
	
	/**
	 * Helper method for compareTo to utilize.
	 * Compares the start date of the current and another task.
	 * Assumes that start date is not null
	 * @param otherTask the task to compare with
	 * @return -1 if the title starts earlier, 0 if they are the same, 1 if the other task starts later
	 */
	private int compareStartDate(IDonTask otherTask) {
		return this.getStartDate().compareTo(otherTask.getStartDate());
	}
	
	/**
	 * Helper method for compareTo to utilize.
	 * Compares the end date of the current and another task.
	 * Assumes that end date is not null
	 * @param otherTask the task to compare with
	 * @return -1 if the title ends earlier, 0 if they are the same, 1 if the other task ends later
	 */
	private int compareEndDate(IDonTask otherTask) {
		return this.getEndDate().compareTo(otherTask.getEndDate());
	}

	@Override
	public DonTask clone() {
		DonTask newTask = new DonTask(taskTitle, taskID);
		if(this.getStartDate()!=null) {
			newTask.setStartDate((Calendar) this.getStartDate().clone());
		}
		if(this.getEndDate()!=null) {
			newTask.setEndDate((Calendar) this.getEndDate().clone());
		}
		
		newTask.setStatus(this.getStatus());
		return newTask;

	}
	
	@Override
	public void copyTaskDetails(IDonTask sourceTask) {
		this.setTitle(sourceTask.getTitle());
		if(this.getStartDate()!=null) {
			this.setStartDate((Calendar) sourceTask.getStartDate().clone());
		}
		if(this.getEndDate()!=null) {
			this.setEndDate((Calendar) sourceTask.getEndDate().clone());
		}
		
		this.setStatus(sourceTask.getStatus());
	}

	/**
	 * Comparator to help sort DonTasks by ID instead of name/date
	 */
	public static class IDComparator implements Comparator<IDonTask> {
		@Override
		public int compare(IDonTask task1, IDonTask task2) {
			return (task1.getID() - task2.getID());
		}

	}
}
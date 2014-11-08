package doornot.util;

import java.util.Comparator;

import doornot.storage.IDonTask;
import doornot.storage.IDonTask.TaskType;

/**
 * Comparator to put deadline tasks in front of duration tasks for the Today
 * panel
 */
// @author A0111995
public class DateTaskComparator implements Comparator<IDonTask> {

	@Override
	public int compare(IDonTask task1, IDonTask task2) {
		assert task1.getType() != TaskType.FLOATING
				&& task2.getType() != TaskType.FLOATING;
		if (task1.getType() == TaskType.DEADLINE) {
			if (task2.getType() == TaskType.DURATION) {
				return -1;
			}

			int startDateComp = task1.getStartDate().compareTo(
					task2.getStartDate());
			if (startDateComp == 0) {
				return task1.getTitle().compareTo(task2.getTitle());
			}

			return startDateComp;

		} else if (task1.getType() == TaskType.DURATION) {
			if (task2.getType() == TaskType.DEADLINE) {
				return 1;
			}
			int startDateComp = task1.getStartDate().compareTo(
					task2.getStartDate());
			int endDateComp = task1.getEndDate().compareTo(task2.getEndDate());
			if (startDateComp == 0 && endDateComp != 0) {
				return endDateComp;
			} else if (startDateComp == 0 && endDateComp == 0) {
				return task1.getTitle().compareTo(task2.getTitle());
			}

			return startDateComp;
		}
		return 0;
	}

}

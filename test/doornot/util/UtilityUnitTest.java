package doornot.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import doornot.DonStorageTestStub;
import doornot.storage.IDonTask;
import doornot.storage.IDonTask.TaskType;
import doornot.util.CalHelper;
import doornot.util.SearchHelper;

//@author A0111995Y
public class UtilityUnitTest {

	// SearchHelper tests
	@Test
	public void testFindDone() {
		DonStorageTestStub testStorage = new DonStorageTestStub();
		List<IDonTask> taskList = new ArrayList<IDonTask>();
		IDonTask t1 = new DonTaskTestStub(0, TaskType.FLOATING);
		taskList.add(t1);
		IDonTask t2 = new DonDoneTask(1, TaskType.FLOATING);
		taskList.add(t2);

		testStorage.setTaskList(taskList);
		List<IDonTask> results = SearchHelper.findDone(testStorage);
		assertEquals(1, results.size());
		assertEquals(1, results.get(0).getID());
	}

	@Test
	public void testFindOverdue() {
		DonStorageTestStub testStorage = new DonStorageTestStub();
		List<IDonTask> taskList = new ArrayList<IDonTask>();
		IDonTask t1 = new DonTaskTestStub(0, TaskType.FLOATING);
		taskList.add(t1);
		IDonTask t2 = new DonOverdueTask(1, TaskType.DEADLINE);
		taskList.add(t2);
		IDonTask t3 = new DonOverdueTask(2, TaskType.DURATION);
		taskList.add(t3);
		// The task below is still occurring
		IDonTask presentBetweenTask = new DonDateTask(3, TaskType.DURATION,
				new GregorianCalendar(1990, 1, 1), new GregorianCalendar(2222,
						1, 2));
		taskList.add(presentBetweenTask);

		testStorage.setTaskList(taskList);
		List<IDonTask> results = SearchHelper.findOverdue(testStorage);
		assertEquals(2, results.size());
		assertEquals(1, results.get(0).getID());
	}

	/**
	 * CalHelper test
	 */
	@Test
	public void testCalendarGetDayStart() {
		Calendar result = CalHelper.getDayStart(new GregorianCalendar(2014, 2,
				1, 10, 20));
		assertEquals(new GregorianCalendar(2014, 2, 1, 0, 0), result);
	}

	@Test
	public void testCalendarGetDayEnd() {
		Calendar result = CalHelper.getDayEnd(new GregorianCalendar(2014, 2, 1,
				10, 20));
		Calendar expected = new GregorianCalendar(2014, 2, 1, 23, 59, 59);
		expected.set(Calendar.MILLISECOND, 999);
		assertEquals(expected, result);
	}

	@Test
	public void testCalendarDateEqualAfter() {
		boolean result = CalHelper.dateEqualOrAfter(new GregorianCalendar(2014,
				2, 1, 10, 20), new GregorianCalendar(2012, 2, 1, 10, 20));
		assertTrue(result);

		boolean result2 = CalHelper.dateEqualOrAfter(new GregorianCalendar(
				2001, 2, 1, 10, 20), new GregorianCalendar(2012, 2, 1, 10, 20));
		assertFalse(result2);

		boolean result3 = CalHelper.dateEqualOrAfter(new GregorianCalendar(
				2012, 2, 1, 10, 20), new GregorianCalendar(2012, 2, 1, 10, 20));
		assertTrue(result3);
	}

	@Test
	public void testCalendarDateEqualBefore() {
		boolean result = CalHelper.dateEqualOrBefore(new GregorianCalendar(
				2014, 2, 1, 10, 20), new GregorianCalendar(2012, 2, 1, 10, 20));
		assertFalse(result);

		boolean result2 = CalHelper.dateEqualOrBefore(new GregorianCalendar(
				2001, 2, 1, 10, 20), new GregorianCalendar(2012, 2, 1, 10, 20));
		assertTrue(result2);

		boolean result3 = CalHelper.dateEqualOrBefore(new GregorianCalendar(
				2012, 2, 1, 10, 20), new GregorianCalendar(2012, 2, 1, 10, 20));
		assertTrue(result3);
	}

	@Test
	public void testCalendarSameDay() {
		boolean result = CalHelper.isSameDay(new GregorianCalendar(2012, 2, 1,
				10, 20), new GregorianCalendar(2012, 2, 1, 9, 1));
		assertTrue(result);

		boolean result2 = CalHelper.isSameDay(new GregorianCalendar(2012, 2, 2,
				0, 0), new GregorianCalendar(2012, 2, 1, 9, 1));
		assertFalse(result2);
	}

	@Test
	public void testCalendarBetweenDates() {
		boolean result = CalHelper.isBetweenDates(new GregorianCalendar(2011,
				3, 1), new GregorianCalendar(2011, 2, 1, 10, 20),
				new GregorianCalendar(2012, 2, 1, 9, 1));
		assertTrue(result);

		boolean result2 = CalHelper.isBetweenDates(new GregorianCalendar(2011,
				3, 1), new GregorianCalendar(2012, 2, 2, 0, 0),
				new GregorianCalendar(2012, 2, 1, 9, 1));
		assertFalse(result2);
	}
	
	@Test
	public void testCalendarRelevantEquals() {
		boolean result = CalHelper.relevantEquals(new GregorianCalendar(2012, 2, 1, 9, 1),
				new GregorianCalendar(2012, 2, 1, 9, 1));
		assertTrue(result);

		
		Calendar a = new GregorianCalendar(2011,2,1,9,1);
		Calendar b = new GregorianCalendar(2011,2,1,9,1);
		b.set(Calendar.SECOND, 20);
		boolean result2 = CalHelper.relevantEquals(a, b);
		assertTrue(result2);
		
		boolean result3 = CalHelper.relevantEquals(new GregorianCalendar(2012, 2, 2, 9, 1),
				new GregorianCalendar(2012, 2, 1, 9, 1));
		assertFalse(result3);
	}

	private class DonTaskTestStub implements IDonTask {
		private int id;
		TaskType type;

		public DonTaskTestStub(int id, IDonTask.TaskType type) {
			this.id = id;
			this.type = type;
		}

		@Override
		public int compareTo(IDonTask o) {
			return 0;
		}

		@Override
		public int getID() {
			return id;
		}

		@Override
		public String getTitle() {
			return null;
		}

		@Override
		public Calendar getStartDate() {
			return null;
		}

		@Override
		public Calendar getEndDate() {
			return null;
		}

		@Override
		public boolean getStatus() {
			return false;
		}

		@Override
		public TaskType getType() {
			return type;
		}

		@Override
		public List<String> getLabels() {
			return null;
		}

		@Override
		public void setTitle(String newTitle) {

		}

		@Override
		public void setStartDate(Calendar newDate) {

		}

		@Override
		public void setEndDate(Calendar newDate) {

		}

		@Override
		public void setStatus(boolean newStatus) {

		}

		@Override
		public void setLabels(List<String> newLabels) {

		}

		@Override
		public void copyTaskDetails(IDonTask sourceTask) {

		}

		@Override
		public boolean addLabel(String newLabel) {
			return false;
		}

		@Override
		public boolean deleteLabel(String labelToDelete) {
			return false;
		}

		@Override
		public void setTimeUsed(boolean timeUsed) {

		}

		@Override
		public boolean isTimeUsed() {
			return false;
		}

		@Override
		public IDonTask clone() {
			return null;
		}
	}

	private class DonDoneTask extends DonTaskTestStub {

		public DonDoneTask(int id, TaskType type) {
			super(id, type);
		}

		@Override
		public boolean getStatus() {
			return true;
		}
	}

	private class DonOverdueTask extends DonTaskTestStub {

		public DonOverdueTask(int id, TaskType type) {
			super(id, type);
		}

		@Override
		public Calendar getStartDate() {
			return new GregorianCalendar(1990, 1, 1);
		}
	}

	private class DonDateTask extends DonTaskTestStub {
		private Calendar start, end;

		public DonDateTask(int id, TaskType type, Calendar start, Calendar end) {
			super(id, type);
			this.start = start;
			this.end = end;
		}

		@Override
		public Calendar getStartDate() {
			return start;
		}

		@Override
		public Calendar getEndDate() {
			return end;
		}
	}

}

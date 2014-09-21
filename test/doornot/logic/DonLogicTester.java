package doornot.logic;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class DonLogicTester {

	@Test
	public void testAddFloatingTask() {
		DonLogic logic = new DonLogic();
		IDonResponse response = logic.runCommand("add Finish homework");
		IDonTask task = response.getTasks().get(0);
		assertEquals("Finish homework", task.getTitle());
		assertEquals(null, task.getStartDate());
		assertEquals(null, task.getEndDate());
		assertEquals(IDonTask.TaskType.FLOATING, task.getType());
	}
	
	@Test
	public void testAddDeadlineTask() {
		DonLogic logic = new DonLogic();
		IDonResponse response = logic.runCommand("add Finish homework 21122014_2030");
		IDonTask task = response.getTasks().get(0);
		assertEquals("Finish homework", task.getTitle());
		Calendar deadline = task.getStartDate();
		assertEquals(2014, deadline.get(Calendar.YEAR));
		assertEquals(Calendar.DECEMBER, deadline.get(Calendar.MONTH));
		assertEquals(21, deadline.get(Calendar.DAY_OF_MONTH));
		assertEquals(20, deadline.get(Calendar.HOUR_OF_DAY));
		assertEquals(30, deadline.get(Calendar.MINUTE));
		assertEquals(null, task.getEndDate());
		assertEquals(IDonTask.TaskType.DEADLINE, task.getType());
	}
	
	@Test
	public void testAddDurationTask() {
		DonLogic logic = new DonLogic();
		IDonResponse response = logic.runCommand("add Finish homework 21122014_2030 21122014_2356");
		IDonTask task = response.getTasks().get(0);
		assertEquals("Finish homework", task.getTitle());
		Calendar startDate = task.getStartDate();
		Calendar endDate = task.getEndDate();
		assertEquals(2014, startDate.get(Calendar.YEAR));
		assertEquals(Calendar.DECEMBER, startDate.get(Calendar.MONTH));
		assertEquals(21, startDate.get(Calendar.DAY_OF_MONTH));
		assertEquals(20, startDate.get(Calendar.HOUR_OF_DAY));
		assertEquals(30, startDate.get(Calendar.MINUTE));
		
		assertEquals(2014, endDate.get(Calendar.YEAR));
		assertEquals(Calendar.DECEMBER, endDate.get(Calendar.MONTH));
		assertEquals(21, endDate.get(Calendar.DAY_OF_MONTH));
		assertEquals(23, endDate.get(Calendar.HOUR_OF_DAY));
		assertEquals(56, endDate.get(Calendar.MINUTE));
		assertEquals(IDonTask.TaskType.DURATION, task.getType());
	}

}

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
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS, response.getResponseType());
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
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS, response.getResponseType());
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
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS, response.getResponseType());
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
	
	/*
	 * Deletion tests
	 * 
	 */
	
	@Test
	public void testDeleteTaskWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add Finish homework");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS, addResponse.getResponseType());
		int taskID = addResponse.getTasks().get(0).getID();
		IDonResponse delResponse = logic.runCommand("del "+taskID);
		assertEquals(IDonResponse.ResponseType.DEL_SUCCESS, delResponse.getResponseType());
		IDonTask deletedTask = delResponse.getTasks().get(0);
		assertEquals(taskID, deletedTask.getID());
		assertEquals("Finish homework", deletedTask.getTitle());

	}
	
	/*
	 * Editing tests
	 */
	@Test
	public void testEditTitleWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add Finish homework");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS, addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("edit "+changedTask.getID()+" Ignore homework");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS, editResponse.getResponseType());
		assertEquals("Ignore homework", changedTask.getTitle());
	}
	
	@Test
	public void testEditDeadlineWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add Finish homework 10052014_0810");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS, addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("edit "+changedTask.getID()+" 21052014_1020");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS, editResponse.getResponseType());
		Calendar date = changedTask.getStartDate();
		assertEquals(21, date.get(Calendar.DAY_OF_MONTH));
		assertEquals(Calendar.MAY, date.get(Calendar.MONTH));
		assertEquals(2014, date.get(Calendar.YEAR));
		assertEquals(10, date.get(Calendar.HOUR_OF_DAY));
		assertEquals(20, date.get(Calendar.MINUTE));
	}
	
	@Test
	public void testEditDatesWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add Finish homework 10052014_0810 10052014_0850");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS, addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("edit "+changedTask.getID()+" 21052014_1020 21052014_1140");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS, editResponse.getResponseType());
		Calendar startDate = changedTask.getStartDate();
		Calendar endDate = changedTask.getEndDate();
		assertEquals(21, startDate.get(Calendar.DAY_OF_MONTH));
		assertEquals(Calendar.MAY, startDate.get(Calendar.MONTH));
		assertEquals(2014, startDate.get(Calendar.YEAR));
		assertEquals(10, startDate.get(Calendar.HOUR_OF_DAY));
		assertEquals(20, startDate.get(Calendar.MINUTE));
		
		assertEquals(21, endDate.get(Calendar.DAY_OF_MONTH));
		assertEquals(Calendar.MAY, endDate.get(Calendar.MONTH));
		assertEquals(2014, endDate.get(Calendar.YEAR));
		assertEquals(11, endDate.get(Calendar.HOUR_OF_DAY));
		assertEquals(40, endDate.get(Calendar.MINUTE));
	}

}

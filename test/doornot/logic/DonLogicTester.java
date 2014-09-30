package doornot.logic;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

//@author A0111995Y
public class DonLogicTester {

	@Test
	public void testAddFloatingTask() {
		DonLogic logic = new DonLogic();
		IDonResponse response = logic.runCommand("add \"Finish homework\"");
		IDonTask task = response.getTasks().get(0);
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				response.getResponseType());
		assertEquals("Finish homework", task.getTitle());
		assertEquals(null, task.getStartDate());
		assertEquals(null, task.getEndDate());
		assertEquals(IDonTask.TaskType.FLOATING, task.getType());
	}

	@Test
	public void testAddDeadlineTask() {
		DonLogic logic = new DonLogic();
		IDonResponse response = logic
				.runCommand("add \"Finish homework\" at 21122014_2030");
		IDonTask task = response.getTasks().get(0);
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				response.getResponseType());
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
		IDonResponse response = logic
				.runCommand("add \"Finish homework\" from 21122014_2030 to 21122014_2356");
		IDonTask task = response.getTasks().get(0);
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				response.getResponseType());
		assertEquals("Finish homework", task.getTitle());
		System.out.println(task.getStartDate().getTime().toString());
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
	 */

	@Test
	public void testDeleteTaskWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		int taskID = addResponse.getTasks().get(0).getID();
		
		IDonResponse delResponse = logic.runCommand("del " + taskID);
		assertEquals(IDonResponse.ResponseType.DEL_SUCCESS,
				delResponse.getResponseType());
		IDonTask deletedTask = delResponse.getTasks().get(0);
		assertEquals(taskID, deletedTask.getID());
		assertEquals("Finish homework", deletedTask.getTitle());

	}
	
	@Test
	public void testDeleteTaskWithName() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		int taskID = addResponse.getTasks().get(0).getID();
		
		IDonResponse delResponse = logic.runCommand("del \"Finish\"");
		assertEquals(IDonResponse.ResponseType.DEL_SUCCESS,
				delResponse.getResponseType());
		IDonTask deletedTask = delResponse.getTasks().get(0);
		assertEquals(taskID, deletedTask.getID());
		assertEquals("Finish homework", deletedTask.getTitle());

	}
	
	/*
	 * Search tests
	 */
	@Test
	public void testSearchWithName() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Do homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask addedTask = addResponse.getTasks().get(0);
		
		IDonResponse searchResponse = logic.runCommand("s \"homework\"");
		assertEquals(IDonResponse.ResponseType.SEARCH_SUCCESS,
				searchResponse.getResponseType());
		IDonTask foundTask = searchResponse.getTasks().get(0);
		
		assertEquals(addedTask, foundTask);
	}
	
	@Test
	public void testSearchWithNameFail() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Do homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());

		IDonResponse searchResponse = logic.runCommand("s \"do work\"");
		assertEquals(IDonResponse.ResponseType.SEARCH_EMPTY,
				searchResponse.getResponseType());
	}
	
	@Test
	public void testSearchWithDate() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Do homework\" @ 26012015");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask addedTask = addResponse.getTasks().get(0);
		
		IDonResponse addResponse2 = logic.runCommand("add \"Complete walk\" @ 26012015");
		IDonTask addedTask2 = addResponse2.getTasks().get(0);
		
		IDonResponse addResponse3 = logic.runCommand("add \"Complete walk\" @ 27012015");
		IDonTask addedTask3 = addResponse3.getTasks().get(0);
		
		IDonResponse searchResponse = logic.runCommand("search 26012015");
		assertEquals(IDonResponse.ResponseType.SEARCH_SUCCESS,
				searchResponse.getResponseType());
		
		assertEquals(true, searchResponse.getTasks().contains(addedTask));
		assertEquals(true, searchResponse.getTasks().contains(addedTask2));
		assertEquals(false, searchResponse.getTasks().contains(addedTask3));
	}
	

	/*
	 * Editing tests
	 */
	@Test
	public void testEditTitleWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("edit "
				+ changedTask.getID() + " to \"Ignore homework\"");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				editResponse.getResponseType());
		assertEquals("Ignore homework", changedTask.getTitle());
	}

	@Test
	public void testEditDeadlineWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic
				.runCommand("add \"Finish homework\" @ 10052014_0810");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("edit "
				+ changedTask.getID() + " to 21052014_1020");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				editResponse.getResponseType());
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
		IDonResponse addResponse = logic
				.runCommand("add \"Finish homework\" from 10052014_0810 to 10052014_0850");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("edit "
				+ changedTask.getID() + " to from 21052014_1020 to 21052014_1140");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				editResponse.getResponseType());
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
	
	@Test
	public void testMarkTaskWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("mark "+changedTask.getID());
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				editResponse.getResponseType());
		assertEquals(true, changedTask.getStatus());
	}
	
	@Test
	public void testMarkTaskWithIDFail() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("mark "+(changedTask.getID()+1));
		assertEquals(IDonResponse.ResponseType.SEARCH_EMPTY,
				editResponse.getResponseType());
		assertEquals(false, changedTask.getStatus());
	}
	
	@Test
	public void testMarkTaskWithTitle() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("mark \"homework\"");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				editResponse.getResponseType());
		assertEquals(true, changedTask.getStatus());
	}

	/*
	 * Undo tests
	 */

	@Test
	public void testUndoAfterAdd() {
		DonLogic logic = new DonLogic();
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		int taskID = addResponse.getTasks().get(0).getID();
		
		IDonResponse addResponse2 = logic.runCommand("add \"Eat food\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse2.getResponseType());
		int taskID2 = addResponse2.getTasks().get(0).getID();

		IDonResponse undoResponse = logic.runCommand("undo");
		assertEquals(IDonResponse.ResponseType.UNDO_SUCCESS,
				undoResponse.getResponseType());
		
		IDonResponse searchResponse = logic.runCommand("s " + taskID2);
		assertEquals(IDonResponse.ResponseType.SEARCH_EMPTY,
				searchResponse.getResponseType());
		
		IDonResponse undoResponse2 = logic.runCommand("undo");
		assertEquals(IDonResponse.ResponseType.UNDO_SUCCESS,
				undoResponse2.getResponseType());

		IDonResponse searchResponse2 = logic.runCommand("s " + taskID);
		assertEquals(IDonResponse.ResponseType.SEARCH_EMPTY,
				searchResponse2.getResponseType());

	}

	@Test
	public void testUndoAfterDelete() {
		DonLogic logic = new DonLogic();
		// Add
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		int taskID = addResponse.getTasks().get(0).getID();
		// Delete
		IDonResponse delResponse = logic.runCommand("del " + taskID);
		assertEquals(IDonResponse.ResponseType.DEL_SUCCESS,
				delResponse.getResponseType());
		IDonTask deletedTask = delResponse.getTasks().get(0);
		// Run a search which should return empty
		IDonResponse searchResponse = logic.runCommand("s " + taskID);
		assertEquals(IDonResponse.ResponseType.SEARCH_EMPTY,
				searchResponse.getResponseType());
		// Undo the deletion
		IDonResponse undoResponse = logic.runCommand("undo");
		assertEquals(IDonResponse.ResponseType.UNDO_SUCCESS,
				undoResponse.getResponseType());

		IDonResponse searchResponse2 = logic.runCommand("s " + taskID);
		assertEquals(IDonResponse.ResponseType.SEARCH_SUCCESS,
				searchResponse2.getResponseType());
		IDonTask readdedTask = searchResponse2.getTasks().get(0);
		assertEquals(readdedTask, deletedTask);
	}

	@Test
	public void testUndoAfterEdit() {
		DonLogic logic = new DonLogic();
		// Add
		IDonResponse addResponse = logic.runCommand("add \"Finish homework\"");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask beforeEditTask = addResponse.getTasks().get(0).clone();
		int taskID = addResponse.getTasks().get(0).getID();
		// Edit
		IDonResponse editResponse = logic.runCommand("edit " + taskID
				+ " to \"Do work\"");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				editResponse.getResponseType());

		// Undo the edit
		IDonResponse undoResponse = logic.runCommand("undo");
		assertEquals(IDonResponse.ResponseType.UNDO_SUCCESS,
				undoResponse.getResponseType());

		IDonResponse searchResponse = logic.runCommand("s " + taskID);
		assertEquals(IDonResponse.ResponseType.SEARCH_SUCCESS,
				searchResponse.getResponseType());
		IDonTask uneditedTask = searchResponse.getTasks().get(0);
		assertEquals(uneditedTask, beforeEditTask);

	}
}

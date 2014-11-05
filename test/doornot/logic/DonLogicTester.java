package doornot.logic;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import doornot.parser.DonParser;
import doornot.storage.DonStorage;
import doornot.storage.IDonTask;
import edu.emory.mathcs.backport.java.util.Collections;

//@author A0111995Y
public class DonLogicTester {
	
	private static DonLogic logic;
	private static DonStorage storage;
	@BeforeClass
	public static void initLogic() {
		storage = new DonStorage();
		storage.changeFileName("logic_test");
		logic = new DonLogic(storage, new DonParser(), false);
	}
	
	@Before
	public void removeTestFile() {
		File f = new File("logic_test");
		f.delete();
		storage.getTaskList().clear();
	}

	@Test
	public void testAddFloatingTask() {
		
		IDonResponse response = logic.runCommand("addf Finish homework");
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
		
		IDonResponse response = logic
				.runCommand("add Finish homework by 21/12/2014 2030");
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
		
		IDonResponse response = logic
				.runCommand("add Finish homework from 21/12/2014 2030 to 21/12/2014 2356");
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
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
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
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		int taskID = addResponse.getTasks().get(0).getID();
		
		IDonResponse delResponse = logic.runCommand("del Finish");
		assertEquals(IDonResponse.ResponseType.DEL_SUCCESS,
				delResponse.getResponseType());
		IDonTask deletedTask = delResponse.getTasks().get(0);
		assertEquals(taskID, deletedTask.getID());
		assertEquals("Finish homework", deletedTask.getTitle());

	}
	
	@Test
	public void testDeleteOverdueTasks() {
		List<IDonTask> addedTasks = new ArrayList<IDonTask>();
		IDonResponse addResponse1 = logic.runCommand("add Finish homework by 5 days ago");
		addedTasks.add(addResponse1.getTasks().get(0));
		IDonResponse addResponse2 = logic.runCommand("add Do work by 2 days ago");
		addedTasks.add(addResponse2.getTasks().get(0));
		Collections.sort(addedTasks);
		
		IDonResponse delResponse = logic.runCommand("del overdue");
		assertEquals(IDonResponse.ResponseType.DEL_SUCCESS,
				delResponse.getResponseType());
		Collections.sort(delResponse.getTasks());
		for(int i=0; i<delResponse.getTasks().size(); i++) {
			assertEquals(delResponse.getTasks().get(i), addedTasks.get(i));
		}
	}
	
	@Test
	public void testDeleteCompletedTasks() {
		List<IDonTask> addedTasks = new ArrayList<IDonTask>();
		IDonResponse addResponse1 = logic.runCommand("add Finish homework by 5 days ago");
		addedTasks.add(addResponse1.getTasks().get(0));
		logic.runCommand("mark Finish");
		IDonResponse addResponse2 = logic.runCommand("add Do work by 2 days ago");
		addedTasks.add(addResponse2.getTasks().get(0));
		logic.runCommand("mark Do work");
		Collections.sort(addedTasks);
		logic.runCommand("add Do not complete this task");
		
		IDonResponse delResponse = logic.runCommand("del done");
		assertEquals(IDonResponse.ResponseType.DEL_SUCCESS,
				delResponse.getResponseType());
		Collections.sort(delResponse.getTasks());
		for(int i=0; i<delResponse.getTasks().size(); i++) {
			assertEquals(delResponse.getTasks().get(i), addedTasks.get(i));
		}
	}
	
	/*
	 * Search tests
	 */
	@Test
	public void testSearchWithName() {
		
		IDonResponse addResponse = logic.runCommand("addf Do homework");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask addedTask = addResponse.getTasks().get(0);
		
		IDonResponse searchResponse = logic.runCommand("s homework");
		assertEquals(IDonResponse.ResponseType.SEARCH_SUCCESS,
				searchResponse.getResponseType());
		IDonTask foundTask = searchResponse.getTasks().get(0);
		
		assertEquals(addedTask, foundTask);
	}
	
	@Test
	public void testSearchWithNameFail() {
		
		IDonResponse addResponse = logic.runCommand("addf Do homework");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());

		IDonResponse searchResponse = logic.runCommand("s do work");
		assertEquals(IDonResponse.ResponseType.SEARCH_EMPTY,
				searchResponse.getResponseType());
	}
	
	@Test
	public void testSearchWithDate() {
		
		IDonResponse addResponse = logic.runCommand("add Do homework by 26/01/2015");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask addedTask = addResponse.getTasks().get(0);
		
		IDonResponse addResponse2 = logic.runCommand("add Complete walk by 26/01/2015");
		IDonTask addedTask2 = addResponse2.getTasks().get(0);
		
		IDonResponse addResponse3 = logic.runCommand("add Complete walk by 27/01/2015");
		IDonTask addedTask3 = addResponse3.getTasks().get(0);
		
		IDonResponse searchResponse = logic.runCommand("son 26/01/2015");
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
		
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
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
		
		IDonResponse addResponse = logic
				.runCommand("add Finish homework by 10/05/2014 0810");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("edit "
				+ changedTask.getID() + " by 21/05/2014 1020");
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
		
		IDonResponse addResponse = logic
				.runCommand("add Finish homework from 10/05/2014 0810 to 10/05/2014 0850");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("edit "
				+ changedTask.getID() + " from 21/05/2014 1020 to 21/05/2014 1140");
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
		
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
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
		
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
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
		
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask changedTask = addResponse.getTasks().get(0);
		IDonResponse editResponse = logic.runCommand("mark homework");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				editResponse.getResponseType());
		assertEquals(true, changedTask.getStatus());
	}

	/*
	 * Undo tests
	 */

	@Test
	public void testUndoAfterAdd() {
		
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		int taskID = addResponse.getTasks().get(0).getID();
		
		IDonResponse addResponse2 = logic.runCommand("addf Eat food");
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
		
		// Add
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
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
		
		// Add
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
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
	
	/*
	 * Redo 
	 */
	
	@Test
	public void testRedoAfterAdd() {
		// Add
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
		IDonTask task1 = addResponse.getTasks().get(0).clone();
		int taskID1 = addResponse.getTasks().get(0).getID();
		
		IDonResponse addResponse2 = logic.runCommand("add Eat by 21/10/2014");
		IDonTask task2 = addResponse2.getTasks().get(0).clone();
		int taskID2 = addResponse2.getTasks().get(0).getID();

		// Undo the edits
		logic.runCommand("undo");
		logic.runCommand("undo");

		//Redo the edit
		IDonResponse redoResponse1 = logic.runCommand("redo");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				redoResponse1.getResponseType());

		IDonResponse searchResponse1 = logic.runCommand("s " + taskID1);
		IDonTask redoneTask1 = searchResponse1.getTasks().get(0);
		assertEquals(redoneTask1, task1);
		
		IDonResponse redoResponse2 = logic.runCommand("redo");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				redoResponse2.getResponseType());

		IDonResponse searchResponse2 = logic.runCommand("s " + taskID2);
		IDonTask redoneTask2 = searchResponse2.getTasks().get(0);
		assertEquals(redoneTask2, task2);

	}
	
	@Test
	public void testRedoAfterEdit() {
		// Add
		IDonResponse addResponse = logic.runCommand("addf Finish homework");
		assertEquals(IDonResponse.ResponseType.ADD_SUCCESS,
				addResponse.getResponseType());
		IDonTask beforeEditTask = addResponse.getTasks().get(0).clone();
		int taskID = addResponse.getTasks().get(0).getID();
		// Edit
		IDonResponse editResponse = logic.runCommand("edit " + taskID
				+ " to \"Do work\"");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				editResponse.getResponseType());
		IDonTask afterEditTask = editResponse.getTasks().get(0);

		// Undo the edit
		IDonResponse undoResponse = logic.runCommand("undo");
		assertEquals(IDonResponse.ResponseType.UNDO_SUCCESS,
				undoResponse.getResponseType());
		
		//Redo the edit
		IDonResponse redoResponse = logic.runCommand("redo");
		assertEquals(IDonResponse.ResponseType.EDIT_SUCCESS,
				redoResponse.getResponseType());

		IDonResponse searchResponse = logic.runCommand("s " + taskID);
		assertEquals(IDonResponse.ResponseType.SEARCH_SUCCESS,
				searchResponse.getResponseType());
		IDonTask editedTask = searchResponse.getTasks().get(0);
		assertEquals(editedTask, afterEditTask);

	}
	
	/*
	 * Label 
	 */
	
	@Test
	public void testLabelAdd() {
		// Add		
		IDonResponse addResponse1 = logic.runCommand("add Eat by 21/10/2014");
		IDonTask task1 = addResponse1.getTasks().get(0);
		int taskID1 = addResponse1.getTasks().get(0).getID();

		IDonResponse labelResponse1 = logic.runCommand("label "+taskID1+" #food");
		assertEquals(IDonResponse.ResponseType.LABEL_ADDED,
				labelResponse1.getResponseType());
		assertEquals(1, task1.getLabels().size());
		assertEquals("food", task1.getLabels().get(0));
		
		IDonResponse labelResponse2 = logic.runCommand("label Ea #good");
		assertEquals(IDonResponse.ResponseType.LABEL_ADDED,
				labelResponse2.getResponseType());
		assertEquals(2, task1.getLabels().size());
		assertEquals(true, task1.getLabels().contains("good"));

	}
	
	@Test
	public void testLabelRemove() {
		// Add		
		IDonResponse addResponse1 = logic.runCommand("add Eat by 21/10/2014");
		IDonTask task1 = addResponse1.getTasks().get(0);
		int taskID1 = addResponse1.getTasks().get(0).getID();

		logic.runCommand("label Ea #food");
		logic.runCommand("label Ea #good");
		
		IDonResponse delabelResponse1 = logic.runCommand("delabel "+taskID1+" #food");
		assertEquals(IDonResponse.ResponseType.LABEL_REMOVED,
				delabelResponse1.getResponseType());
		assertEquals(false, task1.getLabels().contains("food"));
		
		IDonResponse delabelResponse2 = logic.runCommand("delabel Ea #good");
		assertEquals(IDonResponse.ResponseType.LABEL_REMOVED,
				delabelResponse2.getResponseType());
		assertEquals(false, task1.getLabels().contains("good"));

	}
	
	@Test
	public void testLabelNotFound() {
		// Add		
		IDonResponse addResponse1 = logic.runCommand("add Eat by 21/10/2014");
		int taskID1 = addResponse1.getTasks().get(0).getID();

		logic.runCommand("label Ea #food");
		logic.runCommand("label Ea #good");
		
		IDonResponse delabelResponse1 = logic.runCommand("delabel "+taskID1+" #mood");
		assertEquals(IDonResponse.ResponseType.LABEL_NOT_FOUND,
				delabelResponse1.getResponseType());
		
		IDonResponse delabelResponse2 = logic.runCommand("delabel Eatup #good");
		assertEquals(IDonResponse.ResponseType.SEARCH_EMPTY,
				delabelResponse2.getResponseType());

	}
}

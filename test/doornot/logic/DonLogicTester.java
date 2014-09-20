package doornot.logic;

import static org.junit.Assert.*;

import org.junit.Test;

public class DonLogicTester {

	@Test
	public void testCreateFloatingTaskWithID() {
		DonLogic logic = new DonLogic();
		IDonResponse response = logic.runCommand("add Finish homework");
		IDonTask task = response.getTasks().get(0);
		assertEquals("Finish homework", task.getTitle());
		assertEquals(null, task.getStartDate());
		assertEquals(null, task.getEndDate());
		assertEquals(IDonTask.TaskType.FLOATING, task.getType());
	}

}

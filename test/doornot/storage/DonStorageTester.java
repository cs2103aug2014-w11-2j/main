package doornot.storage;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

//@author A0100493R
public class DonStorageTester {

	@Test
	public void testLoad() {
		DonStorage storage = new DonStorage();
		assertEquals(null, true, storage.loadFromDisk());
	}

	@Test
	public void testAdd() {
		DonStorage storage = new DonStorage();
		storage.changeFileName("testFile1.txt");
		storage.clear();
		if (storage.loadFromDisk()) {
			int ID = 3;
			IDonTask task = new DonTask("something", ID);
			assertEquals(null, 3, storage.addTask(task));
		}
	}

	@Test
	public void testRemove() {
		DonStorage storage = new DonStorage();
		storage.changeFileName("testFile2.txt");
		storage.clear();
		if (storage.loadFromDisk()) {
			int ID = 3;
			assertEquals(null, false, storage.removeTask(ID));
			IDonTask task = new DonTask("something", ID);
			ID = storage.addTask(task);
			assertEquals(null, true, storage.removeTask(ID));
		}
	}

	@Test
	public void testGetNext() {
		DonStorage storage = new DonStorage();
		storage.changeFileName("testFile3.txt");
		storage.clear();
		if (storage.loadFromDisk()) {
			assertEquals(null, 1, storage.getNextID());
			int ID = 1;
			IDonTask task = new DonTask("something", ID);
			ID = storage.addTask(task);
			assertEquals(null, 2, storage.getNextID());
			storage.removeTask(ID);
			assertEquals(null, 1, storage.getNextID());
		}

	}

	@Test
	public void testGetTasks() {
		DonStorage storage = new DonStorage();
		storage.changeFileName("testFile4.txt");
		storage.clear();
		if (storage.loadFromDisk()) {
			List<IDonTask> tasks = new ArrayList<IDonTask>();

			List<IDonTask> tasksTemp = storage.getTaskList();
			int ID = storage.getNextID();
			IDonTask task = new DonTask("something", ID);
			storage.addTask(task);
			tasks.add(task);

			ID = storage.getNextID();
			task = new DonTask("anything", ID);
			storage.addTask(task);
			tasks.add(task);

			assertEquals(null, true, storage.removeTask(2));
			tasks.remove(1);

			tasksTemp = storage.getTaskList();

			assertEquals(null, true, tasksTemp.equals(tasks));
			storage.saveToDisk();
		}

	}

	@Test
	public void testSave() {
		DonStorage storage = new DonStorage();
		storage.changeFileName("testFile5.txt");
		storage.clear();
		if (storage.loadFromDisk()) {
			int ID = 1;
			IDonTask task = new DonTask("something", ID);
			storage.addTask(task);
			task = new DonTask("something", storage.getNextID());
			storage.addTask(task);
			task = new DonTask("something", storage.getNextID());
			storage.addTask(task);
			task = new DonTask("something", storage.getNextID());
			storage.addTask(task);
			assertEquals(null, true, storage.saveToDisk());
		}
	}

	@Test
	public void testLabelsSave() {
		DonStorage storage = new DonStorage();
		storage.changeFileName("testFile6.txt");
		storage.clear();
		if (storage.loadFromDisk()) {

			List<String> testLabels = new ArrayList<String>();
			testLabels.add("label1");
			testLabels.add("label2");
			testLabels.add("label3");
			IDonTask task = new DonTask("something", null, null,
					storage.getNextID(), testLabels);
			storage.addTask(task);
			testLabels.add("label4");
			testLabels.add("label5");
			testLabels.add("label6");
			task = new DonTask("something", null, null, storage.getNextID(),
					testLabels);
			storage.addTask(task);
			task = new DonTask("something", storage.getNextID());
			storage.addTask(task);
			task = new DonTask("something", storage.getNextID());
			storage.addTask(task);
			assertEquals(null, true, storage.saveToDisk());
		}
	}

	@Test
	public void testLabelsLoad() {
		DonStorage storage = new DonStorage();
		storage.changeFileName("testFile6.txt");
		if (storage.loadFromDisk()) {
			storage.changeFileName("testFile6_Compare.txt");
			storage.saveToDisk();
		}
	}

	@Test
	public void testAddDeleteLabel() {
		DonStorage storage = new DonStorage();
		DonStorage storage_output = new DonStorage();
		storage_output.changeFileName("testFile9_AddDeleteLabel.txt");
		storage_output.clear();
		storage.changeFileName("testFile9.txt");
		storage.clear();
		if (storage.loadFromDisk()) {

			List<String> testLabels = new ArrayList<String>();
			testLabels.add("label1");
			testLabels.add("label2");
			testLabels.add("label3");
			IDonTask task = new DonTask("something", null, null,
					storage.getNextID(), testLabels);
			storage.addTask(task);
			testLabels.add("label4");
			testLabels.add("label5");
			testLabels.add("label6");
			task = new DonTask("something", null, null, storage.getNextID(),
					testLabels);
			storage.addTask(task);
			task = new DonTask("something", storage.getNextID());
			storage.addTask(task);
			task = new DonTask("something", storage.getNextID());
			storage.addTask(task);
			assertEquals(null, true, storage.saveToDisk());
		}
		
		if (storage_output.loadFromDisk()) {

			assertEquals(null, true, storage_output.getTask(1).addLabel("label7"));
			assertEquals(null, true, storage_output.getTask(1).deleteLabel("label1"));
			assertEquals(null, false, storage_output.getTask(1).addLabel("label2"));
			assertEquals(null, false, storage_output.getTask(1).deleteLabel("label8"));
			storage.changeFileName("testFile9_AddDeleteLabel.txt");
			storage.saveToDisk();
		}

	}

	@Test
	public void testMaxID() {
		DonStorage storage = new DonStorage();

		storage.changeFileName("testFile8.txt");
		storage.clear();
		if (storage.loadFromDisk()) {
			Random ran = new Random();
			for (int i = 0; i < 1100; i++) {
				IDonTask task = new DonTask("something", storage.getNextID());
				assertEquals(null, i + 1, storage.addTask(task));
			}
			for (int i = 0; i < 100; i++) {
				assertEquals(null, true,
						storage.removeTask(ran.nextInt(9) + i * 10 + 1));
			}
			for (int i = 0; i < 800; i++) {
				IDonTask task = new DonTask("something", storage.getNextID());
				storage.addTask(task);
			}
		}
		storage.saveToDisk();
	}

}

package doornot.storage;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;


import org.junit.Test;

import doornot.logic.DonTask;
import doornot.logic.IDonTask;

//@author A0100493R
public class DonStorageTester {
	
	@Test
	public void testLoad(){
		DonStorage storage = new DonStorage();
		assertEquals(null, true,storage.loadFromDisk());
	}
	
	@Test
	public void testAdd(){
		DonStorage storage = new DonStorage();
		if(storage.loadFromDisk())
		{
			int ID = 3;
			IDonTask task = new DonTask("something",ID);
			assertEquals(null,3,storage.addTask(task));
		}
		storage.clear();
	}
	
	@Test 
	public void testRemove(){
		DonStorage storage = new DonStorage();
		if(storage.loadFromDisk())
		{
			int ID = 3;
			assertEquals(null,false,storage.removeTask(ID));
			IDonTask task = new DonTask("something",ID);
			ID = storage.addTask(task);
			assertEquals(null,true,storage.removeTask(ID));
		}
		storage.clear();
	}
	
	@Test 
	public void testGetNext(){
		DonStorage storage = new DonStorage();
		if(storage.loadFromDisk())
		{
			assertEquals(null,1,storage.getNextID());
			int ID = 1;
			IDonTask task = new DonTask("something",ID);
			ID = storage.addTask(task);
			assertEquals(null,2,storage.getNextID());
			storage.removeTask(ID);
			assertEquals(null,1,storage.getNextID());
		}
		storage.clear();
	}
	
	@Test 
	public void testGetTasks(){
		DonStorage storage = new DonStorage();
		if(storage.loadFromDisk())
		{
			List<IDonTask> tasks = new ArrayList<IDonTask>();
			

			List<IDonTask> tasksTemp = storage.getTaskList();
			int ID = 1;
			IDonTask task = new DonTask("something",ID);
			storage.addTask(task);
			tasks.add(task);
			
			ID = storage.getNextID();
			task = new DonTask("anything",ID);
			storage.addTask(task);
			tasks.add(task);

			
			storage.removeTask(2);
			tasks.remove(1);
			
			tasksTemp = storage.getTaskList();
			boolean flag = tasksTemp.equals(tasks);
			System.out.println(flag);
		}
		storage.clear();
	}
	
	@Test 
	public void testSave(){
		DonStorage storage = new DonStorage();
		if(storage.loadFromDisk())
		{
			int ID = 1;
			IDonTask task = new DonTask("something",ID);
			storage.addTask(task);
			task = new DonTask("something",storage.getNextID());
			storage.addTask(task);
			task = new DonTask("something",storage.getNextID());
			storage.addTask(task);
			task = new DonTask("something",storage.getNextID());
			storage.addTask(task);
			assertEquals(null,true,storage.saveToDisk());
		}
		storage.clear();
	}

}

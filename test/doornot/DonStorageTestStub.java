package doornot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import doornot.storage.DonStorage;
import doornot.storage.IDonStorage;
import doornot.storage.IDonTask;

/**
 * Stub for testing logic. Assumes DonStorage is in working condition.
 * Adds functionality to allow the logic tester to save to a specified path.
 */
//@author A0111995Y

public class DonStorageTestStub implements IDonStorage {
	
	private List<IDonTask> taskList;

	@Override
	public int addTask(IDonTask task) {
		return 0;
	}

	@Override
	public boolean removeTask(int taskID) {
		return false;
	}

	@Override
	public int getNextID() {
		return 0;
	}

	@Override
	public IDonTask getTask(int ID) {
		return null;
	}

	@Override
	public List<IDonTask> getTaskByName(String name) {
		return null;
	}

	@Override
	public boolean saveToDisk() {
		return false;
	}

	@Override
	public boolean loadFromDisk() {
		return false;
	}

	@Override
	public List<IDonTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<IDonTask> taskList) {
		this.taskList = taskList;
	}
}

package doornot;

import java.util.ArrayList;
import java.util.List;

import doornot.logic.IDonTask;
import doornot.logic.IDonTask.TaskType;
import doornot.storage.IDonStorage;


//temporary class for DonLogic to work with, awaiting actual class
public class DonStorageTMP implements IDonStorage {
	
	private List<IDonTask> taskList;
	private int id = 0;
	
	public DonStorageTMP() {
		taskList = new ArrayList<IDonTask>();
	}

	@Override
	public boolean removeTask(int taskID) {
		for(int i=0; i<taskList.size(); i++) {
			
			if(taskList.get(i).getID()==taskID) {
				taskList.remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public int addTask(IDonTask task) {
		taskList.add(task);
		return id++;
	}

	@Override
	public int getNextID() {
		return id;
	}

	@Override
	public IDonTask getTask(int ID) {
		for(IDonTask task : taskList) {
			if(task.getID()==ID) {
				return task;
			}
		}
		return null;
	}

	@Override
	public boolean saveToDisk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loadFromDisk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<IDonTask> getTaskList(TaskType type) {
		// TODO Auto-generated method stub
		return taskList;
	}

}

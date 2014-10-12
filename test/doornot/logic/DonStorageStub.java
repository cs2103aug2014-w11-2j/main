package doornot.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import doornot.storage.DonStorage;

/**
 * Stub for testing logic. Assumes DonStorage is in working condition.
 * Adds functionality to allow the logic tester to save to a specified path.
 */
//@author A0111995Y

public class DonStorageStub extends DonStorage {
	
	private String fileName;
	
	public DonStorageStub(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Remove all the tasks in the task list
	 */
	public void clearTasks() {
		tasks.clear();
	}
	
	@Override
	public boolean saveToDisk() {
		try {
			File file = new File(fileName);
			FileWriter myWriter = new FileWriter(file);
			if (!tasks.isEmpty()) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"ddMMyyyy_HHmm");
				for (int i = 0; i < tasks.size(); i++) {
					String taskTitle, taskStartDate = "null", taskEndDate = "null";
					String taskStatus = "false";
					int taskID = 0;
					taskTitle = tasks.get(i).getTitle();
					if (tasks.get(i).getStartDate() != null)
						taskStartDate = formatter.format(tasks.get(i)
								.getStartDate().getTime());
					if (tasks.get(i).getEndDate() != null)
						taskEndDate = formatter.format(tasks.get(i)
								.getEndDate().getTime());
					if (tasks.get(i).getStatus() == true)
						taskStatus = "true";
					taskID = tasks.get(i).getID();
					myWriter.write(taskTitle + ";" + taskStartDate + ";"
							+ taskEndDate + ";" + taskStatus + ";" + taskID
							+ "\n");
				}
			}
			myWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean loadFromDisk() {
		File textFile = new File(fileName);
		if (!textFile.exists()) {
			try {
				textFile.createNewFile();
				return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			return readFile(textFile);
		}
	}
}

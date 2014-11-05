package doornot.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * DonStorage - Class for handling the storage of the program
 * (save/read/retrieve of tasks)
 * 
 */
//@author A0100493R
public class DonStorage implements IDonStorage {

	private static String FILE_NAME = "DoorNot_current.txt";
	private static final int POSITION_OF_TASK_TITLE = 0;
	private static final int POSITION_OF_TASK_START_DATE = 1;
	private static final int POSITION_OF_TASK_END_DATE = 2;
	private static final int POSITION_OF_TASK_STATUS = 3;
	private static final int POSITION_OF_TASK_ID = 4;
	private static final int POSITION_OF_TASK_TIMEUSAGE = 5;
	private static final int POSITION_OF_TASK_LABELS = 6;
	private static final int MIN_OF_TASK_ID = 1;

	protected List<IDonTask> tasks = new ArrayList<IDonTask>();

	@Override
	public int addTask(IDonTask task) {
		tasks.add(task);
		return task.getID();
	}

	public void clear() {
		tasks.clear();
		saveToDisk();
	}

	@Override
	public boolean removeTask(int ID) {
		int taskIndex = searchTask(ID);
		if (taskIndex != -1) {
			tasks.remove(taskIndex);
			return true;
		} else
			return false;
	}

	@Override
	public int getNextID() {
		if (tasks.isEmpty()) {
			return MIN_OF_TASK_ID;
		} else {
			int[] IDArray = constructIDArray();
			for (int i = 1; i < IDArray.length; i++) {
				if (IDArray[i] == 0) {
					return i;
				}
			}
			return IDArray.length;
		}
	}

	private int[] constructIDArray() {
		int maxID = findMaxID();
		int IDArray[] = new int[maxID + 1];
		for (int i = 0; i < tasks.size(); i++) {
			IDArray[tasks.get(i).getID()] = 1;
		}
		return IDArray;
	}

	private int findMaxID() {
		int max = 0;
		for (int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).getID() > max) {
				max = tasks.get(i).getID();
			}
		}
		return max;
	}

	@Override
	public IDonTask getTask(int ID) {
		int taskIndex = searchTask(ID);
		if (taskIndex != -1)
			return tasks.get(taskIndex);
		else
			return null;
	}

	/**
	 * Find tasks with the given name
	 * 
	 * @param name
	 *            the name to search for
	 * @return the response containing the tasks
	 */
	public List<IDonTask> getTaskByName(String name) {
		assert name != null;
		List<IDonTask> foundTasks = new ArrayList<IDonTask>();

		for (IDonTask task : tasks) {
			// Search for the given name/title without case sensitivity
			if (task.getTitle().toLowerCase().contains(name.toLowerCase())) {
				foundTasks.add(task);
			}
		}

		return foundTasks;
	}

	private int searchTask(int ID) {
		if (!tasks.isEmpty()) {
			for (int i = 0; i < tasks.size(); i++) {
				if (tasks.get(i).getID() == ID)
					return i;
			}
		}
		return -1;
	}

	@Override
	public boolean saveToDisk() {
		try {
			File file = new File(FILE_NAME);
			FileWriter myWriter = new FileWriter(file);
			if (!tasks.isEmpty()) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"ddMMyyyy_HHmm");
				for (int i = 0; i < tasks.size(); i++) {
					String taskTitle, taskStartDate = "null", taskEndDate = "null";
					String taskStatus = "Undone";
					String taskTimeUsage = "False";
					int taskID = 0;
					List<String> taskLabels = new ArrayList<String>();

					taskTitle = tasks.get(i).getTitle();
					if (tasks.get(i).getStartDate() != null) {
						taskStartDate = formatter.format(tasks.get(i)
								.getStartDate().getTime());
					}
					if (tasks.get(i).getEndDate() != null) {
						taskEndDate = formatter.format(tasks.get(i)
								.getEndDate().getTime());
					}
					if (tasks.get(i).getStatus() == true) {
						taskStatus = "Done";
					}
					taskID = tasks.get(i).getID();
					if (tasks.get(i).isTimeUsed()) {
						taskTimeUsage = "True";
					}
					if (!tasks.get(i).getLabels().isEmpty()) {
						taskLabels = tasks.get(i).getLabels();
					} else {
						taskLabels.add("null");
					}

					String taskInfo = taskTitle + ";" + taskStartDate + ";"
							+ taskEndDate + ";" + taskStatus + ";" + taskID
							+ ";" + taskTimeUsage + ";";

					for (int j = 0; j < taskLabels.size(); j++) {
						taskInfo = taskInfo + taskLabels.get(j) + ";";
					}
					taskInfo = taskInfo + "\n";
					myWriter.write(taskInfo);
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
		File textFile = new File(FILE_NAME);
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

	public void changeFileName(String newFileName) {
		FILE_NAME = newFileName;
	}

	protected boolean readFile(File file) {
		try {
			BufferedReader myReader = new BufferedReader(new FileReader(file));
			String textLine = null;

			while ((textLine = myReader.readLine()) != null) {
				String[] taskInfo = textLine.split(";");
				List<String> taskLabels = new ArrayList<String>();
				IDonTask task = null;
				int taskID = Integer.parseInt(taskInfo[POSITION_OF_TASK_ID]);
				if (taskInfo[POSITION_OF_TASK_START_DATE]
						.equalsIgnoreCase("null")) {
					task = new DonTask(taskInfo[POSITION_OF_TASK_TITLE], taskID);
				} else {
					if (taskInfo[POSITION_OF_TASK_END_DATE]
							.equalsIgnoreCase("null")) {
						Calendar deadline = convertToDate(taskInfo[POSITION_OF_TASK_START_DATE]);
						task = new DonTask(taskInfo[POSITION_OF_TASK_TITLE],
								deadline, taskID);
					} else {
						Calendar startDate = convertToDate(taskInfo[POSITION_OF_TASK_START_DATE]);
						Calendar endDate = convertToDate(taskInfo[POSITION_OF_TASK_END_DATE]);
						task = new DonTask(taskInfo[POSITION_OF_TASK_TITLE],
								startDate, endDate, taskID);
					}
				}
				if (taskInfo[POSITION_OF_TASK_STATUS].equalsIgnoreCase("done")) {
					task.setStatus(true);
				}
				if (taskInfo[POSITION_OF_TASK_TIMEUSAGE]
						.equalsIgnoreCase("True")) {
					task.setTimeUsed(true);
				}

				if (!taskInfo[POSITION_OF_TASK_LABELS].equalsIgnoreCase("null")) {
					for (int i = POSITION_OF_TASK_LABELS; i < taskInfo.length; i++) {
						taskLabels.add(taskInfo[i]);
					}
					task.setLabels(taskLabels);
				}
				tasks.add(task);
			}
			myReader.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Calendar convertToDate(String date) {
		int day = Integer.parseInt(date.substring(0, 2));
		int month = Integer.parseInt(date.substring(2, 4)) - 1;
		int year = Integer.parseInt(date.substring(4, 8));
		int hour = Integer.parseInt(date.substring(9, 11));
		int min = Integer.parseInt(date.substring(11, 13));

		return new GregorianCalendar(year, month, day, hour, min);
	}

	@Override
	public List<IDonTask> getTaskList() {
		return tasks;
	}

}

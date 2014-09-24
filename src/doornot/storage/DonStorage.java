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

import doornot.logic.DonTask;
import doornot.logic.IDonTask;


/**
 * DonStorage - Class for handling the storage of the program
 * (save/read/retrieve of tasks)
 * @author A0100493R
 */


public class DonStorage implements IDonStorage{
	
	private static final String FILE_NAME = "DoorNot_current.txt";
	private static final int NUM_OF_TASK_INFO = 5;
	private static final int POSITION_OF_TASK_TITLE = 0;
	private static final int POSITION_OF_TASK_START_DATE = 1;
	private static final int POSITION_OF_TASK_END_DATE = 2;
	private static final int POSITION_OF_TASK_STATUS = 3;
	private static final int POSITION_OF_TASK_ID = 4;
	private static final int MAX_OF_TASK_ID = 1000;
	private static final int MIN_OF_TASK_ID = 1;
	
	private List<IDonTask> tasks = new ArrayList<IDonTask>();

	private int nextID = 0;
	private int[] listOfIDs = new int[MAX_OF_TASK_ID + 1];

	@Override
	public int addTask(IDonTask task) {
		tasks.add(task);
		listOfIDs[task.getID()] = task.getID();
		setNextID();
		return task.getID();
	}
	
	public void clear(){
		tasks.clear();
		saveToDisk();
	}
	@Override
	public boolean removeTask(int ID) {
		int taskIndex = searchTask(ID);
		if(taskIndex != -1) {
			tasks.remove(taskIndex);
			listOfIDs[ID] = -1;
			setNextID();
			return true;
		}else
			return false;
	}

	@Override
	public int getNextID() {
		return nextID;
	}

	private void setNextID() {
		if(!tasks.isEmpty()) {
			int i = MIN_OF_TASK_ID;
			while((listOfIDs[i] == i) && (i <= MAX_OF_TASK_ID))
				i++;
			nextID = i;
		} else 
			nextID = MIN_OF_TASK_ID;
	}
	@Override
	
	public IDonTask getTask(int ID) {
		int taskIndex = searchTask(ID);
		if(taskIndex != -1)
			return tasks.get(taskIndex);
		else
			return null;
	}
	
	private int searchTask(int ID) {
		if(!tasks.isEmpty()) {
			for(int i = 0; i < tasks.size(); i++){
				if(tasks.get(i).getID() == ID)
					return i;
			}
		}
		return -1;
	}

	@Override
	public boolean saveToDisk() {
		try {
			File file = new File(FILE_NAME);
			FileWriter myWriter =new FileWriter(file);
			if(!tasks.isEmpty()){
				SimpleDateFormat formatter=new SimpleDateFormat("DDMMyyyy"); 
				for(int i=0;i<tasks.size();i++){
					String taskTitle, taskStartDate = "null", taskEndDate = "null";
					String taskStatus = "false";
					int taskID = 0;
					taskTitle = tasks.get(i).getTitle();
					if(tasks.get(i).getStartDate() != null)
						taskStartDate = formatter.format(tasks.get(i).getStartDate());
					if(tasks.get(i).getEndDate() != null)
						taskEndDate = formatter.format(tasks.get(i).getEndDate());
					if(tasks.get(i).getStatus() == true)
						taskStatus ="true";
					taskID = tasks.get(i).getID();
					myWriter.write(taskTitle + ";" 
								+ taskStartDate + ";"
								+ taskEndDate + ";"
								+ taskStatus + ";"
								+ taskID + "\n");
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
		if(!textFile.exists()){
			try {
				textFile.createNewFile();
				return true;
			} catch (IOException e) {
				return false;
			}
		}else{
				return readFile(textFile);
		}
	}
	

	private boolean readFile(File file) {
		try {
			BufferedReader myReader = new BufferedReader(new FileReader(file));
			String textLine = null;
			
			while((textLine = myReader.readLine()) != null) {
				String[] taskInfo = new String[NUM_OF_TASK_INFO];
				taskInfo = textLine.split(";");
				IDonTask task = null;
				int taskID = Integer.parseInt(taskInfo[POSITION_OF_TASK_ID]);
				if(taskInfo[POSITION_OF_TASK_START_DATE].equalsIgnoreCase("null")) {
					task = new DonTask(taskInfo[POSITION_OF_TASK_TITLE],taskID);
				} else {
					if(taskInfo[POSITION_OF_TASK_END_DATE].equalsIgnoreCase("null")) {
						Calendar deadline = convertToDate(taskInfo[POSITION_OF_TASK_START_DATE]);
						task = new DonTask(taskInfo[POSITION_OF_TASK_TITLE],deadline,taskID);
					} else {
						Calendar startDate = convertToDate(taskInfo[POSITION_OF_TASK_START_DATE]);
						Calendar endDate = convertToDate(taskInfo[POSITION_OF_TASK_END_DATE]);
						task = new DonTask(taskInfo[POSITION_OF_TASK_TITLE],startDate,endDate,taskID);
					}
				}
				if(taskInfo[POSITION_OF_TASK_STATUS].equalsIgnoreCase("true"))
					task.setStatus(true);
				tasks.add(task);
				listOfIDs[task.getID()] = task.getID();
			}
			myReader.close();
			setNextID();
			return true;
		} catch (IOException e) {
				e.printStackTrace();
				return false;
		}
	}
	
	private Calendar convertToDate(String date) {
		int day = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(2,4));
		int year = Integer.parseInt(date.substring(4,8));
		
		return new GregorianCalendar(year, month, day);
	}

	@Override
	public List<IDonTask> getTaskList() {
		return tasks;
	}
	

}

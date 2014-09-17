import java.util.Calendar;
import java.util.Date;

/**
 * Class containing the properties of a task
 * 
 * @author cs2103aug2014-w11-2j
 *
 */
public class DonTask implements IDonTask {
	
	private String taskTitle;
	private Calendar startDate, endDate;
	private boolean status;

	/**
	 * Creates an empty task
	 */
	public DonTask() {
		taskTitle = null;
		startDate = null;
		endDate = null;
		status = false;
	}
	
	public DonTask(String title) {
		taskTitle = title;
		startDate = null;
		endDate = null;
		status = false;
	}
	
	public DonTask(String title, Calendar deadline) {
		taskTitle = title;	
		startDate = deadline;
		endDate = null;
		status = false;
	}
	
	public DonTask(String title, Calendar startDate, Calendar endDate) {
		taskTitle = title;	
		this.startDate = startDate;
		this.endDate = endDate;
		status = false;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Calendar getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Calendar getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTitle(String newTitle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartDate(Date newDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEndDate(Date newDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(boolean newStatus) {
		// TODO Auto-generated method stub
	}

	@Override
	public TaskType getType() {
		// TODO Auto-generated method stub
		return null;
	}
}

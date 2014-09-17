import java.util.Date;

/**
 * Interface defining the basic requirements of a Task
 * All references to "date" in the class refer to date and time (if present)
 * 
 * @author cs2103aug2014-w11-2j
 *
 */
public interface IDonTask {
	
	public String getTitle();
	
	public Date getStartDate();
	
	public Date getEndDate();
	
	public boolean getStatus();
	
	public void setTitle(String newTitle);

	public void setStartDate(Date newDate);

	public void setEndDate(Date newDate);
	
	public void setStatus(boolean newStatus);
}

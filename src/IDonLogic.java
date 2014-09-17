import java.util.Calendar;

/**
 * Interface defining the methods required of the logic component
 * 
 * @author cs2103aug2014-w11-2j
 *
 */
public interface IDonLogic {
	
	/**
	 * Creates a floating task
	 * @param	title	the title of the task
	 * @return	the created IDonTask object
	 */
	public IDonTask createTask(String title);
	
	/**
	 * Creates a deadline task
	 * @param	title		the title of the task
	 * @param	deadline	the deadline of the task
	 * @return	the created IDonTask object
	 */
	public IDonTask createTask(String title, Calendar deadline);
	
	/**
	 * Creates a task with a duration
	 * @param	title 		the title of the task
	 * @param	startDate	the start date of the task
	 * @param	endDate		the end date of the task
	 * @return	the created IDonTask object
	 */
	public IDonTask createTask(String title, Calendar startDate, Calendar endDate);
}

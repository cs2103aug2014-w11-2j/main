/**
 * Interface defining the functionalities of the storage component
 * 
 * @author cs2103aug2014-w11-2j
 *
 */
public interface IDonStorage {
	
	public int addTask(DonTask task);
	
	public boolean removeTask(DonTask task);
	
}
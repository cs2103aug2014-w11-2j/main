/**
 * DonLogic - Class for handling the logic
 * of the program (creation/deletion of tasks)
 * 
 * @author cs2103aug2014-w11-2j
 *
 */
public class DonLogic {
	
	private IDonStorage donStorage;
	
	public DonLogic(IDonStorage storage) {
		donStorage = storage;
	}
}

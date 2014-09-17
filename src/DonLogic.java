import java.util.Calendar;

/**
 * DonLogic - Class for handling the logic
 * of the program (creation/deletion of tasks)
 * 
 * @author cs2103aug2014-w11-2j
 *
 */
public class DonLogic implements IDonLogic {
	
	private IDonStorage donStorage;
	
	public DonLogic(IDonStorage storage) {
		donStorage = storage;
	}

	@Override
	public IDonTask createTask(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDonTask createTask(String title, Calendar deadline) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDonTask createTask(String title, Calendar startDate,
			Calendar endDate) {
		// TODO Auto-generated method stub
		return null;
	}
}

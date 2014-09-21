package doornot.logic;

/**
 * Interface defining the methods required of the logic component
 * 
 * @author cs2103aug2014-w11-2j
 * 
 */
public interface IDonLogic {

	/**
	 * Runs the given command and returns the result in an IDonResponse object
	 * 
	 * @param command
	 *            the command given by the user
	 * @return the response
	 */
	public IDonResponse runCommand(String command);

	/**
	 * Perform a save of the data in DoOrNot to drive. This is expected to be
	 * called from the GUI on a repeating interval.
	 * 
	 * @return whether the save was successful
	 */
	public IDonResponse saveToDrive();

	/**
	 * Method to get called by DoOrNot at startup of the program to bring up a
	 * list of latest tasks. Gets information required by a welcome "page"
	 * 
	 * @return	the response containing relevant tasks and messages
	 */
	public IDonResponse initialize();
}

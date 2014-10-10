package doornot.parser;
/**
 * Interface for Parsing commands and creating a DonCommand
 * 
 */
//@author A0115503W

public interface IDonParser {
	
	/**
	 * Initialises and returns DonCommand object with the respective properties.
	 * Calls setDonCommand(String command).
	 * @param command string from user
	 * @return DonCommand
	 */
	public DonCommand parseCommand(String command);
	
	/**
	 * Creates the respective dCommand according to the user input.
	 * Requires parseCommand(String command) to initialise DonCommand object.
	 * @param command string from user
	 */
	public void setDonCommand(String command);

}

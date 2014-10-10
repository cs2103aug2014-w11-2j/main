package doornot.parser;
/**
 * Interface for Parsing commands and creating a DonCommand
 * 
 */
//@author A0115503W

public interface IDonParser {
	
	/**
	 * Returns DonCommand object with the respective properties
	 * Calls setDonCommand()
	 * @param command
	 * @return DonCommand
	 */
	public DonCommand parseCommand(String command);
	
	/**
	 * Creates the respective dCommand according to the user input
	 * along with its properties
	 */
	public void setDonCommand();

}

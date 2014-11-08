package doornot.parser;

import doornot.util.AbstractDonCommand;

/**
 * Interface for Parsing commands and creating a DonCommand
 * 
 */
//@author A0115503W

public interface IDonParser {
	
	/**
	 * Initialises and returns DonCommand object with the respective properties.
	 * Calls setDonCommand(String command) to create the respective AbstractDonCommand 
	 * according to the user input.
	 * @param command string from user
	 * @return DonCommand
	 */
	public AbstractDonCommand parseCommand(String command);
	
	/**
	 * Creates the respective dCommand according to the user input.
	 * Requires parseCommand(String command) to initialise DonCommand object.
	 */
	public void setDonCommand();

}

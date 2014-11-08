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
	 * Calls setDonCommand(String command).
	 * @param command string from user
	 * @return DonCommand
	 */
	public AbstractDonCommand parseCommand(String command);


}

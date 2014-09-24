package doornot.parser;
/**
 * Interface for Parsing commands and creating a DonCommand
 * @author Haritha Ramesh
 * @author A0115503W
 * 
 */
public interface IDonParser {
	
	/**
	 * Returns DonCommand object with the respective properties
	 * @param command
	 * @return DonCommand
	 */
	public DonCommand parseCommand(String command);
	
	
	

}

package doornot.parser;

public interface IDonParser {
	
	/**
	 * Returns DonCommand object with the respective properties
	 * @param command
	 * @return DonCommand
	 */
	public DonCommand parseCommand(String command);
	
	
	

}

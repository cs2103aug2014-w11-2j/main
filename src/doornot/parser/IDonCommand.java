package doornot.parser;

public interface IDonCommand {
	
	
	public enum CommandType {
		ADD, EDIT, DELETE, SEARCH, MARK, UNDO, INVALID 
	}
	
	/**
	 * Returns the type of command
	 * 
	 * @return the type of command
	 */
	public CommandType getType();
	
}

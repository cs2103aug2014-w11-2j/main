package doornot.util;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.logic.IDonResponse.ResponseType;
import doornot.storage.IDonStorage;

//@author A0111995Y
public class DonInvalidCommand extends AbstractDonCommand{
	
	public enum InvalidType {
		INVALID_DATE,
		INVALID_FORMAT,
		INVALID_COMMAND
	}
	
	private String command;
	private InvalidType type;
	
	/**
	 * Creates an InvalidCommand 
	 * @param type the type of invalid command
	 */
	public DonInvalidCommand(InvalidType type) {
		this.type = type;
	}
	
	/**
	 * Creates an InvalidCommand with the given command
	 * @param type the type of InvalidCommand
	 * @param str the command string
	 */
	public DonInvalidCommand(InvalidType type, String str) {
		this.type = type;
		command = str;
	}
	
	public InvalidType getType() {
		return type;
	}
	
	public String getStringInput() {
		return command;
	}
	
	/**
	 * Creates a response for user entered commands with invalid formatting
	 * 
	 * @return the response
	 */
	private IDonResponse createInvalidFormatResponse() {
		IDonResponse response = new DonResponse();
		response.addMessage(String.format(MSG_COMMAND_WRONG_FORMAT, command));
		response.setResponseType(ResponseType.UNKNOWN_COMMAND);
		return response;
	}

	/**
	 * Creates a response for user entered commands with invalid dates
	 * 
	 * @return the response
	 */
	private IDonResponse createInvalidDateResponse() {
		IDonResponse response = new DonResponse();
		response.addMessage(MSG_COMMAND_WRONG_DATE);
		response.setResponseType(ResponseType.UNKNOWN_COMMAND);
		return response;
	}

	/**
	 * Creates a response for unrecognized commands
	 * 
	 * @return the response
	 */
	private IDonResponse createInvalidCommandResponse() {
		IDonResponse response = new DonResponse();
		response.addMessage(String.format(MSG_UNKNOWN_COMMAND, command));
		response.setResponseType(ResponseType.UNKNOWN_COMMAND);
		return response;
	}
	
	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
	
		IDonResponse response = null;
		if(type==InvalidType.INVALID_COMMAND) {
			response = createInvalidCommandResponse();
		} else if(type == InvalidType.INVALID_DATE) {
			response = createInvalidDateResponse();
		} else if(type == InvalidType.INVALID_FORMAT) {
			response = createInvalidFormatResponse();
		}
		return response;

	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		throw new UnsupportedOperationException();
	}

}

package doornot.logic;

import doornot.storage.IDonStorage;

public class DonInvalidCommand extends AbstractDonCommand{
	
	private String command;
	
	public DonInvalidCommand(GeneralCommandType type) {
		generalCommandType = type;
		error = true;
	}
	
	public DonInvalidCommand(GeneralCommandType type, String str) {
		generalCommandType = type;
		command = str;
		error = true;
	}
	
	public String getStringInput() {
		return command;
	}
	
	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		return null;	
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		throw new UnsupportedOperationException();
	}

}

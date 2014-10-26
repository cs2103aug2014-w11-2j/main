package doornot.logic;

import doornot.storage.IDonStorage;

/**
 * Empty implementation of the AbstractDonCommand
 * For use with commands that have no execute/undo functionalities
 *
 */
public class DonGeneralCommand extends AbstractDonCommand {

	public DonGeneralCommand(GeneralCommandType type) {
		generalCommandType = type;
	}
	
	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		throw new UnsupportedOperationException();
	}

}

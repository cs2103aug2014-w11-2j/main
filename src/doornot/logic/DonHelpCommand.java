package doornot.logic;

import doornot.logic.AbstractDonCommand.GeneralCommandType;
import doornot.storage.IDonStorage;

public class DonHelpCommand extends AbstractDonCommand {

	public enum HelpType {
		HELP_ADD,
		HELP_EDIT,
		HELP_SEARCH,
		HELP_DELETE,
		HELP_MARK,
		HELP_UNDO,
		HELP_REDO,
		// for help with all commands in general. I think undo goes here
		HELP_GENERAL,
	}
	
	private HelpType requestedCommand;
	
	public DonHelpCommand(HelpType command) {
		requestedCommand = command;
		generalCommandType = GeneralCommandType.HELP;
	}
	
	/**
	 * Show the user help information
	 * 
	 * @return response containing help messages
	 */
	private IDonResponse getHelp(HelpType commandType) {
		// TODO: decide the format of the help
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.HELP);

		if (commandType == HelpType.HELP_GENERAL) {
			// Give info on all commands available
			response.addMessage("Welcome to DoOrNot. These are the available commands:");
			response.addMessage("add / a, edit / ed / e, search / s, del / d, mark / m");
			response.addMessage("Type help command name to learn how to use the command!");
		} else if (commandType == HelpType.HELP_ADD) {
			// Help for add
			response.addMessage("add / a: Adds a task to the todo list");
			response.addMessage("Command format: add \"Task title\"");
			response.addMessage("Command format: add \"Task title\" @ DD/MM/YYYY HHmm");
			response.addMessage("Command format: add \"Task title\" from DDMMYYYY_HHmm to DD/MM/YYYY HHmm");
			response.addMessage("All dates can either be with time (DD/MM/YYYY HHmm) or without (DD/MM/YYYY)");
			response.addMessage("Examples:");
			response.addMessage("add \"Finish reading Book X\" <-- Adds a floating task");
			response.addMessage("add \"Submit CS9842 assignment\" @ 18/11/2014 <-- Adds a task with a deadline at 18th of November 2014");
			response.addMessage("add \"Talk by person\" from 05/08/2015 1500 to 05/08/2015 1800 <-- Adds an event that lasts from 3pm of 5th August 2015 to 6pm of the same day");
		} else if (commandType == HelpType.HELP_EDIT) {
			// Help for edit
			response.addMessage("edit / ed / e: Edits a task in the todo list");
			response.addMessage("Command format: edit Task_id to \"New task title\"");
			response.addMessage("Command format: edit \"Part of old Task title\" to \"New task title\"");
			response.addMessage("Command format: edit Task_id to DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit \"Part of old Task title\" to DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit Task_id to from DD/MM/YYYY HHmm to DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit \"Part of old Task title\" to from DD/MM/YYYY HHmm to DD/MM/YYYY HHmm");
			response.addMessage("If multiple tasks are found with the given title, nothing will be edited.");
			response.addMessage("All dates can either be with time (DD/MM/YYYY HHmm) or without (DD/MM/YYYY)");
			response.addMessage("Examples:");
			response.addMessage("edit 22 to \"Do work\" <-- Changes task 22's title to Do work");
			response.addMessage("edit \"Do work\" to 17/01/2015 <-- Changes the deadline of the task containing \"Do work\" as the title to 17th January 2015");
			response.addMessage("edit 14 to from 02/05/2015 to 03/05/2015 <-- Changes the start and end dates of task 14 to 2nd and 3rd of May 2015 respectively");
		} else if (commandType == HelpType.HELP_DELETE) {
			// Help for delete
			response.addMessage("del / d: Delete a task in the todo list");
			response.addMessage("Command format: del Task_id");
			response.addMessage("Command format: del \"Part of Task title\"");
			response.addMessage("If multiple tasks are found with the given title, nothing will be deleted.");
			response.addMessage("Examples:");
			response.addMessage("del 22 <-- Deletes task 22");
			response.addMessage("del \"Do work\" <-- Deletes the task containing \"Do work\" in the title");
		} else if (commandType == HelpType.HELP_SEARCH) {
			// Help for search
			response.addMessage("search / s: Finds a task with the given ID, title or date");
			response.addMessage("Command format: search Task_id");
			response.addMessage("Command format: search \"Part of Task title\"");
			response.addMessage("Command format: search 22/01/2016");
			response.addMessage("All dates can either be with time (DD/MM/YYYY HHmm) or without (DD/MM/YYYY)");
			response.addMessage("Examples:");
			response.addMessage("search 22 <-- Searches for task 22");
			response.addMessage("search \"Do work\" <-- Searches for tasks containing \"Do work\" in the title");
			response.addMessage("search 22/01/2016 <-- Searches for tasks starting or occurring on the 22nd of January 2016");
		} else if (commandType == HelpType.HELP_UNDO || commandType == HelpType.HELP_REDO) {
			//Help for undo
			response.addMessage("undo : Undoes the previous action");
			response.addMessage("redo : Performs the last undone action");
			response.addMessage("Command format: undo/redo");
			response.addMessage("Examples:");
			response.addMessage("undo");
			response.addMessage("redo");
			response.addMessage("(What were you expecting?)");

		}

		return response;
	}
	
	@Override
	public IDonResponse executeCommand(IDonStorage donStorage) {
		IDonResponse response = getHelp(requestedCommand);
		return response;
	}

	@Override
	public IDonResponse undoCommand(IDonStorage donStorage) {
		// not supposed to be run
		return null;
	}

}

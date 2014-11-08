package doornot.util;

import doornot.logic.DonResponse;
import doornot.logic.IDonResponse;
import doornot.storage.IDonStorage;

//@author A0111995Y
public class DonHelpCommand extends AbstractDonCommand {

	public enum HelpType {
		HELP_ADD,
		HELP_EDIT,
		HELP_SEARCH,
		HELP_DELETE,
		HELP_MARK,
		HELP_UNDO,
		HELP_REDO,
		HELP_LABEL,
		HELP_DELABEL,
		// for help with all commands in general. I think undo goes here
		HELP_GENERAL,
	}
	
	private HelpType requestedCommand;
	
	public DonHelpCommand(HelpType command) {
		requestedCommand = command;
		generalCommandType = GeneralCommandType.HELP;
	}
	
	public HelpType getRequestedCommand() {
		return requestedCommand;
	}
	
	/**
	 * Show the user help information
	 * 
	 * @return response containing help messages
	 */
	private IDonResponse getHelp(HelpType commandType) {
		IDonResponse response = new DonResponse();
		response.setResponseType(IDonResponse.ResponseType.HELP);

		if (commandType == HelpType.HELP_GENERAL) {
			// Give info on all commands available
			response.addMessage("Welcome to DoOrNot. These are the available commands:");
			response.addMessage("add / a, edit / ed / e, search / s, del / d, mark / m, label / l, delabel, slabel / sl");
			response.addMessage("Type help command name to learn how to use the command!");
		} else if (commandType == HelpType.HELP_ADD) {
			// Help for add
			response.addMessage("add / a: Adds a task to the todo list");
			response.addMessage("Command format: addf Task title");
			response.addMessage("Command format: add Task title by DD/MM/YYYY HHmm");
			response.addMessage("Command format: add Task title from DD/MM/YYYY HHmm to DD/MM/YYYY HHmm");
			response.addMessage("The date can be in any logical date format");
			response.addMessage("Examples:");
			response.addMessage("addf Finish reading Book X <-- Adds a floating task");
			response.addMessage("add Submit CS9842 assignment by 18/11/2014 <-- Adds a task with a deadline at 18th of November 2014");
			response.addMessage("add Talk by person from 05/08/2015 1500 to 05/08/2015 1800 <-- Adds an event that lasts from 3pm of 5th August 2015 to 6pm of the same day");
		} else if (commandType == HelpType.HELP_EDIT) {
			// Help for edit
			response.addMessage("edit / ed / e: Edits a task in the todo list");
			response.addMessage("Command format: edit Task_id to \"New task title\"");
			response.addMessage("Command format: edit Part of old Task title to \"New task title\"");
			response.addMessage("Command format: edit Task_id by DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit Part of old Task title to DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit Task_id to from DD/MM/YYYY HHmm to DD/MM/YYYY HHmm");
			response.addMessage("Command format: edit Part of old Task title from DD/MM/YYYY HHmm to DD/MM/YYYY HHmm");
			response.addMessage("The date can be in any logical date format");
			response.addMessage("If multiple tasks are found with the given title, nothing will be edited.");
			response.addMessage("Examples:");
			response.addMessage("edit 22 to \"Do work\" <-- Changes task 22's title to Do work");
			response.addMessage("edit Do work by 17/01/2015 <-- Changes the deadline of the task containing \"Do work\" as the title to 17th January 2015");
			response.addMessage("edit 14 from 02/05/2015 to 03/05/2015 <-- Changes the start and end dates of task 14 to 2nd and 3rd of May 2015 respectively");
		} else if (commandType == HelpType.HELP_DELETE) {
			// Help for delete
			response.addMessage("del / d: Delete a task in the todo list");
			response.addMessage("Command format: del Task_id");
			response.addMessage("Command format: del Part of Task title");
			response.addMessage("If multiple tasks are found with the given title, nothing will be deleted.");
			response.addMessage("Examples:");
			response.addMessage("del 22 <-- Deletes task 22");
			response.addMessage("del Do work <-- Deletes the task containing \"Do work\" in the title");
		} else if (commandType == HelpType.HELP_SEARCH) {
			// Help for search
			response.addMessage("search / s / son / saf: Finds a task with the given ID, title or date");
			response.addMessage("Command format: search Task_id");
			response.addMessage("Command format: search Part of Task title");
			response.addMessage("Command format: search 22/01/2016");
			response.addMessage("Command format: saf 22/01/2016");
			response.addMessage("Examples:");
			response.addMessage("search 22 <-- Searches for task 22");
			response.addMessage("search Do work <-- Searches for tasks containing \"Do work\" in the title");
			response.addMessage("son 22/01/2016 <-- Searches for tasks starting or occurring on the 22nd of January 2016");
			response.addMessage("saf 22/01/2016 <-- Searches for tasks occurring after 22nd of January 2016");
		} else if (commandType == HelpType.HELP_MARK) {
			// Help for mark
			response.addMessage("mark: Marks a task as done/undone");
			response.addMessage("Command format: mark Task_id");
			response.addMessage("Command format: mark Part of Task title");
			response.addMessage("Examples:");
			response.addMessage("mark 22 <-- Marks task 22 as done/undone depending on its current status");
			response.addMessage("mark Buy paper <-- Marks the task with \"Buy paper\" in the title as done/undone");
		} else if (commandType == HelpType.HELP_LABEL) {
			// Help for label
			response.addMessage("label / l: Tags a task with a given label");
			response.addMessage("Command format: label Task_id #Label title");
			response.addMessage("Command format: label Part of Task title #Label title");
			response.addMessage("Examples:");
			response.addMessage("label 22 #work <-- Adds the \"work\" label to task 22");
			response.addMessage("label Buy paper #personal <-- Adds the \"personal\" label to the task with \"Buy paper\" in the title");
			
			response.addMessage("delabel / dl: Removes a given label from a task");
			response.addMessage("Command format: delabel Task_id");
			response.addMessage("Command format: delabel Task_id #Label title");
			response.addMessage("Command format: delabel Part of Task title #Label title");
			response.addMessage("Examples:");
			response.addMessage("delabel 21 <-- Removes all labels from task 21");
			response.addMessage("delabel 22 #work <-- Removes the \"work\" label from task 22");
			response.addMessage("delabel Buy paper #personal <-- Removes the \"personal\" label from the task with \"Buy paper\" in the title");
			
			response.addMessage("slabel / sl: Searches for tasks with the given label");
			response.addMessage("Command format: slabel #Label name");
			response.addMessage("Examples:");
			response.addMessage("slabel #work <-- Searches for all tasks with the \"work\" label");
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
		throw new UnsupportedOperationException();
	}

}

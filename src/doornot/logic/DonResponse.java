package doornot.logic;

import java.util.ArrayList;
import java.util.List;

import doornot.storage.IDonTask;

/**
 * Class defining a response object
 */
//@author A0111995Y
public class DonResponse implements IDonResponse {
	
	private List<String> messages;
	private List<IDonTask> tasks;
	private List<DonPeriod> periods;
	private ResponseType type;
	
	public DonResponse() {
		messages = new ArrayList<String>();
		tasks = new ArrayList<IDonTask>();
		periods = new ArrayList<DonPeriod>();
	}
	
	@Override
	public ResponseType getResponseType() {
		return type;
	}

	@Override
	public List<String> getMessages() {
		return new ArrayList<String>(messages);
	}

	@Override
	public List<IDonTask> getTasks() {
		return new ArrayList<IDonTask>(tasks);
	}

	@Override
	public boolean addMessage(String message) {
		return messages.add(message);
	}

	@Override
	public boolean addTask(IDonTask task) {
		return tasks.add(task);
	}

	@Override
	public boolean hasMessages() {
		return (messages.size()>0);
	}

	@Override
	public boolean hasTasks() {
		return (tasks.size()>0);
	}

	@Override
	public void setResponseType(ResponseType type) {
		this.type = type;
	}

	@Override
	public void copyTasks(IDonResponse response) {
		for(IDonTask task : response.getTasks()) {
			this.addTask(task);
		}
		
	}

	@Override
	public List<DonPeriod> getPeriods() {
		return new ArrayList<DonPeriod>(periods);
	}

	@Override
	public boolean addPeriod(DonPeriod period) {
		return periods.add(period);
	}

}

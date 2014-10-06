package doornot.logic;

import java.util.Calendar;


/**
 * Class for containing a time period
 *
 */
//@author A0111995Y

public class DonPeriod {
	private Calendar start, end;
	
	public DonPeriod(Calendar start, Calendar end) {
		this.start = start;
		this.end = end;
	}
	
	public void setStart(Calendar start) {
		this.start = start;
	}
	
	public void setEnd(Calendar end) {
		this.end = end;
	}
	
	public Calendar getStart() {
		return start;
	}
	
	public Calendar getEnd() {
		return end;
	}
	
}

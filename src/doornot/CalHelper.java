package doornot;

import java.util.Calendar;

/**
 * 
 * Class containing Calendar helper methods
 *
 */
//@author A0111995Y
public class CalHelper {
	
	/**
	 * Return today's Calendar object
	 */
	public static Calendar getTodayStart() {
		Calendar ret = Calendar.getInstance();
		ret.set(Calendar.HOUR_OF_DAY, 0);
		ret.set(Calendar.MINUTE, 0);
		ret.set(Calendar.SECOND, 0);
		ret.set(Calendar.MILLISECOND, 0);
		return ret;
	}

	public static Calendar getTodayEnd() {
		Calendar ret = Calendar.getInstance();
		ret.set(Calendar.HOUR_OF_DAY, 23);
		ret.set(Calendar.MINUTE, 59);
		ret.set(Calendar.SECOND, 59);
		ret.set(Calendar.MILLISECOND, 999);
		return ret;
	}
}

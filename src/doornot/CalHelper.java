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

	/**
	 * Gets the first moment of today
	 * 
	 * @return the Calendar set to today at 0 hour, 0 minute, 0 second, 0
	 *         millisecond
	 */
	public static Calendar getTodayStart() {
		Calendar ret = Calendar.getInstance();
		ret.set(Calendar.HOUR_OF_DAY, 0);
		ret.set(Calendar.MINUTE, 0);
		ret.set(Calendar.SECOND, 0);
		ret.set(Calendar.MILLISECOND, 0);
		return ret;
	}

	/**
	 * Gets the last moment of today
	 * 
	 * @return the Calendar set to today at 23 hour, 59 minute, 59 second, 999
	 *         millisecond
	 */
	public static Calendar getTodayEnd() {
		Calendar ret = Calendar.getInstance();
		ret.set(Calendar.HOUR_OF_DAY, 23);
		ret.set(Calendar.MINUTE, 59);
		ret.set(Calendar.SECOND, 59);
		ret.set(Calendar.MILLISECOND, 999);
		return ret;
	}

	/**
	 * Returns the Calendar object that is set to the day after the given day
	 * (ignores hour/minute)
	 * 
	 * @param currentDay
	 *            the day to get the next day of
	 * @return
	 */
	public static Calendar getDayAfter(Calendar currentDay) {
		Calendar ret = (Calendar) currentDay.clone();
		ret.add(Calendar.DAY_OF_MONTH, 1);
		return ret;
	}

	/**
	 * Determines if date is the same as or after the base date.
	 * 
	 * @param date
	 *            the date to compare
	 * @param baseDate
	 *            the base date to check against
	 * @return true if date is >= baseDate
	 */
	public static boolean dateEqualOrAfter(Calendar date, Calendar baseDate) {
		if (date.after(baseDate) || date.equals(baseDate)) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if date is the same as or before the base date.
	 * 
	 * @param date
	 *            the date to compare
	 * @param baseDate
	 *            the base date to check against
	 * @return true if date is <= baseDate
	 */
	public static boolean dateEqualOrBefore(Calendar date, Calendar baseDate) {
		if (date.before(baseDate) || date.equals(baseDate)) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if date is on the same day as baseDate
	 * 
	 * @param date
	 *            the date to compare
	 * @param baseDate
	 *            the base date to check against
	 * @return true if date is on the same DAY as baseDate
	 */
	public static boolean isSameDay(Calendar date, Calendar baseDate) {
		if (date.get(Calendar.DATE) == baseDate.get(Calendar.DATE)
				&& date.get(Calendar.MONTH) == baseDate.get(Calendar.MONTH)
				&& date.get(Calendar.YEAR) == baseDate.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if date is between minDate and maxDate
	 * 
	 * @param date
	 *            the date to check
	 * @param minDate
	 *            the earlier date
	 * @param maxDate
	 *            the later date
	 * @return true if date is between minDate and maxDate
	 */
	public static boolean isBetweenDates(Calendar date, Calendar minDate,
			Calendar maxDate) {
		if (dateEqualOrAfter(date, minDate) && dateEqualOrBefore(date, maxDate)) {
			return true;
		}
		return false;
	}
}

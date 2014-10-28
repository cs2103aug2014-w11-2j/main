package doornot.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

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
	 * Sets the Calendar to the first moment of the given date
	 * 
	 * @param cal The date to get the first moment of
	 * @return the Calendar set to today at 0 hour, 0 minute, 0 second, 0
	 *         millisecond
	 */
	public static Calendar getDayStart(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	/**
	 * Gets the first moment of today
	 * 
	 * @return the Calendar set to today at 0 hour, 0 minute, 0 second, 0
	 *         millisecond
	 */
	public static Calendar getTodayStart() {
		Calendar ret = Calendar.getInstance();
		getDayStart(ret);
		return ret;
	}
	
	/**
	 * Sets the Calendar to the first moment of the given date
	 * 
	 * @param cal The date to get the first moment of
	 * @return the Calendar set to today at 23 hour, 59 minute, 59 second, 999
	 *         millisecond
	 */
	public static Calendar getDayEnd(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal;
	}
	

	/**
	 * Gets the last moment of today
	 * 
	 * @return the Calendar set to today at 23 hour, 59 minute, 59 second, 999
	 *         millisecond
	 */
	public static Calendar getTodayEnd() {
		Calendar ret = Calendar.getInstance();
		getDayEnd(ret);
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
	
	/**
	 * Copy calendar day, month, year, hour, minute and second from source to destination date object
	 * @param sourceDate the Calendar object to get the fields from
	 * @param destDate the Calendar object to copy the fields to
	 */
	public static void copyCalendar(Calendar sourceDate, Calendar destDate) {
		destDate.set(Calendar.DATE, sourceDate.get(Calendar.DATE));
		destDate.set(Calendar.MONTH, sourceDate.get(Calendar.MONTH));
		destDate.set(Calendar.YEAR, sourceDate.get(Calendar.YEAR));
		
		destDate.set(Calendar.HOUR_OF_DAY, sourceDate.get(Calendar.HOUR_OF_DAY));
		destDate.set(Calendar.MINUTE, sourceDate.get(Calendar.MINUTE));
		destDate.set(Calendar.SECOND, sourceDate.get(Calendar.SECOND));
	}
	
	/**
	 * Compares the year, month, date, hour, minutes of the 2 calendar objects
	 * @return
	 */
	public static boolean relevantEquals(Calendar c1, Calendar c2) {
		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
				&& c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& c1.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH)
				&& c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)
				&& c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE);
	}
	
	public static Calendar getDaysFromNow(int numDays) {
		Parser nattyParser = new Parser();
		List<DateGroup> dateGroup = nattyParser.parse(numDays+" from today");
		Calendar outCal = new GregorianCalendar();
		outCal.setTime(dateGroup.get(0).getDates().get(0));
		return outCal;
	}
	
}

package com.certacure.core.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;

/**
 * DateUtil.java, Used as a utility to handle Date calculations
 *
 **/
public class DateUtil {

	public static String GMT;

	static {
		GMT = "GMT";
	}

	/**
	 * Used this to replace the deprecated:
	 * ISO8601DateFormat jacksonDateFormat = new ISO8601DateFormat();
	 * return jacksonDateFormat.parse(dateString);
	 * 
	 * Creating a new object of SimpleDateFormat for thread safety.
	 * 
	 * @return
	 */
	public static DateFormat getISODateFormatter() {
		DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		ISO_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(GMT));
		return ISO_DATE_FORMAT;
	}

	/**
	 * Calculates the difference between the user-request's timezoneOffset (from "TimezoneOffset" header) and server timezoneOffset in minutes.
	 * Useful when printing reports and the times should be in the user-timezone.
	 * 
	 * @return int totalTimezoneOffset the number of minutes needed to add to the time
	 */
	public static int getTotalTimezoneOffset() {
		return getTotalTimezoneOffset(HttpUtil.getUserTimezoneOffset());
	}

	/**
	 * Calculates the difference between the userTimezoneOffset and server timezoneOffset in minutes.
	 * Useful when printing reports and the times should be in the user-timezone.
	 * 
	 * @param userTimezoneOffset The user timezoneOffset in minutes
	 * 
	 * @return int totalTimezoneOffset the number of minutes needed to add to the time
	 */
	public static int getTotalTimezoneOffset(Integer userTimezoneOffset) {
		int serverTimezoneOffset = getServerTimezoneOffset() / 1000 / 60;
		int totalTimezoneOffset = userTimezoneOffset + serverTimezoneOffset;
		return -totalTimezoneOffset;
	}

	/**
	 * Returns the offset of the server's time zone from UTC at the specified
	 * date. If Daylight Saving Time is in effect at the specified
	 * date, the offset value is adjusted with the amount of daylight
	 * saving.
	 * <p>
	 * This method returns a historically correct offset value if an
	 * underlying TimeZone implementation subclass supports historical
	 * Daylight Saving Time schedule and GMT offset changes.
	 *
	 * @param date the date represented in milliseconds since January 1, 1970 00:00:00 GMT
	 * @return the amount of time in milliseconds to add to UTC to get local time.
	 */
	public static int getServerTimezoneOffset() {
		Calendar c = Calendar.getInstance();
		//get current TimeZone using
		TimeZone tz = c.getTimeZone();

		//to get the correct offset, use getOffset function and pass the current date
		//this will take into consideration if the current date is in daylight-saving-time
		int serverTimezoneOffset = tz.getOffset(new Date().getTime());
		return serverTimezoneOffset;
	}

	public static String formatDate(Date date, String format) {
		if (date == null || StringUtil.isEmpty(format)) {
			return null;
		}
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * Parse a string into a Date object with the provided format
	 * 
	 * @param dateString The string to read a date from
	 * @param dateFormat The format of the date string
	 * 
	 * @return Date object
	 */
	public static Date parseDate(String dateString, String dateFormat) {
		return parseDateWithTimeZone(dateString, dateFormat, null);
	}

	/**
	 * Parse a string into a Date object with the provided format
	 * 
	 * @param dateString The string to read a date from
	 * @param dateFormat The format of the date string
	 * @param timeZoneId The ID of the desired output-date timeZone
	 * 
	 * @return Date object
	 */
	public static Date parseDateWithTimeZone(String dateString, String dateFormat, String timeZoneId) {
		try {
			if (StringUtil.isEmpty(dateString)) {
				return null;
			}
			SimpleDateFormat dateParser = new SimpleDateFormat(dateFormat);
			if (!StringUtil.isEmpty(timeZoneId)) {
				dateParser.setTimeZone(TimeZone.getTimeZone(timeZoneId));
			}
			return dateParser.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Compares if the date specified is equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isEqual(Date date, Date compareDate) {
		return date.equals(compareDate);
	}

	/**
	 * Compares if the date specified is equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isEqualTrimTime(Date date, Date compareDate) {
		Date tempDate = (Date) date.clone();
		Date tempCompareDate = (Date) compareDate.clone();
		return trimTime(tempDate).equals(trimTime(tempCompareDate));
	}

	/**
	 * Compares if the date specified is after or equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isAfterOrEqual(Date date, Date compareDate) {
		return (date.equals(compareDate) || date.after(compareDate));
	}

	/**
	 * Compares if the date specified is before or equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isBeforeOrEqual(Date date, Date compareDate) {
		return (date.equals(compareDate) || date.before(compareDate));
	}

	/**
	 * Compares if the date specified is after compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isAfter(Date date, Date compareDate) {
		return (date.after(compareDate));
	}

	/**
	 * Compares if the date specified is after or equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isAfterWithTrimTime(Date date, Date compareDate) {
		return (trimTime(date).after(trimTime(compareDate)));
	}

	/**
	 * Compares if the date specified is before compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isBefore(Date date, Date compareDate) {
		return (date.before(compareDate));
	}

	/**
	 * Compares if the date specified is before or equal to compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isBeforeWithTrimTime(Date date, Date compareDate) {
		Date tempDate = (Date) date.clone();
		Date tempCompareDate = (Date) compareDate.clone();
		return (trimTime(tempDate).before(trimTime(tempCompareDate)));
	}

	public static boolean isBeforeOrEqualWithTrimTime(Date date, Date compareDate) {
		return isBeforeOrEqual(trimTime(date), trimTime(compareDate));
	}

	/**
	 * Compares if the date specified is between startDate, and endDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isBetween(Date date, Date startDate, Date endDate) {
		return isAfterOrEqual(date, startDate) && isBeforeOrEqual(date, endDate);
	}

	/**
	 * Compares if the date specified is between startDate, and endDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isIntersected(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
		if (startDate1 == null || startDate2 == null || (endDate1 == null && endDate2 == null)) {
			return true;
		}

		if ((endDate2 != null && isBetween(startDate1, startDate2, endDate2))
				|| (endDate1 != null && isBetween(startDate2, startDate1, endDate1))
				|| (endDate1 == null && isAfterOrEqual(startDate2, startDate1))
				|| (endDate2 == null && isAfterOrEqual(startDate1, startDate2))) {
			return true;
		}
		return false;
	}

	public static Date getCurrentDateWithoutTime() {
		return trimTime(new Date());
	}

	/**
	 * set time to zeros in specific Date object
	 *
	 * @param date
	 * @return
	 */
	public static Date trimTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal = trimTime(cal);
		date.setTime(cal.getTimeInMillis());
		return date;
	}

	/**
	 * set time to zeros in specific Calendar object
	 *
	 * @param calendar
	 * @return
	 */
	public static Calendar trimTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	/**
	 * set date always to default date 1/1/2000
	 *
	 * @param date
	 * @return
	 */
	public static Date trimDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return trimDate(cal);
	}

	public static Date trimDate(Calendar cal) {
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.YEAR, 2000);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Compares if the time-only specified is after or equal to time in compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isTimeAfterOrEqual(Date date, Date compareDate) {
		date = trimDate(date);
		compareDate = trimDate(compareDate);
		return (date.equals(compareDate) || date.after(compareDate));
	}

	/**
	 * Compares if the time-only specified is before or equal to time in compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isTimeBeforeOrEqual(Date date, Date compareDate) {
		date = trimDate(date);
		compareDate = trimDate(compareDate);
		return (date.equals(compareDate) || date.before(compareDate));
	}

	/**
	 * Compares if the time-only specified is after time in compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isTimeAfter(Date date, Date compareDate) {
		date = trimDate(date);
		compareDate = trimDate(compareDate);
		return (date.after(compareDate));
	}

	/**
	 * Compares if the time-only specified is before time in compareDate
	 *
	 * @param date
	 * @param compareDate
	 * @return
	 */
	public static boolean isTimeBefore(Date date, Date compareDate) {
		date = trimDate(date);
		compareDate = trimDate(compareDate);
		return (date.before(compareDate));
	}

	/**
	 * Compares if the time-only in date specified is between time in startDate, and time in endDate
	 *
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isTimeBetween(Date date, Date startDate, Date endDate) {
		return isTimeAfterOrEqual(date, startDate) && isTimeBeforeOrEqual(date, endDate);
	}

	/**
	 * Compares if the time-only in date specified is between time in startDate, and time in endDate (but not equal end)
	 *
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isTimeBetweenWithOpenEnd(Date date, Date startDate, Date endDate) {
		return isTimeAfterOrEqual(date, startDate) && isTimeBefore(date, endDate);
	}

	/**
	 * Compares if the time-only in date specified is between time in startDate, and time in endDate
	 *
	 * @param date
	 * @param date2
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isTimeIntersected(Date date, Date date2, Date startDate, Date endDate) {
		return isTimeBetween(date, startDate, endDate) || isTimeBetween(date2, startDate, endDate)
				|| (isTimeBefore(date, startDate) && isTimeAfter(date2, endDate));
	}

	/**
	 * add years to specified date
	 *
	 * @param date
	 * @param years
	 * 
	 * @return Date
	 */
	public static Date addYears(Date date, int years) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years); // minus number would decrement the years
		return cal.getTime();
	}

	/**
	 * add months to specified date
	 *
	 * @param date
	 * @param months
	 * 
	 * @return Date
	 */
	public static Date addMonths(Date date, int months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months); // minus number would decrement the months
		return cal.getTime();
	}

	/**
	 * add days to specified date
	 *
	 * @param date
	 * @param days
	 * @return long
	 */
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}

	/**
	 * add hours to specified date
	 * accept minus value to subtract
	 *
	 * @param date
	 * @param hours
	 * @return Date
	 */
	public static Date addHours(Date date, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hours); // minus number would decrement the hours
		return cal.getTime();
	}

	/**
	 * add minutes to specified date
	 * accept minus value to subtract
	 * 
	 * @param date
	 * @param minutes
	 * @return Date
	 */
	public static Date addMinutes(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes); // minus number would decrement the minutes
		return cal.getTime();
	}

	/**
	 * add seconds to specified date
	 * accept minus value to subtract
	 * 
	 * @param date
	 * @param seconds
	 * @return Date
	 */
	public static Date addSeconds(Date date, int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, seconds); // minus number would decrement the seconds
		return cal.getTime();
	}

	/**
	 * set the date to a specified hour
	 * 24 hour clock
	 * 
	 * @param date
	 * @param hour
	 * @return Date
	 */
	public static Date setHour(Date date, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}

	/**
	 * set the date to a specified minute
	 * 
	 * @param date
	 * @param minute
	 * @return Date
	 */
	public static Date setMinute(Date date, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, minute);
		return cal.getTime();
	}

	/**
	 * set the date to a specified second
	 * 
	 * @param date
	 * @param second
	 * @return Date
	 */
	public static Date setSecond(Date date, int second) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.SECOND, second);
		return cal.getTime();
	}

	public static Date validateDate(String input) throws ParseException {
		if (null == input) {
			throw new ParseException("Entered Date is null", 0);
		}
		boolean valid = false;
		List<SimpleDateFormat> dateFormats = new ArrayList<>();
		dateFormats.add(new SimpleDateFormat("M/dd/yyyy"));
		dateFormats.add(new SimpleDateFormat("dd.M.yyyy"));
		dateFormats.add(new SimpleDateFormat("dd.MMM.yyyy"));
		dateFormats.add(new SimpleDateFormat("dd-MMM-yyyy"));
		dateFormats.add(new SimpleDateFormat("dd-mm-yyyy"));
		dateFormats.add(new SimpleDateFormat("yyyy-mm-dd"));
		dateFormats.add(new SimpleDateFormat("yyyy-MMM-dd"));
		dateFormats.add(new SimpleDateFormat("dd-MM-yy"));
		dateFormats.add(new SimpleDateFormat("dd-MM-yyyy"));
		dateFormats.add(new SimpleDateFormat("MM-dd-yyyy"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd"));
		for (SimpleDateFormat format : dateFormats) {
			try {
				format.setLenient(false);
				Date date = format.parse(input);
				valid = true;
				return date;
			} catch (ParseException e) {
				valid = false;
			}

		}
		if (!valid) {
			throw new ParseException("Not a valid date", 0);
		}
		return null;
	}

	public static int convertToSecondfromMidnight(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		hour = hour * 3600;
		int minute = calendar.get(Calendar.MINUTE);
		minute = minute * 60;
		int second = calendar.get(Calendar.SECOND);

		int timeInSeconds = hour + minute + second;
		return timeInSeconds;
	}

	public static Date convertFromSecondfromMidnight(int seconds) {
		int hour = seconds / 3600;
		int reminder = seconds % 3600;

		int minute = reminder / 60;
		reminder = seconds % 60;

		int second = reminder;

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		return calendar.getTime();
	}

	public static Period getPeriod(Date start, Date end, DurationFieldType... units) {
		if (end == null) {
			end = new Date();
		}
		Period period = new Period(start.getTime(), end.getTime(), PeriodType.forFields(units));
		return period;
	}

	public static Long convertPeriodToMillis(Period period) {
		Long total = 0L;
		for (DurationFieldType type : period.getFieldTypes()) {
			total += ChronoUnit.valueOf(type.getName().toUpperCase()).getDuration().multipliedBy(period.get(type)).toMillis();
		}
		return total;
	}

	public static Date parseUTCDate(String dateString) {
		try {
			if (StringUtil.isEmpty(dateString)) {
				return null;
			}
			return getISODateFormatter().parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get client offset and timezone, and sends it to JasperReports for printing
	 * 
	 * @param dateString The string to read a date from
	 * @param dateFormat The format of the date string
	 * 
	 * @return Date object
	 */
	public static TimeZone returnClientTimeZone(String timezoneId, Integer timezoneOffset) {
		try {
			if (!timezoneId.isEmpty() && timezoneOffset != null) {
				//Get the user's timezone from variable timezoneId 
				//and create a timezone variable to send it to Jasper
				Calendar cal = Calendar.getInstance();
				TimeZone timeZone = cal.getTimeZone();
				timeZone.setID(timezoneId);
				timeZone.setRawOffset(-(timezoneOffset * 60000));
				return timeZone;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param month : range 1 - 12
	 * @return the first day of the month
	 */
	public static Date getFirstDayOfMonth(int month) {
		Date result = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);//Zero based
		cal.set(Calendar.DAY_OF_MONTH, 1);
		result = cal.getTime();
		result = DateUtil.trimTime(result);
		return result;
	}

	/**
	 * 
	 * @param month : range 1 - 12
	 * @return the last day of the month
	 */
	public static Date getLastDayOfMonth(int month) {
		Date result = new Date();
		Date currentDate = getFirstDayOfMonth(month);
		YearMonth yearMonthObject = YearMonth.of(Calendar.getInstance().get(Calendar.YEAR), month);
		int daysInMonth = yearMonthObject.lengthOfMonth();
		result = DateUtil.addDays(currentDate, daysInMonth);
		result = DateUtil.addSeconds(result, -1);
		return result;
	}

	/**
	 * Get the year of the date.
	 * 
	 * @param date
	 * 
	 * @return year number
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * Get a number the represents the duration between the two dates according to unit.
	 * 
	 * @param unit
	 * @param date1
	 * @param date2
	 * 
	 * @return double
	 */
	public static double getDurationBetween(ChronoUnit unit, Date date1, Date date2) {
		double difference = date1.getTime() - date2.getTime();
		return difference / unit.getDuration().toMillis();
	}

}

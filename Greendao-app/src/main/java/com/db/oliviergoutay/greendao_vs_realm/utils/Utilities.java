package com.db.oliviergoutay.greendao_vs_realm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by olivier.goutay on 6/7/16.
 */
public final class Utilities {

    /**
     * Year-Month-Day pattern: 2015-01-28
     */
    public static final String DATE_YEAR_MONTH_DAY_STRING_FORMAT = "yyyy-MM-dd";

    /**
     * Standard format 'yyyy-MM-dd'
     *
     * @param dateString The string to parse
     * @return The date or null
     */
    public static Date stringToDate(String dateString) {
        return stringToDate(dateString, DATE_YEAR_MONTH_DAY_STRING_FORMAT);
    }

    /**
     * Converts a string representation of a date to that date
     *
     * @param dateString The string to parse
     * @param format     The format to parse the string with
     * @return The date, or null
     */
    public static Date stringToDate(String dateString, String format) {
        if (dateString == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.US);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param date
     * @return String in the format {@link #DATE_YEAR_MONTH_DAY_STRING_FORMAT}
     */
    public static String dateToString(Date date) {
        return dateToString(date, DATE_YEAR_MONTH_DAY_STRING_FORMAT);
    }

    /**
     * @param date
     * @param
     * @return String in the format passed
     */
    public static String dateToString(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.US);
        return df.format(date);
    }

    /**
     * Returns the previous day of the passed {@link Date}
     *
     * @return The date - 24 hours
     */
    public static Date getYesterday(Date date) {
        if (date == null) {
            return getYesterday(new Date());
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_WEEK, -1);

        return new Date(cal.getTimeInMillis());
    }
}

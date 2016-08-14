package org.fossasia.openevent.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Helper class for handling a most common subset of ISO 8601 strings
 * (in the following format: "2008-03-01T13:00:00+01:00"). It supports
 * parsing the "Z" timezone, but many other less-used features are
 * missing.
 */
public final class ISO8601Date {
    /**
     * Transform Calendar to ISO 8601 string.
     */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .format(date);
        //to add the ':' to timezone
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Get current date and time formatted as ISO 8601 string.
     */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }


    public static String getTimeZoneDateString(final Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM yyyy, HH:mm, z");
        dateFormat.setTimeZone(TimeZone.getDefault());
        String DateToStr = dateFormat.format(date);
        return DateToStr;
    }

    public static Date getTimeZoneDate(final Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM yyyy, HH:mm, z");
        dateFormat.setTimeZone(TimeZone.getDefault());
        String DateToStr = dateFormat.format(date);
        return date;
    }

    public static String get24HourTime(final Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ");

        return dateFormat.format(date);
    }

    public static String get12HourTime(final Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("KK:mm a");

        return dateFormat.format(date);
    }


    public static Date getDateObject(final String iso8601String) {

        StringBuilder s = new StringBuilder();
        s.append(iso8601String).append("Z");
        String final1 = s.toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date date = null;
        try {
            date = format.parse(final1);
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at ISO8601Date::getDateObject.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return date;
    }


}
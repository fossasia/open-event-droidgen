package org.fossasia.openevent.utils;

import android.preference.PreferenceManager;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.RealmDataRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
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

    private static String eventTimezone = "";
    private static final String TIMEZONE_MODE = "timezone_mode";


    private static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
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

    public static String dateFromCalendar(Calendar currentDate) {
        return fromCalendar(currentDate).split("T")[0];
    }

    public static String getTimeZoneDateFromString(String dateString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formatter.setTimeZone(getTimezone());
        Date date = formatter.parse(dateString);
        SimpleDateFormat newFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
        newFormat.setTimeZone(getTimezone());
        return newFormat.format(date);
    }

    public static String getTimeZoneDateString(final Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM yyyy, HH:mm, z", Locale.getDefault());
        dateFormat.setTimeZone(getTimezone());
        return dateFormat.format(date);
    }

    public static Date getTimeZoneDate(final Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM yyyy, HH:mm, z", Locale.getDefault());
        dateFormat.setTimeZone(getTimezone());
        String DateToStr = dateFormat.format(date);
        return date;
    }

    public static String get24HourTime(final Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ", Locale.getDefault());
        dateFormat.setTimeZone(getTimezone());
        return dateFormat.format(date);
    }

    public static String get12HourTime(final Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        dateFormat.setTimeZone(getTimezone());
        return dateFormat.format(date);
    }

    public static  String getDate(final Date date) {
       SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        dateFormat.setTimeZone(getTimezone());
        return dateFormat.format(date);
    }


    public static Date getDateObject(final String iso8601String) {
        setEventTimezone();
        String final1 = iso8601String + "Z";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(getEventTimezone());

        Date date = null;
        try {
            date = format.parse(final1);
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at ISO8601Date::getDateObject.");
        }

        return date;
    }

    private static TimeZone getEventTimezone() {
        setEventTimezone();
        return TimeZone.getTimeZone(eventTimezone);
    }

    private static TimeZone getTimezone() {
        TimeZone selected;
        if (!PreferenceManager.getDefaultSharedPreferences(OpenEventApp.getAppContext()).getBoolean(TIMEZONE_MODE, false)) {
            setEventTimezone();
            selected = TimeZone.getTimeZone(eventTimezone);
        } else {
            selected = TimeZone.getDefault();
        }
        return selected;

    }

    private static void setEventTimezone() {
        if (eventTimezone.isEmpty()) {
            Event event = RealmDataRepository.getDefaultInstance().getEventSync();
            ISO8601Date.eventTimezone = (event.getTimezone());
        }
    }
}
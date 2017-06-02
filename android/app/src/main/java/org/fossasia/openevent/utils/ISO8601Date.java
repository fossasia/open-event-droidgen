package org.fossasia.openevent.utils;

import android.preference.PreferenceManager;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private static TimeZone timeZone;
    private static TimeZone eventTimeZone;

    //set timezone of event
    public static void setTimeZone() {
        timeZone = getTimeZone();
    }

    //set timezone to user-preferred timezone
    public static void setTimeZone(String id) {
        timeZone = TimeZone.getTimeZone(id);
    }

    //set event timezone
    public static void setEventTimeZone() {
        eventTimeZone = getEventTimeZone();
    }

    //set user-preferred event timezone
    public static void setEventTimeZone(String id) {
        eventTimeZone = TimeZone.getTimeZone(id);
    }

    //set default timezone
    public static void setTimeZoneDefault() {
        timeZone = TimeZone.getDefault();
    }

    //return timezone by id
    public static TimeZone getTimeZoneById(String id) {
        return TimeZone.getTimeZone(id);
    }

    //return timezone of event
    public static TimeZone getEventTimeZone() {
        String eventTimeZone = PreferenceManager.getDefaultSharedPreferences(OpenEventApp.getAppContext()).getString(ConstantStrings.TIMEZONE, "");
        return ISO8601Date.getTimeZoneById(eventTimeZone);
    }

    //return timezone of the event
    public static TimeZone getTimeZone() {
        String eventTimeZone;

        //sets timezone to event timezone
        eventTimeZone = PreferenceManager.getDefaultSharedPreferences(OpenEventApp.getAppContext()).getString(ConstantStrings.TIMEZONE, "");
        //sets timezone to local if user has selected
        String isLocalTimeZone = OpenEventApp.getAppContext().getString(R.string.timezone_mode_key);
        if(PreferenceManager.getDefaultSharedPreferences(OpenEventApp.getAppContext()).getBoolean(isLocalTimeZone, false)) {
            return TimeZone.getDefault();
        }
        return ISO8601Date.getTimeZoneById(eventTimeZone);
    }

    //return date from calender
    public static String dateFromCalendar(Calendar currentDate) {
        return fromCalendar(currentDate).split("T")[0];
    }

    private static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
                .format(date);
        //to add the ':' to timezone
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    //return part of a date string
    public static String getDateStringSplit(String dateString, int splitId) {
        if(dateString!=null) {
            return dateString.split(",")[splitId];
        }
        return dateString;
    }

    //return combined day and date from start and end string
    public static String getDateFromDateString(String startDate, String endDate) {
        String start = ISO8601Date.getTimeZoneDateStringFromString(startDate);
        String end = ISO8601Date.getTimeZoneDateStringFromString(endDate);
        String combinedDateString = ISO8601Date.getDateStringSplit(start, 0) + "," + ISO8601Date.getDateStringSplit(start, 1) + " - " +
                ISO8601Date.getDateStringSplit(end, 0) + "," + ISO8601Date.getDateStringSplit(end, 1);
        return combinedDateString;
    }

    //returns day and date of the event
    public static String getDateFromStartDateString(String startDate) {
        String start = ISO8601Date.getTimeZoneDateStringFromString(startDate);
        String combinedDateString = ISO8601Date.getDateStringSplit(start, 0) + "," + ISO8601Date.getDateStringSplit(start, 1);
        return combinedDateString;
    }

    //return combined time and timezone string from start string
    public static String getTimeFromStartDateString(String startDate) {
        String start = ISO8601Date.getTimeZoneDateStringFromString(startDate);
        String combinedDateString = ISO8601Date.getDateStringSplit(start, 2) + "," + ISO8601Date.getDateStringSplit(start, 3);
        return combinedDateString;
    }

    //return combined time and timezone string from end string
    public static String getTimeFromEndDateString(String endDate) {
        String end = ISO8601Date.getTimeZoneDateStringFromString(endDate);
        String combinedDateString = ISO8601Date.getDateStringSplit(end, 2) + "," + ISO8601Date.getDateStringSplit(end, 3);
        return combinedDateString;
    }

    //return combined 12 hour date string from start and end string
    public static String get12HourTimeFromCombinedDateString(String startDate, String endDate) {
        String startString = ISO8601Date.get12HourTimeFromString(startDate);
        String endString = ISO8601Date.get12HourTimeFromString(endDate);
        return startString + " - " + endString;
    }

    //return date string of a specified timezone from a given date string
    public static String getTimeZoneDateStringFromString(String dateString) {
        String currentDate = dateString + "Z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        simpleDateFormat.setTimeZone(eventTimeZone);
        Date date = null;
        String formattedDateString = null;
        try {
            if(dateString!=null) {
                date = simpleDateFormat.parse(currentDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM yyyy, HH:mm, z", Locale.getDefault());
                dateFormat.setTimeZone(timeZone);
                formattedDateString = dateFormat.format(date);
            }
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at ISO8601Date::getDateObject.");
        }

        return formattedDateString;
    }

    //return date of specified timezone from a given date string
    public static Date getTimeZoneDateFromString(String dateString) {
        String currentDate = dateString + "Z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        simpleDateFormat.setTimeZone(eventTimeZone);
        Date date = null;
        try {
            if(dateString!=null) {
                date = simpleDateFormat.parse(currentDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM yyyy, HH:mm, z", Locale.getDefault());
                dateFormat.setTimeZone(timeZone);
                String date_string = dateFormat.format(date);
            }
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at ISO8601Date::getDateObject.");
        }
        return date;
    }

    //return date string of a specified timezone from a given date string for schedule fragment
    public static String getTimeZoneDateStringFromStringForDayFragment(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        simpleDateFormat.setTimeZone(eventTimeZone);
        Date date = null;
        String formattedDateString = null;
        try {
            if(dateString!=null) {
                date = simpleDateFormat.parse(dateString);
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
                dateFormat.setTimeZone(timeZone);
                formattedDateString = dateFormat.format(date);
            }
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at ISO8601Date::getDateObject.");
        }
        return formattedDateString;
    }

    //get 24 hour time string of specified timezone from a given date string
    public static String get24HourTimeFromString(String dateString) {
        String currentDate = dateString + "Z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        simpleDateFormat.setTimeZone(eventTimeZone);
        Date date = null;
        String formattedDateString = null;
        try {
            if(dateString!=null) {
                date = simpleDateFormat.parse(currentDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ", Locale.getDefault());
                dateFormat.setTimeZone(timeZone);
                formattedDateString = dateFormat.format(date);
            }
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at ISO8601Date::getDateObject.");
        }
        return formattedDateString;
    }

    //get 12 hour time string of specified timezone from a given date string
    public static String get12HourTimeFromString(String dateString) {
        String currentDate = dateString + "Z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        simpleDateFormat.setTimeZone(eventTimeZone);
        Date date = null;
        String formattedDateString = null;
        try {
            if(dateString!=null) {
                date = simpleDateFormat.parse(currentDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                dateFormat.setTimeZone(timeZone);
                formattedDateString = dateFormat.format(date);
            }
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at ISO8601Date::getDateObject.");
        }
        return formattedDateString;
    }
}
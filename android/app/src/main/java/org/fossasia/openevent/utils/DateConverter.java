package org.fossasia.openevent.utils;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.fossasia.openevent.data.extras.EventDates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class DateConverter {

    public static final String FORMAT_12H = "hh:mm a";
    public static final String FORMAT_24H = "HH:mm";
    public static final String FORMAT_DATE_COMPLETE = "EE, dd MMM yyyy";
    public static final String FORMAT_DATE = "d MMM";

    private static boolean showLocalTimeZone;

    // Need to replace dynamically for test
    private static String iso8601WithTimezone = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String ISO8601_WITHOUT_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String INVALID_DATE = "Invalid Date";
    private static final Locale defaultLocale = Locale.getDefault();

    // Formatters to parse date
    private static SimpleDateFormat ISO_TIMEZONE_FORMATTER;
    private static SimpleDateFormat ISO_FORMATTER;
    private static SimpleDateFormat DATE_FORMATTER;

    // Formatters to format dates
    private static SimpleDateFormat TIME_12H_FORMATTER;
    private static SimpleDateFormat TIME_24H_FORMATTER;
    private static SimpleDateFormat DATE_COMPLETE_FORMATTER;
    private static SimpleDateFormat DATE_SHORT_FORMATTER;

    static {
        instantiateFormatters();
    }

    private static void instantiateFormatters() {
        ISO_TIMEZONE_FORMATTER = new SimpleDateFormat(iso8601WithTimezone, defaultLocale);
        ISO_FORMATTER = new SimpleDateFormat(ISO8601_WITHOUT_TIMEZONE, defaultLocale);
        DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, defaultLocale);

        TIME_12H_FORMATTER = new SimpleDateFormat(FORMAT_12H, defaultLocale);
        TIME_24H_FORMATTER = new SimpleDateFormat(FORMAT_24H, defaultLocale);
        DATE_COMPLETE_FORMATTER = new SimpleDateFormat(FORMAT_DATE_COMPLETE, defaultLocale);
        DATE_SHORT_FORMATTER = new SimpleDateFormat(FORMAT_DATE, defaultLocale);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public static void setForTest() {
        // Android does not support 'XXX' and Java does not support 'Z'
        // So, we need to replace this just for testing

        iso8601WithTimezone = iso8601WithTimezone.replace("Z", "XXX");
        ISO_TIMEZONE_FORMATTER = new SimpleDateFormat(iso8601WithTimezone, defaultLocale);
    }

    public static void setShowLocalTimeZone(boolean showLocalTimeZone) {
        DateConverter.showLocalTimeZone = showLocalTimeZone;
        instantiateFormatters();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private static SimpleDateFormat getTemplateStringFormatter() {
        if (showLocalTimeZone)
            return ISO_TIMEZONE_FORMATTER;

        return ISO_FORMATTER;
    }

    private static SimpleDateFormat getFormatter(@NonNull String format) {
        // Match with pre-compiled formatters and instantiate new if not matched
        switch (format) {
            case FORMAT_12H:
                return TIME_12H_FORMATTER;
            case FORMAT_24H:
                return TIME_24H_FORMATTER;
            case FORMAT_DATE:
                return DATE_SHORT_FORMATTER;
            case FORMAT_DATE_COMPLETE:
                return DATE_COMPLETE_FORMATTER;
            default:
                return new SimpleDateFormat(format, defaultLocale);
        }
    }

    // Internal convenience methods to reduce boilerplate

    @NonNull
    private static Date getDate(@NonNull SimpleDateFormat formatter, @NonNull String isoDateString) throws ParseException {
        return formatter.parse(isoDateString);
    }

    @NonNull
    private static String formatDate(@NonNull String format, @NonNull Date isoDate) throws ParseException {
        return getFormatter(format).format(isoDate);
    }

    // Public methods

    @NonNull
    public static Date getDate(@NonNull String isoDateString) throws ParseException {
        return getDate(getTemplateStringFormatter(), isoDateString);
    }

    // Currently unused but should be used in future to hide fields if not using default string
    @NonNull
    public static String formatDate(@NonNull String format, @NonNull String isoDateString) throws ParseException {
        return formatDate(format, getDate(isoDateString));
    }

    @NonNull
    public static String formatDateWithDefault(@NonNull String format, @NonNull String isoString, @NonNull String defaultString) {
        String formatted = defaultString;

        try {
            formatted = formatDate(format, isoString);
        } catch (ParseException pe) {
            Timber.e(pe);
            Timber.e("Error parsing date %s with format %s and default string %s",
                    isoString,
                    format,
                    defaultString);
        }

        return formatted;
    }

    @NonNull
    public static String formatDateWithDefault(@NonNull String format, @NonNull String isoString) {
        return formatDateWithDefault(format, isoString, INVALID_DATE);
    }

    @NonNull
    public static String formatDay(@NonNull String isoString) throws ParseException {
        return formatDate(FORMAT_DATE, getDate(DATE_FORMATTER, isoString));
    }

    @NonNull
    public static List<EventDates> getDaysInBetween(@NonNull String startDate, @NonNull String endDate) throws ParseException {
        List<EventDates> dates = new ArrayList<>();

        Date start = getDate(DATE_FORMATTER, startDate);
        Date end = getDate(DATE_FORMATTER, endDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        while (calendar.getTime().getTime() <= end.getTime()) {
            dates.add(new EventDates(formatDate(DATE_FORMAT, calendar.getTime())));
            calendar.add(Calendar.DATE, 1);
        }

        return dates;
    }

    public static String getRelativeTimeFromTimestamp(String timeStamp) throws ParseException {
        Date timeCreatedDate = getDate(ISO_TIMEZONE_FORMATTER, timeStamp);

        return (String) android.text.format.DateUtils.getRelativeTimeSpanString(
                (timeCreatedDate.getTime()),
                System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS);
    }
}

package org.fossasia.openevent.common.date;

import android.support.annotation.NonNull;

import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.data.extras.EventDates;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.Temporal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class DateConverter {

    public static final String FORMAT_12H = "hh:mm a";
    public static final String FORMAT_24H = "HH:mm";
    public static final String FORMAT_DATE_COMPLETE = "EE, dd MMM yyyy";
    public static final String FORMAT_DATE = "d MMM";
    public static final String FORMAT_ISO_DATE_TIME_WITH_TIME_ZONE = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String INVALID_DATE = "Invalid Date";

    private static final Map<String, DateTimeFormatter> formatterMap = new HashMap<>();
    private static boolean showLocal = false;
    private static String eventTimeZone;

    private static String getEventTimeZone() {
        if (eventTimeZone == null)
            eventTimeZone = SharedPreferencesUtil.getString(ConstantStrings.TIMEZONE, "");
        return eventTimeZone;
    }

    // Internal convenience methods to reduce boilerplate

    private static DateTimeFormatter getFormatter(@NonNull String format) {
        if (!formatterMap.containsKey(format))
            formatterMap.put(format, DateTimeFormatter.ofPattern(format));

        return formatterMap.get(format);
    }

    @NonNull
    private static String formatDate(@NonNull String format, @NonNull Temporal isoDate) {
        return getFormatter(format).format(isoDate);
    }

    // Public methods

    @NonNull
    public static ZoneId getZoneId() {
        if (showLocal || Utils.isEmpty(getEventTimeZone()))
            return ZoneId.systemDefault();
        else
            return ZoneId.of(getEventTimeZone());
    }

    public static void setShowLocalTime(boolean showLocal) {
        DateConverter.showLocal = showLocal;
    }

    public static void setEventTimeZone(String timeZone) {
        DateConverter.eventTimeZone = timeZone;
    }

    @NonNull
    public static String formatDateToIso(@NonNull LocalDateTime date) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date.atZone(getZoneId()));
    }

    @NonNull
    public static ZonedDateTime getDate(@NonNull String isoDateString) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        try {
            zonedDateTime = ZonedDateTime.parse(isoDateString).withZoneSameInstant(getZoneId());
        } catch (DateTimeParseException pe) {
            Timber.e(pe);
            Timber.e("Error parsing date %s. Default ZonedDateTime : %s",
                    isoDateString, zonedDateTime.toString());
            throw pe;
        } catch (NullPointerException ne) {
            Timber.e(ne);
            Timber.e("Error parsing date because input string is null");
        }
        return zonedDateTime;
    }

    @NonNull
    public static ZonedDateTime getDate(@NonNull String format, @NonNull String dateString) {
        return ZonedDateTime.from(getFormatter(format).parse(dateString));
    }

    // Currently unused but should be used in future to hide fields if not using default string
    @NonNull
    public static String formatDate(@NonNull String format, @NonNull String isoDateString) {
        return formatDate(format, getDate(isoDateString));
    }

    @NonNull
    public static String formatDateWithDefault(@NonNull String format, @NonNull String isoString, @NonNull String defaultString) {
        String formatted = defaultString;

        try {
            formatted = formatDate(format, isoString);
        } catch (DateTimeParseException pe) {
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
    public static String formatDay(@NonNull String dateString) throws DateTimeParseException {
        return formatDate(FORMAT_DATE, LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(dateString)));
    }

    @NonNull
    public static List<EventDates> getDaysInBetween(@NonNull String startDate, @NonNull String endDate) throws DateTimeParseException {
        List<EventDates> dates = new ArrayList<>();

        LocalDate start = getDate(startDate).toLocalDate();
        LocalDate end = getDate(endDate).toLocalDate();

        //add start date
        dates.add(new EventDates(DateTimeFormatter.ISO_LOCAL_DATE.format(start)));

        //add dates between start date and end date
        for (int i = 1; start.plusDays(i).isBefore(end); i++) {
            dates.add(new EventDates(DateTimeFormatter.ISO_LOCAL_DATE.format(start.plusDays(i))));
        }

        //add end date
        dates.add(new EventDates(DateTimeFormatter.ISO_LOCAL_DATE.format(end)));

        return dates;
    }

    public static String getRelativeTimeFromTimestamp(String timeStamp) throws DateTimeParseException {
        ZonedDateTime timeCreatedDate = ZonedDateTime.from(getFormatter(FORMAT_ISO_DATE_TIME_WITH_TIME_ZONE).parse(timeStamp));
        return (String) android.text.format.DateUtils.getRelativeTimeSpanString(
                (timeCreatedDate.toInstant().toEpochMilli()),
                System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS);
    }

    public static String getRelativeTimeFromUTCTimeStamp(String timeStamp) throws DateTimeParseException {
        Instant timestampInstant = Instant.parse(timeStamp);
        ZonedDateTime timeCreatedDate = ZonedDateTime.ofInstant(timestampInstant, getZoneId());
        return (String) android.text.format.DateUtils.getRelativeTimeSpanString(
                (timeCreatedDate.toInstant().toEpochMilli()),
                System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS);
    }
}

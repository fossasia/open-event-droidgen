package org.fossasia.openevent.common.date;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

public class AndroidDateConverter {

    public static String getRelativeTimeFromOffsetDateTime(String timeStamp) throws DateTimeParseException {
        ZonedDateTime timeCreatedDate = ZonedDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(timeStamp));
        return (String) android.text.format.DateUtils.getRelativeTimeSpanString(
                (timeCreatedDate.toInstant().toEpochMilli()),
                System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS);
    }
}

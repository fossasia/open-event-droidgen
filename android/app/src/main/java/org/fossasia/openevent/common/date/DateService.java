package org.fossasia.openevent.common.date;

import org.threeten.bp.ZonedDateTime;

public class DateService {
    public static boolean isOngoingSession(ZonedDateTime start, ZonedDateTime end, ZonedDateTime current) {
        return (start.isBefore(current) || start.equals(current)) && end.isAfter(current);
    }

    public static boolean isUpcomingSession(ZonedDateTime start, ZonedDateTime end, ZonedDateTime current) {
        return start.isAfter(current) && end.isAfter(current);
    }
}
package org.fossasia.openevent.utils;

import org.threeten.bp.ZonedDateTime;

/**
 * Created by mw on 22/07/17.
 */

public class DateService {
    public static boolean isOngoingSession(ZonedDateTime start, ZonedDateTime end, ZonedDateTime current) {
        return (start.isBefore(current) || start.equals(current)) && end.isAfter(current);
    }

    public static boolean isUpcomingSession(ZonedDateTime start, ZonedDateTime end, ZonedDateTime current) {
        return start.isAfter(current) && end.isAfter(current);
    }
}
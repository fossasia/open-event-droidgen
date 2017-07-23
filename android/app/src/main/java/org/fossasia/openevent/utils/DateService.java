package org.fossasia.openevent.utils;

import java.util.Date;

/**
 * Created by mw on 22/07/17.
 */

public class DateService {
    public static boolean isUpcomingSession(Date start, Date end, Date current) {
        return ((start.before(current) || start.equals(current)) && (end.after(current) || end.equals(current)));
    }
}

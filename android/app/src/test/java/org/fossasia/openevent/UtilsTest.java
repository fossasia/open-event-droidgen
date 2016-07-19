package org.fossasia.openevent;

import org.fossasia.openevent.utils.ISO8601Date;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;


/**
 * Created by championswimmer on 20/7/16.
 */
public class UtilsTest {

    @Test
    public void calendarTest () {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(2016-1900, 6-1, 15, 10, 0, 0));
        String timestamp = ISO8601Date.fromCalendar(cal).substring(0,19);
        assertEquals(timestamp, "2016-06-15T10:00:00");

        assertEquals(ISO8601Date.getDateObject(timestamp).getTime(), 1465984800000L);
    }
}

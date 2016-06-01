package org.fossasia.openevent.utils;


import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static junit.framework.TestCase.assertEquals;


/**
 * Created by championswimmer on 11/4/16.
 */

public class DateTest {

    @Test
    public void testISO8601DateUtils() {
        assertEquals("02:02", ISO8601Date.getTime(new Date(2015, 2, 2, 2,2,2)));


        Calendar cal = new GregorianCalendar(2015, 2, 2, 2,2,2);
        String returnedTime = ISO8601Date.fromCalendar((cal));
        assertEquals("2015-03-02T02:02:02", returnedTime.split("(\\+)")[0]);

        assertEquals(1, ISO8601Date.getDateObject("2015-02-02T02:02:02Z").getMonth());
        assertEquals(2, ISO8601Date.getDateObject("2015-02-02T02:02:02Z").getDate());
    }
}

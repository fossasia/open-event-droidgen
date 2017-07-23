package org.fossasia.openevent.utils;

import org.fossasia.openevent.data.extras.EventDates;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static org.fossasia.openevent.utils.DateConverter.getDate;

public class DateTest {

    @Before
    public void setUp() {
        DateConverter.setForTest();
    }

    // Conversion checks
    @Test
    public void shouldFormatArbitraryWithoutTimeZone() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));
        DateConverter.setShowLocalTimeZone(false);

        String date  = "2017-03-17T14:00:00+08:00";
        assertEquals("17 03 2017 02:00:00 PM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
        DateConverter.setShowLocalTimeZone(false);
        assertEquals("17 03 2017 02:00:00 PM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        DateConverter.setShowLocalTimeZone(false);
        assertEquals("17 03 2017 02:00:00 PM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));
    }

    @Test
    public void shouldFormatArbitraryWithTimeZone() throws Exception {
        DateConverter.setShowLocalTimeZone(true);

        String date  = "2017-03-18T02:00:00+08:00";

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        assertEquals("Failed for Kolkata", "17 03 2017 11:30:00 PM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));
        assertEquals("Failed for US/Pacific", "17 03 2017 11:00:00 AM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Singapore"));
        assertEquals("Failed for Asia/Singapore", "18 03 2017 02:00:00 AM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));
    }

    @Test
    public void shouldReturn12HourTime() throws Exception {
        String date = "2017-01-20T16:00:00+10:00";

        DateConverter.setShowLocalTimeZone(false);
        assertEquals("Failed for Global Time", "04:00 PM", DateConverter.formatDate(DateConverter.FORMAT_12H, date));

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        DateConverter.setShowLocalTimeZone(true);
        assertEquals("Failed for Local Time", "11:30 AM", DateConverter.formatDate(DateConverter.FORMAT_12H, date));
    }

    @Test
    public void shouldReturn24HourTime() throws Exception {
        String date = "2017-01-20T24:24:00-09:00";

        DateConverter.setShowLocalTimeZone(false);
        assertEquals("Failed for Global Time", "00:24", DateConverter.formatDate(DateConverter.FORMAT_24H, date));

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
        DateConverter.setShowLocalTimeZone(true);
        assertEquals("Failed for Local Time", "20:24", DateConverter.formatDate(DateConverter.FORMAT_24H, date));
    }

    @Test
    public void shouldReturnCompleteDate() throws Exception {
        String date = "2017-11-09T23:08:06-07:30";

        DateConverter.setShowLocalTimeZone(false);
        assertEquals("Failed for Global Time", "Thu, 09 Nov 2017", DateConverter.formatDate(DateConverter.FORMAT_DATE_COMPLETE, date));

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        DateConverter.setShowLocalTimeZone(true);
        assertEquals("Failed for Local Time", "Fri, 10 Nov 2017", DateConverter.formatDate(DateConverter.FORMAT_DATE_COMPLETE, date));
    }

    @Test
    public void shouldReturnFormattedDatedWithDefaultString() throws ParseException {
        String date = "Wrong Date";

        assertEquals(date, date, DateConverter.formatDateWithDefault(date, date, date));
    }

    @Test
    public void shouldReturnFormattedDatedWithDefaultStringImplicit() throws ParseException {
        String date = "Wrong Date";

        assertEquals(date, "Invalid Date", DateConverter.formatDateWithDefault(date, date));
    }

    private static void assertDateEquals(Date date, int day, int month, int year, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(month, calendar.get(Calendar.MONTH) + 1);
        assertEquals(year, calendar.get(Calendar.YEAR));
        assertEquals(hour, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, calendar.get(Calendar.MINUTE));
        assertEquals(second, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldReturnDay() throws ParseException {
        String date = "2017-11-09T23:08:56-07:30";

        DateConverter.setShowLocalTimeZone(false);
        Date date1 = getDate(date);
        assertDateEquals(date1, 9, 11, 2017, 23, 8, 56);

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        DateConverter.setShowLocalTimeZone(true);
        Date date2 = getDate(date);
        assertDateEquals(date2, 10, 11, 2017, 6, 38, 56);
    }

    @Test
    public void shouldReturnDaysInBetween() throws ParseException {
        // End time before start time
        String start = "2017-11-09T23:08:56-03:30";
        String end = "2017-11-12T09:23:45-03:30";

        List<EventDates> eventDates = DateConverter.getDaysInBetween(start, end);
        assertEquals(4, eventDates.size());
        assertEquals("2017-11-09", eventDates.get(0).getDate());
        assertEquals("2017-11-10", eventDates.get(1).getDate());
        assertEquals("2017-11-11", eventDates.get(2).getDate());
        assertEquals("2017-11-12", eventDates.get(3).getDate());

        // End Time after start time
        start = "2017-01-19T10:08:56-03:30";
        end = "2017-01-21T20:23:45-03:30";

        eventDates = DateConverter.getDaysInBetween(start, end);
        assertEquals(3, eventDates.size());
        assertEquals("2017-01-19", eventDates.get(0).getDate());
        assertEquals("2017-01-20", eventDates.get(1).getDate());
        assertEquals("2017-01-21", eventDates.get(2).getDate());
    }
}
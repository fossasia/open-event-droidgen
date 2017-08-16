package org.fossasia.openevent.utils;

import org.fossasia.openevent.data.extras.EventDates;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;

public class DateTest {

    @Before
    public void setUp() {
        DateConverter.setEventTimeZone("Asia/Singapore");
        DateConverter.setShowLocalTime(false);
    }

    // Conversion checks
    @Test
    public void shouldFormatArbitraryWithoutTimeZone() throws Exception {
        DateConverter.setShowLocalTime(false);

        String date = "2017-03-17T14:00:00+08:00";

        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));
        assertEquals("17 03 2017 02:00:00 PM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
        assertEquals("17 03 2017 02:00:00 PM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        assertEquals("17 03 2017 02:00:00 PM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));
    }

    @Test
    public void shouldFormatArbitraryWithTimeZone() throws Exception {
        DateConverter.setShowLocalTime(true);

        String date = "2017-03-18T02:00:00+08:00";

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        assertEquals("Failed for Kolkata", "17 03 2017 11:30:00 PM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("US/Pacific"));
        assertEquals("Failed for US/Pacific", "17 03 2017 11:00:00 AM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Singapore"));
        assertEquals("Failed for Asia/Singapore", "18 03 2017 02:00:00 AM", DateConverter.formatDate("dd MM YYYY hh:mm:ss a", date));
    }

    @Test
    public void shouldReturn12HourTime() throws Exception {
        String date = "2017-01-20T16:00:00+08:00";

        DateConverter.setShowLocalTime(false);
        assertEquals("Failed for Global Time", "04:00 PM", DateConverter.formatDate(DateConverter.FORMAT_12H, date));

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        DateConverter.setShowLocalTime(true);
        assertEquals("Failed for Local Time", "01:30 PM", DateConverter.formatDate(DateConverter.FORMAT_12H, date));
    }

    @Test
    public void shouldReturn24HourTime() throws Exception {
        DateConverter.setEventTimeZone("Pacific/Gambier");
        String date = "2017-01-20T23:24:00-09:00";

        DateConverter.setShowLocalTime(false);
        assertEquals("Failed for Global Time", "23:24", DateConverter.formatDate(DateConverter.FORMAT_24H, date));

        TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
        DateConverter.setShowLocalTime(true);
        assertEquals("Failed for Local Time", "19:24", DateConverter.formatDate(DateConverter.FORMAT_24H, date));
    }

    @Test
    public void shouldReturnCompleteDate() throws Exception {
        DateConverter.setEventTimeZone("Pacific/Gambier");
        String date = "2017-11-09T23:24:06-09:00";

        DateConverter.setShowLocalTime(false);
        assertEquals("Failed for Global Time", "Thu, 09 Nov 2017", DateConverter.formatDate(DateConverter.FORMAT_DATE_COMPLETE, date));

        TimeZone.setDefault(TimeZone.getTimeZone("Amsterdam"));
        DateConverter.setShowLocalTime(true);
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

    @Test
    public void shouldReturnDay() throws ParseException {
        String date = "2017-11-09T23:08:56+08:00";
        DateConverter.setEventTimeZone("Asia/Singapore");

        DateConverter.setShowLocalTime(false);
        ZonedDateTime date1 = DateConverter.getDate(date);
        assertEquals(date1, ZonedDateTime.of(2017, 11, 9, 23, 8, 56, 0, ZoneId.of("Asia/Singapore")));

        TimeZone.setDefault(TimeZone.getTimeZone("Canada/Newfoundland"));
        DateConverter.setShowLocalTime(true);
        ZonedDateTime date2 = DateConverter.getDate(date);
        assertEquals(date2, ZonedDateTime.of(2017, 11, 9, 11, 38, 56, 0, ZoneId.of("Canada/Newfoundland")));
    }

    @Test
    public void shouldReturnDaysInBetween() throws ParseException {
        DateConverter.setEventTimeZone("Canada/Newfoundland");
        DateConverter.setShowLocalTime(false);

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

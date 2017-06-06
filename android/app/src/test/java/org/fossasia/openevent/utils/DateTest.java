package org.fossasia.openevent.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by arpitdec5 on 02-06-2017.
 */

public class DateTest {

    @Test
    public void shouldConvertLocalTimeZoneDateStringToSpecifiedTimeZoneDateString() {
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        //date strings to be used for testing
        String dateString1 = "2017-06-02T07:59:10Z";
        String dateString2 = "2017-06-02T20:00:10Z";
        String dateString3 = "2017-06-03T00:00:10Z";
        String dateString4 = "2017-06-03T07:00:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString1);
        expectedString = "Thu, 01 Jun 2017, 23:59, UTC";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString2);
        expectedString = "Fri, 02 Jun 2017, 12:00, UTC";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString3);
        expectedString = "Fri, 02 Jun 2017, 16:00, UTC";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString4);
        expectedString = "Fri, 02 Jun 2017, 23:00, UTC";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertLocalTimeZoneDateStringToInternationalTimeZoneDateString() {
        //first case
        ISO8601Date.setTimeZone("Europe/Amsterdam");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        String dateString = "2017-06-02T02:29:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString);
        expectedString = "Thu, 01 Jun 2017, 20:29, CEST";
        Assert.assertEquals(expectedString, actualString);

        //second case
        ISO8601Date.setTimeZone("Asia/Kolkata");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString);
        expectedString = "Thu, 01 Jun 2017, 23:59, IST";
        Assert.assertEquals(expectedString, actualString);

        //third case
        ISO8601Date.setTimeZone("Europe/Berlin");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString);
        expectedString = "Thu, 01 Jun 2017, 20:29, CEST";
        Assert.assertEquals(expectedString, actualString);

        //fourth case
        ISO8601Date.setTimeZone("Australia/Sydney");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString);
        expectedString = "Fri, 02 Jun 2017, 04:29, AEST";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertInternationalTimeZoneDateStringToLocalTimeZoneDateString() {
        //first case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        String dateString = "2017-06-02T02:29:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString);
        expectedString = "Thu, 01 Jun 2017, 18:29, UTC";
        Assert.assertEquals(expectedString, actualString);

        //second case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Europe/Amsterdam");

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString);
        expectedString = "Fri, 02 Jun 2017, 00:29, UTC";
        Assert.assertEquals(expectedString, actualString);

        //third case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Kolkata");

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString);
        expectedString = "Thu, 01 Jun 2017, 20:59, UTC";
        Assert.assertEquals(expectedString, actualString);

        //fourth case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Australia/Sydney");

        actualString = ISO8601Date.getTimeZoneDateStringFromString(dateString);
        expectedString = "Thu, 01 Jun 2017, 16:29, UTC";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertLocalTimeZoneDateStringToDesiredTimeZoneDateStringForDayFragment() {
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("UTC");

        //date strings to be used for testing
        String day1 = "2017-06-01";
        String day2 = "2017-06-02";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.getTimeZoneDateStringFromStringForDayFragment(day1);
        expectedString = "1 Jun";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.getTimeZoneDateStringFromStringForDayFragment(day2);
        expectedString = "2 Jun";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertLocalTimeZoneDateStringToSpecifiedTimeZone24HourTimeDateString() {
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        //date strings to be used for testing
        String dateString1 = "2017-06-02T07:59:10Z";
        String dateString2 = "2017-06-02T20:00:10Z";
        String dateString3 = "2017-06-03T00:00:10Z";
        String dateString4 = "2017-06-03T07:00:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.get24HourTimeFromString(dateString1);
        expectedString = "23:59 ";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.get24HourTimeFromString(dateString2);
        expectedString = "12:00 ";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.get24HourTimeFromString(dateString3);
        expectedString = "16:00 ";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.get24HourTimeFromString(dateString4);
        expectedString = "23:00 ";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertLocalTimeZoneDateStringToInternationalTimeZone24HourTimeDateString() {
        //first case
        ISO8601Date.setTimeZone("Europe/Amsterdam");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        String dateString = "2017-06-02T02:29:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.get24HourTimeFromString(dateString);
        expectedString = "20:29 ";
        Assert.assertEquals(expectedString, actualString);

        //second case
        ISO8601Date.setTimeZone("Asia/Kolkata");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.get24HourTimeFromString(dateString);
        expectedString = "23:59 ";
        Assert.assertEquals(expectedString, actualString);

        //third case
        ISO8601Date.setTimeZone("Europe/Berlin");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.get24HourTimeFromString(dateString);
        expectedString = "20:29 ";
        Assert.assertEquals(expectedString, actualString);

        //fourth case
        ISO8601Date.setTimeZone("Australia/Sydney");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.get24HourTimeFromString(dateString);
        expectedString = "04:29 ";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertInternationalTimeZoneDateStringToLocalTimeZone24HourTimeDateString() {
        //first case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        String dateString = "2017-06-02T02:29:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.get24HourTimeFromString(dateString);
        expectedString = "18:29 ";
        Assert.assertEquals(expectedString, actualString);

        //second case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Europe/Amsterdam");

        actualString = ISO8601Date.get24HourTimeFromString(dateString);
        expectedString = "00:29 ";
        Assert.assertEquals(expectedString, actualString);

        //third case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Kolkata");

        actualString = ISO8601Date.get24HourTimeFromString(dateString);
        expectedString = "20:59 ";
        Assert.assertEquals(expectedString, actualString);

        //fourth case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Australia/Sydney");

        actualString = ISO8601Date.get24HourTimeFromString(dateString);
        expectedString = "16:29 ";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertLocalTimeZoneDateStringToSpecifiedTimeZone12HourTimeDateString() {
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        //date strings to be used for testing
        String dateString1 = "2017-06-02T07:59:10Z";
        String dateString2 = "2017-06-02T20:00:10Z";
        String dateString3 = "2017-06-03T00:00:10Z";
        String dateString4 = "2017-06-03T07:00:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.get12HourTimeFromString(dateString1);
        expectedString = "11:59 PM";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.get12HourTimeFromString(dateString2);
        expectedString = "12:00 PM";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.get12HourTimeFromString(dateString3);
        expectedString = "04:00 PM";
        Assert.assertEquals(expectedString, actualString);

        actualString = ISO8601Date.get12HourTimeFromString(dateString4);
        expectedString = "11:00 PM";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertLocalTimeZoneDateStringToInternationalTimeZone12HourTimeDateString() {
        //first case
        ISO8601Date.setTimeZone("Europe/Amsterdam");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        String dateString = "2017-06-02T02:29:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.get12HourTimeFromString(dateString);
        expectedString = "08:29 PM";
        Assert.assertEquals(expectedString, actualString);

        //second case
        ISO8601Date.setTimeZone("Asia/Kolkata");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.get12HourTimeFromString(dateString);
        expectedString = "11:59 PM";
        Assert.assertEquals(expectedString, actualString);

        //third case
        ISO8601Date.setTimeZone("Europe/Berlin");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.get12HourTimeFromString(dateString);
        expectedString = "08:29 PM";
        Assert.assertEquals(expectedString, actualString);

        //fourth case
        ISO8601Date.setTimeZone("Australia/Sydney");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        actualString = ISO8601Date.get12HourTimeFromString(dateString);
        expectedString = "04:29 AM";
        Assert.assertEquals(expectedString, actualString);
    }

    @Test
    public void shouldConvertInternationalTimeZoneDateStringToLocalTimeZone12HourTimeDateString() {
        //first case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Singapore");

        String dateString = "2017-06-02T02:29:10Z";
        String expectedString;
        String actualString;

        actualString = ISO8601Date.get12HourTimeFromString(dateString);
        expectedString = "06:29 PM";
        Assert.assertEquals(expectedString, actualString);

        //second case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Europe/Amsterdam");

        actualString = ISO8601Date.get12HourTimeFromString(dateString);
        expectedString = "12:29 AM";
        Assert.assertEquals(expectedString, actualString);

        //third case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Asia/Kolkata");

        actualString = ISO8601Date.get12HourTimeFromString(dateString);
        expectedString = "08:59 PM";
        Assert.assertEquals(expectedString, actualString);

        //fourth case
        ISO8601Date.setTimeZone("UTC");
        ISO8601Date.setEventTimeZone("Australia/Sydney");

        actualString = ISO8601Date.get12HourTimeFromString(dateString);
        expectedString = "04:29 PM";
        Assert.assertEquals(expectedString, actualString);
    }
}
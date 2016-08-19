package org.fossasia.openevent.data;

import org.fossasia.openevent.dbutils.DbContract;

import java.util.Locale;

/**
 * Created by Manan Wason on 18/06/16.
 */
public class EventDates {
    String date;

    public EventDates(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String generateSql() {
        String insertQuery = "INSERT OR IGNORE INTO %s VALUES ('%s');";
        return String.format(Locale.ENGLISH,
                insertQuery,
                DbContract.EventDates.TABLE_NAME,
                date);
    }
}

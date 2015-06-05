package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Track {

    int id;
    String name;
    String description;

    public Track(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return escapeChar(name);
    }

    public String getDescription() {
        return description;
    }


    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', '%s', %s);";
        String query = String.format(query_normal, DbContract.Tracks.TABLE_NAME, id, name, DatabaseUtils.sqlEscapeString(description));
        return query;

    }

    private String escapeChar(String string) {
        return string.replaceAll("'", "''");
    }

}

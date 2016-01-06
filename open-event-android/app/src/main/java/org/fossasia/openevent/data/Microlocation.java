package org.fossasia.openevent.data;


import android.database.DatabaseUtils;

import org.fossasia.openevent.dbutils.DbContract;


/**
 * Created by MananWason on 26-05-2015.
 */
public class Microlocation {
    int id;

    String name;

    float latitude;

    float longitude;

    int floor;

    public Microlocation(int id, String name, float latitude,
                         float longitude, int floor) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }


    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, '%f', '%f', '%d');";
        String query = String.format(
                query_normal,
                DbContract.Microlocation.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(name),
                latitude,
                longitude,
                floor);
        return query;
    }
}

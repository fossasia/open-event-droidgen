package org.fossasia.openevent.data;


import android.database.DatabaseUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.util.Locale;


/**
 * Created by MananWason on 26-05-2015.
 */
public class Microlocation implements ClusterItem {
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
        String insertQuery = "INSERT INTO %s VALUES ('%d', %s, '%f', '%f', '%d');";
        return String.format(Locale.ENGLISH,
                insertQuery,
                DbContract.Microlocation.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(name)),
                latitude,
                longitude,
                floor);
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude,longitude);
    }
}

package org.fossasia.openevent.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MananWason on 27-05-2015.
 */
public class Event {
    int id;
    String name;
    String logo;
    SimpleDateFormat start;
    SimpleDateFormat end;
    float latitude;
    float longitude;
    String location_name;

    public Event(int id, String name, String logo, SimpleDateFormat start,
                 SimpleDateFormat end, float latitude, float longitude, String location_name) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.start = start;
        this.end = end;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location_name = location_name;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public SimpleDateFormat getStart() {
        return start;
    }

    public void setStart(SimpleDateFormat start) {
        this.start = start;
    }

    public SimpleDateFormat getEnd() {
        return end;
    }

    public void setEnd(SimpleDateFormat end) {
        this.end = end;
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

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }
}

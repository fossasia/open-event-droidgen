package org.fossasia.openevent.data;

import java.text.SimpleDateFormat;

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
    String locationname;

    public Event(int id, String name, String logo, SimpleDateFormat start,
                 SimpleDateFormat end, float latitude, float longitude, String locationname) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.start = start;
        this.end = end;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationname = locationname;
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

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }
}

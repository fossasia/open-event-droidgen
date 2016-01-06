package org.fossasia.openevent.data;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * Created by MananWason on 27-05-2015.
 */
public class Event {

    int id;

    String name;

    String email;

    String color;

    String logo;

    @SerializedName("begin")
    String start;

    @SerializedName("end")
    String end;

    float latitude;

    float longitude;

    @SerializedName("location_name")
    String locationName;

    String url;

    String slogan;

    public Event(int id, String name, String email, String color, String logo, String start,
                 String end, float latitude, float longitude, String locationName, String url, String slogan) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.color = color;
        this.logo = logo;
        this.start = start;
        this.end = end;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.url = url;
        this.slogan = slogan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', '%s', '%s','%s', '%s', '%s', '%s', '%f', '%f', '%s', '%s', '%s');";
        String query = String.format(
                query_normal,
                DbContract.Event.TABLE_NAME,
                id,
                name,
                email,
                color,
                logo,
                start,
                end,
                latitude,
                longitude,
                locationName,
                url,
                slogan);
        return query;
    }


}

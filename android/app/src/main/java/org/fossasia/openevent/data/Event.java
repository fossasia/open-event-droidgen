package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.util.Locale;

/**
 * Created by MananWason on 27-05-2015.
 */
public class Event {

    int id;

    String name;

    String email;

    String logo;

    @SerializedName("start_time")
    String start;

    @SerializedName("end_time")
    String end;

    float latitude;

    float longitude;

    @SerializedName("location_name")
    String locationName;

    @SerializedName("event_url")
    String url;

    String timezone;

    Version version;

    public Event(int id, String name, String email, String logo, String start,
                 String end, float latitude, float longitude, String locationName, String url, String timezone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.logo = logo;
        this.start = start;
        this.end = end;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.url = url;
        this.timezone = timezone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String generateSql() {
        String insertQuery = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, %s, '%f', '%f', %s, %s, %s);";
        return String.format(Locale.ENGLISH,
                insertQuery,
                DbContract.Event.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(name)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(email)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(logo)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(start)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(end)),
                latitude,
                longitude,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(locationName)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(url)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(timezone))
                );
    }
}

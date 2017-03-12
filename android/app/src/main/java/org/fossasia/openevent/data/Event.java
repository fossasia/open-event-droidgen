package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * Created by MananWason on 27-05-2015.
 */
public class Event {

    private int id;

    private String name;

    private String email;

    private String logo;

    @SerializedName("start_time")
    private String start;

    @SerializedName("end_time")
    private String end;

    private float latitude;

    private float longitude;

    @SerializedName("location_name")
    private String locationName;

    @SerializedName("event_url")
    private String url;

    private String timezone;

    private Version version;

    private String description;

    @SerializedName("organizer_description")
    private String organizerDescription;

    @SerializedName("social_links")
    private List<SocialLink> socialLink;

    public Event(int id, String name, String email, String logo, String start,
                 String end, float latitude, float longitude, String locationName, String url, String timezone,
                 String description, String organizerDescription) {
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
        this.description = description;
        this.organizerDescription = organizerDescription;
    }

    public List<SocialLink> getSocialLink() {
        return socialLink;
    }

    public void setSocialLink(List<SocialLink> socialLink) {
        this.socialLink = socialLink;
    }

    public String getOrganizerDescription() {
        return organizerDescription;
    }

    public void setOrganizerDescription(String organizerDescription) {
        this.organizerDescription = organizerDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        String insertQuery = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, %s, '%f', '%f', %s, %s, %s, %s, %s);";
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
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(timezone)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(description)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(organizerDescription))
        );
    }
}
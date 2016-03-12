package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.StringUtils;

import java.text.ParseException;
import java.util.Locale;

import timber.log.Timber;


/**
 * Created by championswimmer on 16/5/15.
 */
public class Session {

    @SerializedName("abstract")
    String summary;

    String description;

    @SerializedName("begin")
    String startTime;

    @SerializedName("end")
    String endTime;

    int id;

    String level;

    @SerializedName("microlocation")
    int microlocations;

    String title;

    String subtitle;

    @SerializedName("format")
    String type;

    int track;

    public Session(int id, String title, String subtitle,
                   String summary, String description,
                   String startTime, String endTime, String type,
                   int track, String level, int microlocations
    ) throws ParseException {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.track = track;
        this.level = level;
        this.microlocations = microlocations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getMicrolocations() {
        return microlocations;
    }

    public void setMicrolocations(int microlocations) {
        this.microlocations = microlocations;
    }

    public String generateSql() {
        String insertQueryFmt = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, %s, %s, %s, '%d', %s, '%d');";
        return String.format(Locale.ENGLISH,
                insertQueryFmt,
                DbContract.Sessions.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(title)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(subtitle)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(summary)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(description)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(startTime)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(endTime)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(type)),
                track,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(level)),
                microlocations);
    }

    public void bookmark(int id) {
        String query_normal = "INSERT INTO %s VALUES ('%d');";
        String query = String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Bookmarks.TABLE_NAME,
                id
        );
        Timber.tag("BOOKMARKS").d(query);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.insertQuery(query);
    }
}

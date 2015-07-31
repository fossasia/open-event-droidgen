package org.fossasia.openevent.data;

import android.database.DatabaseUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.text.ParseException;


/**
 * Created by championswimmer on 16/5/15.
 */
public class Session {

    int id;

    String title;

    String subtitle;

    @SerializedName("abstract")
    String summary;

    String description;

    @SerializedName("start_time")
    String startTime;

    @SerializedName("end_time")
    String endTime;

    String type;

    int track;

    String level;

    int[] speakers;

    @SerializedName("microlocation")
    Integer microlocations;

    public Session(int id, String title, String subtitle,
                   String summary, String description,
                   String startTime, String endTime, String type,
                   int track, String level, int[] speakers, int microlocations
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
        this.speakers = speakers;
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

    public int[] getSpeakers() {
        return speakers;
    }

    public void setSpeakers(int[] speakers) {
        this.speakers = speakers;
    }

    public Integer getMicrolocations() {
        return microlocations;
    }

    public void setMicrolocations(Integer microlocations) {
        this.microlocations = microlocations;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, %s, %s, %s, '%d', %s, '%d');";
        String query = String.format(
                query_normal,
                DbContract.Sessions.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(title),
                DatabaseUtils.sqlEscapeString(subtitle),
                DatabaseUtils.sqlEscapeString(summary),
                DatabaseUtils.sqlEscapeString(description),
                DatabaseUtils.sqlEscapeString(startTime),
                DatabaseUtils.sqlEscapeString(endTime),
                DatabaseUtils.sqlEscapeString(type),
                track,
                DatabaseUtils.sqlEscapeString(level),
                microlocations);
        return query;
    }

    public void bookmark(int id) {
        String query_normal = "INSERT INTO %s VALUES ('%d');";
        String query = String.format(
                query_normal,
                DbContract.Bookmarks.TABLE_NAME,
                id
        );
        Log.d("BOOKMARKS", query);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.insertQuery(query);
    }


}

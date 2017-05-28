package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.parsingExtra.Microlocation;
import org.fossasia.openevent.data.parsingExtra.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.text.ParseException;
import java.util.Locale;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Session {

    @SerializedName("short_abstract")
    private String summary;

    @SerializedName("long_abstract")
    private String description;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    private int id;

    @SerializedName("comments")
    private String level;

    private Microlocation microlocation;

    @SerializedName("title")
    private String title;

    private String subtitle;

    @SerializedName("track")
    private Track track;

    @SerializedName("slides")
    private String type;

    private String startDate;


    public Session(int id, String title, String subtitle,
                   String summary, String description,
                   String startTime, String endTime, String startDate, String type,
                   Track track, String level, Microlocation microlocation
    ) throws ParseException {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.type = type;
        this.track = track;
        this.level = level;
        this.microlocation = microlocation;
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


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Microlocation getMicrolocation() {
        return microlocation;
    }

    public void setMicrolocation(Microlocation microlocation) {
        this.microlocation = microlocation;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String generateSql() {
        String insertQueryFmt = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, %s, %s, %s, %s, '%d', %s, '%d');";
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
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(startDate)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(type)),
                track.getId(),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(level)),
                microlocation.getId());
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
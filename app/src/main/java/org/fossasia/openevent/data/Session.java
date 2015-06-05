package org.fossasia.openevent.data;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;

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
    int microlocations;

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

    public int getMicrolocations() {
        return microlocations;
    }

    public void setMicrolocations(int microlocations) {
        this.microlocations = microlocations;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int[] getSpeakers() {
        return speakers;
    }

    public void setSpeakers(int[] speakers) {
        this.speakers = speakers;
    }

    public String getTitle() {
        return escapeChar(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getSubtitle() {
        return escapeChar(subtitle);
    }


    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }


    public String getSummary() {
        return escapeChar(summary);
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public String getDescription() {
        return escapeChar(description);
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getStartTime() {
        return escapeChar(startTime);
    }


    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }



    public String getEndTime() {
        return escapeChar(endTime);
    }


    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getType() {
        return escapeChar(type);
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
        return escapeChar(level);
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', '%s', '%s','%s', '%s', '%s', '%s', '%s', '%d', '%s', '%d');";
        String query = String.format(query_normal, DbContract.Sessions.TABLE_NAME, id, title, subtitle, summary, description, startTime, endTime, type, track, level, microlocations);
        return query;
    }

    private String escapeChar(String string) {
        return string.replaceAll("'", "''");
    }


}

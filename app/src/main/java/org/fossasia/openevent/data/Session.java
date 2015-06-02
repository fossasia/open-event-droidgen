package org.fossasia.openevent.data;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.ISO8601Date;

import java.text.ParseException;
import java.util.Calendar;


/**
 * Created by championswimmer on 16/5/15.
 */
public class Session {

    int id;
    String title;
    String subtitle;
    String summary;
    String description;
    String startTime;
    String endTime;
    String type;
    int track;
    String speakers;
    String level;
    int microlocation;

    public Session(int id, String title, String subtitle,
                   String summary, String description,
                   String startTime, String endTime, String type,
                   int track, String speakers, String level,
                   int microlocation) throws ParseException {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.track = track;
        this.speakers = speakers;
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


    public Calendar getStartTimeAsCalendar() throws ParseException {
        return ISO8601Date.toCalendar(startTime);
    }


    public String getStartTime() {
        return (startTime);
    }


    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public Calendar getEndTimeAsCalendar() throws ParseException {
        return ISO8601Date.toCalendar(endTime);
    }


    public String getEndTime() {
        return (endTime);
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

    public String getSpeakers() {
        return speakers;
    }

    public void setSpeakers(String speakers) {
        this.speakers = speakers;
    }


    public int[] getSpeakersAsIntArray() {
        return new int[]{1, 2, 3};
        //TODO: Parse and deserialise properly
    }


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getMicrolocation() {
        return microlocation;
    }

    public void setMicrolocation(int microlocation) {
        this.microlocation = microlocation;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', '%s', '%s','%s', '%s', '%s', '%s', '%s', '%d', '%s', '%s', '%d');";
        String query = String.format(query_normal, DbContract.Sessions.TABLE_NAME, id, title, subtitle, summary, description, startTime, endTime, type, track, speakers, level, microlocation);
        return query;
    }


}

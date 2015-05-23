package org.fossasia.openevent.data;

import org.fossasia.openevent.utils.ISO8601Date;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public void setSpeakers(String speakers) {
        this.speakers = speakers;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setMicrolocation(int microlocation) {
        this.microlocation = microlocation;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public Calendar getStartTimeAsCalendar() throws ParseException {
        return ISO8601Date.toCalendar(startTime);
    }

    public String getStartTime() {
        return (startTime);
    }

    public Calendar getEndTimeAsCalendar() throws ParseException {
        return ISO8601Date.toCalendar(endTime);
    }

    public String getEndTime() {
        return (endTime);
    }

    public String getType() {
        return type;
    }

    public int getTrack() {
        return track;
    }

    public String getSpeakers() {
        return speakers;
    }

    public int[] getSpeakersAsIntArray() {
        return new int[]{1, 2, 3};
        //TODO: Parse and deserialise properly
    }


    public String getLevel() {
        return level;
    }

    public int getMicrolocation() {
        return microlocation;
    }

}

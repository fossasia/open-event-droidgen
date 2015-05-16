package org.fossasia.openevent.data;

import org.fossasia.openevent.utils.ISO8601Date;

import java.text.ParseException;
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
    Calendar startTime;
    Calendar endTime;
    String type;
    int track;
    int[] speakers;
    String level;
    int microlocation;

    public Session(int id, String title, String subtitle,
                   String summary, String description,
                   String startTime, String endTime, String type,
                   int track, int[] speakers, String level,
                   int microlocation) throws ParseException {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.description = description;
        this.startTime = ISO8601Date.toCalendar(startTime);
        this.endTime = ISO8601Date.toCalendar(endTime);
        this.type = type;
        this.track = track;
        this.speakers = speakers;
        this.level = level;
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

    public Calendar getStartTime() {
        return startTime;
    }

    public String getStartTimeAsString() {
        return ISO8601Date.fromCalendar(startTime);
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public String getEndTimeAsString() {
        return ISO8601Date.fromCalendar(endTime);
    }

    public String getType() {
        return type;
    }

    public int getTrack() {
        return track;
    }

    public int[] getSpeakers() {
        return speakers;
    }

    public String getLevel() {
        return level;
    }

    public int getMicrolocation() {
        return microlocation;
    }
}

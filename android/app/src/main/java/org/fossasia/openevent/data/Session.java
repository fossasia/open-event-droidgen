package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.fossasia.openevent.data.extras.SessionType;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Session extends RealmObject {

    /* Sort criteria */

    public static final String TITLE = "title";
    // Track is object in Realm. So sort by track.name
    public static final String TRACK = "track.name";
    public static final String START_TIME = "startTime";

    @PrimaryKey
    private int id;
    @Index
    private String title;
    private String subtitle;
    @JsonProperty("short_abstract")
    private String shortAbstract;
    @JsonProperty("long_abstract")
    private String longAbstract;
    private String comments;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    private String language;
    private String slides;
    private String video;
    private String audio;
    @JsonProperty("signup_url")
    private String signupUrl;
    private String state;
    private String level;
    @JsonProperty("session_type")
    private SessionType sessionType;
    private Track track;
    private Microlocation microlocation;
    private RealmList<Speaker> speakers;
    @Index
    private String startDate;
    private boolean isBookmarked;


    public Integer getId() {
        return id;
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

    public String getShortAbstract() {
        return shortAbstract;
    }

    public String getLongAbstract() {
        return longAbstract;
    }

    public String getComments() {
        return comments;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLanguage() {
        return language;
    }

    public String getSlides() {
        return slides;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public String getSignupUrl() {
        return signupUrl;
    }

    public String getState() {
        return state;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public String getLevel() {
        return level;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Microlocation getMicrolocation() {
        return microlocation;
    }

    public void setMicrolocation(Microlocation microlocation) {
        this.microlocation = microlocation;
    }

    public RealmList<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(RealmList<Speaker> speakers) {
        this.speakers = speakers;
    }

    @Override
    public String toString() {
        return getId() + " : " + getTitle();
    }
}
package org.fossasia.openevent.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    @Expose
    @PrimaryKey
    private int id;

    @SerializedName("session_type")
    @Expose
    private SessionType sessionType;

    @SerializedName("short_abstract")
    @Expose
    private String shortAbstract;

    @Expose
    private String subtitle;

    @Expose
    private String language;

    @Expose
    @Index
    private String title;

    @Expose
    private Track track;

    @SerializedName("start_time")
    @Expose
    private String startTime;

    @Expose
    private String level;

    @Expose
    private String comments;

    @Expose
    private String slides;

    @Expose
    private String state;

    @Expose
    private Microlocation microlocation;
    @SerializedName("end_time")
    @Expose
    private String endTime;

    @Expose
    private String video;

    @Expose
    private String audio;

    @SerializedName("signup_url")
    @Expose
    private String signupUrl;

    @SerializedName("long_abstract")
    @Expose
    private String longAbstract;

    private RealmList<Speaker> speakers;

    @Index
    private String startDate;

    private boolean isBookmarked;

    public SessionType getSessionType() {
        return sessionType;
    }

    public String getShortAbstract() {
        return shortAbstract;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getLanguage() {
        return language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getLevel() {
        return level;
    }

    public String getComments() {
        return comments;
    }

    public String getSlides() {
        return slides;
    }

    public String getState() {
        return state;
    }

    public Microlocation getMicrolocation() {
        return microlocation;
    }

    public void setMicrolocation(Microlocation microlocation) {
        this.microlocation = microlocation;
    }

    public String getEndTime() {
        return endTime;
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

    public Integer getId() {
        return id;
    }

    public String getLongAbstract() {
        return longAbstract;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public RealmList<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(RealmList<Speaker> speakers) {
        this.speakers = speakers;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    @Override
    public String toString() {
        return getId() + " : " + getTitle();
    }
}
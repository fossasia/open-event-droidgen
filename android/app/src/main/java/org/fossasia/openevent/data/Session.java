package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

@Type("session")
public class Session extends RealmObject {

    /* Sort criteria */

    public static final String TITLE = "title";
    // Track is object in Realm. So sort by track.name
    public static final String TRACK = "track.name";
    public static final String START_TIME = "startTime";

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    @Index
    private String title;
    private String subtitle;
    private String shortAbstract;
    private String longAbstract;
    private String comments;
    private String startTime;
    private String endTime;
    private String language;
    private String slides;
    private String video;
    private String audio;
    private String signupUrl;
    private String state;
    private String level;
    @Index
    private String startDate;
    private boolean isBookmarked;
    @JsonProperty("created-at")
    private String createdAt;
    @JsonProperty("deleted-at")
    private String deletedAt;
    @JsonProperty("submitted-at")
    private String submittedAt;
    @JsonProperty("is-mail-sent")
    private boolean isMailSent;
    @Relationship("session-type")
    private SessionType sessionType;
    @Relationship("track")
    private Track track;
    @Relationship("microlocation")
    private Microlocation microlocation;
    @Relationship("speakers")
    private RealmList<Speaker> speakers;

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setMailSent(boolean isMailSent) {
        this.isMailSent = isMailSent;
    }

    @JsonSetter("session_type")
    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    @JsonSetter("session-type")
    public void setSessionTypeForNewModel(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    @JsonSetter("starts-at")
    public void setStartTimeForNewModel(String startTime) {
        this.startTime = startTime;
    }

    @JsonSetter("start_time")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @JsonSetter("short-abstract")
    public void setShortAbstractForNewModel(String shortAbstract) {
        this.shortAbstract = shortAbstract;
    }

    @JsonSetter("ends-at")
    public void setEndTimeForNewModel(String endTime) {
        this.endTime = endTime;
    }

    @JsonSetter("end_time")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @JsonSetter("short_abstract")
    public void setShortAbstract(String shortAbstract) {
        this.shortAbstract = shortAbstract;
    }

    @JsonSetter("long-abstract")
    public void setLongAbstractForNewModel(String longAbstract) {
        this.longAbstract = longAbstract;
    }

    @JsonSetter("long_abstract")
    public void setLongAbstract(String longAbstract) {
        this.longAbstract = longAbstract;
    }

    @JsonSetter("slides-url")
    public void setSlidesForNewModel(String slides) {
        this.slides = slides;
    }

    @JsonSetter("slides")
    public void setSlides(String slides) {
        this.slides = slides;
    }

    @JsonSetter("videos-url")
    public void setVideoForNewModel(String video) {
        this.video = video;
    }

    @JsonSetter("video")
    public void setVideo(String video) {
        this.video = video;
    }

    @JsonSetter("audios-url")
    public void setAudioForNewModel(String audio) {
        this.audio = audio;
    }

    @JsonSetter("audio")
    public void setAudio(String audio) {
        this.audio = audio;
    }

    @JsonSetter("signup-url")
    public void setSignupUrlForNewModel(String signupUrl) {
        this.signupUrl = signupUrl;
    }

    @JsonSetter("signup_url")
    public void setSignupUrl(String signupUrl) {
        this.signupUrl = signupUrl;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public boolean isMailSent() {
        return isMailSent;
    }

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
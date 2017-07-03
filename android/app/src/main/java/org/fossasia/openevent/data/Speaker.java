package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

@Type("speaker")
public class Speaker extends RealmObject {

    public static final String SPEAKER = "speaker";
    public static final String NAME = "name";
    public static final String ORGANISATION = "organisation";
    public static final String COUNTRY = "country";

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    @Index
    private String name;
    @Index
    private String country;
    @Index
    private String organisation;
    private String photo;
    private String thumbnail;
    private String small;
    private String icon;
    private String twitter;
    private String linkedin;
    private String facebook;
    private String github;
    private String website;
    private Boolean featured;
    private String city;
    private String longBiography;
    private String shortBiography;
    private String speakingExperience;
    private String gender;
    private String position;
    @Relationship("sessions")
    private RealmList<Session> sessions;

    @JsonSetter("short_biography")
    public void setShortBiography(String shortBiography) {
        this.shortBiography = shortBiography;
    }

    @JsonSetter("short-biography")
    public void setShortBiographyForNewModel(String shortBiography) {
        this.shortBiography = shortBiography;
    }

    @JsonSetter("long_biography")
    public void setLongBiography(String longBiography) {
        this.longBiography = longBiography;
    }

    @JsonSetter("long-biography")
    public void setLongBiographyForNewModel(String longBiography) {
        this.longBiography = longBiography;
    }

    @JsonSetter("speaking_experience")
    public void setSpeakingExperience(String speakingExperience) {
        this.speakingExperience = speakingExperience;
    }

    @JsonSetter("speaking-experience")
    public void setSpeakingExperienceForNewModel(String speakingExperience) {
        this.speakingExperience = speakingExperience;
    }

    @JsonSetter("featured")
    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    @JsonSetter("is-featured")
    public void setFeaturedForNewModel(Boolean featured) {
        this.featured = featured;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getPhoto() {
        return photo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSmall() {
        return small;
    }

    public String getIcon() {
        return icon;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getGithub() {
        return github;
    }

    public String getWebsite() {
        return website;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public String getCity() {
        return city;
    }

    public String getLongBiography() {
        return longBiography;
    }

    public String getShortBiography() {
        return shortBiography;
    }

    public RealmList<Session> getSessions() {
        return sessions;
    }

    public String getSpeakingExperience() {
        return speakingExperience;
    }

    public String getGender() {
        return gender;
    }

    public String getPosition() {
        return position;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setSessions(RealmList<Session> sessions) {
        this.sessions = sessions;
    }

    public void setSession(RealmList<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public String toString() {
        return getName();
    }
}
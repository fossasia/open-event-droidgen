package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Speaker extends RealmObject {

    public static final String SPEAKER = "speaker";
    public static final String NAME = "name";
    public static final String ORGANISATION = "organisation";
    public static final String COUNTRY = "country";

    @PrimaryKey
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
    @JsonProperty("long_biography")
    private String longBiography;
    @JsonProperty("heard_from")
    private String heardFrom;
    @JsonProperty("short_biography")
    private String shortBiography;
    private RealmList<Session> sessions;
    @JsonProperty("sponsorship_required")
    private String sponsorshipRequired;
    @JsonProperty("speaking_experience")
    private String speakingExperience;
    private String gender;
    private String position;

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

    public String getHeardFrom() {
        return heardFrom;
    }

    public String getShortBiography() {
        return shortBiography;
    }

    public RealmList<Session> getSessions() {
        return sessions;
    }

    public String getSponsorshipRequired() {
        return sponsorshipRequired;
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
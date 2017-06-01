package org.fossasia.openevent.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Speaker extends RealmObject {

    public static final String SPEAKER = "speaker";
    public static final String NAME = "name";
    public static final String ORGANISATION = "organisation";
    public static final String COUNTRY = "country";

    @Expose
    @PrimaryKey
    private int id;

    @Expose
    @Index
    private String name;

    @Expose
    @Index
    private String country;

    @Expose
    @Index
    private String organisation;

    @Expose
    private String photo;

    @Expose
    private String thumbnail;

    @Expose
    private String small;

    @Expose
    private String icon;

    @Expose
    private String twitter;

    @Expose
    private String linkedin;

    @Expose
    private String facebook;

    @Expose
    private String github;

    @Expose
    private String website;

    @Expose
    private Boolean featured;

    @Expose
    private String city;

    @Expose
    @SerializedName("long_biography")
    private String longBiography;

    @Expose
    @SerializedName("heard_from")
    private String heardFrom;

    @Expose
    @SerializedName("short_biography")
    private String shortBiography;

    @Expose
    private RealmList<Session> sessions;

    @Expose
    @SerializedName("sponsorship_required")
    private String sponsorshipRequired;

    @Expose
    @SerializedName("speaking_experience")
    private String speakingExperience;

    @Expose
    private String gender;

    @Expose
    private String position;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public Integer getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getLongBiography() {
        return longBiography;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getHeardFrom() {
        return heardFrom;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getWebsite() {
        return website;
    }

    public String getShortBiography() {
        return shortBiography;
    }

    public RealmList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(RealmList<Session> sessions) {
        this.sessions = sessions;
    }

    public void setSession(RealmList<Session> sessions) {
        this.sessions = sessions;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getSponsorshipRequired() {
        return sponsorshipRequired;
    }

    public String getIcon() {
        return icon;
    }

    public String getGithub() {
        return github;
    }

    public String getName() {
        return name;
    }

    public String getSpeakingExperience() {
        return speakingExperience;
    }

    public String getCountry() {
        return country;
    }

    public String getGender() {
        return gender;
    }

    public String getSmall() {
        return small;
    }

    public String getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return getName();
    }
}
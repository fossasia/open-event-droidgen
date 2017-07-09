package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

@Type("speaker")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

    public void setShortBiography(String shortBiography) {
        this.shortBiography = shortBiography;
    }

    public void setShortBiographyForNewModel(String shortBiography) {
        this.shortBiography = shortBiography;
    }

    public void setLongBiography(String longBiography) {
        this.longBiography = longBiography;
    }

    public void setLongBiographyForNewModel(String longBiography) {
        this.longBiography = longBiography;
    }

    public void setSpeakingExperience(String speakingExperience) {
        this.speakingExperience = speakingExperience;
    }

    public void setSpeakingExperienceForNewModel(String speakingExperience) {
        this.speakingExperience = speakingExperience;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

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
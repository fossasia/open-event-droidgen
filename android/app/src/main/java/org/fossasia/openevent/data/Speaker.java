package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Speaker {

    public static final String SPEAKER = "speaker";

    private int id;

    private String name;

    private String photo;

    private String thumbnail;

    @SerializedName("short_biography")
    private String shortBiography;

    private String email;

    private String website;

    private String twitter;

    private String facebook;

    private String github;

    private String linkedin;

    private String organisation;

    private String position;

    @SerializedName("sessions")
    private ArrayList<Session> sessionArrayList;

    private String country;

    public Speaker(int id, String name, String photo, String thumbnail,
                   String shortBiography, String email, String website,
                   String twitter, String facebook, String github,
                   String linkedin, String organisation, String position,
                   ArrayList<Session> sessionArrayList, String country) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.thumbnail = thumbnail;
        this.shortBiography = shortBiography;
        this.email = email;
        this.website = website;
        this.twitter = twitter;
        this.facebook = facebook;
        this.github = github;
        this.linkedin = linkedin;
        this.organisation = organisation;
        this.position = position;
        this.sessionArrayList = sessionArrayList;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Session> getSession() {
        return sessionArrayList;
    }

    public void setSession(ArrayList<Session> session) {
        this.sessionArrayList = session;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getShortBiography() {
        return shortBiography;
    }

    public void setShortBiography(String shortBiography) {
        this.shortBiography = shortBiography;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
        return String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Speakers.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(name)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(photo)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(thumbnail)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(shortBiography)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(email)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(website)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(facebook)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(twitter)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(github)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(linkedin)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(organisation)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(position)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(country)));
    }

    @Override
    public String toString() {
        return getName();
    }
}

package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Speaker {

    public static final String SPEAKERNAME = "speakerName";
    public static final String SPEAKEREMAIL = "speakerEmail";

    int id;

    String name;

    String photo;

    @SerializedName("biography")
    String bio;

    String email;

    String web;

    String twitter;

    String facebook;

    String github;

    String linkedin;

    String organisation;

    String position;

    @SerializedName("sessions")
    int[] session;

    String country;

    public Speaker(int id, String name, String photo,
                   String bio, String email, String web,
                   String twitter, String facebook, String github,
                   String linkedin, String organisation,
                   String position, int[] session, String country) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.bio = bio;
        this.email = email;
        this.web = web;
        this.twitter = twitter;
        this.facebook = facebook;
        this.github = github;
        this.linkedin = linkedin;
        this.organisation = organisation;
        this.position = position;
        this.session = session;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getSession() {
        return session;
    }

    public void setSession(int[] session) {
        this.session = session;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
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
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
        String query = String.format(
                query_normal,
                DbContract.Speakers.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(name + ""),
                DatabaseUtils.sqlEscapeString(photo),
                DatabaseUtils.sqlEscapeString(bio + ""),
                DatabaseUtils.sqlEscapeString(email + ""),
                DatabaseUtils.sqlEscapeString(web + ""),
                DatabaseUtils.sqlEscapeString(facebook + ""),
                DatabaseUtils.sqlEscapeString(twitter + ""),
                DatabaseUtils.sqlEscapeString(github + ""),
                DatabaseUtils.sqlEscapeString(linkedin + ""),
                DatabaseUtils.sqlEscapeString(organisation + ""),
                DatabaseUtils.sqlEscapeString(position + ""),
                DatabaseUtils.sqlEscapeString(country + ""));
        return query;
    }
}

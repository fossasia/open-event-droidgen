package org.fossasia.openevent.data;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Speaker {

    int id;
    String name;
    String photo;
    String bio;
    String email;
    String web;
    String twitter;
    String facebook;
    String github;
    String linkedin;
    String organisation;
    String position;
    String country;


    public Speaker(int id, String name, String photo,
                   String bio, String email, String web,
                   String twitter, String facebook, String github,
                   String linkedin, String organisation,
                   String position, String country) {
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
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        String query_normal = "INSERT INTO %s VALUES ('%d', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
        String query = String.format(query_normal, DbContract.Speakers.TABLE_NAME, id, name, photo, bio, email, web, facebook, twitter, github, linkedin, organisation, position, country);
        return query;
    }
}

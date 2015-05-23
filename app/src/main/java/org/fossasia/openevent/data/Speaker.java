package org.fossasia.openevent.data;

import java.util.ArrayList;

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
    String sessions; // In the format [1, 4, 2]

    public Speaker(int id, String name, String photo,
                   String bio, String email, String web,
                   String twitter, String facebook, String github,
                   String linkedin, String organisation,
                   String position, String country, String sessions) {
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
        this.sessions = sessions;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setSessions(String sessions) {
        this.sessions = sessions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getBio() {
        return bio;
    }

    public String getEmail() {
        return email;
    }

    public String getWeb() {
        return web;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getGithub() {
        return github;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getPosition() {
        return position;
    }

    public String getCountry() {
        return country;
    }

    public String getSessions() {
        return sessions;
    }

    public int[] getSessionsAsIntArray() {
        return new int[]{1,2,3};
        //TODO: Parse and return int array
    }

}

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
    int[] sessions;

    public Speaker(int id, String name, String photo,
                   String bio, String email, String web,
                   String twitter, String facebook, String github,
                   String linkedin, String organisation,
                   String position, String country, int[] sessions) {
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

    public int[] getSessions() {
        return sessions;
    }

    public static ArrayList<Speaker> getSpeakerList () {
        ArrayList<Speaker> speakers = new ArrayList<>();
        //TODO: Get data from the database
        return speakers;
    }

    public static Speaker getSpeakerById (int id) {
        return null; //TODO: Write real code here
    }
}

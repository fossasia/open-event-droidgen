package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Speaker {

    public static final String SPEAKER = "speaker";

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("github")
    @Expose
    private String github;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("linkedin")
    @Expose
    private String linkedin;
    @SerializedName("long_biography")
    @Expose
    private String long_biography;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("organisation")
    @Expose
    private String organisation;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("sessions")
    @Expose
    private List<Session> sessions = new ArrayList<Session>();
    @SerializedName("short_biography")
    @Expose
    private String short_biography;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("website")
    @Expose
    private String website;

    /**
     * No args constructor for use in serialization
     */
    public Speaker() {
    }

    /**
     * @param country
     * @param email
     * @param facebook
     * @param github
     * @param id
     * @param linkedin
     * @param long_biography
     * @param name
     * @param organisation
     * @param photo
     * @param position
     * @param twitter
     * @param website
     */
    public Speaker(Integer id, String name, String photo, String long_biography, String email, String website, String twitter, String facebook, String github, String linkedin, String organisation,  String position, List<Session> sessions, String country) {
        this.country = country;
        this.email = email;
        this.facebook = facebook;
        this.github = github;
        this.id = id;
        this.linkedin = linkedin;
        this.long_biography = long_biography;
        this.name = name;
        this.organisation = organisation;
        this.photo = photo;
        this.position = position;
        this.sessions = sessions;
        this.twitter = twitter;
        this.website = website;
    }

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The facebook
     */
    public String getFacebook() {
        return facebook;
    }

    /**
     * @param facebook The facebook
     */
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    /**
     * @return The github
     */
    public String getGithub() {
        return github;
    }

    /**
     * @param github The github
     */
    public void setGithub(String github) {
        this.github = github;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The linkedin
     */
    public String getLinkedin() {
        return linkedin;
    }

    /**
     * @param linkedin The linkedin
     */
    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    /**
     * @return The long_biography
     */
    public String getLongBiography() {
        return long_biography;
    }

    /**
     * @param long_biography The long_biography
     */
    public void setLongBiography(String long_biography) {
        this.long_biography = long_biography;
    }

    /**
     * @return The mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile The mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The organisation
     */
    public String getOrganisation() {
        return organisation;
    }

    /**
     * @param organisation The organisation
     */
    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    /**
     * @return The photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * @param photo The photo
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * @return The position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position The position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return The sessions
     */
    public List<Session> getSessions() {
        return sessions;
    }

    /**
     * @param sessions The sessions
     */
    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    /**
     * @return The short_biography
     */
    public String getShortBiography() {
        return short_biography;
    }

    /**
     * @param short_biography The short_biography
     */
    public void setShortBiography(String short_biography) {
        this.short_biography = short_biography;
    }

    /**
     * @return The twitter
     */
    public String getTwitter() {
        return twitter;
    }

    /**
     * @param twitter The twitter
     */
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    /**
     * @return The website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website The website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
        return String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Speakers.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(name)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(photo)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(long_biography)),
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
}
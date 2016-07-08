package org.fossasia.openevent.data;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;

import java.util.Locale;

/**
 * User: MananWason
 * Date: 05-06-2015
 */
public class Version {
    @SerializedName("event_id")
    int eventId;

    @SerializedName("event_ver")
    int eventVer;

    int id;

    @SerializedName("microlcations_ver")
    int microlocationsVer;

    @SerializedName("session_ver")
    int sessionVer;

    @SerializedName("sponsors_ver")
    int sponsorVer;

    @SerializedName("speakers_ver")
    int speakerVer;

    @SerializedName("tracks_ver")
    int tracksVer;

    public Version(int id, int eventVer, int tracksVer, int sessionVer, int sponsorVer, int speakerVer, int microlocationsVer) {
        this.id = id;
        this.eventVer = eventVer;
        this.tracksVer = tracksVer;
        this.sessionVer = sessionVer;
        this.sponsorVer = sponsorVer;
        this.speakerVer = speakerVer;
        this.microlocationsVer = microlocationsVer;
    }

    public int getEventVer() {
        return eventVer;
    }

    public void setEventVer(int eventVer) {
        this.eventVer = eventVer;
    }

    public int getSpeakerVer() {
        return speakerVer;
    }

    public void setSpeakerVer(int speakerVer) {
        this.speakerVer = speakerVer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMicrolocationsVer() {
        return microlocationsVer;
    }

    public void setMicrolocationsVer(int microlocationsVer) {
        this.microlocationsVer = microlocationsVer;
    }

    public int getSessionVer() {
        return sessionVer;
    }

    public void setSessionVer(int sessionVer) {
        this.sessionVer = sessionVer;
    }

    public int getSponsorVer() {
        return sponsorVer;
    }

    public void setSponsorVer(int sponsorVer) {
        this.sponsorVer = sponsorVer;
    }

    public int getTracksVer() {
        return tracksVer;
    }

    public void setTracksVer(int tracksVer) {
        this.tracksVer = tracksVer;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', '%d', '%d', '%d', '%d', '%d', '%d');";
        return String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Versions.TABLE_NAME,
                id, eventVer, tracksVer, sessionVer, sponsorVer, speakerVer, microlocationsVer);
    }
}

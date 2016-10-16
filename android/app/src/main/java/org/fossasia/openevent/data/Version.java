package org.fossasia.openevent.data;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;

import java.util.Locale;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 05-06-2015
 */
public class Version {

    @SerializedName("event_ver")
    private int eventVer;

    @SerializedName("microlcations_ver")
    private int microlocationsVer;

    @SerializedName("session_ver")
    private int sessionVer;

    @SerializedName("sponsors_ver")
    private int sponsorVer;

    @SerializedName("speakers_ver")
    private int speakerVer;

    @SerializedName("tracks_ver")
    private int tracksVer;

    public Version( int eventVer, int tracksVer, int sessionVer, int sponsorVer, int speakerVer, int microlocationsVer) {
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
        String query_normal = "INSERT INTO %s VALUES ('%d', '%d', '%d', '%d', '%d', '%d');";
        Timber.d(query_normal);
        String query = String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Versions.TABLE_NAME,
                eventVer, tracksVer, sessionVer, sponsorVer, speakerVer, microlocationsVer);
        Timber.d(query);
        return query;

    }
}

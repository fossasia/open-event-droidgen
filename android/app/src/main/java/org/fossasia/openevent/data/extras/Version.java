package org.fossasia.openevent.data.extras;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Version extends RealmObject {

    @SerializedName("tracks_ver")
    @Expose
    @PrimaryKey
    private int tracksVer;

    @SerializedName("event_ver")
    @Expose
    private int eventVer;

    @SerializedName("speakers_ver")
    @Expose
    private int speakersVer;

    @SerializedName("sessions_ver")
    @Expose
    private int sessionsVer;

    @SerializedName("microlocations_ver")
    @Expose
    private int microlocationsVer;

    @SerializedName("sponsors_ver")
    @Expose
    private int sponsorsVer;

    public int getTracksVer() {
        return tracksVer;
    }

    public int getEventVer() {
        return eventVer;
    }

    public int getSpeakersVer() {
        return speakersVer;
    }

    public int getSessionsVer() {
        return sessionsVer;
    }

    public int getMicrolocationsVer() {
        return microlocationsVer;
    }

    public int getSponsorsVer() {
        return sponsorsVer;
    }
}

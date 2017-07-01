package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Version extends RealmObject {

    @PrimaryKey
    @JsonProperty("tracks_ver")
    private int tracksVer;
    @JsonProperty("event_ver")
    private int eventVer;
    @JsonProperty("speakers_ver")
    private int speakersVer;
    @JsonProperty("sessions_ver")
    private int sessionsVer;
    @JsonProperty("microlocations_ver")
    private int microlocationsVer;
    @JsonProperty("sponsors_ver")
    private int sponsorsVer;
    @JsonProperty("session_types")
    private int sessionTypesVer;

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

    public int getSessionTypesVer() {
        return sessionTypesVer;
    }
}

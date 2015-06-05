package org.fossasia.openevent.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MananWason on 05-06-2015.
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
    @SerializedName("sponsor_ver")
    int sponsorVer;
    @SerializedName("tracks_ver")
    int tracksVer;
}

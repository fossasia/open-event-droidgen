package org.fossasia.openevent.api.protocol;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.Track;

import java.util.List;

/**
 * Created by MananWason on 26-05-2015.
 */
public class TrackResponseList {
    @SerializedName("tracks")
    public List<Track> tracks;
}

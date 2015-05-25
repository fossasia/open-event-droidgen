package org.fossasia.openevent.api.protocol;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.Speaker;

import java.util.List;

/**
 * User: mohit
 * Date: 25/5/15
 */
public class SpeakerResponseList {
    @SerializedName("speakers")
    public List<Speaker> speakers;
}

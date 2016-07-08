package org.fossasia.openevent.api.protocol;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.Session;

import java.util.List;

/**
 * Created by MananWason on 27-05-2015.
 */
public class SessionResponseList {
    @SerializedName("sessions")
    public List<Session> sessions;
}

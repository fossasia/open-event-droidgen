package org.fossasia.openevent.api.protocol;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.EventDates;

import java.util.List;

/**
 * Created by Manan Wason on 18/06/16.
 */
public class EventDatesResponseList {
    @SerializedName("dates")
    public List<EventDates> event;

}

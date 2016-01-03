package org.fossasia.openevent.api.protocol;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.Sponsor;

import java.util.List;

/**
 * Created by MananWason on 26-05-2015.
 */
public class SponsorResponseList {
    @SerializedName("sponsors")
    public List<Sponsor> sponsors;
}

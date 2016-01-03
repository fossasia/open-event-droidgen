package org.fossasia.openevent.api.protocol;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.Version;

import java.util.List;

/**
 * Created by MananWason on 12-06-2015.
 */
public class VersionResponseList {
    @SerializedName("version")
    public List<Version> versions;
}

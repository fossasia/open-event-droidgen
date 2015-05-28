package org.fossasia.openevent.api.protocol;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbContract;

import java.util.List;

/**
 * Created by MananWason on 26-05-2015.
 */
public class MicrolocationResponseList {
    @SerializedName("microlocations")
    public List<Microlocation> microlocations;
}

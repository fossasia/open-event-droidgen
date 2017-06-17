package org.fossasia.openevent.data.extras;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallForPapers extends RealmObject {

    @Expose
    @PrimaryKey
    private String announcement;

    @Expose
    private String privacy;

    @SerializedName("start_date")
    @Expose
    private String startDate;

    @SerializedName("end_date")
    @Expose
    private String endDate;

    @Expose
    private String timezone;

    public String getAnnouncement() {
        return announcement;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getTimezone() {
        return timezone;
    }

}

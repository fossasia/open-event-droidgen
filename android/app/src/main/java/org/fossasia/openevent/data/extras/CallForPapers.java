package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CallForPapers extends RealmObject {

    @PrimaryKey
    private String announcement;
    private String privacy;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
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

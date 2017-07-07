package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Type("speakers-call")
public class CallForPapers extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String announcement;
    private String privacy;
    @JsonProperty("starts-at")
    private String startDate;
    @JsonProperty("ends-at")
    private String endDate;
    private String timezone;

    public Integer getId() {
        return id;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTimezone() {
        return timezone;
    }
}

package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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
    private String startDate;
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

    @JsonSetter("start_date")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @JsonSetter("starts-at")
    public void setStartDateForNewModel(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @JsonSetter("end_date")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @JsonSetter("ends-at")
    public void setEndDateForNewModel(String endDate) {
        this.endDate = endDate;
    }

    public String getTimezone() {
        return timezone;
    }
}

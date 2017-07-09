package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Type("speakers-call")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class SpeakersCall extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String announcement;
    private String privacy;
    private String startsAt;
    private String endsAt;
    private String hash;

    public Integer getId() {
        return id;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(String endsAt) {
        this.endsAt = endsAt;
    }

    public String getHash() {
        return hash;
    }
}

package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sponsor extends RealmObject {

    @PrimaryKey
    private Integer id;
    private String name;
    private String description;
    private String level;
    private String url;
    @JsonProperty("sponsor_type")
    private String sponsorType;
    private String logo;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLevel() {
        return level;
    }

    public String getUrl() {
        return url;
    }

    public String getSponsorType() {
        return sponsorType;
    }

    public String getLogo() {
        return logo;
    }
}
package org.fossasia.openevent.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Type("sponsor")
public class Sponsor extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String name;
    private String description;
    private String level;
    private String url;
    @JsonProperty("type")
    private String sponsorType;
    @JsonProperty("logo-url")
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

    public void setSponserType(String sponserType) {
        this.sponsorType = sponserType;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSponsorType() {
        return sponsorType;
    }

    public String getLogo() {
        return logo;
    }
}
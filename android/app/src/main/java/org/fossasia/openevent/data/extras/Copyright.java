package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Type("event-copyright")
public class Copyright extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String licenceUrl;
    private String holderUrl;
    private String licence;
    private int year;
    private String logo;
    private String holder;

    public Integer getId() {
        return id;
    }

    public String getLicenceUrl() {
        return licenceUrl;
    }

    @JsonSetter("licence_url")
    public void setLicenceUrl(String licenceUrl) {
        this.licenceUrl = licenceUrl;
    }

    @JsonSetter("licence-url")
    public void setLicenceUrlForNewModel(String licenceUrl) {
        this.licenceUrl = licenceUrl;
    }

    public String getHolderUrl() {
        return holderUrl;
    }

    @JsonSetter("holder_url")
    public void setHolderUrl(String holderUrl) {
        this.holderUrl = holderUrl;
    }

    @JsonSetter("holder-url")
    public void setHolderUrlForNewModel(String holderUrl) {
        this.holderUrl = holderUrl;
    }

    public String getLicence() {
        return licence;
    }

    public Integer getYear() {
        return year;
    }

    public String getLogo() {
        return logo;
    }

    public String getHolder() {
        return holder;
    }
}

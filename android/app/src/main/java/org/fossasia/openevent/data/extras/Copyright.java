package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.IntegerIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Type("event-copyright")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class Copyright extends RealmObject {

    @PrimaryKey
    @Id(IntegerIdHandler.class)
    private int id;
    private String licenceUrl;
    private String holderUrl;
    private String licence;
    private int year;
    private String logoUrl;
    private String holder;

    public Integer getId() {
        return id;
    }

    public String getLicenceUrl() {
        return licenceUrl;
    }

    public String getHolderUrl() {
        return holderUrl;
    }

    public String getLicence() {
        return licence;
    }

    public Integer getYear() {
        return year;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getHolder() {
        return holder;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLicenceUrl(String licenceUrl) {
        this.licenceUrl = licenceUrl;
    }

    public void setHolderUrl(String holderUrl) {
        this.holderUrl = holderUrl;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }
}

package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Copyright extends RealmObject {

    @JsonProperty("licence_url")
    private String licenceUrl;
    @JsonProperty("holder_url")
    private String holderUrl;
    @PrimaryKey
    private String licence;
    private int year;
    private String logo;
    private String holder;

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

    public String getLogo() {
        return logo;
    }

    public String getHolder() {
        return holder;
    }
}

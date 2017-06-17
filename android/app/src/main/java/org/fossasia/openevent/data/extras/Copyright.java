package org.fossasia.openevent.data.extras;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Copyright extends RealmObject {

    @SerializedName("licence_url")
    @Expose
    private String licenceUrl;

    @SerializedName("holder_url")
    @Expose
    private String holderUrl;

    @Expose
    @PrimaryKey
    private String licence;

    @Expose
    private int year;

    @Expose
    private String logo;

    @Expose
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

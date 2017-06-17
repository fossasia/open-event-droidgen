package org.fossasia.openevent.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sponsor extends RealmObject {

    @Expose
    @PrimaryKey
    private Integer id;

    @Expose
    private String description;

    @Expose
    private String level;

    @Expose
    private String url;

    @SerializedName("sponsor_type")
    @Expose
    private String sponsorType;

    @Expose
    private String logo;

    @Expose
    private String name;

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


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
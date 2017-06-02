package org.fossasia.openevent.data.extras;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LicenseDetails extends RealmObject {

    @Expose
    @PrimaryKey
    private String name;

    @SerializedName("compact_logo")
    @Expose
    private String compactLogo;

    @Expose
    private String url;

    @SerializedName("long_name")
    @Expose
    private String longName;

    @Expose
    private String logo;

    @Expose
    private String description;

    public String getName() {
        return name;
    }

    public String getCompactLogo() {
        return compactLogo;
    }

    public String getUrl() {
        return url;
    }

    public String getLongName() {
        return longName;
    }

    public String getLogo() {
        return logo;
    }

    public String getDescription() {
        return description;
    }

}

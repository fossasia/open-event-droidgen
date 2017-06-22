package org.fossasia.openevent.data.extras;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LicenseDetails extends RealmObject {

    @PrimaryKey
    private String name;
    @JsonProperty("compact_logo")
    private String compactLogo;
    private String url;
    @JsonProperty("long_name")
    private String longName;
    private String logo;
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

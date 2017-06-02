package org.fossasia.openevent.data.extras;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SocialLink extends RealmObject {

    @SerializedName("link")
    private String link;
    @PrimaryKey
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

    public SocialLink() {}

    /**
     * @param id
     * @param name
     * @param link
     */
    public SocialLink(String link, String id, String name) {
        super();
        this.link = link;
        this.id = id;
        this.name = name;
    }

    @SerializedName("link")
    public String getLink() {
        return link;
    }

    @SerializedName("link")
    public void setLink(String link) {
        this.link = link;
    }

    @SerializedName("id")
    public String getId() {
        return id;
    }

    @SerializedName("id")
    public void setId(String id) {
        this.id = id;
    }

    @SerializedName("name")
    public String getName() {
        return name;
    }

    @SerializedName("name")
    public void setName(String name) {
        this.name = name;
    }

}

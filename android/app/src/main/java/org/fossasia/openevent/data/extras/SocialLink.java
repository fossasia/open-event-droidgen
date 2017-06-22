package org.fossasia.openevent.data.extras;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SocialLink extends RealmObject {

    @PrimaryKey
    private String id;
    private String name;
    private String link;

    public SocialLink() {
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

package org.fossasia.openevent.data;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;

import java.util.Locale;

import timber.log.Timber;

/**
 * Created by harshita30 on 13/3/17.
 */

public class SocialLink {

    @SerializedName("link")
    private String link;
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;

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

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%s', '%s', '%s');";
        Timber.d(query_normal);
        String query = String.format(Locale.ENGLISH,
                query_normal,
                DbContract.SocialLink.TABLE_NAME,
                link,id,name);
        Timber.d(query);
        return query;

    }
}

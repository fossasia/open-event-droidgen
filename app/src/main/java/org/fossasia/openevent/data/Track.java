package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Track {

    int id;
    String name;
    String description;
    @SerializedName("track_image_url")
    String image;

    public Track(int id, String name, String description, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, %s , %s);";
        String query = String.format(
                query_normal,
                DbContract.Tracks.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(name + ""),
                DatabaseUtils.sqlEscapeString(description + ""),
                DatabaseUtils.sqlEscapeString(image + ""));
        return query;

    }


}

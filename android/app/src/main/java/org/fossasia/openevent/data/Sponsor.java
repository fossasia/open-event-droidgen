package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.util.Locale;

/**
 * User: championswimmer
 * Date: 16/5/15
 */
public class Sponsor{

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;

    /**
     * No args constructor for use in serialization
     *
     */
    public Sponsor() {
    }

    /**
     *
     * @param level
     * @param description
     * @param name
     * @param link
     * @param image
     * @param type
     */
    public Sponsor(String name, String link, String image, String description, String type, String level) {
        this.description = description;
        this.level = level;
        this.image = image;
        this.link = link;
        this.type = type;
        this.name = name;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The level
     */
    public String getLevel() {
        return level;
    }

    /**
     *
     * @param level
     * The level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     *
     * @return
     * The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     *
     * @return
     * The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     * The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }


    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES (%s, %s, %s, %s, %s, %s);";
        return String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Sponsors.TABLE_NAME,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(name)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(link)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(image)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(description)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(type)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(level)));
    }
}

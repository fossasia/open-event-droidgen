package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.util.Locale;

/**
 * User: championswimmer
 * Date: 16/5/15
 */
public class Sponsor {

    private int id;

    private String name;

    private String url;

    private String logo;

    @SerializedName("sponsor_type")
    private String type;

    @SerializedName("level")
    private int level;

    public Sponsor(int id, String name, String url, String logo, String type, int level) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.logo = logo;
        this.type = type;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLogo() {
        return logo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void changeSponsorTypeToInt(String type) {
        switch (type) {
            case "Platinum":
                this.type = OpenEventApp.getAppContext().getString(R.string.platinumInt);
                break;
            case "Diamond":
                this.type = OpenEventApp.getAppContext().getString(R.string.diamondInt);
                break;
            case "Gold":
                this.type = OpenEventApp.getAppContext().getString(R.string.goldInt);
                break;
            case "Silver":
                this.type = OpenEventApp.getAppContext().getString(R.string.silverInt);
                break;
            case "Bronze":
                this.type = OpenEventApp.getAppContext().getString(R.string.bronzeInt);
                break;
            default:
                //Do nothing
        }
    }

    public void changeSponsorTypeToString(String intType) {
        switch (intType) {
            case "1":
                this.type = OpenEventApp.getAppContext().getString(R.string.platinum);
                break;
            case "2":
                this.type = OpenEventApp.getAppContext().getString(R.string.diamond);
                break;
            case "3":
                this.type = OpenEventApp.getAppContext().getString(R.string.gold);
                break;
            case "4":
                this.type = OpenEventApp.getAppContext().getString(R.string.silver);
                break;
            case "5":
                this.type = OpenEventApp.getAppContext().getString(R.string.bronze);
                break;
            default:
        }
    }


    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s, '%d');";
        return String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Sponsors.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(name)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(url)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(logo)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(type)),
                level);
    }
}
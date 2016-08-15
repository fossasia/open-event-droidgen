package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

import com.google.gson.annotations.SerializedName;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.utils.StringUtils;

import java.util.Locale;

/**
 * User: championswimmer
 * Date: 16/5/15
 */
public class Sponsor {

    int id;

    String name;

    String url;

    String logo;

    @SerializedName("sponsor_type")
    String sponsorType;

    public Sponsor(int id, String name, String url, String logo, String sponsorType) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.logo = logo;
        this.sponsorType = sponsorType;
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

    public String getSponsorType() {
        return sponsorType;
    }

    public void setSponsorType() {
        switch (getSponsorType()){
            case "Platinum":
                this.sponsorType = "1";
                break;
            case "Diamond":
                this.sponsorType = "2";
                break;
            case "Gold":
                this.sponsorType = "3";
                break;
            case "Silver":
                this.sponsorType = "4";
                break;
            case "Bronze":
                this.sponsorType = "5";
                break;
        }
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, %s, %s, %s);";
        return String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Sponsors.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(name)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(url)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(logo)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(sponsorType)));
    }
}
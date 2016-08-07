package org.fossasia.openevent.data;

import android.database.DatabaseUtils;

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

    String preUrl = "https://raw.githubusercontent.com/fossasia/open-event/master/sample/FOSSASIA16";

    public Sponsor(int id, String name, String url, String logo) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.logo = preUrl + logo;
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


    public String generateSql() {
        String query_normal = "INSERT INTO %s VALUES ('%d', %s, %s, %s);";
        return String.format(Locale.ENGLISH,
                query_normal,
                DbContract.Sponsors.TABLE_NAME,
                id,
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(name)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(url)),
                DatabaseUtils.sqlEscapeString(StringUtils.optionalString(logo)));
    }
}
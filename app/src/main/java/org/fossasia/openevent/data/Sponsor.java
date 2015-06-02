package org.fossasia.openevent.data;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * Created by championswimmer on 16/5/15.
 */
public class Sponsor {

    int id;
    String name;
    String url;
    String logo;

    public Sponsor(int id, String name, String url, String logo) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.logo = logo;
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
        String query_normal = "INSERT INTO %s VALUES ('%d', '%s', '%s', '%s');";
        String query = String.format(query_normal, DbContract.Sponsors.TABLE_NAME, id, name, url, logo);
        return query;
    }
}
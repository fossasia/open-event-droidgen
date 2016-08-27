package org.fossasia.openevent.data;

import org.fossasia.openevent.dbutils.DbContract;

import java.util.Locale;

/**
 * Created by Manan Wason on 27/08/16.
 */
public class ServerSessionIdMappings {
    int serverId;

    int sortedId;

    public ServerSessionIdMappings(int serverId, int sortedId) {
        this.serverId = serverId;
        this.sortedId = sortedId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getSortedId() {
        return sortedId;
    }

    public void setSortedId(int sortedId) {
        this.sortedId = sortedId;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s(serverid ,localid) VALUES ('%d', '%d');";
        return String.format(Locale.ENGLISH, query_normal, DbContract.ServerSessionIdMapping.TABLE_NAME, serverId, sortedId);
    }

}

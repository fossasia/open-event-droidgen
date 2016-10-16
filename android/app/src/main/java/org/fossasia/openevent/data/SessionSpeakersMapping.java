package org.fossasia.openevent.data;

import org.fossasia.openevent.dbutils.DbContract;

import java.util.Locale;

/**
 * User: MananWason
 * Date: 04-06-2015
 */
public class SessionSpeakersMapping {

    private int sessionId;

    private int speakerId;

    public SessionSpeakersMapping(int sessionId, int speakerId) {
        this.sessionId = sessionId;
        this.speakerId = speakerId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(int speakerId) {
        this.speakerId = speakerId;
    }

    public String generateSql() {
        String query_normal = "INSERT INTO %s(sessionid ,speakerid) VALUES ('%d', '%d');";
        return String.format(Locale.ENGLISH, query_normal, DbContract.Sessionsspeakers.TABLE_NAME, sessionId, speakerId);
    }
}

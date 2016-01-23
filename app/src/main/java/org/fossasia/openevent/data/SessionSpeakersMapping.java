package org.fossasia.openevent.data;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * Created by MananWason on 04-06-2015.
 */
public class SessionSpeakersMapping {
    private static final String TAG = "SessionSpeaker";
    int sessionId;
    int speakerId;

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
        String query = String.format(query_normal, DbContract.Sessionsspeakers.TABLE_NAME, sessionId, speakerId);
        return query;
    }

}

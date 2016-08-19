package org.fossasia.openevent.events;

/**
 * Created by Manan Wason on 25/06/16.
 */
public class RetrofitResponseEvent {

    private int statusCode;

    public RetrofitResponseEvent(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

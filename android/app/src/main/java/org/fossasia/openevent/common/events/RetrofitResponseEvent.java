package org.fossasia.openevent.common.events;

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

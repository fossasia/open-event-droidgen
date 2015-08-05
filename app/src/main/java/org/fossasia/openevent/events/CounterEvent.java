package org.fossasia.openevent.events;

/**
 * Created by MananWason on 8/5/2015.
 */
public class CounterEvent {
    int requestsCount;

    public CounterEvent(int count) {
        this.requestsCount = count;
    }

    public int getRequestsCount() {
        return requestsCount;
    }

    public void setRequestsCount(int requestsCount) {
        this.requestsCount = requestsCount;
    }
}

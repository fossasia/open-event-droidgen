package org.fossasia.openevent.common.events;

public class CounterEvent {
    private int requestsCount;

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

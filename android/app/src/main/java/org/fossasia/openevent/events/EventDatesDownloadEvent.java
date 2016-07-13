package org.fossasia.openevent.events;

/**
 * Created by Manan Wason on 15/07/16.
 */
public class EventDatesDownloadEvent {
    private boolean state;

    public EventDatesDownloadEvent(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

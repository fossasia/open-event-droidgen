package org.fossasia.openevent.events;

/**
 * Created by MananWason on 8/5/2015.
 */
public class EventDownloadEvent {
    boolean state;

    public EventDownloadEvent(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

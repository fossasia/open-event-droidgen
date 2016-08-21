package org.fossasia.openevent.events;

/**
 * Created by Saurabh on 21-08-2016.
 * Base event for all download events.
 */

public class DownloadEvent {
    private boolean state;

    DownloadEvent(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

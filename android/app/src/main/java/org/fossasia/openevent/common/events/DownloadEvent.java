package org.fossasia.openevent.common.events;

/**
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

package org.fossasia.openevent.events;

/**
 * Created by MananWason on 8/4/2015.
 */
public class TracksDownloadEvent {
    boolean state;

    public TracksDownloadEvent(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

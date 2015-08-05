package org.fossasia.openevent.events;

/**
 * Created by MananWason on 8/5/2015.
 */
public class VersionDownloadEvent {
    boolean state;

    public VersionDownloadEvent(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

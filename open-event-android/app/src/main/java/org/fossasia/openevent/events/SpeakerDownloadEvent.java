package org.fossasia.openevent.events;

/**
 * Created by MananWason on 8/4/2015.
 */
public class SpeakerDownloadEvent {
    boolean state;

    public SpeakerDownloadEvent(boolean state) {
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

package org.fossasia.openevent.events;

/**
 * User: MananWason
 * Date: 8/4/2015
 */
public class SpeakerDownloadEvent {
    private boolean state;

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

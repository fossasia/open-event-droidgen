package org.fossasia.openevent.events;

/**
 * User: MananWason
 * Date: 8/4/2015
 */
public class SessionDownloadEvent {
    boolean state;

    public SessionDownloadEvent(boolean state) {

        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

package org.fossasia.openevent.events;

/**
 * Created by MananWason on 8/4/2015.
 */
public class MicrolocationDownloadEvent {
    boolean state;

    public MicrolocationDownloadEvent(boolean state) {

        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

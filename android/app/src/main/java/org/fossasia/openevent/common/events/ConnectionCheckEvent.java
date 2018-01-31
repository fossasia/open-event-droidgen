package org.fossasia.openevent.common.events;

public class ConnectionCheckEvent {

    public boolean isConnected;

    public ConnectionCheckEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean connState() {
        return isConnected;
    }
}

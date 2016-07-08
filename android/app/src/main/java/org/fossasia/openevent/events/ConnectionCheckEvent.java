package org.fossasia.openevent.events;

/**
 * User: shivenmian
 * Date: 04/01/16
 */
public class ConnectionCheckEvent {

    public boolean isConnected;

    public ConnectionCheckEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean connState() {
        return isConnected;
    }
}

package org.fossasia.openevent.events;

/**
 * Created by shivenmian on 21/01/16.
 */
public class ErrorLogProcessEvent {

    public int priority;
    public String tag;
    public String message;
    public Throwable t;

    public ErrorLogProcessEvent(int priority, String tag, String message, Throwable t) {
        this.priority = priority;
        this.tag = tag;
        this.message = message;
        this.t = t;
    }
}

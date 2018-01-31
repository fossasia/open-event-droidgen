package org.fossasia.openevent.common.events;

import org.fossasia.openevent.data.Event;

public class EventLoadedEvent {
    private Event event;

    public EventLoadedEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}

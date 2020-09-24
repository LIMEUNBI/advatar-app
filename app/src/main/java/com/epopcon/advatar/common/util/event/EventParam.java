package com.epopcon.advatar.common.util.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventParam implements Serializable {

    private Event event;
    private int retry = 0;
    private List<EventHandler> eventHandlers = new ArrayList<EventHandler>();

    EventParam(Event event) {
        this.event = event;
    }

    public void addEventHandler(EventHandler eventHandler) {
        eventHandlers.add(eventHandler);
    }

    public Event getEvent() {
        return event;
    }

    public List<EventHandler> getEventHandlers() {
        return eventHandlers;
    }

    public int getRetry() {
        return retry;
    }

    public void increseRetry() {
        retry++;
    }
}

package com.epopcon.advatar.common.util.event;

public class EventDetail {

    public final static int STEP_RUNNING = 0;
    public final static int STEP_FINISH = 1;

    private Event event;
    private int step;

    public EventDetail(Event event, int step) {
        this.event = event;
        this.step = step;
    }

    public Event getEvent() {
        return event;
    }

    public int getStep() {
        return step;
    }
}

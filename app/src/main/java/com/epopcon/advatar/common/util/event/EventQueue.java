package com.epopcon.advatar.common.util.event;

import java.util.concurrent.LinkedBlockingDeque;

public class EventQueue extends LinkedBlockingDeque<EventParam> {

    public static EventQueue queue;

    public static EventQueue getInstance() {
        if (queue == null) {
            synchronized (EventQueue.class) {
                if (queue == null) {
                    queue = new EventQueue();
                }
            }
        }
        return queue;
    }
}

package com.epopcon.advatar.common.util.event;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventRepository {

    private List<Repository> tempEvents = new CopyOnWriteArrayList<>();

    private Context context;

    EventRepository(Context context) {
        this.context = context;
    }

    public void register(Object object, Event.Type type, EventHandler handler) {
        Repository repo = new Repository(object);
        int index = tempEvents.indexOf(repo);

        if (index > -1) {
            repo = tempEvents.get(index);
        } else {
            tempEvents.add(repo);
        }

        repo.events.put(type, handler);
    }

    public boolean hasEvent(Event.Type type) {
        for (Repository repo : tempEvents) {
            if (repo.events.containsKey(type))
                return true;
        }
        return false;
    }

    public List<EventHandler> getEventHandlers(Event.Type type) {
        List<EventHandler> handlers = new ArrayList<>();

        for (Repository repo : tempEvents) {
            if (repo.events.containsKey(type))
                handlers.add(repo.events.get(type));
        }

        return handlers;
    }

    public void unregister(Object object) {
        tempEvents.remove(new Repository(object));
    }

    private class Repository {
        Object object;
        Map<Event.Type, EventHandler> events = new HashMap<>();

        Repository(Object object) {
            this.object = object;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Repository)
                return object.equals(((Repository) o).object);
            return false;
        }
    }
}

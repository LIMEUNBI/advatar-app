package com.epopcon.advatar.common.util.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {

    private Type type;
    private Event next;
    private boolean allowConcurrency = false;
    private boolean logging = false;

    private String eventCode = null;

    private Map<String, Object> param = new HashMap<>();

    private Deferred deferred = new Deferred() {
        @Override
        public void onProgress(Object... o) {
        }

        @Override
        public void onSuccess(Object... o) {
        }

        @Override
        public void onFailure(Throwable e, Object... o) {
        }
    };

    public Event(Type type) {
        this(type, null, false);
    }

    public Event(Type type, Deferred deferred) {
        this(type, null, false);
        this.deferred = deferred;
    }

    public Event(Type type, boolean allowConcurrency) {
        this(type, null, allowConcurrency);
    }

    public Event(Type type, Event next) {
        this(type, next, false);
    }

    public Event(Type type, Event next, boolean allowConcurrency) {
        this.type = type;
        this.next = next;
        this.allowConcurrency = allowConcurrency;
        this.eventCode = type.toString();
    }

    public Event(Type type, Deferred deferred, Event next) {
        this(type, next, false);
        this.deferred = deferred;
        this.eventCode = type.toString();
    }

    public Type getType() {
        return type;
    }

    public Map<String, Object> param() {
        return param;
    }

    public void param(Map<String, Object> param) {
        this.param = param;
    }

    public Event next() {
        return next;
    }

    public Deferred deferred() {
        return deferred;
    }

    public void putObject(String key, Object value) {
        param.put(key, value);
    }

    public <T> T getObject(String key, T defaultValue) {
        Object object = param.get(key);
        if (object == null)
            return defaultValue;
        return (T) object;
    }

    public boolean isAllowConcurrency() {
        return allowConcurrency;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public String getEventCode() {
        return eventCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Event) {
            return eventCode.equals(((Event) o).getEventCode());
        }
        return false;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    @Override
    public int hashCode() {
        return eventCode.hashCode();
    }

    public enum Type {
        IMPORT_ONLINE_STORE, IMPORT_ONLINE_STORE_CART, REQUEST_ONLINE_STORE, ON_ONLINE_STORE_UPDATE
    }

}

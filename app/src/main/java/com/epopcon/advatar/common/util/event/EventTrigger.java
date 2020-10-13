package com.epopcon.advatar.common.util.event;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.epopcon.extra.common.utils.ExecutorPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class EventTrigger extends Observable {

    private final static String SERVICE_NAME = "com.epopcon.advatar.common.util.event.EventService";

    private EventRepository repository;
    private EventHandlerFactory factory;

    private EventQueue items = EventQueue.getInstance();

    private Context context;

    private Map<String, EventParam> runningJobs = new HashMap<>();
    public final int MAX_WAIT_TIME = 5000;

    private static EventTrigger trigger;

    public EventTrigger(Context context) {
        this.context = context;
        this.repository = new EventRepository(context);
        this.factory = new EventHandlerFactory(context);
    }

    public static EventTrigger getInstance(Context context) {
        if (trigger == null) {
            synchronized (EventTrigger.class) {
                if (trigger == null) {
                    trigger = new EventTrigger(context);
                }
            }
        }
        return trigger;
    }

    public void register(Object object, Event.Type type, EventHandler handler) {
        repository.register(object, type, handler);
    }

    public boolean trigger(Event event) {
        final EventParam param = getEventParam(event);

        if (param == null)
            return false;
        if (event.isAllowConcurrency() || addOrWaitRunning(param, MAX_WAIT_TIME)) {

            try {
                notifyObservers(event, EventDetail.STEP_RUNNING);
                for (EventHandler handler : param.getEventHandlers())
                    handler.execute(event);
            } finally {
                removeRunning(event.getEventCode());
                notifyObservers(event, EventDetail.STEP_FINISH);
            }
            return true;
        }
        return false;
    }

    private EventParam getEventParam(Event event) {
        EventParam param = null;
        if (repository.hasEvent(event.getType())) {
            param = new EventParam(event);
            List<EventHandler> handlers = repository.getEventHandlers(event.getType());
            for (EventHandler handler : handlers) {
                if (handler.isExecutable(event))
                    param.addEventHandler(handler);
            }
        }

        EventHandler handler = factory.getEventHandler(event.getType());

        if (handler != null && handler.isExecutable(event)) {
            if (param == null)
                param = new EventParam(event);
            param.addEventHandler(handler);
        }
        return param;
    }

    public boolean triggerAsync(final Event event) {
        final EventParam param = getEventParam(event);

        if (param == null)
            return false;
        if (event.isAllowConcurrency() || addOrWaitRunning(param, MAX_WAIT_TIME)) {
            if (param.getEventHandlers().size() > 0) {
                ExecutorPool.execute(new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            notifyObservers(event, EventDetail.STEP_RUNNING);
                            for (EventHandler handler : param.getEventHandlers()) {
                                handler.execute(event);
                            }
                        } finally {
                            removeRunning(event.getEventCode());
                            notifyObservers(event, EventDetail.STEP_FINISH);
                        }
                        return null;
                    }
                });
                return true;
            }
        }
        return false;
    }

    public void triggerService(Event event) {

        try {
            EventParam param = new EventParam(event);
            EventHandler handler = factory.getEventHandler(event.getType());

            if (handler != null && handler.isExecutable(event)) {
                param.addEventHandler(handler);
            }

            if (param.getEventHandlers().size() > 0) {
                items.add(param);
                if (!isServiceRunning()) {
                    context.startService(new Intent(context, EventService.class));
                }
            }
        } catch (Exception e) {}
    }

    public void interrupt(String eventCode) {
        if (isRunning(eventCode)) {
            EventParam param = null;
            synchronized (runningJobs) {
                param = runningJobs.get(eventCode);
            }

            if (param != null) {
                for (EventHandler handler : param.getEventHandlers()) {
                    handler.setInterrupted(true);
                    if (handler.getStatus() == EventHandler.STATUS_RUNNING)
                        handler.onInterrupted();
                }
            }
        }
    }

    public Object getCurrentStep(String eventCode) {
        if (isRunning(eventCode)) {
            EventParam param = null;
            synchronized (runningJobs) {
                param = runningJobs.get(eventCode);
            }

            if (param != null) {
                for (EventHandler handler : param.getEventHandlers()) {
                    if (handler.getStatus() == EventHandler.STATUS_RUNNING) {
                        return handler.getCurrentStep();
                    }
                }
            }
        }
        return null;
    }

    public List<String> getRunningJobNames() {
        synchronized (runningJobs) {
            List<String> jobNames = new ArrayList<>(runningJobs.size());
            for (String jobName : runningJobs.keySet())
                jobNames.add(jobName);
            return jobNames;
        }
    }

    public void unregister(Object object) {
        repository.unregister(object);
    }

    public void destroyService() {
        if (isServiceRunning())
            context.stopService(new Intent(context, EventService.class));
        items.clear();
        runningJobs.clear();
    }

    public EventRepository getRepository() {
        return repository;
    }

    @Override
    public void addObserver(Observer o) {
        synchronized (runningJobs) {
            for (Map.Entry<String, EventParam> runningJob : runningJobs.entrySet()) {
                EventParam param = runningJob.getValue();
                Event event = param.getEvent();

                for (EventHandler handler : param.getEventHandlers()) {
                    if (handler.getStatus() == EventHandler.STATUS_RUNNING) {
                        o.update(this, new EventDetail(event, EventDetail.STEP_RUNNING));
                        break;
                    }
                }
            }
        }
        super.addObserver(o);
    }

    void notifyObservers(Event event, int step) {
        try {
            setChanged();
            notifyObservers(new EventDetail(event, step));
        } catch (Throwable e) {
            Log.e(EventTrigger.class.getSimpleName(), e.getMessage(), e);
        }
    }

    boolean removeRunning(String eventCode) {
        boolean remove = false;
        synchronized (runningJobs) {
            if (runningJobs.containsKey(eventCode)) {
                runningJobs.remove(eventCode);
                remove = true;
            }
            runningJobs.notifyAll();
        }
        return remove;
    }

    boolean addOrWaitRunning(EventParam param, int waitTime) {

        long start = System.currentTimeMillis();
        boolean loop;

        Event event = param.getEvent();
        do {
            synchronized (runningJobs) {
                long current = System.currentTimeMillis();
                loop = start + waitTime >= current;
                if (runningJobs.containsKey(event.getEventCode())) {
                    if (loop) {
                        try {
                            long sleep = waitTime - (current - start);
                            if (sleep > 0)
                                runningJobs.wait(sleep);
                        } catch (InterruptedException e) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    runningJobs.put(event.getEventCode(), param);
                    break;
                }
            }
        } while (loop);
        return true;
    }

    public boolean isRunning(String eventCode) {
        synchronized (runningJobs) {
            return runningJobs.containsKey(eventCode);
        }
    }

    public boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (SERVICE_NAME.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

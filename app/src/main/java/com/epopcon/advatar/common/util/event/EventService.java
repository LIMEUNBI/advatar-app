package com.epopcon.advatar.common.util.event;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class EventService extends Service {

    private final static String TAG = EventService.class.getSimpleName();

    private final int POOL_SIZE = 2;
    public final int MAX_WAIT_TIME = 10000;

    private AtomicInteger counter = new AtomicInteger(0);

    private EventQueue items = EventQueue.getInstance();

    private EventTrigger trigger;

    private ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, EventService.this.getClass().getSimpleName());
        }
    });

    private boolean destroy = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.trigger = EventTrigger.getInstance(this);
        Log.i(TAG, "EventService has been created...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        counter.set(POOL_SIZE);

        for (int i = 0; i < POOL_SIZE; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            EventParam item = items.poll(60, TimeUnit.SECONDS);

                            if (item == null) {
                                if (counter.decrementAndGet() <= 0)
                                    break;
                                continue;
                            }
                            counter.set(POOL_SIZE);

                            Event event = item.getEvent();

                            if (!event.isAllowConcurrency() && !trigger.addOrWaitRunning(item, MAX_WAIT_TIME)) {
                                if (item.getRetry() >= 3) {
                                    Log.e(TAG, "Drop the event -> " + event.getType());
                                } else {
                                    item.increaseRetry();
                                    items.add(item);
                                }
                                continue;
                            }

                            try {

                                long elapsedTime = System.currentTimeMillis();
                                trigger.notifyObservers(event, EventDetail.STEP_RUNNING);
                                for (EventHandler eventHandler : item.getEventHandlers()) {
                                    if (eventHandler.isExecutable(event))
                                        eventHandler.execute(event);
                                }
                                Log.d(TAG, String.format("Trigger event -> %s, elased time -> %dms", event.getType(), System.currentTimeMillis() - elapsedTime));
                            } catch (Throwable e) {
                                Log.e(TAG, String.format("%s -> %s", TAG, e.getMessage()), e);
                            } finally {
                                trigger.removeRunning(event.getEventCode());
                                trigger.notifyObservers(event, EventDetail.STEP_FINISH);

                            }
                        }
                    } catch (InterruptedException e) {
                        if (!destroy)
                            Log.e(TAG, e.getMessage(), e);
                    } catch (Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                    } finally {
                        stopSelf();
                    }
                }
            });
        }
        return START_STICKY;
    }

    void shutdown() {
        if (!executorService.isShutdown()) {
            Log.i(TAG, "Forcing shutdown...");
            executorService.shutdownNow();
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy = true;
        shutdown();
        Log.i(TAG, "EventService has been destroyed...");
    }
}

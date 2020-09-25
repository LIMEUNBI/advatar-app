package com.epopcon.advatar.common.util.event;

import android.content.Context;

import com.epopcon.advatar.common.util.event.handler.OnlineStoreImportHandler;
import com.epopcon.advatar.common.util.event.handler.OnlineStoreReportHandler;
import com.epopcon.advatar.common.util.event.handler.OnlineStoreRequestHandler;


public class EventHandlerFactory {

    private Context context;

    public EventHandlerFactory(Context context) {
        this.context = context;
    }

    public EventHandler getEventHandler(Event.Type type) {
        switch (type) {
            case IMPORT_ONLINE_STORE:
                return new OnlineStoreImportHandler(context);
            case REQUEST_ONLINE_STORE:
                return new OnlineStoreRequestHandler(context);
            case REPORT_ONLINE_STORE:
                return new OnlineStoreReportHandler(context);
        }
        return null;
    }
}
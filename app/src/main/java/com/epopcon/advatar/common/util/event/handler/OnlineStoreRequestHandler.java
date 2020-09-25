package com.epopcon.advatar.common.util.event.handler;

import android.content.Context;

import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.util.event.Event;
import com.epopcon.advatar.common.util.event.EventHandler;
import com.epopcon.advatar.common.util.event.EventTrigger;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiryHelper;

import java.util.Collections;
import java.util.Set;

/**
 * SMS 혹은 카드사 동기화 시에 온라인상점 동기화를 위한 이벤트 핸들러
 */
public class OnlineStoreRequestHandler extends EventHandler {

    private final String TAG = Event.Type.REQUEST_ONLINE_STORE.toString();

    private Context context;

    public OnlineStoreRequestHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onEvent(Event event) {

        Event e = new Event(Event.Type.ON_ONLINE_STORE_UPDATE, true);
        e.putObject("driven", Event.Type.REQUEST_ONLINE_STORE.toString());
        e.putObject("status", Config.EVENT_STATUS_STEP_END);

        EventTrigger.getInstance(context).triggerAsync(e);

        Set<OnlineConstant> onlineStores = event.getObject("stores", Collections.EMPTY_SET); // 반영된 온라인상점 타입(필수)

        for (OnlineConstant constant : onlineStores) {
            if (OnlineDeliveryInquiryHelper.hasStoredIdAndPassword(context, constant)
                    && OnlineDeliveryInquiryHelper.getStatus(context, constant) == OnlineConstant.ONLINE_STORE_PROCESS_STATUS_IMPORT) {
                String encUserId = OnlineDeliveryInquiryHelper.getStoredId(context, constant);
                long lastOrderDateTime = MessageDao.getInstance().getLastOrderDateTime(constant.toString(), encUserId);

                Event e1 = new Event(Event.Type.IMPORT_ONLINE_STORE);

                e1.setEventCode(String.format("%s[%s]", Event.Type.IMPORT_ONLINE_STORE, constant));
                e1.putObject("type", constant);
                e1.putObject("first", false);
                e1.putObject("lastOrderDateTime", lastOrderDateTime);

                EventTrigger.getInstance(context).triggerService(e1);
            }
        }
    }
}

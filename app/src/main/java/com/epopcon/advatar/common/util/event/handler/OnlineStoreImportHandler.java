package com.epopcon.advatar.common.util.event.handler;

import android.content.Context;
import android.util.Log;

import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.model.OnlineBizType;
import com.epopcon.advatar.common.util.event.Deferred;
import com.epopcon.advatar.common.util.event.Event;
import com.epopcon.advatar.common.util.event.EventHandler;
import com.epopcon.advatar.common.util.event.EventTrigger;
import com.epopcon.extra.ExtraClassLoader;
import com.epopcon.extra.common.ErrorType;
import com.epopcon.extra.common.exception.PException;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiryHandler;
import com.epopcon.extra.online.OnlineDeliveryInquiryHelper;
import com.epopcon.extra.online.model.OrderDetail;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 온라인상점 로그인 혹은 동기화 시에 호출되는 이벤트 핸들러
 */
public class OnlineStoreImportHandler extends EventHandler {

    private final String TAG = Event.Type.IMPORT_ONLINE_STORE.toString();

    private Context context;
    private OnlineBizType bizType;

    public OnlineStoreImportHandler(Context context) {
        this.context = context;
        this.bizType = new OnlineBizType(context);
    }

    @Override
    public void onEvent(Event event) {

        OnlineConstant type = event.getObject("type", null); // 온라인상점 타입(선택)
        boolean firstRun = event.getObject("first", true); // 로그인 후 초기 IMPORT 여부(선택)
        Long lastOrderDateTime = event.getObject("lastOrderDateTime", -1L); // 마지막으로 IMPORT 된 주문의 날짜(선택)

        Log.d(TAG, String.format("# type -> %s, first -> %s, lastOrderDateTime -> %s", type, firstRun, lastOrderDateTime));

        Deferred deferred = new Deferred() {
            @Override
            public void onProgress(Object... o) {
            }

            @Override
            public void onSuccess(Object... o) {
                OnlineConstant constant = (OnlineConstant) o[0];
                Integer action = (Integer) o[1];
                Integer page = null;
                List<OrderDetail> list = null;
                Set<String> originIds = null;

                switch (action) {
                    case OnlineConstant.ACTION_QUERY_ORDER_DETAILS:
                        page = (Integer) o[2];
                        list = (List) o[3];
                        originIds = (Set) o[4];
                        break;
                    case OnlineConstant.ACTION_QUERY_PAYMENT_DETAILS:
                        page = -1;
                        list = (List) o[2];
                        break;
                }
                progress(constant, action, true, page, list, originIds, null);
            }

            @Override
            public void onFailure(Throwable e, Object... o) {
                OnlineConstant constant = (OnlineConstant) o[0];
                Integer action = (Integer) o[1];
                Integer page = null;

                switch (action) {
                    case OnlineConstant.ACTION_QUERY_ORDER_DETAILS:
                        page = o.length < 3 ? -1 : (Integer) o[2];
                        break;
                    case OnlineConstant.ACTION_QUERY_PAYMENT_DETAILS:
                        page = -1;
                        break;
                }
                PException exception = (PException) e;
                // 로그인 실패 시 아이디와 비밀번호 삭제
                if (exception.getErrorNumber() == ErrorType.ERROR_LOGIN_FAIL.getErrorNumber()) {
                    OnlineDeliveryInquiryHelper.setStatus(context, constant, OnlineConstant.ONLINE_STORE_PROCESS_STATUS_NOT_LOGIN);
                    ExtraClassLoader.getInstance().newOnlineDeliveryInquiry(constant, new OnlineDeliveryInquiryHandler()).removeIdAndPassword();
                }
                progress(constant, action, false, page, Collections.EMPTY_LIST, null, exception);
            }
        };

        // 온라인상점 타입 값을 넘기지 않았으면 로그인된 모든 온라인상점 동기화를 수행
        if (type == null) {
            final long TWO_WEEKS = 14 * 24 * 60 * 60 * 1000;

            for (OnlineConstant constant : bizType.getAvailableTypes()) {

                start(constant);

                String encUserId = OnlineDeliveryInquiryHelper.getStoredId(context, constant);
                lastOrderDateTime = MessageDao.getInstance().getLastOrderDateTime(constant.toString(), encUserId);

                if (lastOrderDateTime > 0)
                    lastOrderDateTime -= TWO_WEEKS;

                OnlineStoreDeliveryInquiry inquiry = null;

                try {
                    if (OnlineDeliveryInquiryHelper.getStatus(context, constant) != OnlineConstant.ONLINE_STORE_PROCESS_STATUS_IMPORT){
                        inquiry = new OnlineStoreDeliveryInquiry(context, constant, deferred, true, -1L);
                    } else {
                        inquiry = new OnlineStoreDeliveryInquiry(context, constant, deferred, false, lastOrderDateTime);
                    }
                    inquiry.queryOrderDetails(OnlineConstant.PERIOD_MAX);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                } finally {
                    if (inquiry != null)
                        inquiry.destory();
                }
            }
            finish();
            // 서버에 결과를 전송
        } else {
            start(type);

            OnlineStoreDeliveryInquiry inquiry = null;
            try {
                inquiry = new OnlineStoreDeliveryInquiry(context, type, deferred, firstRun, lastOrderDateTime);
                inquiry.queryOrderDetails(OnlineConstant.PERIOD_MAX);
                // 서버에 결과를 전송
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if (inquiry != null)
                    inquiry.destory();
            }
            finish();
        }
    }

    private void start(OnlineConstant constant) {
        setCurrentStep(constant);

        Event event = new Event(Event.Type.ON_ONLINE_STORE_UPDATE, true);

        event.putObject("driven", Event.Type.IMPORT_ONLINE_STORE.toString());
        event.putObject("status", Config.EVENT_STATUS_STEP_START); // 시작
        event.putObject("type", constant);
        event.putObject("name", bizType.name(constant));

        EventTrigger.getInstance(context).triggerAsync(event);
    }

    private void progress(OnlineConstant constant, int action, boolean success, Integer page, List<OrderDetail> list, Set<String> originIds, PException exception) {
        if (page == -1)
            setCurrentStep(null);

        Event event = new Event(Event.Type.ON_ONLINE_STORE_UPDATE, true);

        event.putObject("driven", Event.Type.IMPORT_ONLINE_STORE.toString());
        event.putObject("action", action);
        event.putObject("status", page == -1 ? Config.EVENT_STATUS_STEP_END : Config.EVENT_STATUS_STEP_PROGRESS); // 진행중 or 끝
        event.putObject("type", constant);
        event.putObject("name", bizType.name(constant));
        event.putObject("success", success);
        if (page != null)
            event.putObject("page", page);
        if (list != null)
            event.putObject("list", list);
        if (originIds != null)
            event.putObject("originIds", originIds);
        if (exception != null)
            event.putObject("exception", exception);

        EventTrigger.getInstance(context).triggerAsync(event);
    }

    private void finish() {
        setCurrentStep(null);

        Event event = new Event(Event.Type.ON_ONLINE_STORE_UPDATE, true);

        event.putObject("driven", Event.Type.IMPORT_ONLINE_STORE.toString());
        event.putObject("status", Config.EVENT_STATUS_ALL_END); // 끝

        EventTrigger.getInstance(context).triggerAsync(event);
    }
}

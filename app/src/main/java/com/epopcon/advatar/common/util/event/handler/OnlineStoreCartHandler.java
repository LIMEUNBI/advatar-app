package com.epopcon.advatar.common.util.event.handler;

import android.content.Context;
import android.util.Log;

import com.epopcon.advatar.common.config.Config;
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
import com.epopcon.extra.online.model.CartDetail;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 온라인상점 장바구니 호출 시에 호출되는 이벤트 핸들러
 */
public class OnlineStoreCartHandler extends EventHandler {

    private final String TAG = Event.Type.IMPORT_ONLINE_STORE_CART.toString();

    private Context context;
    private OnlineBizType bizType;

    public OnlineStoreCartHandler(Context context) {
        this.context = context;
        this.bizType = new OnlineBizType(context);
    }

    @Override
    public void onEvent(Event event) {

        OnlineConstant type = event.getObject("type", null); // 온라인상점 타입(선택)

        Deferred deferred = new Deferred() {
            @Override
            public void onProgress(Object... o) {
            }

            @Override
            public void onSuccess(Object... o) {
                OnlineConstant constant = (OnlineConstant) o[0];
                Integer action = (Integer) o[1];
                Integer page;
                Set<String> originIds = null;
                List<CartDetail> cartList;

                switch (action) {
                    case OnlineConstant.ACTION_QUERY_CART_DETAILS:
                        page = -1;
                        cartList = (List) o[2];
                        progress(constant, action, true, page, cartList, originIds, null);
                }
            }

            @Override
            public void onFailure(Throwable e, Object... o) {
                OnlineConstant constant = (OnlineConstant) o[0];
                Integer action = (Integer) o[1];
                Integer page = null;

                switch (action) {
                    case OnlineConstant.ACTION_QUERY_CART_DETAILS:
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
            for (OnlineConstant constant : bizType.getAvailableTypes()) {
                start(constant);
                OnlineStoreCartInquiry inquiry = null;

                try {
                    inquiry = new OnlineStoreCartInquiry(context, constant, deferred);
                    inquiry.queryCartDetails();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                } finally {
                    if (inquiry != null)
                        inquiry.destory();
                }
            }
            // 서버에 결과를 전송
        } else {
            start(type);

            OnlineStoreCartInquiry inquiry = null;
            try {
                inquiry = new OnlineStoreCartInquiry(context, type, deferred);
                inquiry.queryCartDetails();
                // 서버에 결과를 전송
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if (inquiry != null)
                    inquiry.destory();
            }
        }
        finish();
    }

    private void start(OnlineConstant constant) {
        setCurrentStep(constant);

        Event event = new Event(Event.Type.ON_ONLINE_STORE_UPDATE, true);

        event.putObject("driven", Event.Type.IMPORT_ONLINE_STORE_CART.toString());
        event.putObject("status", Config.EVENT_STATUS_STEP_START); // 시작
        event.putObject("type", constant);
        event.putObject("name", bizType.name(constant));

        EventTrigger.getInstance(context).triggerAsync(event);
    }

    private void progress(OnlineConstant constant, int action, boolean success, Integer page, List<CartDetail> list, Set<String> originIds, PException exception) {
        if (page == -1)
            setCurrentStep(null);

        Event event = new Event(Event.Type.ON_ONLINE_STORE_UPDATE, true);

        event.putObject("driven", Event.Type.IMPORT_ONLINE_STORE_CART.toString());
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

        event.putObject("driven", Event.Type.IMPORT_ONLINE_STORE_CART.toString());
        event.putObject("status", Config.EVENT_STATUS_ALL_END); // 끝

        EventTrigger.getInstance(context).triggerAsync(event);
    }
}

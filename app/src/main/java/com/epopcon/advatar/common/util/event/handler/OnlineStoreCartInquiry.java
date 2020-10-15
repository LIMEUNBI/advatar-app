package com.epopcon.advatar.common.util.event.handler;

import android.content.Context;
import android.util.Log;

import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.common.util.event.Deferred;
import com.epopcon.extra.ExtraClassLoader;
import com.epopcon.extra.common.exception.PException;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiry;
import com.epopcon.extra.online.OnlineDeliveryInquiryHandler;
import com.epopcon.extra.online.model.CartDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OnlineStoreCartInquiry extends OnlineDeliveryInquiryHandler {

    private final String TAG = OnlineStoreCartInquiry.class.getSimpleName();

    private Context context;

    private OnlineConstant constant;
    private String storeName;

    private Map<String, Set<String>> removedOrders;

    private Object lock = new Object();

    private OnlineDeliveryInquiry inquiry;

    private Deferred deferred;

    public OnlineStoreCartInquiry(Context context, OnlineConstant constant, Deferred deferred) {
        this.context = context;
        this.constant = constant;
        this.storeName = constant.toString();
        this.deferred = deferred;
        this.inquiry = ExtraClassLoader.getInstance().newOnlineDeliveryInquiry(constant, this);
        this.removedOrders = (Map) SharedPreferenceBase.getPrefObject(context, Config.ONLINE_REMOVED_ORDERS);
        if (this.removedOrders == null)
            this.removedOrders = new HashMap<>();
    }

    @Override
    public void onReadySuccess(int action) {
    }

    @Override
    public void onReadyFailure(int action, PException exception) {
        Log.d(TAG, "onReadyFailure -> " + exception.getMessage());

        switch (action) {
            case OnlineConstant.ACTION_INITIALIZE:
            case OnlineConstant.ACTION_TRY_LOGIN:
                deferred.onFailure(exception, constant, action);
                break;
        }
        unlock();
    }

    @Override
    public void onQueryCartDetails(boolean success, List<CartDetail> list, PException exception) {
        Log.d(TAG, "onQueryCartDetails -> " + success + ", message : " + exception);

        if (success) {
            if (list != null) {
                for (CartDetail cartDetail : list)
                    MessageDao.getInstance().insertOnlineCart(storeName, cartDetail);
            }
            deferred.onSuccess(constant, OnlineConstant.ACTION_QUERY_CART_DETAILS, list);
        } else {
            deferred.onFailure(exception, constant, OnlineConstant.ACTION_QUERY_CART_DETAILS);
        }
        unlock();
    }

    public void queryCartDetails() {
        inquiry.queryCartDetails();
        lock();
    }

    public void destory() {
        inquiry.destory();
    }

    private void lock() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void unlock() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}

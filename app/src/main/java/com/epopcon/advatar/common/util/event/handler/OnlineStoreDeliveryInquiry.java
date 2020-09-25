package com.epopcon.advatar.common.util.event.handler;

import android.content.Context;
import android.util.Log;

import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.util.Utils;
import com.epopcon.advatar.common.util.event.Deferred;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.extra.ExtraClassLoader;
import com.epopcon.extra.common.exception.PException;
import com.epopcon.extra.online.OnlineConstant;
import com.epopcon.extra.online.OnlineDeliveryInquiry;
import com.epopcon.extra.online.OnlineDeliveryInquiryHandler;
import com.epopcon.extra.online.OnlineDeliveryInquiryHelper;
import com.epopcon.extra.online.model.OrderDetail;
import com.epopcon.extra.online.model.ProductDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OnlineStoreDeliveryInquiry extends OnlineDeliveryInquiryHandler {

    private final String TAG = OnlineStoreDeliveryInquiry.class.getSimpleName();

    private Context context;

    private OnlineConstant constant;
    private String encUserId;
    private String storeName;

    private Map<String, Set<String>> removedOrders;

    private long minTimeStamp = 0L;
    private final long ONE_DAY = (long) 24 * 60 * 60 * 1000 - 1;

    private boolean firstRun = false;
    private Long lastOrderDateTime = -1L;

    private Object lock = new Object();

    private OnlineDeliveryInquiry inquiry;

    private Deferred deferred;

    public OnlineStoreDeliveryInquiry(Context context, OnlineConstant constant, Deferred deferred, boolean firstRun, Long lastOrderDateTime) {
        this.context = context;
        this.constant = constant;
        this.encUserId = OnlineDeliveryInquiryHelper.getStoredId(context, constant);
        this.storeName = constant.toString();
        this.deferred = deferred;
        this.firstRun = firstRun;
        this.lastOrderDateTime = lastOrderDateTime;
        this.inquiry = ExtraClassLoader.getInstance().newOnlineDeliveryInquiry(constant, this);
        this.removedOrders = (Map) SharedPreferenceBase.getPrefObject(context, Config.ONLINE_REMOVED_ORDERS);
        if (this.removedOrders == null)
            this.removedOrders = new HashMap<>();
    }

    @Override
    public void onReadySuccess(int action) {
        if (!firstRun && action == OnlineConstant.ACTION_INITIALIZE) {
            List<Long> fails = OnlineDeliveryInquiryHelper.getFailMessageIds(context, constant);
            minTimeStamp = Long.MAX_VALUE;

            if (lastOrderDateTime > -1L) {
                minTimeStamp = Math.min(minTimeStamp, lastOrderDateTime);
            }
        }
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

    private boolean skipRemovedOrderDetail(OrderDetail orderDetail) {
        String orderNumber = orderDetail.getOrderNumber();
        String key = String.format("%s/%s", storeName, orderNumber);

        if (removedOrders.containsKey(key)) {
            Set<String> products = removedOrders.get(key);
            List<ProductDetail> productDetails = orderDetail.getProductDetails();

            for (String product : products) {
                String[] temp = product.split("\\|");

                int[] priority = new int[]{0, 0};
                long h0 = Long.parseLong(temp[0]);
                int q0 = Integer.parseInt(temp[1]);
                String s0 = temp[2] == null ? "" : temp[2];

                for (int i = 0; i < productDetails.size(); i++) {
                    ProductDetail pd = productDetails.get(i);

                    long h1 = Utils.convertStringToHash(String.format("%s|%s|%s", pd.getProductName(), pd.getProductOption(), pd.getSeller(), pd.getPrice()));
                    int q1 = pd.getQuantity();
                    String s1 = pd.getStatus() == null ? "" : pd.getStatus();

                    if (h0 == h1 && q0 == q1 && s0.equals(s1)) {
                        priority[0] = 3;
                        priority[1] = i;
                        break;
                    } else if (h0 == h1 && q0 == q1 && priority[0] < 3) {
                        priority[0] = 2;
                        priority[1] = i;
                    } else if (h0 == h1 && priority[0] < 2) {
                        priority[0] = 1;
                        priority[1] = i;
                    }
                }

                if (priority[0] > 0)
                    productDetails.remove(priority[1]);
            }
            return productDetails.size() == 0;
        }
        return false;
    }

    @Override
    public boolean onQueryOrderDetails(boolean success, int page, List<OrderDetail> list, PException exception) {

        Log.d(TAG, "onQueryOrderDetails -> " + success + ", page : " + page + ", message : " + exception);
        boolean keepGoing = true;

        if (success) {
            List<String> orderNumbers = new ArrayList<>();

            for (OrderDetail orderDetail : list) {
                Log.d(TAG, orderDetail.toString());

                // 사용자가 삭제한 건은 무시
                if (skipRemovedOrderDetail(orderDetail))
                    continue;

                long timestamp = Utils.parseDate(orderDetail.getOrderDate()) + ONE_DAY;

                if (minTimeStamp < timestamp) {
                    MessageDao.getInstance().insertOnlineStore(storeName, encUserId, orderDetail);
                    orderNumbers.add(orderDetail.getOrderNumber());
                } else {
                    keepGoing = false;
                    break;
                }
            }

        } else {
            deferred.onFailure(exception, constant, OnlineConstant.ACTION_QUERY_ORDER_DETAILS, page);
            unlock();
        }
        return keepGoing;
    }

    @Override
    public void onQueryPaymentDetails(boolean success, List<OrderDetail> list, PException exception) {
        Log.d(TAG, "onQueryPaymentDetails -> " + success + ", message : " + exception);

        if (success) {
            for (OrderDetail orderDetail : list)
                MessageDao.getInstance().insertOnlineStore(storeName, encUserId, orderDetail);
            deferred.onSuccess(constant, OnlineConstant.ACTION_QUERY_PAYMENT_DETAILS, list);
        } else {
            deferred.onFailure(exception, constant, OnlineConstant.ACTION_QUERY_PAYMENT_DETAILS);
        }
        unlock();
    }

    public void queryOrderDetails(int period) {
        inquiry.queryOrderDetails(period, "", "");
        lock();
    }

    public void queryPaymentDetails(List<OrderDetail> orderDetails) {
        inquiry.queryPaymentDetails(orderDetails);
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

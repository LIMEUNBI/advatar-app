package com.epopcon.advatar.common.util.event.handler;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.epopcon.advatar.common.db.MessageDao;
import com.epopcon.advatar.common.model.OnlineBizDetail;
import com.epopcon.advatar.common.util.event.Event;
import com.epopcon.advatar.common.util.event.EventHandler;
import com.epopcon.extra.ExtraClassLoader;
import com.epopcon.extra.common.ErrorType;
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

/**
 * 수집된 온라인 상점 주문 상품을 서버에 전송하기 위한 이벤트 핸들러
 */
public class OnlineStoreReportHandler extends EventHandler {

    private final String TAG = Event.Type.REPORT_ONLINE_STORE.toString();
    private final int TIMEOUT = 3000;

    private Context context;

    public OnlineStoreReportHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onEvent(Event event) {

        Map<OnlineConstant, List<OrderDetail>> listMap = MessageDao.getInstance().getReportOnlineStoreOrderDetails(context);

        for (Map.Entry<OnlineConstant, List<OrderDetail>> entry : listMap.entrySet()) {
            final OnlineConstant type = entry.getKey();
            final List<OrderDetail> orderDetails = entry.getValue();

            OnlineDeliveryInquiry inquiry = ExtraClassLoader.getInstance().newOnlineDeliveryInquiry(type, new OnlineDeliveryInquiryHandler() {

                private boolean execute = false;

                @Override
                public void onReadyFailure(int action, PException exception) {
                    if (!execute && action == OnlineConstant.ACTION_TRY_LOGIN) {
                        execute = true;
                        // 로그인 실패 시 아이디와 비밀번호 삭제
                        if (exception.getErrorNumber() == ErrorType.ERROR_LOGIN_FAIL.getErrorNumber()) {
                            OnlineDeliveryInquiryHelper.setStatus(context, type, OnlineConstant.ONLINE_STORE_PROCESS_STATUS_NOT_LOGIN);
                            inquiry.removeIdAndPassword();
                        }
                        execute();
                    }
                }

                @Override
                public void onReadySuccess(int action) {
                    if (!execute && action == OnlineConstant.ACTION_TRY_LOGIN) {
                        execute = true;
                        execute();
                    }
                }

                private void execute() {
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        AsyncTask asyncTask = new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                executeTask(type, inquiry, orderDetails);
                                return null;
                            }
                        };
                        asyncTask.execute();

                    } else {
                        executeTask(type, inquiry, orderDetails);
                    }
                }
            });

            inquiry.tryLoginIfNotAuthenticated();
        }
    }

    private void executeTask(OnlineConstant type, OnlineDeliveryInquiry inquiry, List<OrderDetail> orderDetails) {

        List<Map<String, Object>> list = new ArrayList<>();

        List<OrderDetail> temp = new ArrayList<>();
        for (int i = 0; i < orderDetails.size(); i++) {
            try {
                temp.clear();
                temp.add(orderDetails.get(i));
//                inquiry.fillOutProductDetails(temp);
            } catch (Exception e) {
                Log.e(TAG, String.format("%s -> %s", Event.Type.REPORT_ONLINE_STORE, e.getMessage()), e);

                for (OrderDetail orderDetail : temp) {
                    MessageDao.getInstance().db().execSQL(
                            String.format("UPDATE online_store SET report_fail_count = IFNULL(report_fail_count, 0) + 1 WHERE name = '%s' AND order_number = '%s'",
                                    type.toString(), orderDetail.getOrderNumber()));
                }

                orderDetails.remove(i);
                i--;
            }
        }

        inquiry.destory();
        addAndReport(type, list, orderDetails);

    }

    private void addAndReport(OnlineConstant type, List<Map<String, Object>> list, List<OrderDetail> orderDetails) {
        for (OrderDetail orderDetail : orderDetails) {

            String storeName = type.toString();
            String orderNumber = orderDetail.getOrderNumber();
            String orderDate = orderDetail.getOrderDate();
            long payAmount = orderDetail.getPayAmount();
            long refundAmount = orderDetail.getRefundAmount();
            long totalAmount = payAmount - refundAmount;
            int cancel = orderDetail.isCancel() ? 1 : 0;
            long companyId = ((OnlineBizDetail) orderDetail).getCompanyId();

            List<Map<String, Object>> productList = new ArrayList<>();

            Map<String, Object> m1 = new HashMap<>();

            m1.put("storeCode", storeName);
            m1.put("companyId", companyId);
            m1.put("orderNum", orderNumber);
            m1.put("orderYmd", orderDate);
            m1.put("initPayment", payAmount);
            m1.put("cancelPayment", refundAmount);
            m1.put("payment", totalAmount);
            m1.put("orderCancelYn", cancel);
            m1.put("productList", productList);

            int i = 1;
            for (ProductDetail productDetail : orderDetail.getProductDetails()) {
                int seq = i++;
                String category = productDetail.getCategory();
                String productUrl = productDetail.getProductUrl();
                String productImageUrl = productDetail.getProductImageUrl();
                String productName = productDetail.getProductName();
                String productOption = productDetail.getProductOption();
                long price = productDetail.getPrice();
                int quantity = productDetail.getQuantity();
                String status = productDetail.getStatus();

                Map<String, Object> m2 = new HashMap<>();

                m2.put("seq", seq);
                m2.put("cate", category);
                m2.put("productUrl", productUrl);
                m2.put("imageUrl", productImageUrl);
                m2.put("name", productName);
                m2.put("option", productOption);
                m2.put("price", price);
                m2.put("qty", quantity);
                m2.put("status", status);

                productList.add(m2);

                if (!TextUtils.isEmpty(category)) {
                    MessageDao.getInstance().db().execSQL(
                            String.format("UPDATE online_store_product SET category = '%s' WHERE store_name = '%s' AND order_number = '%s' AND seq = %s",
                                    category.replaceAll("'", "''"), storeName, orderNumber, seq));
                }
            }

            list.add(m1);

        }
    }
}

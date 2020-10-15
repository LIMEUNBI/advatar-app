package com.epopcon.advatar.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.epopcon.advatar.common.CommonLibrary;
import com.epopcon.advatar.common.config.Config;
import com.epopcon.advatar.common.model.OnlineProductInfo;
import com.epopcon.advatar.common.network.RequestListener;
import com.epopcon.advatar.common.network.rest.RestAdvatarProtocol;
import com.epopcon.advatar.common.util.SharedPreferenceBase;
import com.epopcon.advatar.common.util.Utils;
import com.epopcon.extra.online.model.CartDetail;
import com.epopcon.extra.online.model.OrderDetail;
import com.epopcon.extra.online.model.ProductDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

public class MessageDao extends Observable {
    private static final String TAG = MessageDao.class.getSimpleName();

    private static MessageDao instance = null;
    private DBHelper dbHelper;
    private static SQLiteDatabase database = null;

    public SQLiteDatabase db() {
        return database;
    }

    private MessageDao() {
        dbHelper = DBHelper.getInstance(CommonLibrary.getContext());
        database = dbHelper.getWritableDatabase();
    }

    /**
     * MessageDao 생성자
     */
    public static synchronized MessageDao getInstance() {
        if (instance == null) {
            synchronized (MessageDao.class) {
                if (instance == null) {
                    instance = new MessageDao();
                }
            }
        }
        return instance;
    }

    /**
     * 온라인 상점의 구매 상품 데이터를 반환한다.
     *
     * @param encUserIdList
     * @param sDate
     * @param eDate
     * @param limitCnt
     * @return
     */
    public List<OnlineProductInfo> getOnlineStoreProductDetails(String encUserIdList, int sDate, int eDate, int limitCnt) {
        String whereDateStr = "";
        if (sDate > 0)
            whereDateStr = String.format("AND S.order_date >= %d ", sDate);
        if (eDate > 0)
            whereDateStr += String.format("AND S.order_date <= %d ", eDate);

        String query;
        query = "SELECT S.order_number, S.order_date, S.total_amount, S.refund_amount, P.* " +
                "FROM " +
                "online_store S " +
                "INNER JOIN " +
                "online_store_product P " +
                "ON s.order_number = P.order_number " +
                "AND S.store_name = P.store_name " +
                "WHERE " +
                "S.enc_user_id in (" + encUserIdList + ") " +
                whereDateStr +
                "GROUP BY S.order_number, P.store_name, P.price, P.status " +
                "ORDER BY S.order_date DESC, P.id DESC ";

        if (limitCnt > 0)
            query += "LIMIT " + limitCnt;

        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            List<OnlineProductInfo> onlineProductInfoList = new ArrayList<>();
            while (cursor.moveToNext()) {
                OnlineProductInfo onlineProductInfo = new OnlineProductInfo();

                long id = cursor.getLong(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_ID));
                String storeName = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_STORE_NAME));
                String productUrl = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_URL));
                String noImageUrl = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_NO_IMAGE_URL));
                String productImageUrl = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_IMAGE_URL));
                String productName = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_PRODUCT_NAME));
                String productOption = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_PRODUCT_OPTION));
                String category = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_CATEGORY));
                int price = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_PRICE));
                int totalAmount = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStore.COLUMN_TOTAL_AMOUNT));
                int refundAmount = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStore.COLUMN_REFUND_AMOUNT));
                int quantity = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_QUANTITY));
                String seller = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_SELLER));
                String status = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_STATUS));

                onlineProductInfo.setId(id);
                onlineProductInfo.setOrderNumber(cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_ORDER_NUMBER)));
                onlineProductInfo.setStoreName(storeName);
                onlineProductInfo.setOrderDate(cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStore.COLUMN_ORDER_DATE)));
                onlineProductInfo.setProductUrl(productUrl);
                onlineProductInfo.setNoImageUrl(noImageUrl);
                onlineProductInfo.setProductImageUrl(productImageUrl);
                onlineProductInfo.setProductName(productName);
                onlineProductInfo.setProductOption(productOption);
                onlineProductInfo.setCategory(category);
                if (storeName.equals("COUPANG")) {
                    onlineProductInfo.setPrice(price * quantity);
                } else {
                    onlineProductInfo.setPrice(price);
                }
                onlineProductInfo.setTotalAmount(totalAmount);
                onlineProductInfo.setRefundAmount(refundAmount);
                onlineProductInfo.setQuantity(quantity);
                onlineProductInfo.setSeller(seller);
                onlineProductInfo.setStatus(status);

                onlineProductInfoList.add(onlineProductInfo);
            }
            return onlineProductInfoList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 온라인 상점의 일별 구매 상품 금액을 반환한다.
     *
     * @param encUserIdList
     * @param date
     * @return
     */
    public int getOnlineStoreProductDetailsDailyAmount(String encUserIdList, int date) {
        String query;
        //query = "SELECT SUM(P.price) AS totalExpense, P.status " +
        query = "SELECT DISTINCT S.total_amount AS price, S.order_number As orderNum, P.status AS status " +
                "FROM " +
                "online_store S " +
                "INNER JOIN " +
                "online_store_product P " +
                "ON S.order_number = P.order_number " +
                "AND S.store_name = P.store_name " +
                "WHERE " +
                "S.enc_user_id in (" + encUserIdList + ") " +
                String.format("AND S.order_date = %d ", date);

        int totalExpense = 0;
        int price = 0;
        String status = "";
        String orderNum = "";

        Cursor cursor = database.rawQuery(query, null);
        List<String> orderNumList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {

                status = cursor.getString(cursor.getColumnIndex("status"));
                orderNum = cursor.getString(cursor.getColumnIndex("orderNum"));

                if (status.contains("취소") || status.contains("환불") || status.contains("반품")) {
                    continue;
                } else {
                    if (!orderNumList.contains(orderNum)) {
                        orderNumList.add(orderNum);
                        price = cursor.getInt(cursor.getColumnIndex("price"));
                        totalExpense += price;
                    }
                }
            }
        }
        return totalExpense;
    }

    public int getOnlineStoreProductNaver(String productName, String orderNum, int productPrice) {
        String query;
        query = "SELECT * " +
                "FROM online_store_product " +
                "WHERE store_name = 'NAVER' " +
                "AND product_name = '" + productName + "' " +
                "AND order_number = '" + orderNum + "' " +
                "AND price = '" + productPrice + "'";

        Cursor cursor = database.rawQuery(query, null);
        int total = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int price = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreProduct.COLUMN_PRICE));
                total += price;
            }
        }
        return total;
    }

    /**
     * orderNumber로 구매 건의 금액 합을 반환한다.
     * @param encUserIdList
     * @param date
     * @return
     */
    public int getOnlineStoreProductDetailsProductAmount(String encUserIdList, String date) {
        String query;
        query = "SELECT DISTINCT S.total_amount AS price, S.order_number As orderNum, P.status AS status " +
                "FROM " +
                "online_store S " +
                "INNER JOIN " +
                "online_store_product P " +
                "ON S.order_number = P.order_number " +
                "AND S.store_name = P.store_name " +
                "WHERE " +
                "S.enc_user_id in (" + encUserIdList + ") " +
                String.format("AND S.order_number = '%s' ", date);

        int totalExpense = 0;
        int price = 0;
        String status = "";
        String orderNum = "";

        Cursor cursor = database.rawQuery(query, null);
        List<String> orderNumList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {

                status = cursor.getString(cursor.getColumnIndex("status"));
                orderNum = cursor.getString(cursor.getColumnIndex("orderNum"));

                if (status.contains("취소") || status.contains("환불") || status.contains("반품")) {
                    continue;
                } else {
                    if (!orderNumList.contains(orderNum)) {
                        orderNumList.add(orderNum);
                        price = cursor.getInt(cursor.getColumnIndex("price"));
                        totalExpense += price;
                    }
                }
            }
        }
        return totalExpense;
    }

    /**
     * 마지막 주문시간 조회
     *
     * @param storeName
     * @param encUserId
     * @return
     */
    public Long getLastOrderDateTime(String storeName, String encUserId) {
        String query = SqlBuilder.getInstance().getQueryString("select.online_store.last_order_date", storeName, encUserId);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Long orderDateTime = cursor.getLong(0);
                return orderDateTime;
            }
            cursor.close();
        }
        return -1L;
    }

    /**
     * 온라인 상점정보를 DB 에 저장한다.
     *
     * @param storeName
     * @param encUserId
     * @param orderDetail
     */
    public void insertOnlineStore(String storeName, String encUserId, OrderDetail orderDetail) {

        ContentValues values;

        Long id = null;
        String orderNumber = orderDetail.getOrderNumber();
        String orderDate = orderDetail.getOrderDate();
        int payAmount = orderDetail.getPayAmount();
        int refundAmount = orderDetail.getRefundAmount();
        int totalAmount = payAmount - refundAmount;
        boolean cancel = orderDetail.isCancel();

        String cancelYn;
        if (cancel) {
            cancelYn = "Y";
        } else {
            cancelYn = "N";
        }

        // 할인내역, 배송비 추가
        String discountDetail = "";
        int deliveryCost = -1;

        if (orderDetail.getDiscountDetail()!=null && !orderDetail.getDiscountDetail().equals("")) { // 할인내역 유무 체크
            discountDetail = orderDetail.getDiscountDetail();
        }

        if (orderDetail.getDeliveryCost() != -1) { // 배송비 유무 체크
            deliveryCost = orderDetail.getDeliveryCost();
        }

        try {
            String userId = SharedPreferenceBase.getPrefString(Utils.getApplicationContext(), Config.USER_ID, "");
            RestAdvatarProtocol.getInstance().onlineStorePurchaseList(userId, storeName, orderNumber, orderDate, Utils.parseDate(orderDate), totalAmount,
                    payAmount, refundAmount, cancelYn, discountDetail, deliveryCost, new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {

                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        Cursor cursor = database.rawQuery(String.format("SELECT %s FROM %s WHERE %s = '%s' AND %s = '%s'",
                DBHelper.DBOnlineStore.COLUMN_ID, DBHelper.DBOnlineStore.TABLE_ONLINE_STORE,
                DBHelper.DBOnlineStore.COLUMN_STORE_NAME, storeName,
                DBHelper.DBOnlineStore.COLUMN_ORDER_NUMBER, orderNumber), null);

        if (cursor != null) {
            if (cursor.moveToFirst())
                id = cursor.getLong(0);
            cursor.close();
        }

        boolean insert = (id == null);
        values = new ContentValues();

        // 취소건이 제대로 표시되지 않은 온라인 상점의 경우
        if (cancel || payAmount == -1) {
            if (!insert) {
                values.put(DBHelper.DBOnlineStore.COLUMN_CANCEL_YN, cancel);
                database.update(DBHelper.DBOnlineStore.TABLE_ONLINE_STORE, values, String.format("%s = %s", DBHelper.DBOnlineStore.COLUMN_ID, id), null);
                return;
            }
        } else {
            values.put(DBHelper.DBOnlineStore.COLUMN_ENC_USER_ID, encUserId);
            values.put(DBHelper.DBOnlineStore.COLUMN_ORDER_DATE, orderDate);
            values.put(DBHelper.DBOnlineStore.COLUMN_ORDER_DATETIME, Utils.parseDate(orderDate));
            values.put(DBHelper.DBOnlineStore.COLUMN_TOTAL_AMOUNT, totalAmount);
            values.put(DBHelper.DBOnlineStore.COLUMN_PAY_AMOUNT, payAmount);
            values.put(DBHelper.DBOnlineStore.COLUMN_REFUND_AMOUNT, refundAmount);
            values.put(DBHelper.DBOnlineStore.COLUMN_CANCEL_YN, cancel);
            values.put(DBHelper.DBOnlineStore.COLUMN_DISCOUNT_DETAIL,discountDetail);
            values.put(DBHelper.DBOnlineStore.COLUMN_DELIVERY_COST,deliveryCost);

            if (insert) {
                values.put(DBHelper.DBOnlineStore.COLUMN_STORE_NAME, storeName);
                values.put(DBHelper.DBOnlineStore.COLUMN_ORDER_NUMBER, orderNumber);

                id = database.insert(DBHelper.DBOnlineStore.TABLE_ONLINE_STORE, null, values);
            } else {
                database.update(DBHelper.DBOnlineStore.TABLE_ONLINE_STORE, values, String.format("%s = %s", DBHelper.DBOnlineStore.COLUMN_ID, id), null);
            }

            List<ProductDetail> productDetails = orderDetail.getProductDetails();

            int i = 1;
            if (productDetails.size() > 0) {

                database.delete(DBHelper.DBOnlineStoreProduct.TABLE_ONLINE_STORE_PRODUCT, String.format("%s = '%s' AND %s = '%s'",
                        DBHelper.DBOnlineStoreProduct.COLUMN_STORE_NAME, storeName,
                        DBHelper.DBOnlineStoreProduct.COLUMN_ORDER_NUMBER, orderNumber), null);

                for (ProductDetail productDetail : productDetails) {

                    values = new ContentValues();

                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_STORE_NAME, storeName);
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_ORDER_NUMBER, orderNumber);
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_SEQ, i++);
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_URL, productDetail.getProductUrl());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_NO_IMAGE_URL, productDetail.getNoImageUrl());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_IMAGE_URL, productDetail.getProductImageUrl());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_PRODUCT_NAME, productDetail.getProductName());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_PRODUCT_OPTION, productDetail.getProductOption());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_CATEGORY, productDetail.getCategory());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_PRICE, productDetail.getPrice());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_QUANTITY, productDetail.getQuantity());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_SELLER, productDetail.getSeller());
                    values.put(DBHelper.DBOnlineStoreProduct.COLUMN_STATUS, productDetail.getStatus());

                    try {
                        RestAdvatarProtocol.getInstance().onlineStoreProductList(storeName, orderNumber, productDetail.getCategory(), productDetail.getProductName(),
                                productDetail.getProductOption(), productDetail.getPrice(), productDetail.getQuantity(), productDetail.getProductUrl(),
                                productDetail.getProductImageUrl(), productDetail.getNoImageUrl(), productDetail.getSeller(), productDetail.getStatus(), new RequestListener() {
                                    @Override
                                    public void onRequestSuccess(int requestCode, Object result) {

                                    }

                                    @Override
                                    public void onRequestFailure(Throwable t) {

                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    database.insert(DBHelper.DBOnlineStoreProduct.TABLE_ONLINE_STORE_PRODUCT, null, values);
                }
            }
        }
    }

    public List<CartDetail> getOnlineStoreCartDetails() {

        String query;
        query = "SELECT * " +
                "FROM " +
                "online_store_cart " +
                "GROUP BY store_name, deal_url " +
                "ORDER BY store_name, cart_type ASC";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            List<CartDetail> cartDetails = new ArrayList<>();
            while (cursor.moveToNext()) {
                CartDetail cartDetail = new CartDetail();

                String storeName = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_STORE_NAME));
                String title = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_TITLE));
                String dealURl = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_DEAL_URL));
                int optionPrice = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_OPTION_PRICE));
                String promoTitle = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_PROMOTION_TITLE));
                String options = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_OPTIONS));
                String imgUrl = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_IMG_URL));
                int selectCount = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_SELECT_COUNT));
                int discount = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_DISCOUNT));
                int totalAmount = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_TOTAL_AMOUNT));
                String deliveryEnd = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_EXPECTED_DELIVERY_END_DATE));
                String deliveryPolicy = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_DELIVERY_POLICY));
                int deliveryIfAmount = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_DELIVERY_IF_AMOUNT));
                int deliveryAmount = cursor.getInt(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_DELIVERY_AMOUNT));
                double avgDeliveryDays = cursor.getDouble(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_AVG_DELIVERY_DAYS));
                String sellerInfo = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_SELLER_INFO));
                String cartType = cursor.getString(cursor.getColumnIndex(DBHelper.DBOnlineStoreCart.COLUMN_CART_TYPE));

                cartDetail.setStoreName(storeName);
                cartDetail.setTitle(title);
                cartDetail.setDealUrl(dealURl);
                cartDetail.setOptionPrice(optionPrice);
                cartDetail.setPromoTitle(promoTitle);
                cartDetail.setOptions(options);
                cartDetail.setImgUrl(imgUrl);
                cartDetail.setSelectCount(selectCount);
                cartDetail.setDiscount(discount);
                cartDetail.setTotalAmount(totalAmount);
                cartDetail.setExpectedDeliveryEndDate(deliveryEnd);
                cartDetail.setDeliveryPolicy(deliveryPolicy);
                cartDetail.setDeliveryIfAmount(deliveryIfAmount);
                cartDetail.setDeliveryAmount(deliveryAmount);
                cartDetail.setAvgDeliveryDays(avgDeliveryDays);
                cartDetail.setSellerInfo(sellerInfo);
                cartDetail.setCartType(cartType);

                cartDetails.add(cartDetail);
            }
            return cartDetails;
        }
        return Collections.EMPTY_LIST;
    }

    public int getOnlineStoreCartCount(String type) {
        int cartCount = 0;

        String query;
        query = "SELECT COUNT(*) AS COUNT " +
                "FROM " +
                "online_store_cart " +
                "WHERE cart_type = '" + type + "'";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cartCount = cursor.getInt(cursor.getColumnIndex("COUNT"));
            }
            cursor.close();
        }

        return cartCount;
    }

    public void insertOnlineCart(String storeName, CartDetail cartDetail) {

        ContentValues values;
        Long id = null;

        Cursor cursor = database.rawQuery(String.format("SELECT %s FROM %s WHERE %s = '%s' AND %s = '%s'",
                DBHelper.DBOnlineStoreCart.COLUMN_ID, DBHelper.DBOnlineStoreCart.TABLE_ONLINE_STORE_CART,
                DBHelper.DBOnlineStoreCart.COLUMN_STORE_NAME, storeName,
                DBHelper.DBOnlineStoreCart.COLUMN_DEAL_URL, cartDetail.getDealUrl()), null);

        if (cursor != null) {
            if (cursor.moveToFirst())
                id = cursor.getLong(0);
            cursor.close();
        }

        String userId = SharedPreferenceBase.getPrefString(Utils.getApplicationContext(), Config.USER_ID, "");
        try {
            RestAdvatarProtocol.getInstance().onlineStoreCartList(userId, storeName, cartDetail.getTitle(), cartDetail.getDealUrl(), cartDetail.getOptionPrice(), cartDetail.getPromoTitle(),
                    cartDetail.getOptions(), cartDetail.getImgUrl(), cartDetail.getSelectCount(), cartDetail.getDiscount(), cartDetail.getTotalAmount(),
                    cartDetail.getExpectedDeliveryEndDate(), cartDetail.getDeliveryPolicy(), cartDetail.getDeliveryIfAmount(), cartDetail.getDeliveryAmount(),
                    cartDetail.getAvgDeliveryDays(), cartDetail.getSellerInfo(), cartDetail.getCartType(), new RequestListener() {
                        @Override
                        public void onRequestSuccess(int requestCode, Object result) {

                        }

                        @Override
                        public void onRequestFailure(Throwable t) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean insert = (id == null);
        values = new ContentValues();

        values.put(DBHelper.DBOnlineStoreCart.COLUMN_TITLE, cartDetail.getTitle());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_OPTION_PRICE, cartDetail.getOptionPrice());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_PROMOTION_TITLE, cartDetail.getPromoTitle());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_OPTIONS, cartDetail.getOptions());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_IMG_URL, cartDetail.getImgUrl());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_SELECT_COUNT, cartDetail.getSelectCount());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_DISCOUNT, cartDetail.getDiscount());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_TOTAL_AMOUNT, cartDetail.getTotalAmount());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_EXPECTED_DELIVERY_END_DATE, cartDetail.getExpectedDeliveryEndDate());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_DELIVERY_POLICY, cartDetail.getDeliveryPolicy());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_DELIVERY_IF_AMOUNT, cartDetail.getDeliveryIfAmount());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_DELIVERY_AMOUNT, cartDetail.getDeliveryAmount());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_AVG_DELIVERY_DAYS, cartDetail.getAvgDeliveryDays());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_SELLER_INFO, cartDetail.getSellerInfo());
        values.put(DBHelper.DBOnlineStoreCart.COLUMN_CART_TYPE, cartDetail.getCartType());

        if (insert) {
            values.put(DBHelper.DBOnlineStoreCart.COLUMN_STORE_NAME, storeName);
            values.put(DBHelper.DBOnlineStoreCart.COLUMN_DEAL_URL, cartDetail.getDealUrl());

            database.insert(DBHelper.DBOnlineStoreCart.TABLE_ONLINE_STORE_CART, null, values);
        } else {
            database.update(DBHelper.DBOnlineStoreCart.TABLE_ONLINE_STORE_CART, values, String.format("%s = %s", DBHelper.DBOnlineStore.COLUMN_ID, id), null);
        }

    }

    /**
     * 온라인쇼핑몰 연동해제시 데이터 삭제
     * @param storeName
     */
    public void deleteOnlineStore(String storeName) {

        String where = DBHelper.DBOnlineStore.COLUMN_STORE_NAME + " = '" + storeName + "'";
        String productWhere = DBHelper.DBOnlineStoreProduct.COLUMN_STORE_NAME + " = '" + storeName + "'";
        String cartWhere = DBHelper.DBOnlineStoreCart.COLUMN_STORE_NAME + " = '" + storeName + "'";
        database.delete(DBHelper.DBOnlineStoreProduct.TABLE_ONLINE_STORE_PRODUCT, productWhere, null);
        database.delete(DBHelper.DBOnlineStore.TABLE_ONLINE_STORE, where, null);
        database.delete(DBHelper.DBOnlineStoreCart.TABLE_ONLINE_STORE_CART, cartWhere, null);
        setChanged();
        notifyObservers(null);
    }
}

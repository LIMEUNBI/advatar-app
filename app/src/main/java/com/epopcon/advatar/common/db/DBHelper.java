package com.epopcon.advatar.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "advatar.db";

    /**
     * 데이터베이스 버전
     */
    public static final int DATABASE_VERSION = 1;
    private static DBHelper instance;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    public static class DBOnlineStore {

        public static final String TABLE_ONLINE_STORE = "online_store";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_STORE_NAME = "store_name";
        public static final String COLUMN_ENC_USER_ID = "enc_user_id";
        public static final String COLUMN_ORDER_NUMBER = "order_number";
        public static final String COLUMN_ORDER_DATE = "order_date";
        public static final String COLUMN_ORDER_DATETIME = "order_date_time";
        public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
        public static final String COLUMN_PAY_AMOUNT = "pay_amount";
        public static final String COLUMN_REFUND_AMOUNT = "refund_amount";
        public static final String COLUMN_CANCEL_YN = "cancel_yn";
        public static final String COLUMN_DISCOUNT_DETAIL = "discount_detail";
        public static final String COLUMN_DELIVERY_COST = "delivery_cost";
    }

    public static class DBOnlineStoreProduct {

        public static final String TABLE_ONLINE_STORE_PRODUCT = "online_store_product";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_STORE_NAME = "store_name";
        public static final String COLUMN_ORDER_NUMBER = "order_number";
        public static final String COLUMN_SEQ = "seq";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_NO_IMAGE_URL = "no_image_url";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRODUCT_OPTION = "product_option";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SELLER = "seller";
        public static final String COLUMN_STATUS = "status";
    }

    public static class DBOnlineStoreCart {

        public static final String TABLE_ONLINE_STORE_CART = "online_store_cart";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_STORE_NAME = "store_name";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DEAL_URL = "deal_url";
        public static final String COLUMN_OPTION_PRICE = "option_price";
        public static final String COLUMN_PROMOTION_TITLE = "promotion_title";
        public static final String COLUMN_OPTIONS = "options";
        public static final String COLUMN_IMG_URL = "img_url";
        public static final String COLUMN_SELECT_COUNT = "select_count";
        public static final String COLUMN_DISCOUNT = "discount";
        public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
        public static final String COLUMN_EXPECTED_DELIVERY_END_DATE = "expected_delivery_end_date";
        public static final String COLUMN_DELIVERY_POLICY = "delivery_policy";
        public static final String COLUMN_DELIVERY_IF_AMOUNT = "delivery_if_amount";
        public static final String COLUMN_DELIVERY_AMOUNT = "delivery_amount";
        public static final String COLUMN_AVG_DELIVERY_DAYS = "avg_delivery_days";
        public static final String COLUMN_SELLER_INFO = "seller_info";
        public static final String COLUMN_CART_TYPE = "cart_type";
        public static final String COLUMN_REG_DT = "reg_dt";

    }

    private void createOnlineStoreTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DBOnlineStore.TABLE_ONLINE_STORE + " (" +
                DBOnlineStore.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBOnlineStore.COLUMN_STORE_NAME + " TEXT NOT NULL, " +
                DBOnlineStore.COLUMN_ORDER_NUMBER + " TEXT NOT NULL, " +
                DBOnlineStore.COLUMN_ENC_USER_ID + " TEXT NOT NULL, " +
                DBOnlineStore.COLUMN_ORDER_DATE + " INTEGER NOT NULL," +
                DBOnlineStore.COLUMN_ORDER_DATETIME + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_TOTAL_AMOUNT + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_PAY_AMOUNT + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_REFUND_AMOUNT + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_CANCEL_YN + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_DISCOUNT_DETAIL + " TEXT, " +
                DBOnlineStore.COLUMN_DELIVERY_COST + " INTEGER, " +
                "UNIQUE (" + DBOnlineStore.COLUMN_STORE_NAME + ", " + DBOnlineStore.COLUMN_ORDER_NUMBER + ") ON CONFLICT REPLACE " +
                ");");
    }

    private void createOnlineStoreProductTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DBOnlineStoreProduct.TABLE_ONLINE_STORE_PRODUCT + " (" +
                DBOnlineStoreProduct.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBOnlineStoreProduct.COLUMN_STORE_NAME + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_ORDER_NUMBER + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_SEQ + " INTEGER NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_CATEGORY + " TEXT, " +
                DBOnlineStoreProduct.COLUMN_URL + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_NO_IMAGE_URL + " TEXT, " +
                DBOnlineStoreProduct.COLUMN_IMAGE_URL + " TEXT, " +
                DBOnlineStoreProduct.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_PRODUCT_OPTION + " TEXT, " +
                DBOnlineStoreProduct.COLUMN_PRICE + " INTEGER NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_SELLER + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_STATUS + " TEXT NOT NULL, " +
                "UNIQUE (" + DBOnlineStoreProduct.COLUMN_STORE_NAME + ", " + DBOnlineStoreProduct.COLUMN_ORDER_NUMBER + ", " + DBOnlineStoreProduct.COLUMN_SEQ + ") ON CONFLICT REPLACE " +
                ");");
    }

    private void createOnlineStoreCartTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DBOnlineStoreCart.TABLE_ONLINE_STORE_CART + " (" +
                DBOnlineStoreCart.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBOnlineStoreCart.COLUMN_STORE_NAME + " TEXT NOT NULL, " +
                DBOnlineStoreCart.COLUMN_TITLE + " TEXT, " +
                DBOnlineStoreCart.COLUMN_DEAL_URL + " TEXT, " +
                DBOnlineStoreCart.COLUMN_OPTION_PRICE + " INTEGER, " +
                DBOnlineStoreCart.COLUMN_PROMOTION_TITLE + " TEXT, " +
                DBOnlineStoreCart.COLUMN_OPTIONS + " TEXT, " +
                DBOnlineStoreCart.COLUMN_IMG_URL + " TEXT NOT NULL, " +
                DBOnlineStoreCart.COLUMN_SELECT_COUNT + " INTEGER, " +
                DBOnlineStoreCart.COLUMN_DISCOUNT + " INTEGER, " +
                DBOnlineStoreCart.COLUMN_TOTAL_AMOUNT + " INTEGER, " +
                DBOnlineStoreCart.COLUMN_EXPECTED_DELIVERY_END_DATE + " TEXT, " +
                DBOnlineStoreCart.COLUMN_DELIVERY_POLICY + " TEXT, " +
                DBOnlineStoreCart.COLUMN_DELIVERY_IF_AMOUNT + " INTEGER, " +
                DBOnlineStoreCart.COLUMN_DELIVERY_AMOUNT + " INTEGER, " +
                DBOnlineStoreCart.COLUMN_AVG_DELIVERY_DAYS + " DOUBLE, " +
                DBOnlineStoreCart.COLUMN_SELLER_INFO + " TEXT, " +
                DBOnlineStoreCart.COLUMN_CART_TYPE + " TEXT, " +
                DBOnlineStoreCart.COLUMN_REG_DT + " datetime, " +
                "UNIQUE (" + DBOnlineStoreCart.COLUMN_STORE_NAME + ", " + DBOnlineStoreCart.COLUMN_DEAL_URL + ") ON CONFLICT REPLACE " +
                ");");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createOnlineStoreTable(database);
        createOnlineStoreProductTable(database);
        createOnlineStoreCartTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {

    }
}

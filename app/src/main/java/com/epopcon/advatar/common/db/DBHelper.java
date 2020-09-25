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
        public static final String COLUMN_STORE_NAME = "name";
        public static final String COLUMN_ENC_USER_ID = "enc_user_id";
        public static final String COLUMN_ORDER_NUMBER = "order_number";
        public static final String COLUMN_ORDER_DATE = "order_date";
        public static final String COLUMN_ORDER_DATETIME = "order_date_time";
        public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
        public static final String COLUMN_PAY_AMOUNT = "pay_amount";
        public static final String COLUMN_REFUND_AMOUNT = "refund_amount";
        public static final String COLUMN_CANCEL_YN = "cancel_yn";
        public static final String COLUMN_PAYMENT_QUERY_STRING = "payment_query_string";
        public static final String COLUMN_DISCOUNT_DETAIL = "discount_detail";
        public static final String COLUMN_DELIVERY_COST = "delivery_cost";
    }

    public static class DBOnlineStoreProduct {

        public static final String TABLE_ONLINE_STORE_PRODUCT = "online_store_product";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_STORE_NAME = "store_name";
        public static final String COLUMN_ORDER_NUMBER = "order_number";
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
                DBOnlineStore.COLUMN_PAYMENT_QUERY_STRING + " TEXT, " +
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
                "UNIQUE (" + DBOnlineStoreProduct.COLUMN_STORE_NAME + ", " + DBOnlineStoreProduct.COLUMN_ORDER_NUMBER + ") ON CONFLICT REPLACE " +
                ");");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createOnlineStoreTable(database);
        createOnlineStoreProductTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {

    }
}

package com.epopcon.advatar.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.epopcon.advatar.common.db.DBHelper.DBOnlineStore.COLUMN_REPORT_FAIL_COUNT;
import static com.epopcon.advatar.common.db.DBHelper.DBOnlineStore.TABLE_ONLINE_STORE;

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
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ENC_USER_ID = "enc_user_id";
        public static final String COLUMN_ORDER_NUMBER = "order_number";
        public static final String COLUMN_ORDER_DATE = "order_date";
        public static final String COLUMN_ORDER_DATETIME = "order_date_time";
        public static final String COLUMN_CARD_NAME = "card_name";
        public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
        public static final String COLUMN_PAY_AMOUNT = "pay_amount";
        public static final String COLUMN_REFUND_AMOUNT = "refund_amount";
        public static final String COLUMN_CANCEL_YN = "cancel_yn";
        public static final String COLUMN_REPORT_YN = "report_yn";
        public static final String COLUMN_REPORT_FAIL_COUNT = "report_fail_count";
        public static final String COLUMN_PAYMENT_QUERY_STRING = "payment_query_string";
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
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_OPTION = "option";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SELLER = "seller";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TRACKING_QUERY_STRING = "tracking_query_string";
        public static final String COLUMN_PARCEL_CODE = "parcel_code";
        public static final String COLUMN_INVOICE = "invoice";
    }

    public static class DBOnlineStoreMapping {

        public static final String TABLE_ONLINE_STORE_MAPPING = "online_store_mapping";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ONLINE_STORE_ID = "online_store_id";
        public static final String COLUMN_ORIGIN_CODE = "origin_code";
        public static final String COLUMN_ORIGIN_ID = "origin_id";
        public static final String COLUMN_ENC_USER_ID = "enc_user_id";
        public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
        public static final String COLUMN_MAIN_YN = "main_yn";
    }

    private void createOnlineStoreTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ONLINE_STORE + " (" +
                DBOnlineStore.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBOnlineStore.COLUMN_NAME + " TEXT NOT NULL, " +
                DBOnlineStore.COLUMN_ORDER_NUMBER + " TEXT NOT NULL, " +
                DBOnlineStore.COLUMN_ENC_USER_ID + " TEXT NOT NULL, " +
                DBOnlineStore.COLUMN_ORDER_DATE + " INTEGER NOT NULL," +
                DBOnlineStore.COLUMN_ORDER_DATETIME + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_CARD_NAME + " TEXT, " +
                DBOnlineStore.COLUMN_TOTAL_AMOUNT + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_PAY_AMOUNT + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_REFUND_AMOUNT + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_CANCEL_YN + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_REPORT_YN + " INTEGER NOT NULL, " +
                COLUMN_REPORT_FAIL_COUNT + " INTEGER NOT NULL, " +
                DBOnlineStore.COLUMN_PAYMENT_QUERY_STRING + " TEXT NULL, " +
                DBOnlineStore.COLUMN_DISCOUNT_DETAIL + " TEXT NULL, " +
                DBOnlineStore.COLUMN_DELIVERY_COST + " INTEGER NULL, " +
                "UNIQUE (" + DBOnlineStore.COLUMN_NAME + ", " + DBOnlineStore.COLUMN_ORDER_NUMBER + ") ON CONFLICT REPLACE " +
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
                DBOnlineStoreProduct.COLUMN_NAME + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_OPTION + " TEXT, " +
                DBOnlineStoreProduct.COLUMN_PRICE + " INTEGER NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_SELLER + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_STATUS + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_TRACKING_QUERY_STRING + " TEXT NOT NULL, " +
                DBOnlineStoreProduct.COLUMN_PARCEL_CODE + " INTEGER NULL, " +
                DBOnlineStoreProduct.COLUMN_INVOICE + " TEXT NULL, " +
                "UNIQUE (" + DBOnlineStoreProduct.COLUMN_STORE_NAME + ", " + DBOnlineStoreProduct.COLUMN_ORDER_NUMBER + ", " + DBOnlineStoreProduct.COLUMN_SEQ + ") ON CONFLICT REPLACE " +
                ");");
    }

    private void createOnlineStoreMappingTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DBOnlineStoreMapping.TABLE_ONLINE_STORE_MAPPING);
        db.execSQL("CREATE TABLE " + DBOnlineStoreMapping.TABLE_ONLINE_STORE_MAPPING + " (" +
                DBOnlineStoreMapping.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBOnlineStoreMapping.COLUMN_ONLINE_STORE_ID + " INTEGER NOT NULL," +
                DBOnlineStoreMapping.COLUMN_ORIGIN_CODE + " INTEGER NOT NULL, " +
                DBOnlineStoreMapping.COLUMN_ORIGIN_ID + " INTEGER NOT NULL, " +
                DBOnlineStoreMapping.COLUMN_ENC_USER_ID + " TEXT NOT NULL, " +
                DBOnlineStoreMapping.COLUMN_TOTAL_AMOUNT + " INTEGER NOT NULL, " +
                DBOnlineStoreMapping.COLUMN_MAIN_YN + " INTEGER NOT NULL, " +
                "UNIQUE (" + DBOnlineStoreMapping.COLUMN_ONLINE_STORE_ID + ", " + DBOnlineStoreMapping.COLUMN_ORIGIN_CODE + ", " + DBOnlineStoreMapping.COLUMN_ORIGIN_ID + ") ON CONFLICT IGNORE " +
                ");");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createOnlineStoreTable(database);
        createOnlineStoreProductTable(database);
        createOnlineStoreMappingTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {

    }
}

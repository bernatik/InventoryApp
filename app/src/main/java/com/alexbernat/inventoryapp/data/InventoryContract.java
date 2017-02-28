package com.alexbernat.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Александр on 28.02.2017.
 */

public final class InventoryContract {

    public static final String AUTHORITY = "com.alexbernat.inventoryapp";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String URI_PATH = "inventory";

    private InventoryContract() {

    }

    public static class InventoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "inventory";
        public static final String COLUMN_NAME_ID = BaseColumns._ID;
        public static final String COLUMN_NAME_PRODUCT_NAME = "name";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_IMAGE = "image";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, URI_PATH);

        public static final String MIME_PATH = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + AUTHORITY + "/" + URI_PATH;
        public static final String MIME_SINGLE_ROW = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + AUTHORITY + "/" + URI_PATH;

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME_PRODUCT_NAME + " TEXT NOT NULL, " +
                        COLUMN_NAME_QUANTITY + " INTEGER NOT NULL, " +
                        COLUMN_NAME_PRICE + " DOUBLE NOT NULL, " +
                        COLUMN_NAME_IMAGE + " BLOB);";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }
}

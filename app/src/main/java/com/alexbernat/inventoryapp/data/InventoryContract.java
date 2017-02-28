package com.alexbernat.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Александр on 28.02.2017.
 */

public final class InventoryContract {

    private InventoryContract() {

    }

    public static class InventoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "inventory";
        public static final String COLUMN_NAME_ID = BaseColumns._ID;
        public static final String COLUMN_NAME_PRODUCT_NAME = "name";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_IMAGE = "image";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                        COLUMN_NAME_PRODUCT_NAME + " TEXT NOT NULL, " +
                        COLUMN_NAME_QUANTITY + " INTEGER NOT NULL, " +
                        COLUMN_NAME_PRICE + " FLOAT NOT NULL, " +
                        COLUMN_NAME_IMAGE + " BLOB)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }
}

package com.alexbernat.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alexbernat.inventoryapp.data.InventoryContract;

/**
 * Created by Александр on 28.02.2017.
 */

public class InventoryAdapter extends CursorAdapter {

    public static final int SALE_INCREMENT = 1;

    public InventoryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.catalog_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        String productName = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY));
        double price = cursor.getDouble(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE));
        TextView tvName = (TextView) view.findViewById(R.id.product_name);
        TextView tvQuantity = (TextView) view.findViewById(R.id.quantity);
        TextView tvPrice = (TextView) view.findViewById(R.id.price);
        tvName.setText(productName);
        tvQuantity.setText(String.valueOf(quantity));
        tvPrice.setText(String.valueOf(price));

        Button btnSale = (Button) view.findViewById(R.id.sale_button);
        btnSale.setTag(cursor.getPosition());
        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition((int) v.getTag());
                String productName = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME));
                long id = cursor.getLong(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_ID));
                int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY));
                quantity = quantity - 1;
                ContentValues values = new ContentValues();
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, quantity);
                Uri currentUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                int rowsUpdated = context.getContentResolver().update(currentUri, values, null, null);
                if (rowsUpdated != 0) {
                    String message = context.getString(R.string.toast_sale_successful, SALE_INCREMENT, productName);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

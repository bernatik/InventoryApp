package com.alexbernat.inventoryapp;

import android.content.Context;
import android.database.Cursor;
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

    public InventoryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.catalog_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
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
        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "I clicked Sale button", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

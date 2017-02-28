package com.alexbernat.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alexbernat.inventoryapp.data.InventoryContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText etName, etQuantity, etPrice, etChange;
    private Button btnSale, btnShipment, btnSave, btnOrder, btnDelete;
    private ImageView ivPhoto;
    private boolean isAddMode = false;
    private Uri contentUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        contentUri = getIntent().getData();
        if (contentUri.equals(InventoryContract.InventoryEntry.CONTENT_URI)) {
            setTitle(getString(R.string.detail_activity_name_add));
            isAddMode = true;
        } else {
            setTitle(getString(R.string.detail_activity_name_edit));
            isAddMode = false;
        }

        etName = (EditText) findViewById(R.id.detail_product_name);
        etQuantity = (EditText) findViewById(R.id.detail_quantity);
        etPrice = (EditText) findViewById(R.id.detail_edit_price);
        etChange = (EditText) findViewById(R.id.detail_edit_quantity);
        btnSale = (Button) findViewById(R.id.detail_button_sale);
        btnShipment = (Button) findViewById(R.id.detail_button_shipment);
        btnSave = (Button) findViewById(R.id.detail_button_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddMode) {
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME, etName.getText().toString());
                    values.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, Integer.parseInt(etQuantity.getText().toString()));
                    values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE, Double.parseDouble(etPrice.getText().toString()));
                    getContentResolver().insert(contentUri, values);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Aga", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnOrder = (Button) findViewById(R.id.detail_button_order);
        btnDelete = (Button) findViewById(R.id.detail_button_delete);
        ivPhoto = (ImageView) findViewById(R.id.detail_image_product);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

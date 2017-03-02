package com.alexbernat.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alexbernat.inventoryapp.data.InventoryContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_CONTENT_URI = "content Uri";
    public static final String TAG_DELETE_DIALOG = "deleteDialog";
    private static final int LOADER_ID = 2;
    private EditText etName, etQuantity, etPrice, etChange;
    private Button btnSale, btnReceive, btnSave, btnOrder, btnDelete;
    private ImageView ivPhoto;
    private boolean isAddMode = false;
    private Uri contentUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        etName = (EditText) findViewById(R.id.detail_product_name);
        etQuantity = (EditText) findViewById(R.id.detail_quantity);
        etPrice = (EditText) findViewById(R.id.detail_edit_price);
        etChange = (EditText) findViewById(R.id.detail_edit_quantity);
        btnSale = (Button) findViewById(R.id.detail_button_sale);
        btnReceive = (Button) findViewById(R.id.detail_button_shipment);
        btnSave = (Button) findViewById(R.id.detail_button_save);
        btnOrder = (Button) findViewById(R.id.detail_button_order);
        btnDelete = (Button) findViewById(R.id.detail_button_delete);
        ivPhoto = (ImageView) findViewById(R.id.detail_image_product);

        contentUri = getIntent().getData();
        if (contentUri.equals(InventoryContract.InventoryEntry.CONTENT_URI)) {
            setTitle(getString(R.string.detail_activity_name_add));
            etChange.setVisibility(View.GONE);
            btnSale.setVisibility(View.GONE);
            btnReceive.setVisibility(View.GONE);
            btnDelete.setText(R.string.detail_button_clear);
            isAddMode = true;
        } else {
            setTitle(getString(R.string.detail_activity_name_edit));
            getLoaderManager().initLoader(LOADER_ID, null, this);
            isAddMode = false;
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddMode) {
                    ContentValues values = new ContentValues();
                    values = getCorrectValues(values);
                    if (values != null) {
                        Uri newItemUri = getContentResolver().insert(contentUri, values);
                        if (newItemUri == null) {
                            Toast.makeText(getApplicationContext(), R.string.toast_fail_create, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getApplicationContext(), R.string.toast_created, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    ContentValues values = new ContentValues();
                    values = getCorrectValues(values);
                    if (values != null) {
                        int rowsUpdated = getContentResolver().update(contentUri, values, null, null);
                        if (rowsUpdated == 0) {
                            Toast.makeText(getApplicationContext(), R.string.toast_fail_update, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.toast_updated, Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddMode) {
                    clearUI();
                } else {
                    DeleteDialogFragment dialogFragment = new DeleteDialogFragment();
                    Bundle args = new Bundle();
                    args.putString(KEY_CONTENT_URI, contentUri.toString());
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getSupportFragmentManager(), TAG_DELETE_DIALOG);
                }
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etName.getText())) {
                    Toast.makeText(getApplicationContext(), R.string.toast_enter_name, Toast.LENGTH_SHORT).show();
                } else {
                    String emailBody = getString(R.string.order_body, etName.getText());
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_subject) + etName.getText());
                    intent.putExtra(Intent.EXTRA_TEXT, emailBody);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.toast_fail_order, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = parseQuantity();
                int numberOfItemsToSale = Integer.parseInt(etChange.getText().toString());
                quantity = quantity - numberOfItemsToSale;
                if (quantity < 0) {
                    Toast.makeText(getApplicationContext(), R.string.toast_sale_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                etQuantity.setText(String.valueOf(quantity));
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = parseQuantity();
                int numberOfItemsToGet = Integer.parseInt(etChange.getText().toString());
                quantity = quantity + numberOfItemsToGet;
                etQuantity.setText(String.valueOf(quantity));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry.COLUMN_NAME_ID,
                InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_NAME_PRICE};
        return new CursorLoader(this, contentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            etName.setText(data.getString(data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME)));
            etQuantity.setText(String.valueOf(data.getInt(data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY))));
            etPrice.setText(String.valueOf(data.getDouble(data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE))));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clearUI();
    }

    private void clearUI() {
        etName.setText("");
        etQuantity.setText("");
        etPrice.setText("");
    }

    private ContentValues getCorrectValues(ContentValues values) {
        if (TextUtils.isEmpty(etName.getText()) || TextUtils.isEmpty(etQuantity.getText()) || TextUtils.isEmpty(etPrice.getText())) {
            Toast.makeText(getApplicationContext(), R.string.toast_check_inputs, Toast.LENGTH_SHORT).show();
            values = null;
        } else {
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME, etName.getText().toString());
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY, Integer.parseInt(etQuantity.getText().toString()));
            values.put(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE, Double.parseDouble(etPrice.getText().toString()));
        }
        return values;
    }

    private int parseQuantity() {
        if (TextUtils.isEmpty(etChange.getText())) {
            etChange.setText(String.valueOf(1));
        }
        return Integer.parseInt(etQuantity.getText().toString());
    }

}

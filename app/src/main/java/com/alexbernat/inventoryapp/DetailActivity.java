package com.alexbernat.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alexbernat.inventoryapp.data.InventoryContract;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_CONTENT_URI = "content Uri";
    public static final String KEY_PRODUCT_NAME = "productName";
    public static final String TAG_DELETE_DIALOG = "deleteDialog";
    public static final String TAG_ORDER_DIALOG = "orderDialog";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOADER_ID = 2;
    private EditText etName, etQuantity, etPrice, etChange;
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
        Button btnSale = (Button) findViewById(R.id.detail_button_sale);
        Button btnReceive = (Button) findViewById(R.id.detail_button_shipment);
        Button btnSave = (Button) findViewById(R.id.detail_button_save);
        Button btnOrder = (Button) findViewById(R.id.detail_button_order);
        Button btnDelete = (Button) findViewById(R.id.detail_button_delete);
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
                    dialogFragment.show(getFragmentManager(), TAG_DELETE_DIALOG);
                }
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etName.getText())) {
                    Toast.makeText(getApplicationContext(), R.string.toast_enter_name, Toast.LENGTH_SHORT).show();
                } else {
                    OrderDialogFragment dialogFragment = new OrderDialogFragment();
                    Bundle args = new Bundle();
                    args.putString(KEY_PRODUCT_NAME, etName.getText().toString());
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getFragmentManager(), TAG_ORDER_DIALOG);
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

        /**Trying to make photo
         * at first we check if camera is available, then make an intent
         */
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (photoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photoBitmap = (Bitmap) extras.get("data");
            ivPhoto.setImageBitmap(photoBitmap);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry.COLUMN_NAME_ID,
                InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_NAME_PRICE,
                InventoryContract.InventoryEntry.COLUMN_NAME_IMAGE};
        return new CursorLoader(this, contentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            etName.setText(data.getString(data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME)));
            etQuantity.setText(String.valueOf(data.getInt(data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY))));
            etPrice.setText(String.valueOf(data.getDouble(data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_PRICE))));
            ivPhoto.setImageBitmap(DbBitmapUtility.getImage(data.getBlob(data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME_IMAGE))));
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
            /*
            Now try to save picture into database
             */
            Bitmap imagePhoto = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
            try {
                values.put(InventoryContract.InventoryEntry.COLUMN_NAME_IMAGE, DbBitmapUtility.getBytes(imagePhoto));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

package com.alexbernat.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alexbernat.inventoryapp.data.InventoryContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        InventoryAdapter.InventoryAdapterOnClickHandler {

    /* indices for simple access to the database */
    public static final int INDEX_NAME_ID = 0;
    public static final int INDEX_PRODUCT_NAME = 1;
    public static final int INDEX_QUANTITY = 2;
    public static final int INDEX_PRICE = 3;
    public static final int INDEX_IMAGE = 4;
    private static final int LOADER_ID = 1;
    private InventoryAdapter adapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mRecyclerView = (RecyclerView) findViewById(R.id.catalog_list);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);
                intent.setData(InventoryContract.InventoryEntry.CONTENT_URI);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new InventoryAdapter(this, this);
        mRecyclerView.setAdapter(adapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry.COLUMN_NAME_ID,
                InventoryContract.InventoryEntry.COLUMN_NAME_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_NAME_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_NAME_PRICE,
                InventoryContract.InventoryEntry.COLUMN_NAME_IMAGE};
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this,
                        InventoryContract.InventoryEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onClick(int idOfProduct) {
        Uri uri = (InventoryContract.InventoryEntry.CONTENT_URI).buildUpon().appendPath(String.valueOf(idOfProduct)).build();
        Intent intent = new Intent(this, DetailActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }
}

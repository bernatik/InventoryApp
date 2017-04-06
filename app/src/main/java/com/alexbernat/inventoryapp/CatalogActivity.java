package com.alexbernat.inventoryapp;

import android.app.ActivityOptions;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexbernat.inventoryapp.data.InventoryContract;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.transitionName;

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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            //called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int idToDelete = (int) viewHolder.itemView.getTag();
                String itemToDelete = String.valueOf(idToDelete);
                /* create correct Uri to delete certain product */
                Uri uri = InventoryContract.InventoryEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(itemToDelete).build();
                getContentResolver().delete(uri, null, null);
                /* refresh data in recycler view */
                //adapter.notifyDataSetChanged();
                getLoaderManager().restartLoader(LOADER_ID, null, CatalogActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

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
    public void onClick(int idOfProduct, View view) {
        Uri uri = (InventoryContract.InventoryEntry.CONTENT_URI).buildUpon().appendPath(String.valueOf(idOfProduct)).build();
        Intent intent = new Intent(this, DetailActivity.class);
        intent.setData(uri);
        startActivity(intent, createTransitionAnimation(view));
        //startActivity(intent);
    }

    private Bundle createTransitionAnimation(View view) {
        /* take views that need to transit between activities */
        ImageView picture = (ImageView) view.findViewById(R.id.product_image);
        String transitionNamePicture = getString(R.string.picture_name);
        TextView name = (TextView) view.findViewById(R.id.product_name);
        String transititonNameProductName = getString(R.string.product_name);
        TextView quantity = (TextView) view.findViewById(R.id.quantity);
        String transitionNameQuantity = getString(R.string.quantity_name);
        TextView price = (TextView) view.findViewById(R.id.price);
        String transitionNamePrice = getString(R.string.price_name);

        /* create pairs of vies and transition names to transit appropriate views */
        ActivityOptions transitionOptions =
                ActivityOptions.makeSceneTransitionAnimation(this,
                        new Pair<View, String>(picture, transitionNamePicture),
                        new Pair<View, String>(name, transititonNameProductName),
                        new Pair<View, String>(quantity, transitionNameQuantity),
                        new Pair<View, String>(price, transitionNamePrice));

        return transitionOptions.toBundle();
    }
}

package com.alexbernat.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ListView listView = (ListView) findViewById(R.id.catalog_list);
        View emptyView = findViewById(R.id.empty_list_text);
        listView.setEmptyView(emptyView);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);
                intent.putExtra(getString(R.string.detail_activity_name_key), getString(R.string.detail_activity_name_add));
                startActivity(intent);
                return true;
            }
        });
    }


}

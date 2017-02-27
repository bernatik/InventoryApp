package com.alexbernat.inventoryapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        String title = getIntent().getStringExtra(getString(R.string.detail_activity_name_key));
        if (title.equals(getString(R.string.detail_activity_name_add))) {
            setTitle(title);
        } else {
            setTitle(getString(R.string.detail_activity_name_edit));
        }
    }
}

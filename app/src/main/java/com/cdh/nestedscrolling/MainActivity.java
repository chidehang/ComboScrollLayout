package com.cdh.nestedscrolling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openNestedScrollViewSample(View view) {
        startActivity(new Intent(this, NestedScrollViewActivity.class));
    }

    public void openNestedRecyclerViewSample(View view) {
        startActivity(new Intent(this, NestedRecyclerViewActivity.class));
    }

    public void openNestedViewPagerSample(View view) {
        startActivity(new Intent(this, NestedViewPagerActivity.class));
    }
}

package com.cdh.nestedscrolling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Toast;

import com.cdh.nestedscrolling.adapter.BannerRecyclerAdapter;
import com.cdh.nestedscrolling.adapter.SimpleRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class NestedRecyclerViewActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView banner;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_recycler_view);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(NestedRecyclerViewActivity.this, "refresh complete", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        });

        banner = findViewById(R.id.banner_view);
        banner.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        BannerRecyclerAdapter bannerAdapter = new BannerRecyclerAdapter(this, getBanner());
        banner.setAdapter(bannerAdapter);
        new PagerSnapHelper().attachToRecyclerView(banner);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SimpleRecyclerAdapter adapter = new SimpleRecyclerAdapter(this, getData());
        recyclerView.setAdapter(adapter);
    }

    private List<String> getBanner() {
        List<String> data = new ArrayList<>();
        data.add("#22dd99");
        data.add("#546e7a");
        data.add("#263238");
        return data;
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
        data.add("#ff9999");
        data.add("#ffaa77");
        data.add("#ff9966");
        data.add("#ffcc55");
        data.add("#ff99bb");
        data.add("#ff77dd");
        data.add("#ff33bb");
        data.add("#ff9999");
        data.add("#ffaa77");
        data.add("#ff9966");
        data.add("#ffcc55");
        return data;
    }
}

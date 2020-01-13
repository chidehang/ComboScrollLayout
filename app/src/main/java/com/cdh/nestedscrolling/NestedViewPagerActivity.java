package com.cdh.nestedscrolling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.Toast;

import com.cdh.nestedscrolling.adapter.BannerRecyclerAdapter;
import com.cdh.nestedscrolling.adapter.SimplePagerAdapter;
import com.cdh.nestedscrolling.fragment.LinearFragment;
import com.cdh.nestedscrolling.fragment.RecyclerViewFragment;
import com.cdh.nestedscrolling.fragment.ScrollViewFragment;
import com.cdh.nestedscrolling.widget.ComboSwipeRefreshLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class NestedViewPagerActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView banner;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_view_pager);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(NestedViewPagerActivity.this, "refresh complete", Toast.LENGTH_SHORT).show();
                    }
                }, 500);
            }
        });

        banner = findViewById(R.id.combo_top_view);
        banner.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        BannerRecyclerAdapter bannerAdapter = new BannerRecyclerAdapter(this, getBanner());
        banner.setAdapter(bannerAdapter);
        new PagerSnapHelper().attachToRecyclerView(banner);

        tabLayout = findViewById(R.id.tablayout);

        viewPager = findViewById(R.id.combo_content_view);
        SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(this, getPageFragments());
        viewPager.setAdapter(pagerAdapter);

        final String[] labels = new String[]{"linear", "scroll", "recycler"};
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(labels[position]);
            }
        }).attach();
    }

    private List<String> getBanner() {
        List<String> data = new ArrayList<>();
        data.add("#22dd99");
        data.add("#546e7a");
        data.add("#263238");
        return data;
    }

    private List<Fragment> getPageFragments() {
        List<Fragment> data = new ArrayList<>();
        data.add(new LinearFragment());
        data.add(new ScrollViewFragment());
        data.add(new RecyclerViewFragment());
        return data;
    }
}

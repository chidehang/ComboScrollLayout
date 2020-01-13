package com.cdh.nestedscrolling.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Created by chidehang on 2020-01-12
 */
public class SimplePagerAdapter extends FragmentStateAdapter {

    private List<Fragment> data;

    public SimplePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> data) {
        super(fragmentActivity);
        this.data = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

package com.superscholar.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 * 事件界面ViewPager适配器
 */

public class EventPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment>fragments;

    public EventPagerAdapter(FragmentManager manager,List<Fragment>list){
        super(manager);
        fragments=list;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

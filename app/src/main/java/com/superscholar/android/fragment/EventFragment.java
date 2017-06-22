package com.superscholar.android.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.superscholar.android.R;
import com.superscholar.android.adapter.EventPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhqy on 2017/6/14.
 */

public class EventFragment extends Fragment {
    private TabLayout tabLayout;
    private List<Fragment>fragments=new ArrayList<>();
    private ViewPager viewPager;

    private void initFragmentList(){
        fragments.add(new EventRecordFragment());
        fragments.add(new ConcentrationTimeFragment());
    }

    private void initTabLayout(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.event_tabLayout);

        TabLayout.Tab tab_record = tabLayout.newTab();
        tab_record.setText("事件记录");
        tabLayout.addTab(tab_record);

        TabLayout.Tab tab_concentration = tabLayout.newTab();
        tab_concentration.setText("专心时间");
        tabLayout.addTab(tab_concentration);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initViewPager(View view){
        viewPager = (ViewPager) view.findViewById(R.id.event_viewPager);

        viewPager.setAdapter(new EventPagerAdapter(getFragmentManager(),fragments));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_event,container,false);
        initFragmentList();
        initTabLayout(view);
        initViewPager(view);
        return view;
    }
}

package com.superscholar.android.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.superscholar.android.R;
import com.superscholar.android.adapter.EventPagerAdapter;
import com.superscholar.android.fragment.TargetCreateCheckFragment;
import com.superscholar.android.fragment.TargetCreateNormalFragment;

import java.util.ArrayList;
import java.util.List;

public class TargetCreateActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private List<Fragment> fragments=new ArrayList<>();
    private ViewPager viewPager;

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.target_create_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFragmentList(){
        fragments.add(new TargetCreateCheckFragment());
        fragments.add(new TargetCreateNormalFragment());
    }

    private void initTabLayout() {
        tabLayout = (TabLayout)findViewById(R.id.target_create_tabLayout);

        TabLayout.Tab tab_check = tabLayout.newTab();
        tab_check.setText("检测打卡");
        tabLayout.addTab(tab_check);

        TabLayout.Tab tab_normal = tabLayout.newTab();
        tab_normal.setText("普通打卡");
        tabLayout.addTab(tab_normal);

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

    private void initViewPager(){
        viewPager = (ViewPager)findViewById(R.id.target_create_viewPager);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_create);

        initToolbar();
        initFragmentList();
        initTabLayout();
        initViewPager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

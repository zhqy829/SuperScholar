package com.superscholar.android.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.TargetItem;
import com.superscholar.android.fragment.TargetCreateCheckFragment;
import com.superscholar.android.fragment.TargetCreateNormalFragment;

public class TargetUpdateActivity extends BaseActivity {

    private TargetItem targetItem;

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.target_update_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFrameLayout(){
        targetItem=getIntent().getParcelableExtra("targetItem");

        FragmentManager manager=getSupportFragmentManager();
        if(targetItem.isCheck()){
            TargetCreateCheckFragment fragment=new TargetCreateCheckFragment();
            Bundle args=new Bundle();
            args.putBoolean("isUpdate",true);
            args.putParcelable("targetItem",targetItem);
            fragment.setArguments(args);
            manager.beginTransaction()
                    .add(R.id.target_update_frameLayout,fragment)
                    .commit();
        }else{
            TargetCreateNormalFragment fragment=new TargetCreateNormalFragment();
            Bundle args=new Bundle();
            args.putBoolean("isUpdate",true);
            args.putParcelable("targetItem",targetItem);
            fragment.setArguments(args);
            manager.beginTransaction()
                    .add(R.id.target_update_frameLayout,fragment)
                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_update);

        initToolbar();
        initFrameLayout();
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

package com.superscholar.android.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.tools.ServerConnector;

public class AboutActivity extends BaseActivity {

    private String []aboutMsg={
            "版本号        "+ ServerConnector.VERSION_NAME,
            "官方QQ      *********",
            "官方微博    ******",
            "制作团队    超级无敌宇宙男神团",
            "团队人员    汉堡 阳神 高磊 浩东 吴洁"};

    private void initMenuList(){
        ListView listView=(ListView)findViewById(R.id.about_listView);
        ArrayAdapter adapter=new ArrayAdapter<String>(AboutActivity.this,android.R.layout.simple_list_item_1,aboutMsg);
        listView.setAdapter(adapter);
        listView.setEnabled(false);
    }

    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.about_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolbar();
        initMenuList();
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

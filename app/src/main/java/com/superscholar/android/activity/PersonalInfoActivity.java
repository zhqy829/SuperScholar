package com.superscholar.android.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;

public class PersonalInfoActivity extends BaseActivity {

    private String []menuItems={"绑定学号","改绑手机","修改密码"};
    private ListView menuList;

    private static final int REQUEST_SID=0;
    private static final int REQUEST_PHONE=1;

    private static final int RESULT_UPDATE_SID=2;
    private static final int RESULT_UPDATE_PHONE=3;
    private static final int RESULT_UPDATE_ALL=4;

    private boolean isUpdateSid=false;
    private boolean isUpdatePhone=false;

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.personal_info_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initMenuList(){
        menuList=(ListView)findViewById(R.id.personal_info_menuList);
        ArrayAdapter adapter=new ArrayAdapter<String>(PersonalInfoActivity.this,android.R.layout.simple_list_item_1,menuItems);
        menuList.setAdapter(adapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        Intent intent_0=new Intent(PersonalInfoActivity.this,BindSidActivity.class);
                        startActivityForResult(intent_0,REQUEST_SID);
                        break;
                    case 1:
                        Intent intent_1=new Intent(PersonalInfoActivity.this,BindPhoneActivity.class);
                        startActivityForResult(intent_1,REQUEST_PHONE);
                        break;
                    case 2:
                        Intent intent_2=new Intent(PersonalInfoActivity.this,ResetPasswordActivity.class);
                        startActivity(intent_2);
                        break;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_SID:
                if(resultCode==RESULT_OK){
                    isUpdateSid=true;
                    if(isUpdateSid&&isUpdatePhone){
                        setResult(RESULT_UPDATE_ALL);
                    }else{
                        setResult(RESULT_UPDATE_SID);
                    }
                }
                break;
            case REQUEST_PHONE:
                if(resultCode==RESULT_OK){
                    isUpdatePhone=true;
                    if(isUpdateSid&&isUpdatePhone){
                        setResult(RESULT_UPDATE_ALL);
                    }else{
                        setResult(RESULT_UPDATE_PHONE);
                    }
                }
                break;
            default:
        }
    }
}

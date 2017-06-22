package com.superscholar.android.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoCreateActivity extends BaseActivity {

    EditText titleEdit;
    EditText contentEdit;

    //输入框初始化
    private void initEditText(){
        Intent intent=getIntent();
        String content=intent.getStringExtra("content");
        String title=intent.getStringExtra("title");
        titleEdit.setText(title);
        contentEdit.setText(content);
    }

    //变量初始化
    private void initVariable(){
        titleEdit=(EditText)findViewById(R.id.memo_create_title);
        contentEdit=(EditText)findViewById(R.id.memo_create_content);
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.memo_create_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //按钮初始化
    private void initButton(){
        Button button=(Button)findViewById(R.id.memo_create_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleEdit.getText().toString();
                String content=contentEdit.getText().toString();
                if(title.equals("")){
                    Toast.makeText(MemoCreateActivity.this,"标题不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String time=df.format(new Date());
                    Intent intent=new Intent();
                    intent.putExtra("title",title);
                    intent.putExtra("time",time);
                    intent.putExtra("content",content);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_create);

        initVariable();
        initToolbar();
        initButton();
        initEditText();
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

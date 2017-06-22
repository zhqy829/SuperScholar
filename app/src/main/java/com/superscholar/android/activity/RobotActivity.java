package com.superscholar.android.activity;

import android.os.Bundle;

import com.superscholar.android.R;
import com.superscholar.android.adapter.MessageAdapter;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.Message;
import com.superscholar.android.tools.LogUtil;
import com.superscholar.android.tools.ServerConnection;


import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RobotActivity extends BaseActivity {

    private List<Message> msgList = new ArrayList<>();
    private EditText inputEdit;
    private RecyclerView msgRecyclerView;
    private MessageAdapter adapter;


    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.robot_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //变量初始化
    private void initVariable(){
        inputEdit=(EditText)findViewById(R.id.robot_inputEdit);
        msgRecyclerView = (RecyclerView) findViewById(R.id.robot_recycler_view);
    }

    //RecyclerView初始化
    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
    }

    //按钮初始化
    private void initButton(){
        Button sendButton = (Button) findViewById(R.id.robot_sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputEdit.getText().toString();
                if (!"".equals(content)) {
                    Message requestMsg = new Message(content, Message.TYPE_SENT);
                    msgList.add(requestMsg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    inputEdit.setText("");
                    ServerConnection.connectRobotAPI(content, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RobotActivity.this,"网络异常，发送失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            final String responseData=response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject responseObject = new JSONObject(responseData);
                                        String response=responseObject.getString("text");
                                        Message responseMsg = new Message(response, Message.TYPE_RECEIVED);
                                        msgList.add(responseMsg);
                                        adapter.notifyItemInserted(msgList.size() - 1);
                                        msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                                    }catch(JSONException e){
                                        LogUtil.e("Robot",responseData);
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    //初始消息初始化
    private void initMsgs() {
        Message msg1 = new Message("您好我是小学霸，很高兴和你聊天，有不懂的都可以来问我，我会尽力解答哦", Message.TYPE_RECEIVED);
        msgList.add(msg1);
        Message msg2 = new Message("遇到以下问题，可以回复问题代号查看答复哦：\n" +
                "Q1 为什么我的提醒时间不准确？\n"+
                "Q2 为什么我设置了提醒却没有正常提醒？\n"+
                "Q3 为什么专心时间过了几分钟了却仅增加了1分钟？\n"+
                "Q4 为什么我的学分绩异常减少了？\n"+
                "Q5 为什么我的程序会闪退？",
                Message.TYPE_RECEIVED);
        msgList.add(msg2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        initVariable();
        initToolbar();
        initMsgs();
        initRecyclerView();
        initButton();
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

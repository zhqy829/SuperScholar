package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.User;
import com.superscholar.android.tools.ActivityCollector;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForgetPasswordActivity extends BaseActivity {

    private EditText usernameEdit;
    private EditText verifyEdit;
    private Button verifyButton;
    private CountDownTimer timer;

    //初始化菜单栏
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.forget_password_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化控件变量
    private void initEditText(){
        usernameEdit=(EditText)findViewById(R.id.forget_password_edit_username);
        verifyEdit=(EditText)findViewById(R.id.forget_password_edit_verify);
    }

    //初始化倒计时
    private void initTimer(){
        timer=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                verifyButton.setText(""+millisUntilFinished/1000+"s");
            }

            @Override
            public void onFinish() {
                verifyButton.setText("获取验证码");
                verifyButton.setEnabled(true);
                verifyButton.setBackgroundColor(Color.parseColor("#1C86EE"));
            }
        };
    }

    private void initButton(){
        verifyButton=(Button)findViewById(R.id.forget_password_button_verify);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=usernameEdit.getText().toString();
                if(username.equals("")){
                    Toast.makeText(ForgetPasswordActivity.this,"请填写用户名",Toast.LENGTH_SHORT).show();
                } else{
                    ServerConnector.getInstance().sendForgetPasswordShortMsg(username, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgetPasswordActivity.this,"网络异常，发送失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resp =response.body().string();
                            JSONObject jsonObject= null;
                            try {
                                jsonObject = new JSONObject(resp);
                                int code =jsonObject.getInt("code");
                                if(code!=1){
                                    final String message=jsonObject.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ForgetPasswordActivity.this,message,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ForgetPasswordActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                                            verifyButton.setEnabled(false);
                                            verifyButton.setBackgroundColor(Color.GRAY);
                                            verifyButton.setText("60s");
                                            timer.start();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgetPasswordActivity.this,"发送异常",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                }
            }
        });

        Button submitButton=(Button)findViewById(R.id.forget_password_button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=usernameEdit.getText().toString();
                String verify=verifyEdit.getText().toString();
                if(username.equals("")){
                    Toast.makeText(ForgetPasswordActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();
                }else if(verify.equals("")){
                    Toast.makeText(ForgetPasswordActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                }else{
                    final ProgressDialog pd=new ProgressDialog(ForgetPasswordActivity.this);
                    pd.setTitle("身份验证");
                    pd.setMessage("正在验证身份，请稍候...");
                    pd.setCancelable(false);
                    pd.show();
                    ServerConnector.getInstance().checkVarify(usernameEdit.getText().toString(),
                            verifyEdit.getText().toString(), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Toast.makeText(ForgetPasswordActivity.this,"网络异常，请检查网络设置",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String resp=response.body().string();
                                    JSONObject jsonObject= null;
                                    try {
                                        jsonObject = new JSONObject(resp);
                                        int code =jsonObject.getInt("code");
                                        if(code!=1){
                                            final String message=jsonObject.getString("message");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(ForgetPasswordActivity.this,message,Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Intent intent=new Intent(ForgetPasswordActivity.this,ForgetSetPasswordActivity.class);
                                                    intent.putExtra("username",username);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(ForgetPasswordActivity.this,"验证身份异常",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initToolbar();
        initTimer();
        initEditText();
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

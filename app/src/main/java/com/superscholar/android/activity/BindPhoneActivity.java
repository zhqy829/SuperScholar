package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.User;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BindPhoneActivity extends BaseActivity {

    private EditText phoneEdit;
    private EditText verifyEdit;
    private Button verifyButton;
    private CountDownTimer timer;

    //初始化菜单栏
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.bind_phone_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化控件变量
    private void initEditText(){
        phoneEdit=(EditText)findViewById(R.id.bind_phone_edit_phone);
        verifyEdit=(EditText)findViewById(R.id.bind_phone_edit_verify);
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

    //按钮初始化
    private void initButton(){
        verifyButton=(Button)findViewById(R.id.bind_phone_button_verify);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=phoneEdit.getText().toString();
                if(phone.equals("")){
                    Toast.makeText(BindPhoneActivity.this,"请输入手机号",Toast.LENGTH_SHORT).show();
                }else{
                    ServerConnector.getInstance().sendBindPhoneMsg(UserLib.getInstance().getUser().getUsername(), phone, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BindPhoneActivity.this,"网络异常，发送失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resp=response.body().string();
                            try {
                                JSONObject jsonObject=new JSONObject(resp);
                                int code=jsonObject.getInt("code");
                                if(code!=1){
                                    final String message=jsonObject.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BindPhoneActivity.this,message,Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BindPhoneActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(BindPhoneActivity.this,"发生异常",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        Button submitButton=(Button)findViewById(R.id.bind_phone_button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone=phoneEdit.getText().toString();
                String verify=verifyEdit.getText().toString();
                if(phone.equals("")){
                    Toast.makeText(BindPhoneActivity.this,"请输入手机号",Toast.LENGTH_SHORT).show();
                }else if(verify.equals("")){
                    Toast.makeText(BindPhoneActivity.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                }else{
                    final ProgressDialog pd=new ProgressDialog(BindPhoneActivity.this);
                    pd.setTitle("绑定手机");
                    pd.setMessage("绑定手机中，请稍候...");
                    pd.setCancelable(false);
                    pd.show();
                    ServerConnector.getInstance().checkVarify(UserLib.getInstance().getUser().getUsername(), verify, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BindPhoneActivity.this,"网络异常，请检查网络设置",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resp=response.body().string();
                            JSONObject jsonObject= null;
                            try {
                                jsonObject = new JSONObject(resp);
                                int code = jsonObject.getInt("code");
                                if (code != 1) {
                                    final String message = jsonObject.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Toast.makeText(BindPhoneActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    ServerConnector.getInstance().bindPhone(UserLib.getInstance().getUser().getUsername(), phone, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(BindPhoneActivity.this, "网络异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String resp=response.body().string();
                                            try {
                                                JSONObject respObject=new JSONObject(resp);
                                                int code=respObject.getInt("code");
                                                if(code!=1){
                                                    final String message=respObject.getString("message");
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            pd.dismiss();
                                                            Toast.makeText(BindPhoneActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }else{
                                                    UserLib.getInstance().getUser().setPhone(phone);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            pd.dismiss();
                                                            Toast.makeText(BindPhoneActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                                            setResult(RESULT_OK);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pd.dismiss();
                                                        Toast.makeText(BindPhoneActivity.this, "发生异常", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                                pd.dismiss();
                                Toast.makeText(BindPhoneActivity.this, "发生异常", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_bind_phone);

        initEditText();
        initToolbar();
        initButton();
        initTimer();
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

package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.*;

import com.superscholar.android.entity.User;
import com.superscholar.android.tools.ActivityCollector;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.tools.EncryptionDevice;
import com.superscholar.android.R;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText cpasswordEdit;
    private EditText phoneEdit;
    private EditText verifyEdit;

    private Button verifyButton;

    private CountDownTimer timer;

    //变量初始化
    private void initVariable(){
        usernameEdit=(EditText)findViewById(R.id.register_usernameEdit);
        passwordEdit=(EditText)findViewById(R.id.register_passwordEdit);
        cpasswordEdit=(EditText)findViewById(R.id.register_cpasswordEdit);
        phoneEdit=(EditText)findViewById(R.id.register_phoneEdit);
        verifyEdit=(EditText)findViewById(R.id.register_verifyEdit);
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.register_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    //输入合法性检测 合法返回true 不合法返回false
    private boolean inputValidityCheck(){
        String username=usernameEdit.getText().toString();
        String password=passwordEdit.getText().toString();
        String cpassword=cpasswordEdit.getText().toString();
        String phone=phoneEdit.getText().toString();
        String verify=verifyEdit.getText().toString();

        if(username.equals("")){
            Toast.makeText(RegisterActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if(password.equals("")){
            Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if(cpassword.equals("")){
            Toast.makeText(RegisterActivity.this,"确认密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if(phone.equals("")){
            Toast.makeText(RegisterActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if(verify.equals("")){
            Toast.makeText(RegisterActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(cpassword)){
            Toast.makeText(RegisterActivity.this,"密码与确认密码不同，请重新输入",Toast.LENGTH_SHORT).show();
            return false;
        }

        String regex="1\\d{10,10}";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(phone);
        if(phone.length()!=11||!matcher.matches()){
            Toast.makeText(RegisterActivity.this,"手机格式错误",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //按钮初始化
    private void initButton(){
        verifyButton=(Button)findViewById(R.id.register_verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=usernameEdit.getText().toString();
                String phone=phoneEdit.getText().toString();
                if(username.equals("")){
                    Toast.makeText(RegisterActivity.this,"请填写用户名",Toast.LENGTH_SHORT).show();
                }else if(phone.equals("")){
                    Toast.makeText(RegisterActivity.this,"请填写手机号",Toast.LENGTH_SHORT).show();
                }else{
                    ServerConnector.getInstance().sendRegisterShortMsg(username, phone, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this,"网络异常，发送失败",Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RegisterActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(RegisterActivity.this,"发送异常",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                }
            }
        });


        Button registerButton=(Button)findViewById(R.id.register_registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputValidityCheck()){
                    final ProgressDialog pd=new ProgressDialog(RegisterActivity.this);
                    pd.setTitle("注册");
                    pd.setMessage("正在注册，请稍候...");
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
                                            Toast.makeText(RegisterActivity.this,"网络异常，请检查网络设置",Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            ServerConnector.getInstance().sendRegisterMsg(usernameEdit.getText().toString(),
                                                    passwordEdit.getText().toString(), phoneEdit.getText().toString(), new Callback() {
                                                        @Override
                                                        public void onFailure(Call call, IOException e) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    pd.dismiss();
                                                                    Toast.makeText(RegisterActivity.this,"网络异常，请检查网络设置",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onResponse(Call call, Response response) throws IOException {
                                                            String responseData =response.body().string();
                                                            try{
                                                                final JSONObject jsonObject=new JSONObject(responseData);
                                                                int code =jsonObject.getInt("code");
                                                                if(code!=1){
                                                                    final String message=jsonObject.getString("message");
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            pd.dismiss();
                                                                            Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }else{
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            pd.dismiss();
                                                                            double grade= 0;
                                                                            try {
                                                                                String userString=jsonObject.getString("user");
                                                                                JSONObject userObject=new JSONObject(userString);
                                                                                grade = userObject.getDouble("grade");
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            User user= UserLib.getInstance().getUser();
                                                                            user.setUsername(usernameEdit.getText().toString());
                                                                            user.setPassword(passwordEdit.getText().toString());
                                                                            user.setGrade(grade);
                                                                            user.setPhone(phoneEdit.getText().toString());
                                                                            user.setSpwd(null);
                                                                            user.setSid(null);
                                                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                                            saveUPInFile(usernameEdit.getText().toString(),
                                                                                    passwordEdit.getText().toString());
                                                                            ActivityCollector.finishAll();
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                                }
                                                            }catch(JSONException e){
                                                                e.printStackTrace();
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        pd.dismiss();
                                                                        Toast.makeText(RegisterActivity.this,"注册异常",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(RegisterActivity.this,"注册异常",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }

    //用户名密码写入文件
    private void saveUPInFile(String username,String password){
        SharedPreferences.Editor editor=getSharedPreferences("data_up",0).edit();
        editor.putString("username",username);
        editor.putString("password", EncryptionDevice.encrypt(password));
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initVariable();
        initToolbar();
        initTimer();
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

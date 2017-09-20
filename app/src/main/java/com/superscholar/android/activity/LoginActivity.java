package com.superscholar.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.User;
import com.superscholar.android.tools.EncryptionDevice;
import com.superscholar.android.R;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.framed.Header;

public class LoginActivity extends BaseActivity {

    /**
     * false清除up文件数据，true不清除
     * 登录成功或网络异常时为true，用户名密码错误时为false
    */
    private boolean fileStatus=true;
    private EditText usernameEdit;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initVariable();
        initButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUPFromFile();
    }

    private void initVariable(){
        usernameEdit=(EditText)findViewById(R.id.login_usernameEdit);
        passwordEdit=(EditText)findViewById(R.id.login_passwordEdit);
    }

    //按钮初始化
    private void initButton(){
        Button loginButton=(Button)findViewById(R.id.login_loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=usernameEdit.getText().toString();
                String password=passwordEdit.getText().toString();
                if(!username.equals("")&&!password.equals("")){
                    logonValidate(username,password);
                }else{
                    Toast.makeText(LoginActivity.this,"用户名/密码不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView forgetPassword=(TextView)findViewById(R.id.login_forgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        TextView registerAccount=(TextView)findViewById(R.id.login_registerAccount);
        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    //登录验证
    private void logonValidate(final String username, final String password){
        final ProgressDialog pd=new ProgressDialog(LoginActivity.this);
        pd.setTitle("登录");
        pd.setMessage("登录中，请稍候...");
        pd.setCancelable(false);
        pd.show();
        ServerConnector.getInstance().sendLoginMsg(username, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this,"网络异常，请检查网络设置",
                                Toast.LENGTH_SHORT).show();
                        fileStatus=true;
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData =response.body().string();
                try{
                    JSONObject jsonObject=new JSONObject(responseData);
                    int code =Integer.parseInt(jsonObject.getString("code"));
                    //存在code，失败的返回
                    if(code==1){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this,"用户名不存在",
                                        Toast.LENGTH_SHORT).show();
                                fileStatus=false;
                            }
                        });
                    } else if(code==2){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this,"用户名/密码错误",
                                        Toast.LENGTH_SHORT).show();
                                fileStatus=false;
                            }
                        });
                    }
                }catch(JSONException e){
                    //不存在code，成功的返回
                    try{
                        pd.dismiss();
                        JSONObject jsonObject=new JSONObject(responseData);
                        String grade=jsonObject.getString("grade");
                        String phone=jsonObject.getString("phone");
                        String sid=jsonObject.getString("studentId");
                        if(sid.equals("null")) sid=null;
                        String spwd=jsonObject.getString("ipassword");
                        if(spwd.equals("null")) spwd=null;
                        Boolean remind=jsonObject.getBoolean("remind");
                        SharedPreferences pref=getSharedPreferences("data_currency",0);
                        int currencyAmount=pref.getInt("currencyAmount",0);

                        User user= UserLib.getInstance().getUser();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setGrade(Double.parseDouble(grade));
                        user.setPhone(phone);
                        user.setSid(sid);
                        user.setSpwd(spwd);
                        user.setRemind(remind);
                        user.setCurrencyAmount(currencyAmount);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                        saveUPInFile(username,password);
                        fileStatus=true;
                    }catch (JSONException ex){
                        ex.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this,"登陆异常",
                                        Toast.LENGTH_SHORT).show();
                                fileStatus=false;
                            }
                        });
                    }
                }
            }
        });
    }

    //用户名密码写入文件
    private void saveUPInFile(String username,String password){
        SharedPreferences.Editor editor=getSharedPreferences("data_up",0).edit();
        editor.putString("username",username);
        editor.putString("password",EncryptionDevice.encrypt(password));
        editor.apply();
    }

    //从文件中读用户名密码
    private void getUPFromFile(){
        SharedPreferences pref=getSharedPreferences("data_up",0);
        SharedPreferences.Editor editor=pref.edit();
        String username=pref.getString("username","");
        String encodedPassword=pref.getString("password","");
        String password=EncryptionDevice.decipher(encodedPassword);
        if(!(username.equals("")&&password.equals(""))){
            usernameEdit.setText(username);
            passwordEdit.setText(password);
            logonValidate(username, password);
            if(!fileStatus){
                editor.clear();
                editor.apply();
            }
        }
    }
}

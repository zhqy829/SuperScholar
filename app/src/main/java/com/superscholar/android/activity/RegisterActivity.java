package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.*;

import com.superscholar.android.tools.ActivityCollector;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.tools.EncryptionDevice;
import com.superscholar.android.tools.ServerConnection;
import com.superscholar.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private TextView usernameEdit;
    private TextView passwordEdit;
    private TextView cpasswordEdit;
    private TextView emailEdit;
    private TextView sIDEdit;

    //变量初始化
    private void initVariable(){
        usernameEdit=(TextView)findViewById(R.id.register_usernameEdit);
        passwordEdit=(TextView)findViewById(R.id.register_passwordEdit);
        cpasswordEdit=(TextView)findViewById(R.id.register_cpasswordEdit);
        emailEdit=(TextView)findViewById(R.id.register_emailEdit);
        sIDEdit=(TextView)findViewById(R.id.register_sIDEdit);
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

    //输入合法性检测 合法返回true 不合法返回false
    private boolean inputValidityCheck(){
        String username=usernameEdit.getText().toString();
        String password=passwordEdit.getText().toString();
        String cpassword=cpasswordEdit.getText().toString();
        String email=emailEdit.getText().toString();

        if(username.equals("")){
            Toast.makeText(RegisterActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if(password.equals("")){
            Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if(cpassword.equals("")){
            Toast.makeText(RegisterActivity.this,"确认密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if(email.equals("")){
            Toast.makeText(RegisterActivity.this,"邮箱不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(cpassword)){
            Toast.makeText(RegisterActivity.this,"密码与确认密码不同，请重新输入",Toast.LENGTH_SHORT).show();
            return false;
        }

        String regex="\\p{Alnum}{1,}@\\p{Alnum}{1,}.[a-z]{1,}";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(email);
        if(!matcher.matches()){
            Toast.makeText(RegisterActivity.this,"邮箱格式错误",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //按钮初始化
    private void initButton(){
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
                    ServerConnection.sendRegisterMsg(usernameEdit.getText().toString(),
                            passwordEdit.getText().toString(), emailEdit.getText().toString(),
                            sIDEdit.getText().toString(), new Callback() {
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
                                        JSONObject jsonObject=new JSONObject(responseData);
                                        int code =Integer.parseInt(jsonObject.getString("code"));
                                        //存在code，失败的返回
                                        if(code==1){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(RegisterActivity.this,"用户名已存在",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else if(code==2){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(RegisterActivity.this,"邮箱已被注册",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else if(code==3){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(RegisterActivity.this,"未知错误，请联系开发人员",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }catch(JSONException e){
                                        try{
                                            pd.dismiss();
                                            JSONObject jsonObject=new JSONObject(responseData);
                                            String grade=jsonObject.getString("grade");
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.putExtra("username",usernameEdit.getText().toString());
                                            intent.putExtra("password",passwordEdit.getText().toString());
                                            intent.putExtra("grade",grade);
                                            intent.putExtra("email",emailEdit.getText().toString());
                                            if(sIDEdit.getText().toString().equals("")){
                                                intent.putExtra("sID","null");
                                            }else{
                                                intent.putExtra("sID",sIDEdit.getText().toString());
                                            }
                                            saveUPInFile(usernameEdit.getText().toString(),
                                                    passwordEdit.getText().toString());
                                            ActivityCollector.finishAll();
                                            startActivity(intent);
                                        }catch (JSONException ex){
                                            ex.printStackTrace();
                                        }
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

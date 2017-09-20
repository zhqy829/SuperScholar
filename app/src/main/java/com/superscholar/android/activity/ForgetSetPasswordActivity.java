package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.User;
import com.superscholar.android.tools.ActivityCollector;
import com.superscholar.android.tools.EncryptionDevice;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForgetSetPasswordActivity extends BaseActivity {

    private String username;
    private EditText pwdEdit;
    private EditText cpwdEdit;


    //初始化菜单栏
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.forget_set_password_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void initEditText(){
        pwdEdit=(EditText)findViewById(R.id.forget_set_password_pwdEdit);
        cpwdEdit=(EditText)findViewById(R.id.forget_set_password_cpwdEdit);
    }

    private void initUsername(){
        username=getIntent().getStringExtra("username");
    }

    //初始化按钮
    private void initButton(){
        Button button=(Button)findViewById(R.id.forget_set_password_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String pwd=pwdEdit.getText().toString();
                String cpwd=cpwdEdit.getText().toString();
                if(pwd.equals("")||cpwd.equals("")){
                    Toast.makeText(ForgetSetPasswordActivity.this,"请填写完整信息"
                            ,Toast.LENGTH_SHORT).show();
                }else if(!pwd.equals(cpwd)){
                    Toast.makeText(ForgetSetPasswordActivity.this,"两次密码不一致，请重新输入"
                            ,Toast.LENGTH_SHORT).show();
                } else{
                    final ProgressDialog pd=new ProgressDialog(ForgetSetPasswordActivity.this);
                    pd.setTitle("修改密码");
                    pd.setMessage("修改密码中，请稍候...");
                    pd.setCancelable(false);
                    pd.show();
                    ServerConnector.getInstance().forgetPasswordReset(username,pwd, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Toast.makeText(ForgetSetPasswordActivity.this,"网络异常，请检查网络设置",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData =response.body().string();
                                    try {
                                        final JSONObject jsonObject = new JSONObject(responseData);
                                        int code = Integer.parseInt(jsonObject.getString("code"));
                                        if (code != 1) {
                                            final String message=jsonObject.getString("message");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(ForgetSetPasswordActivity.this, message,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ForgetSetPasswordActivity.this, "重置密码成功",
                                                            Toast.LENGTH_SHORT).show();
                                                    try {
                                                        modifyUPFile(username,pwd);

                                                        String userString=jsonObject.getString("user");
                                                        JSONObject userObject=new JSONObject(userString);
                                                        double grade=userObject.getDouble("grade");
                                                        String phone=userObject.getString("phone");
                                                        boolean remind=userObject.getBoolean("remind");
                                                        String sid=userObject.getString("studentId");
                                                        if(sid.equals("null")) sid=null;
                                                        String spwd=userObject.getString("ipassword");
                                                        if(spwd.equals("null")) spwd=null;
                                                        SharedPreferences pref=getSharedPreferences("data_currency",0);
                                                        int currencyAmount=pref.getInt("currencyAmount",0);

                                                        User user=UserLib.getInstance().getUser();
                                                        user.setUsername(username);
                                                        user.setGrade(grade);
                                                        user.setPassword(pwd);
                                                        user.setPhone(phone);
                                                        user.setRemind(remind);
                                                        user.setSid(sid);
                                                        user.setSpwd(spwd);
                                                        user.setCurrencyAmount(currencyAmount);

                                                        pd.dismiss();
                                                        ActivityCollector.finishAll();

                                                        Intent intent=new Intent(ForgetSetPasswordActivity.this,MainActivity.class);
                                                        startActivity(intent);

                                                    } catch (JSONException e) {
                                                        pd.dismiss();
                                                        e.printStackTrace();
                                                        Toast.makeText(ForgetSetPasswordActivity.this, "发生异常",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    catch(JSONException e){
                                        pd.dismiss();
                                        e.printStackTrace();
                                        Toast.makeText(ForgetSetPasswordActivity.this, "发生异常",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    //修改密码后修改up文件
    private void modifyUPFile(String username,String password){
        SharedPreferences.Editor editor=getSharedPreferences("data_up",0).edit();
        editor.clear();
        editor.apply();
        editor.putString("username",username);
        editor.putString("password", EncryptionDevice.encrypt(password));
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_set_password);

        initToolbar();
        initUsername();
        initEditText();
        initButton();
    }
}

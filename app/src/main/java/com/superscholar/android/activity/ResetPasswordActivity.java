package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.tools.EncryptionDevice;
import com.superscholar.android.tools.ServerConnection;
import com.superscholar.android.R;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ResetPasswordActivity extends BaseActivity {

    //初始化菜单栏
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.reset_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化按钮
    private void initButton(){
        Button button=(Button)findViewById(R.id.reset_resetPasswordButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText oldPasswordEdit=(EditText)findViewById(R.id.reset_oldPasswordEdit);
                EditText newPasswordEdit=(EditText)findViewById(R.id.reset_newPasswordEdit);
                EditText cPasswordEdit=(EditText)findViewById(R.id.reset_cpasswordEdit);

                String op=oldPasswordEdit.getText().toString();
                final String np=newPasswordEdit.getText().toString();
                String cp=cPasswordEdit.getText().toString();
                if(op.equals("")||np.equals("")||cp.equals("")){
                    Toast.makeText(ResetPasswordActivity.this,"请填写完整信息"
                            ,Toast.LENGTH_SHORT).show();
                }else if(!np.equals(cp)){
                    Toast.makeText(ResetPasswordActivity.this,"两次密码不一致，请重新输入"
                            ,Toast.LENGTH_SHORT).show();
                } else{
                    final ProgressDialog pd=new ProgressDialog(ResetPasswordActivity.this);
                    pd.setTitle("修改密码");
                    pd.setMessage("修改密码中，请稍候...");
                    pd.setCancelable(false);
                    pd.show();
                    ServerConnection.sendResetPasswordMsg(UserLib.getInstance(null).getUser().getUsername(),
                            op, np, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Toast.makeText(ResetPasswordActivity.this,"网络异常，请检查网络设置",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData =response.body().string();
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        int code = Integer.parseInt(jsonObject.getString("code"));
                                        if (code == 2) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(ResetPasswordActivity.this, "密码错误",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }else if(code==0){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(ResetPasswordActivity.this, "修改成功",
                                                            Toast.LENGTH_SHORT).show();
                                                    modifyUPFile(getIntent().getStringExtra("username"),np);
                                                    UserLib.getInstance(null).getUser().setPassword(np);
                                                    finish();

                                                }
                                            });
                                        }
                                    }
                                    catch(JSONException e){

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
        setContentView(R.layout.activity_reset_password);

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

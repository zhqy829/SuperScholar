package com.superscholar.android.activity;

import android.app.ProgressDialog;
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
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BindSidActivity extends BaseActivity {

    private EditText sidEdit;
    private EditText spwdEdit;

    //初始化菜单栏
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.bind_sid_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initEditText(){
        User user=UserLib.getInstance().getUser();
        sidEdit=(EditText)findViewById(R.id.bind_sid_edit_sid);
        spwdEdit=(EditText)findViewById(R.id.bind_sid_edit_spwd);
        if(user.getSid()!=null){
            sidEdit.setText(user.getSid());
        }
        if(user.getSpwd()!=null){
            spwdEdit.setText(user.getSpwd());
        }
    }

    private void initButton(){
        Button button=(Button)findViewById(R.id.bind_sid_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sid=sidEdit.getText().toString();
                final String spwd=spwdEdit.getText().toString();
                if(sid.equals("")||spwd.equals("")){
                    Toast.makeText(BindSidActivity.this,"请填写完整信息"
                            ,Toast.LENGTH_SHORT).show();
                }else{
                    final ProgressDialog pd=new ProgressDialog(BindSidActivity.this);
                    pd.setTitle("绑定学号");
                    pd.setMessage("绑定中，请稍候...");
                    pd.setCancelable(false);
                    pd.show();
                    ServerConnector.getInstance().sendBindSIDMsg(UserLib.getInstance().getUser().getUsername(),
                            sid, spwd, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Toast.makeText(BindSidActivity.this,"网络异常，请检查网络设置",
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
                                                    Toast.makeText(BindSidActivity.this, "未知错误",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }else if(code==0){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(BindSidActivity.this, "绑定成功",
                                                            Toast.LENGTH_SHORT).show();
                                                    UserLib.getInstance().getUser().setSid(sid);
                                                    UserLib.getInstance().getUser().setSpwd(spwd);
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                    catch(JSONException e){
                                        pd.dismiss();
                                        e.printStackTrace();
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
        setContentView(R.layout.activity_bind_sid);

        initToolbar();
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

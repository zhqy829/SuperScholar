package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.tools.ServerConnection;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FeedbackActivity extends BaseActivity {

    private Spinner typeSpinner;
    private EditText detailEdit;
    private EditText contactEdit;
    private String username;

    private void initVariable(){
        typeSpinner=(Spinner)findViewById(R.id.feedback_spinner);
        detailEdit=(EditText)findViewById(R.id.feedback_contentEdit);
        contactEdit=(EditText)findViewById(R.id.feedback_contactWayEdit);
        username= UserLib.getInstance(null).getUser().getUsername();
    }

    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.feedback_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initButton(){
        Button submitButton=(Button)findViewById(R.id.feedback_submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String detail=detailEdit.getText().toString();
                if(detail.equals("")){
                    Toast.makeText(FeedbackActivity.this,"问题描述不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                String contact=contactEdit.getText().toString();
                String type;
                switch(typeSpinner.getSelectedItemPosition()){
                    case 0:
                        type="bug反馈";
                        break;
                    case 1:
                        type="建议";
                        break;
                    case 2:
                        type="其他";
                        break;
                    default:
                        type=null;
                }
                final ProgressDialog pd=new ProgressDialog(FeedbackActivity.this);
                pd.setTitle("反馈建议");
                pd.setMessage("正在提交，请稍候...");
                pd.setCancelable(false);
                pd.show();
                ServerConnection.sendUserFeedback(username, type, detail, contact, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        pd.dismiss();
                        Toast.makeText(FeedbackActivity.this,"网络异常，请检查网络设置",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData =response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            int code=jsonObject.getInt("code");
                            if (code == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        Toast.makeText(FeedbackActivity.this, "提交失败\n",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else if(code==0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        Toast.makeText(FeedbackActivity.this, "提交成功，感谢您的支持",
                                                Toast.LENGTH_SHORT).show();
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
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

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

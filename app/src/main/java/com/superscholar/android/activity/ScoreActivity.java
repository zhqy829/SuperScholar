package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.superscholar.android.R;
import com.superscholar.android.adapter.ScoreAdapter;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.Score;
import com.superscholar.android.entity.ServerResponse;
import com.superscholar.android.fragment.GradeRemindDialogFragment;
import com.superscholar.android.fragment.IntegratedFragment;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ScoreActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView hintText;
    private List<Score> scoreList;
    private TextView creditText;

    private void initVariable(){
        recyclerView=(RecyclerView)findViewById(R.id.score_recycler);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        hintText=(TextView)findViewById(R.id.score_hint);
        creditText=(TextView)findViewById(R.id.score_credit_text);
    }

    private void initData(){
        scoreList = new ArrayList<>();
        final ProgressDialog pd = new ProgressDialog(ScoreActivity.this);
        pd.setTitle("成绩");
        pd.setMessage("数据获取中，请稍候...");
        pd.setCancelable(false);
        pd.show();
        ServerConnector.getInstance().queryScore(UserLib.getInstance().getUser().getUsername(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScoreActivity.this,"网络异常，请检查网络设置",Toast.LENGTH_SHORT).show();
                        hintText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        pd.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                final ServerResponse<List<Score>> data = new Gson().fromJson(resp, new TypeToken<ServerResponse<List<Score>>>(){}.getType());
                if(data.getCode() != 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hintText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            String msg = data.getMessage();
                            Toast.makeText(ScoreActivity.this, msg, Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                } else {
                    scoreList = data.getData();
                    final Score creditScore = scoreList.get(0);
                    scoreList.remove(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(scoreList.size() == 0){
                                hintText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }else{
                                ScoreAdapter adapter = new ScoreAdapter(scoreList);
                                recyclerView.setAdapter(adapter);
                            }
                            creditText.setText(creditScore.getScore());
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.score_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        initToolbar();
        initVariable();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.score_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.remind:
                FragmentManager manager=getSupportFragmentManager();
                GradeRemindDialogFragment dialog=new GradeRemindDialogFragment();
                dialog.show(manager,"GradeRemindDialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

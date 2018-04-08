package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.superscholar.android.R;
import com.superscholar.android.adapter.ConsumptionDetailAdapter;
import com.superscholar.android.adapter.CreditDetailAdapter;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.ConsumptionDetail;
import com.superscholar.android.entity.CreditDetail;
import com.superscholar.android.tools.GoodsBaseManager;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;
import com.twotoasters.jazzylistview.effects.CardsEffect;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreditDetailActivity extends BaseActivity {

    private SwipeMenuRecyclerView recyclerView;
    private TextView hintText;
    private List<CreditDetail> detailList;
    private CreditDetailAdapter adapter;

    private void initView(){
        hintText = findViewById(R.id.credit_detail_hintText);
        recyclerView = findViewById(R.id.credit_detail_recyclerView);
        TextView creditText = findViewById(R.id.tv_credit_detail_my_credit);
        creditText.setText(String.valueOf(UserLib.getInstance().getUser().getGrade()));
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewSwipeEnabled(false);
    }

    private void initData(){

        ServerConnector.getInstance().getUserGradeDetail(UserLib.getInstance().getUser().getUsername(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreditDetailActivity.this, "网络异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                        hintText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                detailList = new Gson().fromJson(resp, new TypeToken<List<CreditDetail>>(){}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(detailList.size() == 0){
                            hintText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            adapter = new CreditDetailAdapter(detailList);

                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        });
    }

    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.credit_detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_detail);

        initToolbar();
        initView();
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}

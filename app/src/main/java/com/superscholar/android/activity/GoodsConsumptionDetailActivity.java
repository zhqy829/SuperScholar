package com.superscholar.android.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.adapter.ConsumptionDetailAdapter;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.ConsumptionDetail;
import com.superscholar.android.tools.GoodsBaseManager;
import com.twotoasters.jazzylistview.effects.CardsEffect;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMovementListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GoodsConsumptionDetailActivity extends BaseActivity {

    private SwipeMenuRecyclerView recyclerView;
    private TextView hintText;
    private List<ConsumptionDetail> detailList;
    private ConsumptionDetailAdapter adapter;
    private GoodsBaseManager manager;

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.goods_consumption_detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initConsumptionData(){
        hintText=(TextView)findViewById(R.id.goods_consumption_detail_hintText);
        recyclerView=(SwipeMenuRecyclerView)findViewById(R.id.goods_consumption_detail_recyclerView);

        manager=new GoodsBaseManager(getApplicationContext());
        detailList=manager.getConsumptionDetailList();

        if(detailList.size()==0){
            hintText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    //初始化recyclerView
    private void initRecyclerView(){
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        adapter=new ConsumptionDetailAdapter(detailList);
        adapter.setOnItemClickedListener(new ConsumptionDetailAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int position) {
                Toast.makeText(GoodsConsumptionDetailActivity.this,"滑动可删除记录",Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewSwipeEnabled(true);
        recyclerView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {
                return false;
            }

            @Override
            public void onItemDismiss(int position) {
                ConsumptionDetail consumptionDetail=detailList.get(position);
                manager.deleteConsumptionDetail(consumptionDetail);
                detailList.remove(position);
                adapter.notifyItemRemoved(position);
                if(detailList.size()==0){
                    hintText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        JazzyRecyclerViewScrollListener jazzyScrollListener=new JazzyRecyclerViewScrollListener();
        recyclerView.addOnScrollListener(jazzyScrollListener);
        jazzyScrollListener.setTransitionEffect(new CardsEffect());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_consumption_detail);

        initToolbar();
        initConsumptionData();
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.consumption_detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.delete:
                Snackbar.make(recyclerView,
                        "您确定要清空所有消费明细吗？",
                        Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(ConsumptionDetail detail:detailList){
                            manager.deleteConsumptionDetail(detail);
                        }
                        detailList.clear();
                        adapter.notifyDataSetChanged();
                        hintText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }).show();
                break;
        }
        return false;
    }
}

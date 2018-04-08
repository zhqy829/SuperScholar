package com.superscholar.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.superscholar.android.R;
import com.superscholar.android.adapter.ConsumptionDetailAdapter;
import com.superscholar.android.adapter.GoodsViewAdapter;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.ConsumptionDetail;
import com.superscholar.android.entity.Goods;
import com.superscholar.android.entity.User;
import com.superscholar.android.tools.GoodsBaseManager;
import com.superscholar.android.tools.UserLib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StoreActivity extends BaseActivity {

    private SwipeMenuListView listView;
    private TextView hintText;
    private TextView currencyText;

    private GoodsViewAdapter adapter;

    private final static int REQUEST_GOODS_CREATE=0;
    private final static int REQUEST_GOODS_UPDATE=1;

    private GoodsBaseManager manager;
    private List<Goods>goodsList;

    private User user;

    //变量初始化
    private void initVariable(){
        listView=(SwipeMenuListView)findViewById(R.id.store_listView);
        hintText=(TextView)findViewById(R.id.store_hintText);
        currencyText=(TextView)findViewById(R.id.store_text_currency);
        user=UserLib.getInstance().getUser();
        currencyText.setText("奖励币:"+user.getCurrencyAmount());
    }

    //初始化商品数据
    private void initGoodsBaseManager(){
        manager=new GoodsBaseManager(getApplicationContext());
        goodsList=manager.getGoodsList();

        if(goodsList.size()==0){
            hintText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.store_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //按钮初始化
    private void initButton(){
        Button button=(Button)findViewById(R.id.store_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StoreActivity.this,GoodsCreateActivity.class);
                startActivityForResult(intent,REQUEST_GOODS_CREATE);
            }
        });
    }

    //初始化商品列表
    private void initListView(){
        adapter=new GoodsViewAdapter(StoreActivity.this, R.layout.goods_item, goodsList,
                new GoodsViewAdapter.OnPurchaseButtonClickedListener() {
                    @Override
                    public void onPurchaseButtonClicked(int position) {
                        Goods goods=goodsList.get(position);
                        int currency=user.getCurrencyAmount();
                        if(currency>=goods.getPrice()){
                            Toast.makeText(StoreActivity.this,"购买成功",Toast.LENGTH_SHORT).show();
                            ConsumptionDetail detail=new ConsumptionDetail(goods.getName(),goods.getPrice());
                            manager.insertConsumptionDetail(detail);
                            user.setCurrencyAmount(currency-goods.getPrice());
                            currencyText.setText("奖励币:"+user.getCurrencyAmount());
                            SharedPreferences pref=getSharedPreferences("data_currency",MODE_PRIVATE);
                            SharedPreferences.Editor editor=pref.edit();
                            editor.putInt("currencyAmount",user.getCurrencyAmount());
                            editor.apply();
                        }else{
                            Toast.makeText(StoreActivity.this,"购买失败，奖励币不足",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        listView.setAdapter(adapter);

        //生成菜单按钮
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem updateItem = new SwipeMenuItem(getApplicationContext());
                updateItem.setBackground(new ColorDrawable(Color.rgb(0xA4, 0xD3, 0xEE)));
                updateItem.setWidth(dp2px(90));
                updateItem.setIcon(R.drawable.goods_item_update);
                menu.addMenuItem(updateItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.goods_item_delete);
                menu.addMenuItem(deleteItem);

            }
        };
        listView.setMenuCreator(creator);

        //子项的菜单按钮点击监听
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        onUpdateMenuClicked(position);
                        break;
                    case 1:
                        onDeleteMenuClicked(position);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.smoothOpenMenu(position);
            }
        });

    }

    //删除菜单被点击
    private void onDeleteMenuClicked(int position){
        Goods goods=goodsList.get(position);
        manager.deleteGoods(goods);
        goodsList.remove(position);
        String imagePath="/sdcard/photos/SuperScholar/"+goods.getUuid().toString()+".jpg";
        File image = new File(imagePath);
        if (image.exists()) {
            image.delete();
        }
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Toast.makeText(StoreActivity.this,"删除成功",Toast.LENGTH_SHORT).show();

        if(goodsList.size()==0){
            hintText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    //更新菜单被点击
    private void onUpdateMenuClicked(int position){
        Intent intent=new Intent(this,GoodsUpdateActivity.class);
        intent.putExtra("goods",goodsList.get(position));
        intent.putExtra("position",position);
        startActivityForResult(intent,REQUEST_GOODS_UPDATE);
    }

    //dp转px
    private int dp2px(float dpValue) {
        final float scale =getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        initVariable();
        initGoodsBaseManager();
        initListView();
        initButton();
        initToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.store_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.more:
                Intent intent=new Intent(StoreActivity.this, GoodsConsumptionDetailActivity.class);
                startActivity(intent);
                break;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_GOODS_CREATE:
                if(resultCode==RESULT_OK){
                    Goods goods=data.getParcelableExtra("goods");
                    goodsList.add(goods);
                    manager.insertGoods(goods);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(StoreActivity.this,"新建成功",Toast.LENGTH_SHORT).show();

                    if(hintText.getVisibility()==View.VISIBLE){
                        hintText.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case REQUEST_GOODS_UPDATE:
                if(resultCode==RESULT_OK){
                    Goods goods=data.getParcelableExtra("goods");
                    int position=data.getIntExtra("position",0);
                    goodsList.set(position,goods);
                    manager.updateGoods(goods);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(StoreActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}

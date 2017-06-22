package com.superscholar.android.activity;

import android.content.Intent;
import android.graphics.Rect;
import toan.android.floatingactionmenu.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import toan.android.floatingactionmenu.FloatingActionsMenu;
import com.superscholar.android.R;
import com.superscholar.android.entity.MemoItem;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.adapter.MemoAdapter;
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class MemoActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private FloatingActionButton fabAdd;
    private FloatingActionsMenu fam;
    private FloatingActionButton fabModify;
    private FloatingActionButton fabDelete;

    private TextView hintText;
    private LinearLayout detailLayout;

    private TextView titleText;
    private TextView timeText;
    private TextView contentText;

    private MemoAdapter adapter;
    private Transition explode;

    private boolean displayStatus=false;

    private List<MemoItem>memoItems;

    private int p=-1;  //position记录

    //recyclerView初始化
    private void initRecyclerView(){
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    //数据初始化
    private void initData(){
        LitePal.getDatabase();
        memoItems= DataSupport.findAll(MemoItem.class);
        hintText=(TextView)findViewById(R.id.memo_hintText);
        recyclerView=(RecyclerView)findViewById(R.id.memo_recyclerView);
        if(memoItems.size()==0){
            hintText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    //变量初始化
    private void initVariable(){
        toolbar=(Toolbar)findViewById(R.id.memo_toolbar);
        fabAdd=(FloatingActionButton)findViewById(R.id.memo_fab_add);
        fam=(FloatingActionsMenu)findViewById(R.id.memo_fam);
        fabModify=(FloatingActionButton)findViewById(R.id.memo_fab_modify);
        fabDelete=(FloatingActionButton)findViewById(R.id.memo_fab_delete);
        detailLayout=(LinearLayout)findViewById(R.id.memo_detail_layout);
        titleText=(TextView)findViewById(R.id.memo_detail_title);
        timeText=(TextView)findViewById(R.id.memo_detail_time);
        contentText=(TextView)findViewById(R.id.memo_detail_content);
        adapter=new MemoAdapter(memoItems);
    }

    //菜单栏初始化
    private void initToolbar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //浮动按钮初始化
    private void initFab(){
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MemoActivity.this,MemoCreateActivity.class);
                intent.putExtra("title","");
                intent.putExtra("content","");
                startActivityForResult(intent,0);
            }
        });
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"确认要删除该备忘录吗？",Snackbar.LENGTH_SHORT)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MemoItem memoItem=memoItems.get(p);
                                DataSupport.deleteAll(MemoItem.class,"title = ? and time = ? and content = ?",
                                        memoItem.getTitle(),memoItem.getTime(),memoItem.getContent());
                                memoItems.remove(p);
                                Toast.makeText(MemoActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                if(memoItems.size()==0){
                                    hintText.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }

                                explode.setDuration(600);
                                TransitionManager.beginDelayedTransition(recyclerView, explode);
                                recyclerView.setAdapter(adapter);

                                detailLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                toolbar.setVisibility(View.VISIBLE);
                                fabAdd.setVisibility(View.VISIBLE);
                                fam.setVisibility(View.GONE);
                                fam.collapse();
                                displayStatus=false;
                            }
                        }).show();
            }
        });
        fabModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MemoActivity.this,MemoCreateActivity.class);
                MemoItem memoItem=memoItems.get(p);
                intent.putExtra("title",memoItem.getTitle());
                intent.putExtra("content",memoItem.getContent());
                startActivityForResult(intent,1);
            }
        });
    }

    //recyclerView子项点击事件，在adapter中被调用
    public void recyclerViewItemOnClicked(View view,int position) {
        displayStatus=true;
        p=position;
        final Rect viewRect=new Rect();
        view.getGlobalVisibleRect(viewRect);
        explode = new Explode().setEpicenterCallback(new Transition.EpicenterCallback() {
            @Override
            public Rect onGetEpicenter(Transition transition) {
                return viewRect;
            }
        });
        explode.setDuration(600);
        TransitionManager.beginDelayedTransition(recyclerView, explode);
        recyclerView.setAdapter(null);

        titleText.setText(memoItems.get(position).getTitle());
        timeText.setText(memoItems.get(position).getTime());
        contentText.setText(memoItems.get(position).getContent());

        recyclerView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        detailLayout.setVisibility(View.VISIBLE);
        fabAdd.setVisibility(View.GONE);
        fam.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(600);
                }catch(Exception e) {
                    e.printStackTrace();
                }
                adapter.clickStatus=false;
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!displayStatus){
                finish();
            }else{
                explode.setDuration(600);
                TransitionManager.beginDelayedTransition(recyclerView, explode);
                recyclerView.setAdapter(adapter);

                detailLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                fabAdd.setVisibility(View.VISIBLE);
                fam.setVisibility(View.GONE);
                fam.collapse();
                displayStatus=false;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        initData();
        initVariable();
        initToolbar();
        initRecyclerView();
        initFab();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(resultCode==RESULT_OK){
                    String title=data.getStringExtra("title");
                    String time=data.getStringExtra("time");
                    String content=data.getStringExtra("content");
                    MemoItem memoItem=new MemoItem(title,time,content);
                    memoItem.save();
                    if(hintText.getVisibility()==View.VISIBLE){
                        hintText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    memoItems.add(memoItem);
                    adapter.notifyItemInserted(memoItems.size()-1);
                    Toast.makeText(MemoActivity.this,"创建成功",Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if(resultCode==RESULT_OK) {
                    String title = data.getStringExtra("title");
                    String content = data.getStringExtra("content");
                    MemoItem memoItem = memoItems.get(p);
                    MemoItem temp = new MemoItem(title, memoItem.getTime(), content);
                    temp.updateAll("title = ? and time = ? and content = ?",
                            memoItem.getTitle(), memoItem.getTime(), memoItem.getContent());
                    memoItem.setContent(content);
                    memoItem.setTitle(title);
                    Toast.makeText(MemoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();

                    explode.setDuration(600);
                    TransitionManager.beginDelayedTransition(recyclerView, explode);
                    recyclerView.setAdapter(adapter);

                    detailLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    fabAdd.setVisibility(View.VISIBLE);
                    fam.setVisibility(View.GONE);
                    fam.collapse();
                    displayStatus = false;
                }
                break;
            default:
        }
    }
}

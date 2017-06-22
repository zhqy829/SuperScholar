package com.superscholar.android.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.adapter.QuestionAdapter;
import com.superscholar.android.entity.QuestionAnswerItem;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends BaseActivity {

    private List<QuestionAnswerItem>mQAList=new ArrayList<>();

    //QA数据初始化
    private void initData(){
        mQAList.add(new QuestionAnswerItem("Q:为什么我的提醒时间不准确？",
                "A:可能是由于您手机长时间处于锁屏状态，系统为节省电量导致提醒无法精准执行哦。"));
        mQAList.add(new QuestionAnswerItem("Q:为什么我设置了提醒却没有正常提醒？",
                "A:可能是由于您长时间未打开程序，后台服务被关闭导致提醒失效，强烈建议不要用别的程序关闭本程序，以免导致程序无法正常运行。"));
        mQAList.add(new QuestionAnswerItem("Q:为什么专心时间过了几分钟了却仅增加了1分钟？",
                "A:由于部分手机品牌对系统的优化，导致计时的机制不能正常执行，建议您咨询手机生产商，对此我们深表歉意。"));
        mQAList.add(new QuestionAnswerItem("Q:为什么我的学分绩异常减少了？",
                "A:建议您在滑动菜单的[我的学分绩]处查看学分绩明细哦。"));
        mQAList.add(new QuestionAnswerItem("Q:为什么我的程序会闪退？",
                "A:建议您在[反馈建议]处将您遇到的问题详细告诉我们，我们会尽快修复。"));
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.question_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //recyclerView初始化
    private void initRecyclerView(){
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.question_recyclerView);
        QuestionAdapter adapter=new QuestionAdapter(mQAList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        initToolbar();
        initData();
        initRecyclerView();
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

package com.superscholar.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.activity.TargetCreateActivity;
import com.superscholar.android.activity.TargetDetailActivity;
import com.superscholar.android.adapter.TargetAdapter;
import com.superscholar.android.control.SlidableFloatingActionButton;
import com.superscholar.android.entity.TargetItem;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.TargetBaseManager;
import com.twotoasters.jazzylistview.effects.WaveEffect;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zhqy on 2017/6/14.
 */

public class TargetFragment extends Fragment{

    //requestCode
    public static final int REQUEST_CREATETARGET = 1;
    public static final int REQUEST_TARGETDETAIL = 2;

    //resultCode RESULT_OK==-1,RESULT_CANCELED==0,RESULT_FIRST_USER==1
    public static final int RESULT_TARGETDETAIL_UPDATE=2;
    public static final int RESULT_TARGETDETAIL_DELETE=3;

    private TargetAdapter adapter;
    private List<TargetItem> targetList=new ArrayList<>();;
    private TextView hintText;    //目标为空时提示信息显示
    private RecyclerView recyclerView;

    private TargetBaseManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_target,container,false);

        initTargetBaseManager();
        initControl(view);
        initButton(view);
        return view;
    }

    private void initButton(View view){
        SlidableFloatingActionButton fab =(SlidableFloatingActionButton) view.findViewById(R.id.target_fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), TargetCreateActivity.class);
                startActivityForResult(intent,REQUEST_CREATETARGET);
            }
        });
    }

    private void initTargetBaseManager(){
        manager=new TargetBaseManager(getActivity());
        targetList=manager.getTargetItemList();
    }

    private void initControl(View view){
        recyclerView =(RecyclerView)view.findViewById(R.id.taget_recyclerView);
        hintText=(TextView)view.findViewById(R.id.target_hintText);

        if(targetList.isEmpty()){
            hintText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        JazzyRecyclerViewScrollListener jazzyScrollListener=new JazzyRecyclerViewScrollListener();
        recyclerView.addOnScrollListener(jazzyScrollListener);
        jazzyScrollListener.setTransitionEffect(new WaveEffect());

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter=new TargetAdapter(targetList);

        adapter.setOnItemClickedListener(new TargetAdapter.OnItemClickedListener() {
            @Override
            public void onButtonClicked(int p) {
                //打卡
            }

            @Override
            public void onViewClicked(int p) {
                //进入详细页
                Intent intent=new Intent(getActivity(),TargetDetailActivity.class);

                intent.putExtra("targetItem",targetList.get(p));
                intent.putExtra("position",p);
                startActivityForResult(intent,REQUEST_TARGETDETAIL);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CREATETARGET:
                if(resultCode==RESULT_OK){
                    TargetItem targetItem=data.getParcelableExtra("targetCreate");
                    manager.insertItem(targetItem);
                    targetList.add(targetItem);
                    adapter.notifyItemInserted(targetList.size()-1);
                    Toast.makeText(getActivity(),"创建成功",Toast.LENGTH_SHORT).show();
                    if(hintText.getVisibility()==View.VISIBLE){
                        hintText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case REQUEST_TARGETDETAIL:
                if(resultCode==RESULT_TARGETDETAIL_UPDATE){
                    int position=data.getIntExtra("position",0);
                    TargetItem targetItem=data.getParcelableExtra("targetItem");
                    targetList.set(position,targetItem);
                    adapter.notifyItemChanged(position);
                    manager.updateItem(targetItem);
                }else if(resultCode==RESULT_TARGETDETAIL_DELETE){
                    int position=data.getIntExtra("position",0);
                    TargetItem targetItem=targetList.get(position);
                    targetList.remove(position);
                    adapter.notifyItemRemoved(position);
                    manager.deleteItem(targetItem);
                    if(targetList.isEmpty()){
                        hintText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    //binder.synchronizeTargetItems(targetList);
                }
                break;
            default:
        }
    }
}

package com.superscholar.android.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.superscholar.android.R;
import com.superscholar.android.entity.TargetItem;
import com.superscholar.android.tools.BoundsTime;
import com.superscholar.android.tools.LogUtil;

import java.util.List;

/**
 * Created by zhqy on 2017/6/22.
 */

public class TargetAdapter extends RecyclerView.Adapter<TargetAdapter.ViewHolder> {

    public interface OnItemClickedListener{
        void onButtonClicked(int p);
        void onViewClicked(int p);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout layout;
        private TextView nameText;
        private TextView weekText;
        private TextView statusText;
        private ImageView signButton;
        private LinearLayout remindLayout;
        private TextView remindText;

        public ViewHolder(View view){
            super(view);
            layout=(LinearLayout)view.findViewById(R.id.target_item_layout);
            nameText=(TextView)view.findViewById(R.id.target_item_targetName);
            weekText=(TextView)view.findViewById(R.id.target_item_targetWeek);
            statusText=(TextView)view.findViewById(R.id.target_item_targetStatus);
            signButton=(ImageView)view.findViewById(R.id.target_item_signButton);
            remindLayout=(LinearLayout)view.findViewById(R.id.target_item_remindLayout);
            remindText=(TextView)view.findViewById(R.id.target_item_remindText);
        }
    }

    private List<TargetItem> targetItems;
    private OnItemClickedListener listener;

    public TargetAdapter(List<TargetItem>targetItems){
        this.targetItems=targetItems;
    }

    public void setOnItemClickedListener(OnItemClickedListener listener){
        this.listener=listener;
    }

    @Override
    public TargetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.target_item,parent,false);
        final TargetAdapter.ViewHolder holder=new TargetAdapter.ViewHolder(view);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //显示目标详细页
                listener.onViewClicked(holder.getAdapterPosition());
            }
        });


        holder.signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打卡
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TargetItem targetItem=targetItems.get(position);
        holder.nameText.setText(targetItem.getName());
        holder.weekText.setText(targetItem.getCurrentWeek()+"/"+targetItem.getLastedWeek()+"周");
        if(targetItem.isCheck()){
            holder.layout.setBackgroundColor(Color.parseColor("#BFEFFF"));
            holder.signButton.setImageResource(R.drawable.target_item_sign_2);
        }

        if(!targetItem.isNeedRemind()){
            holder.remindLayout.setVisibility(View.GONE);
        } else{
            String h,m;
            BoundsTime remindTime=targetItem.getRemindTime();
            if(remindTime.getHour()<10){
                h="0"+String.valueOf(remindTime.getHour());
            }else {
                h=String.valueOf(remindTime.getHour());
            }
            if(remindTime.getMin()<10){
                m="0"+String.valueOf(remindTime.getMin());
            }else{
                m=String.valueOf(remindTime.getMin());
            }
            holder.remindText.setText(h+":"+m);
        }

        if(!targetItem.isValid()){
            holder.statusText.setText("目标已失效");
            holder.statusText.setTextColor(Color.parseColor("#EE0000"));
        } else if(!targetItem.isTodaySign()) {
            holder.statusText.setText("今日未打卡");
            holder.statusText.setTextColor(Color.parseColor("#969696"));
        } else{
            holder.statusText.setText("本周打卡"+targetItem.getWeekSignTimes()[targetItem.getCurrentWeek()-1]+"次，目标" +
                    targetItem.getTimesPerWeek()+"次");
            holder.statusText.setTextColor(Color.parseColor("#1C86EE"));
        }

    }

    @Override
    public int getItemCount() {
        return targetItems.size();
    }
}

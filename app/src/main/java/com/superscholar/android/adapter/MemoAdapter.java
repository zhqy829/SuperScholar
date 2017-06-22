package com.superscholar.android.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.entity.MemoItem;
import com.superscholar.android.activity.MemoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/1.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private List<MemoItem> memoItems;
    private List<String> colors=new ArrayList<>();
    public boolean clickStatus=false;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView titleText;
        TextView contentText;
        TextView timeText;

        public ViewHolder(View view){
            super(view);
            cardView =(CardView)view;
            titleText=(TextView)view.findViewById(R.id.memo_item_title);
            contentText=(TextView)view.findViewById(R.id.memo_item_content);
            timeText=(TextView)view.findViewById(R.id.memo_item_time);
        }
    }

    public MemoAdapter(List <MemoItem> memoItems){
        this.memoItems=memoItems;
        colors.add("#FFE7BA");
        colors.add("#fccbcb");
        colors.add("#BFEFFF");
        colors.add("#c3fec3");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clickStatus){
                    clickStatus=true;
                    int position=holder.getAdapterPosition();
                    MemoActivity memoActivity=(MemoActivity)view.getContext();
                    memoActivity.recyclerViewItemOnClicked(view,position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemoItem memoItem=memoItems.get(position);
        holder.cardView.setCardBackgroundColor(Color.parseColor(colors.get(position%4)));
        holder.titleText.setText(memoItem.getTitle());
        holder.contentText.setText(memoItem.getContent());
        holder.timeText.setText(memoItem.getTime());
    }

    @Override
    public int getItemCount() {
        return memoItems.size();
    }
}

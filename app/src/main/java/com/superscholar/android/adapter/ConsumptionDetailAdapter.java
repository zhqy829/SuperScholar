package com.superscholar.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.entity.ConsumptionDetail;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by zhqy on 2017/7/2.
 */

public class ConsumptionDetailAdapter extends RecyclerView.Adapter<ConsumptionDetailAdapter.ViewHolder> {

    public interface OnItemClickedListener{
        void onItemClicked(int position);
    }

    private List<ConsumptionDetail> detailList;
    private OnItemClickedListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView dateText;
        private TextView timeText;
        private TextView nameText;
        private TextView priceText;

        public ViewHolder(View view) {
            super(view);
            this.view=view;
            dateText=(TextView)view.findViewById(R.id.consumption_detail_date);
            timeText=(TextView)view.findViewById(R.id.consumption_detail_time);
            nameText=(TextView)view.findViewById(R.id.consumption_detail_name);
            priceText=(TextView)view.findViewById(R.id.consumption_detail_price);
        }
    }

    public ConsumptionDetailAdapter(List<ConsumptionDetail>detailList){
        this.detailList=detailList;
    }

    public void setOnItemClickedListener(OnItemClickedListener listener){
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.consumption_detail_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(position);
            }
        });
        holder.dateText.setText(detailList.get(position).getDate());
        holder.timeText.setText(detailList.get(position).getTime());
        holder.nameText.setText(detailList.get(position).getName());
        holder.priceText.setText("-"+String.valueOf(detailList.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }
}

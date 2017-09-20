package com.superscholar.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.entity.EventItem;

import java.util.List;

/**
 * Created by zhqy on 2017/9/4.
 */

public class EventItemAdapter extends RecyclerView.Adapter<EventItemAdapter.ViewHolder>{

    private List<EventItem>events;

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View upLineView;
        private View dowmLineView;

        private TextView startTimeText;
        private TextView endTimeText;
        private TextView lastedTimeText;

        private TextView nameText;
        private ImageView typeImage;

        public ViewHolder(View view) {
            super(view);
            //this.view=view;
            upLineView=view.findViewById(R.id.event_item_line_up);
            dowmLineView=view.findViewById(R.id.event_item_line_dowm);
            startTimeText=(TextView)view.findViewById(R.id.event_item_time_start);
            endTimeText=(TextView)view.findViewById(R.id.event_item_time_end);
            lastedTimeText=(TextView)view.findViewById(R.id.event_item_time_lasted);
            nameText=(TextView)view.findViewById(R.id.event_item_text_name);
            typeImage=(ImageView)view.findViewById(R.id.event_item_image_type);
        }
    }

    public EventItemAdapter(List<EventItem>events){
        this.events=events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        EventItem event=events.get(position);

        holder.startTimeText.setText(event.getStartTime().toString());
        holder.endTimeText.setText(event.getEndTime().toString());
        holder.lastedTimeText.setText(event.getLastedTime()+"\n分钟");
        holder.nameText.setText(event.getName());

        if(event.getType().equals("投资")){
            holder.typeImage.setImageResource(R.drawable.event_icon_invest);
        }else if(event.getType().equals("固定")){
            holder.typeImage.setImageResource(R.drawable.event_icon_regular);
        }else if(event.getType().equals("睡眠")){
            holder.typeImage.setImageResource(R.drawable.event_icon_sleep);
        }else if(event.getType().equals("浪费")){
            holder.typeImage.setImageResource(R.drawable.event_icon_waste);
        }

        if(position==0){
            holder.upLineView.setVisibility(View.INVISIBLE);
        }

        if(position==events.size()-1){
            holder.dowmLineView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}

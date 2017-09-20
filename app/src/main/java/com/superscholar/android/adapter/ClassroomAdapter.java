package com.superscholar.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.entity.Classroom;

import java.util.List;

/**
 * Created by zhqy on 2017/9/17.
 */

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ViewHolder>{
    private List<Classroom> classroomList;

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nameText;
        private TextView ridText;


        public ViewHolder(View view) {
            super(view);
            nameText=(TextView)view.findViewById(R.id.classroom_item_text_name);
            ridText=(TextView)view.findViewById(R.id.classroom_item_text_rid);
        }
    }

    public ClassroomAdapter(List<Classroom>classroomList){
        this.classroomList=classroomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.classroom_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameText.setText(classroomList.get(position).getName());
        holder.ridText.setText(classroomList.get(position).getRid());
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }
}

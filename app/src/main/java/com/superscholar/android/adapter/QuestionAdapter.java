package com.superscholar.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.entity.QuestionAnswerItem;

import java.util.List;

/**
 * Created by Administrator on 2017/4/1.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<QuestionAnswerItem> mQAList;

    public QuestionAdapter(List<QuestionAnswerItem> qAList){
        mQAList=qAList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionAnswerItem questionAnswer=mQAList.get(position);
        holder.qTextView.setText(questionAnswer.getQuestion());
        holder.aTextView.setText(questionAnswer.getAnswer());
    }

    @Override
    public int getItemCount() {
        return mQAList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView qTextView;
        TextView aTextView;

        public ViewHolder(View view){
            super(view);
            qTextView=(TextView)view.findViewById(R.id.question_item_qTextView);
            aTextView=(TextView)view.findViewById(R.id.question_item_aTextView);
        }
    }


}

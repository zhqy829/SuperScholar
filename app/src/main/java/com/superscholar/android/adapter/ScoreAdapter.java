package com.superscholar.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.entity.Score;

import java.util.List;

/**
 * Created by zhqy on 2017/9/17.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder>{

    private List<Score> scoreList;

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nameText;
        private TextView creditText;
        private TextView scoreText;


        public ViewHolder(View view) {
            super(view);
            nameText=(TextView)view.findViewById(R.id.score_item_text_name);
            creditText=(TextView)view.findViewById(R.id.score_item_text_credit);
            scoreText=(TextView)view.findViewById(R.id.score_item_text_score);
        }
    }

    public ScoreAdapter(List<Score>scoreList){
        this.scoreList=scoreList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameText.setText(scoreList.get(position).getName());
        holder.creditText.setText(scoreList.get(position).getCredit());
        holder.scoreText.setText(scoreList.get(position).getScore());
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}

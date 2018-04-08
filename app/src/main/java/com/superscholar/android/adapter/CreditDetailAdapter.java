package com.superscholar.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.entity.CreditDetail;

import java.util.List;

/**
 * @author zhqy
 * @date 2018/4/8
 */

public class CreditDetailAdapter extends RecyclerView.Adapter<CreditDetailAdapter.ViewHolder> {

    private List<CreditDetail> detailList;
    private ConsumptionDetailAdapter.OnItemClickedListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView dateText;
        private TextView describeText;
        private TextView changesText;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            dateText = view.findViewById(R.id.credit_detail_date);
            describeText = view.findViewById(R.id.credit_detail_describe);
            changesText = view.findViewById(R.id.credit_detail_changes);
        }
    }

    public CreditDetailAdapter(List<CreditDetail>detailList){
        this.detailList = detailList;
    }

    @Override
    public CreditDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_detail_item,parent,false);
        return new CreditDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreditDetailAdapter.ViewHolder holder, final int position) {
        CreditDetail detail = detailList.get(position);
        String date = detail.getYear() + "/" + detail.getMonth() + "/" +detail.getDay();
        holder.dateText.setText(date);
        holder.describeText.setText(detail.getDetail());
        holder.changesText.setText(String.valueOf(detail.getChanges()));
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }
}

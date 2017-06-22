package com.superscholar.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.superscholar.android.R;
import com.superscholar.android.entity.Menu;

import java.util.List;

/**
 * Created by Administrator on 2017/4/5.
 */

public class SlidingMenuListAdapter extends ArrayAdapter<Menu>{
    private int resourceId;

   public SlidingMenuListAdapter(Context context,int resourceId,List<Menu>objects){
       super(context,resourceId,objects);
       this.resourceId=resourceId;
   }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Menu menu=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.menuImage=(ImageView)view.findViewById(R.id.sliding_menulist_item_image);
            viewHolder.menuText=(TextView)view.findViewById(R.id.sliding_menulist_item_text);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.menuImage.setImageResource(menu.getImageId());
        viewHolder.menuText.setText(menu.getText());
        return view;
    }
    class ViewHolder{
        ImageView menuImage;
        TextView menuText;
    }
}

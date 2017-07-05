package com.superscholar.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.entity.Goods;

import java.io.File;
import java.util.List;

/**
 * Created by zhqy on 2017/6/28.
 */

public class GoodsViewAdapter extends ArrayAdapter<Goods> {

    public interface OnPurchaseButtonClickedListener{
        void onPurchaseButtonClicked(int position);
    }

    private int resourceId;
    private OnPurchaseButtonClickedListener listener;

    public GoodsViewAdapter(Context context, int resourceId, List<Goods> objects,OnPurchaseButtonClickedListener listener){
        super(context,resourceId,objects);
        this.resourceId=resourceId;
        this.listener=listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Goods goods=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        ImageView imageView=(ImageView) view.findViewById(R.id.goods_item_image);
        String imagePath="/sdcard/photos/SuperScholar/"+goods.getUuid().toString()+".jpg";
        File image = new File(imagePath);
        if (image.exists()) {
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.goods_item_image_not_found);
        }


        TextView nameText=(TextView)view.findViewById(R.id.goods_item_name);
        nameText.setText(goods.getName());

        TextView detailText=(TextView)view.findViewById(R.id.goods_item_detail);
        detailText.setText(goods.getDetail());

        TextView priceText=(TextView)view.findViewById(R.id.goods_item_price);
        priceText.setText(String.valueOf(goods.getPrice()));

        ImageView purchaseButton=(ImageView)view.findViewById(R.id.goods_item_purchaseButton);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPurchaseButtonClicked(position);
            }
        });
        return view;
    }
}
package com.superscholar.android.holder;


import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


import com.bigkoo.convenientbanner.holder.Holder;


/**
 * Created by Administrator on 2017/4/4.
 */

public class NewsBannerHolderView implements Holder<String>{
    private TextView textView;

    @Override
    public View createView(Context context) {
        textView = new TextView(context);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        return textView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        textView.setText(data);
    }

}
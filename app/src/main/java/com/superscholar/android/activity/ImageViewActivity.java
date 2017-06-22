package com.superscholar.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;

public class ImageViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView imageView=(ImageView)findViewById(R.id.image_view_imageView);
        String imageUrl=getIntent().getStringExtra("imageUrl");
        ImageLoader.getInstance().displayImage(imageUrl,imageView);


        //单击返回
        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.activity_image_view);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}

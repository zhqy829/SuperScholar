package com.superscholar.android.control;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;


import toan.android.floatingactionmenu.FloatingActionButton;

/**
 * Created by Administrator on 2017/4/20.
 */

public class SlidableFloatingActionButton extends FloatingActionButton implements View.OnTouchListener{

    private float scale;
    private boolean isMove;
    private int lastX;
    private int lastY;
    int screenWidth;
    int screenHeight;
    private long startTime = 0;
    private long endTime = 0;

    public SlidableFloatingActionButton(Context context) {
        this(context, null);
        this.setOnTouchListener(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        scale=screenHeight/960.0f;
    }

    public SlidableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        scale=screenHeight/960.0f;
    }

    public SlidableFloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnTouchListener(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        scale=screenHeight/960.0f;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            isMove=false;
            startTime = System.currentTimeMillis();
            lastX=(int)motionEvent.getRawX();
            lastY=(int)motionEvent.getRawY();
        } else if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
            isMove=true;
            int dx =(int)motionEvent.getRawX() - lastX;
            int dy =(int)motionEvent.getRawY() - lastY;

            int left = view.getLeft() + dx;
            int top = view.getTop() + dy;
            int right = view.getRight() + dx;
            int bottom = view.getBottom() + dy;
            if(left < 0){
                left = 0;
                right = left + view.getWidth();
            }
            if(right > screenWidth){
                right = screenWidth;
                left = right - view.getWidth();
            }
            if(top < 0){
                top = 0;
                bottom = top + view.getHeight();
            }
            if(bottom > screenHeight-200*scale){
                bottom = (int)(screenHeight-200*scale);
                top = bottom - view.getHeight();
            }
            view.layout(left, top, right, bottom);

        }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
            endTime = System.currentTimeMillis();
            if ((endTime - startTime) > 0.2 * 1000L) {
                isMove = true;
            } else {
                isMove = false;
            }
        }
        lastX = (int) motionEvent.getRawX();
        lastY = (int) motionEvent.getRawY();
        this.setPressed(false);
        return isMove;
    }

}

package com.superscholar.android.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import toan.android.floatingactionmenu.FloatingActionButton;

/**
 * Created by Administrator on 2017/4/20.
 */

public class SlidableFloatingActionButton extends FloatingActionButton implements View.OnTouchListener{

    private boolean isMove;
    private int lastX;
    private int lastY;
    private long startTime = 0;
    private long endTime = 0;

    public SlidableFloatingActionButton(Context context) {
        this(context, null);
        this.setOnTouchListener(this);
    }

    public SlidableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    public SlidableFloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x=(int)motionEvent.getRawX();
        int y=(int)motionEvent.getRawY();
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            isMove=false;
            startTime = System.currentTimeMillis();
            lastX=(int)motionEvent.getRawX();
            lastY=(int)motionEvent.getRawY();
        } else if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
            isMove=true;
            int deltaX=x-lastX;
            int deltaY=y-lastY;
            int translationX=(int)this.getTranslationX()+deltaX;
            int translationY=(int)this.getTranslationY()+deltaY;
            this.setTranslationX(translationX);
            this.setTranslationY(translationY);
        }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
            endTime = System.currentTimeMillis();
            if ((endTime - startTime) > 0.1 * 1000L) {
                isMove = true;
            } else {
                isMove = false;
            }
        }
        lastX=x;
        lastY=y;
        this.setPressed(!isMove);
        return isMove;
    }

}

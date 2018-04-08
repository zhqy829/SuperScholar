package com.superscholar.android.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.superscholar.android.R;

import toan.android.floatingactionmenu.FloatingActionButton;

/**
 * Created by Administrator on 2017/4/20.
 */

public class SlidableFloatingActionButton extends FloatingActionButton {

    // 滑动时间的阀值，DOWN和UP之间的时间间隔大于该时间认为在滑动
    private static final long MOVE_SLOP = 200L;

    // 缩放比例
    private float mScale;

    // 上一次滑动的x,y轴坐标
    private int mLastX;
    private int mLastY;

    // 一个事件序列中产生ACTION_DOWN的时间
    private long mStartTime;

    // 屏幕的宽高像素
    private int mScreenWidth;
    private int mScreenHeight;

    private boolean mSlidable = true;

    public SlidableFloatingActionButton(Context context) {
        this(context, null);
    }

    public SlidableFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidableFloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        if(attrs != null){
            // 获取自定义属性
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidableFloatingActionButton);
            mSlidable = ta.getBoolean(R.styleable.SlidableFloatingActionButton_slidable, true);
            ta.recycle();
        }

        // 根据分辨率计算缩放比例
        // 比例用于适配不同手机的滑动边界
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        // 基于分辨率高度为960的手机进行开发
        mScale = mScreenHeight / 960.0f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mSlidable){
            return super.onTouchEvent(event);
        }
        boolean isMove = false;
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                isMove = false;
                // 防止按钮拖动时事件被父View给拦截
                getParent().requestDisallowInterceptTouchEvent(true);
                mStartTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                isMove = true;
                setPressed(false);

                int dx = x - mLastX;
                int dy = y - mLastY;

                int left = getLeft() + dx;
                int top = getTop() + dy;
                int right = getRight() + dx;
                int bottom = getBottom() + dy;

                // 滑出边界处理
                if(left < 0){
                    left = 0;
                    right = left + getWidth();
                }
                if(right > mScreenWidth){
                    right = mScreenWidth;
                    left = right - getWidth();
                }
                if(top < 0){
                    top = 0;
                    bottom = top + getHeight();
                }
                if(bottom > mScreenHeight - 200 * mScale){
                    bottom = (int)(mScreenHeight - 200 * mScale);
                    top = bottom - getHeight();
                }

                // 重新布局实现滑动效果
                layout(left, top, right, bottom);

                break;

            case MotionEvent.ACTION_UP:
                long endTime = System.currentTimeMillis();
                if ((endTime - mStartTime) > MOVE_SLOP) {
                    // 大于该时间认为在滑动
                    // 不触发点击响应
                    isMove = true;
                } else {
                    isMove = false;
                }
                break;

            default:
        }

        mLastX = x;
        mLastY = y;
        return isMove || super.onTouchEvent(event);
    }

    public boolean isSlidable() {
        return mSlidable;
    }

    public void setSlidable(boolean slidable) {
        mSlidable = slidable;
    }

}

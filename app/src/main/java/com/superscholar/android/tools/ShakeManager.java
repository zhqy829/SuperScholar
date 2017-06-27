package com.superscholar.android.tools;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

/**
 * 定时摇晃次数检测
 * Created by zhqy on 2017/6/9.
 */

public class ShakeManager implements SensorEventListener {

    //回调在主线程中，可以直接更新UI
    public interface OnResponseListener {
        //每次晃动
        void onShake(int count);
        //规定时间内晃够指定次数
        void onSuccess();
        //规定时间内未晃够指定次数
        void onTimeOut();
    }

    // 速度阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_SHRESHOLD = 5000;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 70;
    //晃动目标次数，规定时间达到次数回调onSuccess
    public static final int SUCCESS_COUNT=25;
    //时间限制，单位秒
    public static final int TIME_LIMIT=30;

    // 传感器管理器
    private SensorManager sensorManager;
    // 传感器
    private Sensor sensor;
    // 回调接口
    private OnResponseListener listener;

    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    // 上次检测时间
    private long lastUpdateTime;
    //摇晃计数器
    private int counter=0;
    //计时变量
    private int timer=TIME_LIMIT;

    //处理定时
    private Handler handler;
    //检测是否开始
    private boolean isStart=false;

    public ShakeManager(Context context) {
        // 获得传感器管理器
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // 获得重力传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            handler = new Handler(){
                public void handleMessage(Message msg){
                    if(!isStart)return;
                    if(listener==null) ShakeManager.this.stop();
                    switch (msg.what) {
                        case 1:
                            timer--;
                            if(timer > 0){
                                Message message = handler.obtainMessage(1);
                                handler.sendMessageDelayed(message, 1000);
                            }else{
                                listener.onTimeOut();
                                ShakeManager.this.stop();
                            }
                    }
                    super.handleMessage(msg);
                }
            };
        }
    }

    // 开始检测
    public void start() {
        if(isStart) return;
        if (sensor != null) {
            // 注册
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
            //开始计时
            Message message = handler.obtainMessage(1);
            handler.sendMessageDelayed(message, 1000);
            isStart=true;
        }
    }

    // 停止检测，检测结束后自动调用
    private void stop() {
        if(!isStart) return;
        sensorManager.unregisterListener(this);
        isStart=false;
        timer=TIME_LIMIT;
        counter=0;
    }

    public void setOnResponseListener(OnResponseListener listener) {
        if(isStart) return;
        this.listener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(listener==null) this.stop();
        // 现在检测时间
        long currentUpdateTime = System.currentTimeMillis();
        // 两次检测的时间间隔
        long timeInterval = currentUpdateTime - lastUpdateTime;
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPTATE_INTERVAL_TIME) return;
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime;
        // 获得x,y,z坐标
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / timeInterval * 10000;
        // 达到速度阀值，发出提示
        if (speed >= SPEED_SHRESHOLD){
            counter++;
            listener.onShake(counter);
            if(counter==SUCCESS_COUNT){
                listener.onSuccess();
                this.stop();
            }
        }
    }

}

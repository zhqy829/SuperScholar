package com.superscholar.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

public class ConcentrationService extends Service {

    public interface OnMinutePassedListener{
        void onMinutePassed();
    }

    private ConcentrationBinder mBinder;
    private OnMinutePassedListener listener;
    private AlarmManager manager;
    private PendingIntent pi;
    private boolean timingRefreshIsStart=false;

    public ConcentrationService() {
        mBinder=new ConcentrationBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(timingRefreshIsStart){
            listener.onMinutePassed();
        }else{
            timingRefreshIsStart=true;
        }
        long triggerAtTime = SystemClock.elapsedRealtime() +60*1000;
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, ConcentrationService.class);
        pi = PendingIntent.getService(this, 0, i, 0);
        if(Build.VERSION.SDK_INT>=19) //Android 4.4
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        else
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ConcentrationBinder extends Binder{

        public void swtOnMinutePassedListener(OnMinutePassedListener listener){
            ConcentrationService.this.listener=listener;
        }

        public void stopConcentrationService() {
            stopSelf();
            manager.cancel(pi);
            timingRefreshIsStart=false;
        }

        public boolean isTimingRefreshStart(){
            return timingRefreshIsStart;
        }

    }
}

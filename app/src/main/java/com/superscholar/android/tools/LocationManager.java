package com.superscholar.android.tools;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

/**
 * 定位比对
 * Created by zhqy on 2017/6/10.
 */

public class LocationManager implements BDLocationListener {

    //比对结果回调接口，子线程中回调，不能直接更新UI
    public interface ComparisonResultListener{
        //比对通过
        void onPassed(int mode,String name);
        //比对失败
        void onFailed(int mode);
        //定位出错
        void onError(int error);
    }

    //运动的POI ID
    private final static String POIID_PLAYINGFIELD="4577342575705663558";  //中国石油大学塑胶运动场
    private final static String POIID_GYMNASIUM="11370668780034195840";  //中国石油大学(华东)-体育馆
    private final static String POIID_BASKETBALLCOURT="4167036225892096349";  //篮球场

    //自习的POI ID
    private final static String POIID_LIBRARY ="17730775486482612223";  //中国石油大学-图书馆
    private final static String POIID_LECTUREROOM ="11363526718154128605";  //中国石油大学-讲堂群
    private final static String POIID_EASTRING ="10596688468717976683";  //中国石油大学-讲堂群东环
    private final static String POIID_SOUTHHALL ="17752017563955899545";  //中国石油大学讲堂群南堂
    private final static String POIID_SOUTHCLASSROOM ="936248011590797006";  //中国石油大学-南教楼

    private static final int MODE_STOP=0;  //停止
    public static final int MODE_EXER=1;  //运动
    public static final int MODE_STUDY=2;  //自习

    private int mode=MODE_STOP;  //检测模式

    private LocationClient mLocationClient = null;

    private ComparisonResultListener listener=null;

    public LocationManager(Context context){
        //声明LocationClient类
        mLocationClient = new LocationClient(context);

        //注册监听函数
        mLocationClient.registerLocationListener(this);

        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);

        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);

        mLocationClient.setLocOption(option);
    }

    //设置比对结果回调接口
    public void setComparisonResultListener(ComparisonResultListener listener){
        if(mode!=MODE_STOP) return;
        this.listener=listener;
    }

    //开始定位
    public void startLocation(int mode){
        if(this.mode!=MODE_STOP) return;
        if(mode==MODE_EXER||mode==MODE_STUDY){
            this.mode=mode;
            mLocationClient.start();
        }
    }

    //停止定位，定位结束自动调用，无需额外调用
    private void stopLocation(){
        if(mode==MODE_STOP) return;
        mLocationClient.stop();
        mode=MODE_STOP;
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(bdLocation!=null){
            //61:GPS定位成功 161：网络定位成功 66：离线定位成功
            int type=bdLocation.getLocType();

            if(type==61|| type==161||type==66){
                List<Poi> pois=bdLocation.getPoiList();
                boolean isPass=false;
                switch(mode){
                    case MODE_EXER:
                        for(Poi p:pois){
                            String id=p.getId();
                            if(id.equals(POIID_PLAYINGFIELD)||
                                    id.equals(POIID_BASKETBALLCOURT)||
                                    id.equals(POIID_GYMNASIUM)){
                                listener.onPassed(mode,p.getName());
                                isPass=true;
                                break;
                            }
                        }
                        if(!isPass) listener.onFailed(mode);
                        break;
                    case MODE_STUDY:
                        for(Poi p:pois){
                            String id=p.getId();
                            if(id.equals(POIID_EASTRING)||
                                    id.equals(POIID_LECTUREROOM)||
                                    id.equals(POIID_LIBRARY)||
                                    id.equals(POIID_SOUTHHALL)||
                                    id.equals(POIID_SOUTHCLASSROOM)){
                                listener.onPassed(mode,p.getName());
                                isPass=true;
                                break;
                            }
                        }
                        if(!isPass) listener.onFailed(mode);
                        break;
                    default:
                        break;
                }
            }else{
                listener.onError(type);
            }
        }
        stopLocation();
    }
}

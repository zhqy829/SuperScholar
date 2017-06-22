package com.superscholar.android.tools;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/2/22.
 * 服务器连接器
 * 连接应用服务器以及API
 */

public class ServerConnection {

    private static OkHttpClient client=new OkHttpClient();

    final static public String VERSION_NAME="0.1.0";  //版本名
    final static public int VERSION_CODE=0;  //版本号

    //api key
    private static final String appCode="043b218ced6e4504b8b976c5a07f00e5"; //阿里云
    private static final String appKey="2621e4d99e99494faa04018d9a2e9cbd";  //图灵

    //服务器地址
    final static public String host = "http://lylllcc.cc:8082/";

    //其他操作
    final static public String site_icon="img/logo.png";  //logo图片
    final static public String site_apk="apk/efficient.apk";  //新版apk

    //GET操作
    final static private String action_getTime="time/now";  //获取服务器时间
    final static private String action_getVersion="version/getversion";  //获取最新版本信息
    final static private String action_getGradeDetail_10="grade/gettop10record";  //获取近10条学分绩记录

    //POST操作
    final static private String action_login="user/login";  //登录
    final static private String action_register="user/regist";  //注册
    final static private String action_bind="user/bind";  //绑定学号
    final static private String action_resetPassword="user/reset";  //重置密码
    final static private String action_gradeChange="grade/change";  //学分绩变化
    final static private String action_feedback="feedback/dofeedback";  //反馈

    //获取指定位置横幅图片的url
    public static String getBannerImageUrl(int p){
        return host+"img/banner_image_"+String.valueOf(p)+".jpg";
    }

    //获取服务器时间 GET
    public static void getServerTime(Callback callback){
        Request request=new Request.Builder()
                .url(host+action_getTime)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //获取最新版本 GET
    public static void getVersion(Callback callback){
        Request request=new Request.Builder()
                .url(host+action_getVersion)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //获取最近10条学分绩明细 GET
    public static void getUserGradeDetail(String username,Callback callback){
        Request request=new Request.Builder()
                .url(host+action_getGradeDetail_10+"?username="+username)
                .build();
        client.newCall(request).enqueue(callback);
    }


    //用户登录 POST
    public static void sendLoginMsg(String username,String password,Callback callback){
        RequestBody requestBody=new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .build();

        requestIncludeRequestBody(action_login,requestBody,callback);
    }

    //新用户注册 POST
    public static void sendRegisterMsg(String username,String password,
                                       String email,String sID,Callback callback){
        RequestBody requestBody;
        if(sID.equals("")){
            requestBody=new FormBody.Builder()
                    .add("username",username)
                    .add("password",password)
                    .add("email",email)
                    .build();
        }
        else{
            requestBody=new FormBody.Builder()
                    .add("username",username)
                    .add("password",password)
                    .add("email",email)
                    .add("studentId",sID)
                    .build();
        }
        requestIncludeRequestBody(action_register,requestBody,callback);
    }

    //绑定学号 POST
    public static void sendBindSIDMsg(String username,String sID,Callback callback){
        RequestBody requestBody=new FormBody.Builder()
                .add("username",username)
                .add("studentId",sID)
                .build();

        requestIncludeRequestBody(action_bind,requestBody,callback);
    }

    //重置密码 POST
    public static void sendResetPasswordMsg(String username,String password,
                                            String newPassword,Callback callback){
        RequestBody requestBody=new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .add("newpassword",newPassword)
                .build();

        requestIncludeRequestBody(action_resetPassword,requestBody,callback);
    }

    //学分绩变化 POST
    public static void sendGradeChange(String username, double gradeChange,
                                       String detail, Callback callback){
        RequestBody requestBody=new FormBody.Builder()
                .add("username",username)
                .add("change",String.valueOf(gradeChange))
                .add("detail",detail)
                .build();

        requestIncludeRequestBody(action_gradeChange,requestBody,callback);
    }

    //用户反馈 POST
    public static void sendUserFeedback(String username,String type, String detail,
                                        String contact,Callback callback){

        RequestBody requestBody=new FormBody.Builder()
                .add("username",username)
                .add("type",type)
                .add("detail",detail)
                .add("contact",contact)
                .build();

        requestIncludeRequestBody(action_feedback,requestBody,callback);
    }

    //POST请求封装方法
    private static void requestIncludeRequestBody(String action,RequestBody requestBody,Callback callback){
        Request request=new Request.Builder()
                .url(host+action)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    //新闻API
    public static void connectNewsAPI(Callback callback){
        Request request=new Request.Builder()
                .url("http://toutiao-ali.juheapi.com/toutiao/index?type=keji")
                .addHeader("Authorization","APPCODE "+appCode)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //天气API
    public static void connectWeatherAPI(Callback callback){
        Request request=new Request.Builder()
                .url("http://jisutqybmf.market.alicloudapi.com/weather/query?cityid=283")
                .addHeader("Authorization","APPCODE "+appCode)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //图灵API
    public static void connectRobotAPI(String content,Callback callback){
        Request request=new Request.Builder()
                .url("http://www.tuling123.com/openapi/api?key="+appKey+"&info="+content)
                .build();
        client.newCall(request).enqueue(callback);
    }
}


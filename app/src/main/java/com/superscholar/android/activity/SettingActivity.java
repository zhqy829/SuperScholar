package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.service.ApkDownloadService;
import com.superscholar.android.tools.ServerConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SettingActivity extends BaseActivity {

    private String []menuItems={"检查更新","常见问题","反馈建议","分享应用","关于我们"};
    private ListView menuList;

    //apk下载服务
    private ApkDownloadService.ApkDownloadBinder apkDownloadBinder;
    private ServiceConnection apkDownloadServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            apkDownloadBinder = (ApkDownloadService.ApkDownloadBinder) service;
        }

    };


    //设置界面菜单列表初始化
    private void initMenuList(){
        menuList=(ListView)findViewById(R.id.setting_menuList);
        ArrayAdapter adapter=new ArrayAdapter<String>(SettingActivity.this,android.R.layout.simple_list_item_1,menuItems);
        menuList.setAdapter(adapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        updateChecked();
                        break;
                    case 1:
                        Intent intent_1=new Intent(SettingActivity.this,QuestionActivity.class);
                        startActivity(intent_1);
                        break;
                    case 2:
                        Intent intent_2=new Intent(SettingActivity.this,FeedbackActivity.class);
                        startActivity(intent_2);
                        break;
                    case 3:
                        ShareSDK.initSDK(SettingActivity.this,"1ca69ae8f1160");
                        showShare();
                        break;
                    case 4:
                        Intent intent_3=new Intent(SettingActivity.this,AboutActivity.class);
                        startActivity(intent_3);
                        break;
                }
            }
        });
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.setting_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //创建下载服务
    private void createApkDownloadService(){
        Intent intent = new Intent(this, ApkDownloadService.class);
        startService(intent);
        bindService(intent, apkDownloadServiceConnection, BIND_AUTO_CREATE);
    }

    //开始下载
    public void startApkDownload(){
        apkDownloadBinder.startDownload(ServerConnector.getInstance().getApkUrl());
    }

    //分享操作
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();

        oks.setTitle(getString(R.string.app_name));
        oks.setTitleUrl("http://lylllcc.cc/doc");
        oks.setText("这是一款高效的习惯养成工具");
        oks.setImageUrl(ServerConnector.getInstance().getIconUrl());
        oks.setUrl("http://lylllcc.cc/doc");
        oks.setSite(getString(R.string.app_name));
        oks.setSiteUrl("http://lylllcc.cc/doc");
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Toast.makeText(SettingActivity.this,"分享成功，您获得了1学分绩",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(SettingActivity.this,"很抱歉，分享出错",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(SettingActivity.this,"您取消了分享",Toast.LENGTH_SHORT).show();
            }
        });

        oks.show(this);

    }

    //检查更新
    private void updateChecked(){
        final ProgressDialog pd=new ProgressDialog(SettingActivity.this);
        pd.setTitle("检查更新");
        pd.setMessage("正在检测是否有新版本...");
        pd.setCancelable(false);
        pd.show();
        ServerConnector.getInstance().getVersion(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(SettingActivity.this,"网络异常，请检查网络设置",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData =response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(responseData);
                    int versionCode=Integer.parseInt(jsonObject.getString("VERSION_CODE"));
                    String  versionName=jsonObject.getString("VERSION_NAME");
                    if(versionCode>ServerConnector.getInstance().VERSION_CODE){
                        Snackbar.make(menuList,"发现新版本：  "+versionName+"   是否更新？",Snackbar.LENGTH_LONG)
                                .setAction("更新", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startApkDownload();
                                    }
                                }).show();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SettingActivity.this,"当前是最新版本",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }finally {
                    pd.dismiss();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initToolbar();
        initMenuList();

        createApkDownloadService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(apkDownloadServiceConnection);
    }
}

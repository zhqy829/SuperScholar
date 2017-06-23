package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.adapter.SlidingMenuListAdapter;
import com.superscholar.android.entity.Menu;
import com.superscholar.android.entity.User;
import com.superscholar.android.entity.Weather;
import com.superscholar.android.fragment.AnalysisFragment;
import com.superscholar.android.fragment.EventFragment;
import com.superscholar.android.fragment.IntegratedFragment;
import com.superscholar.android.fragment.TargetFragment;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.R;

import com.superscholar.android.tools.BoundsTime;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.LogUtil;
import com.superscholar.android.tools.ServerConnection;
import com.superscholar.android.tools.Time;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import static com.superscholar.android.tools.ServerConnection.sendBindSIDMsg;

public class MainActivity extends BaseActivity {

    //用户信息类，登录成功后先构造用户信息类，在通过该类实例化单例类
    private User user;

    private DrawerLayout mDrawerLayout; //活动主布局
    private TextView toolbarText;  //菜单栏TextView

    //滑动菜单
    private TextView username;
    private TextView email;
    private TextView grade;
    private TextView sID;
    private List<Menu>menuList=new ArrayList<>();
    private Weather weather;

    //底部菜单栏 子项布局 图片 文字
    private LinearLayout integrated;
    private LinearLayout event;
    private LinearLayout target;
    private LinearLayout analysis;

    private ImageView integrated_img;
    private ImageView event_img;
    private ImageView target_img;
    private ImageView analysis_img;

    private TextView integrated_text;
    private TextView event_text;
    private TextView target_text;
    private TextView analysis_text;

    //ViewPager显示内容
    private ViewPager viewPager;
    private int page=0;     //ViewPager页码
    private List<Fragment>fragments=new ArrayList<>();

    //变量初始化
    private void initVariable(){
        mDrawerLayout=(DrawerLayout)findViewById(R.id.main_drawerLayout);

        toolbarText=(TextView)findViewById(R.id.main_toolbarText);

        username=(TextView)findViewById(R.id.slidingMenu_header_username);
        email=(TextView)findViewById(R.id.slidingMenu_header_email);
        grade=(TextView)findViewById(R.id.slidingMenu_header_grade);
        sID=(TextView)findViewById(R.id.slidingMenu_header_sID);

        integrated=(LinearLayout)findViewById(R.id.bottom_integrated);
        event=(LinearLayout)findViewById(R.id.bottom_event);
        target=(LinearLayout)findViewById(R.id.bottom_target);
        analysis=(LinearLayout)findViewById(R.id.bottom_analysis);

        integrated_img=(ImageView)findViewById(R.id.bottom_integrated_img);
        event_img=(ImageView)findViewById(R.id.bottom_event_img);
        target_img=(ImageView)findViewById(R.id.bottom_target_img);
        analysis_img=(ImageView)findViewById(R.id.bottom_analysis_img);

        integrated_text=(TextView)findViewById(R.id.bottom_integrated_text);
        event_text=(TextView)findViewById(R.id.bottom_event_text);
        target_text=(TextView)findViewById(R.id.bottom_target_text);
        analysis_text=(TextView)findViewById(R.id.bottom_analysis_text);

        viewPager=(ViewPager)findViewById(R.id.main_contentView);
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        toolbarText.setText("综合");
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    //滑动菜单菜单列表数据初始化
    private void initMenuData(){
        menuList.add(new Menu("绑定学号",R.drawable.sliding_menu_bindsid));
        menuList.add(new Menu("我的学分绩",R.drawable.sliding_menu_mygrade));
        menuList.add(new Menu("修改密码",R.drawable.sliding_menu_password));
        menuList.add(new Menu("设置",R.drawable.sliding_menu_setting));
        menuList.add(new Menu("退出登录",R.drawable.sliding_menu_logout));
        menuList.add(new Menu("关闭程序",R.drawable.sliding_menu_exit));
    }

    //滑动菜单初始化
    private void initSlidingMenu(){
        initMenuData();
        final ListView menu=(ListView)findViewById(R.id.slidingMenu_menuList);
        SlidingMenuListAdapter adapter=new SlidingMenuListAdapter(MainActivity.this,R.layout.sliding_menulist_item,menuList);
        menu.setAdapter(adapter);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        if(user.getsID()!=null){
                            Snackbar.make(menu,"您已绑定学号，再次绑定将解绑原学号，是否继续？"
                                    ,Snackbar.LENGTH_LONG).setAction("继续", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bindSID();
                                }
                            }).show();
                        }else{
                            bindSID();
                        }
                        break;
                    case 1:
                        break;
                    case 2:
                        Intent intent=new Intent(MainActivity.this,ResetPasswordActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        Intent intent_2=new Intent(MainActivity.this,SettingActivity.class);
                        startActivity(intent_2);
                        break;
                    case 4:
                        exitLogin();
                        break;
                    case 5:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });

        //过滤Banner点击事件
        RelativeLayout headerLayout=(RelativeLayout)findViewById(R.id.slidingMenu_headerLayout);
        LinearLayout occupyLayout_1=(LinearLayout)findViewById(R.id.slidingMenu_bottom_occupyLayout_1);
        LinearLayout occupyLayout_2=(LinearLayout)findViewById(R.id.slidingMenu_bottom_occupyLayout_2);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        occupyLayout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        occupyLayout_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        initWeather();
    }

    //天气数据初始化
    private void initWeather(){
        final TextView weatherText=(TextView)findViewById(R.id.slidingMenu_weather_text);
        final ImageView weatherImage=(ImageView)findViewById(R.id.slidingMenu_weather_image);

        ServerConnection.connectWeatherAPI(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"网络异常，数据获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData =response.body().string();
                weather=new Weather();
                try {
                    JSONObject responseObject = new JSONObject(responseData);
                    String resultData=responseObject.getString("result");
                    JSONObject resultObject=new JSONObject(resultData);
                    weather.setWeather(resultObject.getString("weather"));
                    weather.setTemp(resultObject.getString("temp"));
                    weather.setMaxTemp(resultObject.getString("temphigh"));
                    weather.setMinTemp(resultObject.getString("templow"));
                    weather.setImgId(resultObject.getString("img"));

                    //检查API返回的相对湿度是否带%
                    String humidity=resultObject.getString("humidity");
                    String regex="\\d+";
                    Pattern p=Pattern.compile(regex);
                    Matcher m=p.matcher(humidity);
                    if(m.matches()) humidity+="%";
                    weather.setHumidity(humidity);

                    String aqiData=resultObject.getString("aqi");
                    JSONObject aqiObject=new JSONObject(aqiData);
                    weather.setAirQuality(aqiObject.getString("quality"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weatherText.setText(weather.getTemp()+"℃");
                            weatherImage.setImageResource(Weather.getResId(String.valueOf("weather_"+weather.getImgId()),R.drawable.class));
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                    weather=null;
                }
            }
        });

        LinearLayout weatherLayout=(LinearLayout)findViewById(R.id.slidingMenu_weather_layout);
        weatherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(weather==null){
                    Toast.makeText(MainActivity.this,"数据初始化失败",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this,
                            "青岛，"+weather.getWeather()+"，"+weather.getMinTemp()+"-"+weather.getMaxTemp()+"℃\n" +
                                    "相对湿度："+weather.getHumidity()+"\n" +
                                    "空气质量："+weather.getAirQuality(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //底部菜单栏初始化
    private void initBottomMenu(){
        integrated_img.setImageResource(R.drawable.integrated_selected);
        integrated_text.setTextColor(Color.parseColor("#1C86EE"));

        integrated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page!=0){
                    viewPager.setCurrentItem(0);
                }
            }
        });

        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page!=1){
                    viewPager.setCurrentItem(1);
                }
            }
        });

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page!=2){
                    viewPager.setCurrentItem(2);
                }
            }
        });

        analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page!=3){
                    viewPager.setCurrentItem(3);
                }
            }
        });
    }

    //重置底部菜单为初始样式
    private void resetBottomMenuStyle(){
        if(page==0){
            integrated_img.setImageResource(R.drawable.integrated_normal);
            integrated_text.setTextColor(Color.parseColor("#B5B5B5"));
        }
        else if(page==1){
            event_img.setImageResource(R.drawable.event_normal);
            event_text.setTextColor(Color.parseColor("#B5B5B5"));
        }
        else if(page==2){
            target_img.setImageResource(R.drawable.target_normal);
            target_text.setTextColor(Color.parseColor("#B5B5B5"));
        }
        else{
            analysis_img.setImageResource(R.drawable.analysis_normal);
            analysis_text.setTextColor(Color.parseColor("#B5B5B5"));
        }
    }

    //根据page设置底部菜单样式
    private void setBottomMenuStyle(){
        if(page==0){
            integrated_img.setImageResource(R.drawable.integrated_selected);
            integrated_text.setTextColor(Color.parseColor("#1C86EE"));
            toolbarText.setText("综合");
        }
        else if(page==1){
            event_img.setImageResource(R.drawable.event_selected);
            event_text.setTextColor(Color.parseColor("#1C86EE"));
            toolbarText.setText("事件");
        }
        else if(page==2){
            target_img.setImageResource(R.drawable.target_selected);
            target_text.setTextColor(Color.parseColor("#1C86EE"));
            toolbarText.setText("目标");
        }
        else{
            analysis_img.setImageResource(R.drawable.analysis_selected);
            analysis_text.setTextColor(Color.parseColor("#1C86EE"));
            toolbarText.setText("分析");
        }
    }

    //ViewPager初始化
    private void initViewPager(){
        fragments.add(new IntegratedFragment());
        fragments.add(new EventFragment());
        fragments.add(new TargetFragment());
        fragments.add(new AnalysisFragment());

        FragmentManager fragmentManager=getSupportFragmentManager();
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                return  fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetBottomMenuStyle();
                page=position;
                setBottomMenuStyle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //用户初始化
    private void initUser(){
        try {
            Intent intent = getIntent();
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            double grade = Double.parseDouble(intent.getStringExtra("grade"));
            String sID = intent.getStringExtra("sID");
            String email = intent.getStringExtra("email");

            //实例化单例类
            user=UserLib.getInstance().getUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setGrade(grade);
            user.setEmail(email);

            this.username.setText("用户名："+username);
            this.email.setText("邮箱："+email);
            this.grade.setText("学分绩：" + String.valueOf(grade));
            if (sID.equals("null")) {
                user.setsID(null);
                this.sID.setText("学号：" + "未绑定");
            } else {
                user.setsID(sID);
                this.sID.setText("学号：" + sID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //退出登录
    private void exitLogin(){
        //清除登录信息文件
        SharedPreferences.Editor editor=getSharedPreferences("data_up",0).edit();
        editor.clear();
        editor.apply();

        //返回登录界面
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //绑定学号
    private void bindSID(){

        final EditText et = new EditText(this);
        et.setHint("请输入学号");
        et.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this).setTitle("绑定学号")
                .setView(et)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String sID = et.getText().toString();
                        if (sID.equals("")) {
                            Toast.makeText(MainActivity.this, "请输入学号", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            final ProgressDialog pd=new ProgressDialog(MainActivity.this);
                            pd.setTitle("绑定学号");
                            pd.setMessage("绑定中，请稍候...");
                            pd.setCancelable(false);
                            pd.show();
                            sendBindSIDMsg(user.getUsername(),sID, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Toast.makeText(MainActivity.this,"网络异常，请检查网络设置",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData =response.body().string();
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        int code = Integer.parseInt(jsonObject.getString("code"));
                                        if (code == 2) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(MainActivity.this, "未知错误",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }else if(code==0){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(MainActivity.this, "绑定成功",
                                                            Toast.LENGTH_SHORT).show();
                                                    MainActivity.this.sID.setText("学号："+sID);
                                                    user.setsID(sID);
                                                }
                                            });
                                        }
                                    }
                                    catch(JSONException e){
                                        pd.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariable();
        initToolbar();
        initSlidingMenu();
        initBottomMenu();
        initViewPager();
        initUser();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //按下返回键
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.robot:
                Intent intent=new Intent(MainActivity.this,RobotActivity.class);
                startActivity(intent);
                break;
            default:

        }
        return true;
    }
}

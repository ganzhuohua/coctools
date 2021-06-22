package com.ssyj.coc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CocAnalysis extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CocAnalysis";

    private String tag;
    private EditText et_tag;
    private Double reduction = 1.0;
    private TextView tv_00_reduction, tv_10_reduction, tv_15_reduction, tv_20_reduction, tv_threeKingCost,
            tv_ywKingCost, tv_zzKingCost, tv_kingTime, tv_KingPercentage,
            tv_mLevel, tv_mCost, tv_mTime, tv_mPercentage,
            tv_nLevel, tv_nCost, tv_nTime, tv_nPercentage,
            tv_yLevel, tv_yCost, tv_yTime, tv_yPercentage,
            tv_rLevel, tv_rCost, tv_rTime, tv_rPercentage,
            tv_zLevel, tv_zCost, tv_zTime, tv_zPercentage;
    private ProgressBar pb_loading;
    private ImageView iv_loading;
    private CardView iv_update;

    private String playersTag[] = {"playersTag1", "playersTag2", "playersTag3", "playersTag4", "playersTag5", "playersTag6"};

    //自定义状态栏和标题栏
    private void init() {


        //沉浸状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //状态栏字体图标灰色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 沉浸式
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //延时器
        new Handler().postDelayed(new Runnable() {
            public void run() {

                //获取状态栏高度
                int statusBarHeight = -1;
                //获取status_bar_height资源的ID
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    //根据资源ID获取响应的尺寸值
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

                //获取标题栏高度
                TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                    Log.d(TAG, "onCreate: ---标题栏高度:" + actionBarHeight);

                    CardView cardDiy = findViewById(R.id.cardDiy);
                    LinearLayout linearCardSetTagZtl = findViewById(R.id.linearCardSetTagZtl);
                    LinearLayout linearCardSetTagBtl = findViewById(R.id.linearCardSetTagBtl);

                    LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) cardDiy.getLayoutParams();
                    linearParams.height = statusBarHeight + actionBarHeight;
                    cardDiy.setLayoutParams(linearParams);

                    linearParams = (LinearLayout.LayoutParams) linearCardSetTagZtl.getLayoutParams();
                    linearParams.height = statusBarHeight;
                    linearCardSetTagZtl.setLayoutParams(linearParams);

                    linearParams = (LinearLayout.LayoutParams) linearCardSetTagBtl.getLayoutParams();
                    linearParams.height = actionBarHeight;
                    linearCardSetTagBtl.setLayoutParams(linearParams);
                }
            }
        }, 1);

        CardView cardReturn = findViewById(R.id.cardReturn);
        cardReturn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coc_analysis);

        //自定义状态栏和标题栏
        init();

        Button btn_analysis = findViewById(R.id.btn_analysis);
        btn_analysis.setOnClickListener(this);

        tv_00_reduction = findViewById(R.id.tv_00_reduction);
        tv_00_reduction.setOnClickListener(this);
        tv_10_reduction = findViewById(R.id.tv_10_reduction);
        tv_10_reduction.setOnClickListener(this);
        tv_15_reduction = findViewById(R.id.tv_15_reduction);
        tv_15_reduction.setOnClickListener(this);
        tv_20_reduction = findViewById(R.id.tv_20_reduction);
        tv_20_reduction.setOnClickListener(this);

        tv_threeKingCost = findViewById(R.id.tv_threeKingCost);
        tv_ywKingCost = findViewById(R.id.tv_ywKingCost);
        tv_zzKingCost = findViewById(R.id.tv_zzKingCost);
        tv_kingTime = findViewById(R.id.tv_kingTime);
        tv_KingPercentage = findViewById(R.id.tv_KingPercentage);

        et_tag = findViewById(R.id.et_tag);

        tv_mLevel = findViewById(R.id.tv_mLevel);
        tv_mLevel.setOnClickListener(this);
        tv_mCost = findViewById(R.id.tv_mCost);
        tv_mCost.setOnClickListener(this);
        tv_mTime = findViewById(R.id.tv_mTime);
        tv_mTime.setOnClickListener(this);
        tv_mPercentage = findViewById(R.id.tv_mPercentage);
        tv_mPercentage.setOnClickListener(this);

        tv_nLevel = findViewById(R.id.tv_nLevel);
        tv_nCost = findViewById(R.id.tv_nCost);
        tv_nTime = findViewById(R.id.tv_nTime);
        tv_nPercentage = findViewById(R.id.tv_nPercentage);

        tv_yLevel = findViewById(R.id.tv_yLevel);
        tv_yCost = findViewById(R.id.tv_yCost);
        tv_yTime = findViewById(R.id.tv_yTime);
        tv_yPercentage = findViewById(R.id.tv_yPercentage);

        tv_rLevel = findViewById(R.id.tv_rLevel);
        tv_rCost = findViewById(R.id.tv_rCost);
        tv_rTime = findViewById(R.id.tv_rTime);
        tv_rPercentage = findViewById(R.id.tv_rPercentage);

        tv_zLevel = findViewById(R.id.tv_zLevel);
        tv_zCost = findViewById(R.id.tv_zCost);
        tv_zTime = findViewById(R.id.tv_zTime);
        tv_zPercentage = findViewById(R.id.tv_zPercentage);

        pb_loading = findViewById(R.id.pb_loading_analysis);
        iv_loading = findViewById(R.id.iv_loading_analysis);

        iv_update = findViewById(R.id.iv_update);
        iv_update.setOnClickListener(this);

        //获取spinner组件的id 用于以后对其操作
        Spinner sp_tag = (Spinner) findViewById(R.id.sp_tag);

        //创建数组列表 用来存放以后要显示的内容
        ArrayList<String> arrayList = new ArrayList<String>();
        //添加要显示的内容
        for (int i = 0; i < playersTag.length; i++) {
            arrayList.add("村庄" + (i + 1) + ":" + SpUtils.getString(this, playersTag[i], "未设置标签"));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);//创建适配器  this--上下文  android.R.layout.simple_spinner_item--显示的模板   arrayList--显示的内容
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//设置下拉之后的布局的样式 这里采用的是系统的一个布局
        sp_tag.setAdapter(arrayAdapter);//将适配器给下拉框
        sp_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//当改变下拉框的时候会触发
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//改变内容的时候

                //添加要显示的内容
                for (int i = 0; i < playersTag.length; i++) {

                    if (id == i) {
                        et_tag.setText(SpUtils.getString(CocAnalysis.this, playersTag[i], ""));
                    }

                }

                Toast.makeText(CocAnalysis.this, arrayList.get(position) + id, Toast.LENGTH_LONG).show();//打印所选中的东西arrayList.get(position)--position--数组中第几个是选中的
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {//没有改变的时候

            }

        });

    }


    //根据当前等级和最高等级统计出数据
    public int tj(JSONArray sz, int level, int s) {
        int sum = (int) 0.0;
//        Log.d(TAG, "tj: ----:::"+sz);
        for (int i = level; i < sz.size(); i++) {
            sum += (Integer) JSONArray.parseArray(String.valueOf(sz.get(i))).get(s);
        }
        Log.d(TAG, "tj: ---科技数据统计" + sum);
        return sum;
    }

    //保存科技信息 如果本地没有科技信息就访问云端并保存到本地
    private void analysisJson() {

        if (!"null".equals(SpUtils.getString(CocAnalysis.this, "analysisJson", "null"))) {//判断科技信息不为空
            Log.d(TAG, "analysisJson: ---科技已保存,取出保存内容:"
                    + SpUtils.getString(CocAnalysis.this, "analysisJson", "null"));
            return;
        }

        new Thread() {
            public void run() {

                tag = et_tag.getText().toString();

                Log.d(TAG, "run: ---科技分析标签:" + tag);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                        .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
                        .writeTimeout(60, TimeUnit.SECONDS)//设置写的超时时间
                        .build();

                //MD5加密
                String md5 = null;
                try {
                    md5 = Md5Utils.md5(CocAnalysis.this.getResources().getString(R.string.app_name)
                            + CocAnalysis.this.getResources().getString(R.string.gzh));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "run: ---科技信息token:" + md5);

                Request request = new Request.Builder()
                        .addHeader("mode", "getsciences")//header头信息必带
                        .addHeader("token", md5)
                        .url("https://coctools.top/sciences/")
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e(TAG, "onFailure: 科技: " + e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String res = response.body().string();
                        Log.d(TAG, "onResponse: ---科技分析返回内容:" + res);

                        if ("200".equals(response.code())) {//状态码不等于200
                            Log.d(TAG, "onResponse: ---科技code错误:" + response.code());
                            return;
                        }

                        //保存科技信息
                        SpUtils.setString(CocAnalysis.this, "analysisJson", res);

                    }
                });

            }
        }.start();
    }

    private void setColor() {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cardReturn://返回
                finish();
                break;
            case R.id.iv_update:
                new Thread() {
                    public void run() {

                        tag = et_tag.getText().toString();

                        Log.d(TAG, "run: ---科技分析标签:" + tag);

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                                .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
                                .writeTimeout(60, TimeUnit.SECONDS)//设置写的超时时间
                                .build();

                        //MD5加密
                        String md5 = null;
                        try {
                            md5 = Md5Utils.md5(CocAnalysis.this.getResources().getString(R.string.app_name)
                                    + CocAnalysis.this.getResources().getString(R.string.gzh));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "run: ---科技信息token:" + md5);

                        Request request = new Request.Builder()
                                .addHeader("mode", "getsciences")//header头信息必带
                                .addHeader("token", md5)
                                .url("https://coctools.top/sciences/")
                                .build();

                        Call call = client.newCall(request);

                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.e(TAG, "onFailure: 科技: " + e);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String res = response.body().string();
                                Log.d(TAG, "onResponse: ---科技分析返回内容:" + res);

                                if ("200".equals(response.code())) {//状态码不等于200
                                    Log.d(TAG, "onResponse: ---科技code错误:" + response.code());
                                    return;
                                }

                                //保存科技信息
                                SpUtils.setString(CocAnalysis.this, "analysisJson", res);
                            }
                        });
                    }
                }.start();
                Toast.makeText(CocAnalysis.this, "已更新科技数据", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_00_reduction://减免无
                reduction = 1.0;
                tv_00_reduction.setTextColor(this.getResources().getColor(R.color.players_max));
                tv_10_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_15_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_20_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                break;
            case R.id.tv_10_reduction://减免10
                reduction = 0.1;
                tv_00_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_10_reduction.setTextColor(this.getResources().getColor(R.color.players_max));
                tv_15_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_20_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                break;
            case R.id.tv_15_reduction://减免15
                reduction = 0.15;
                tv_00_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_10_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_15_reduction.setTextColor(this.getResources().getColor(R.color.players_max));
                tv_20_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                break;
            case R.id.tv_20_reduction://减免20
                reduction = 0.2;
                tv_00_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_10_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_15_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_20_reduction.setTextColor(this.getResources().getColor(R.color.players_max));
                break;
            case R.id.btn_analysis://开始分析

                //保存王数据 如果本地没有王数据就访问云端并保存到本地
                analysisJson();

                pb_loading.setVisibility(View.VISIBLE);
                iv_loading.setVisibility(View.GONE);

                Log.d(TAG, "onClick: ---减免:" + reduction);

                new Thread() {
                    public void run() {

                        tag = et_tag.getText().toString();

                        Log.d(TAG, "run: ---科技分析标签：" + tag);

                        OkHttpClient client = new OkHttpClient.Builder()
                                //.connectTimeout(10, TimeUnit.SECONDS)
                                //.readTimeout(15, TimeUnit.SECONDS)
                                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                                .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
                                .writeTimeout(60, TimeUnit.SECONDS)//设置写的超时时间
                                .build();

                        //MD5加密
                        String md5 = null;
                        try {
                            md5 = Md5Utils.md5(CocAnalysis.this.getResources().getString(R.string.app_name)
                                    + CocAnalysis.this.getResources().getString(R.string.gzh));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "run: ---个人信息token:" + md5);

                        Request request = new Request.Builder()
                                .addHeader("mode", "getplayers")//header头信息必带
                                .addHeader("token", md5)
                                .get()
                                .url("https://coctools.top/players/" + tag)
                                .build();

                        Call call = client.newCall(request);

                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.e(TAG, "onFailure: 科技分析:" + e);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                String res = response.body().string();
                                Log.d(TAG, "onResponse: ---科技分析返回内容:" + res);

                                if (response.code() != 200) {
                                    Log.d(TAG, "onResponse: ---科技分析-状态码错误->" + response.code());
                                    return;
                                }

                                if (res.contains("未找到资源")) {
                                    Log.d(TAG, "onResponse: ---科技分析-未找到资源->" + res);
                                    return;
                                }

                                try {
                                    JSONObject json = JSONObject.parseObject(res);

                                    String kingName[] = {"Barbarian King", "Archer Queen", "Grand Warden", "Royal Champion", "Battle Machine"};

                                    //英雄信息
                                    JSONArray heroes = json.getJSONArray("heroes");
                                    Log.d(TAG, "onResponse: ---:" + heroes);

                                    Map<String, String> kingData = new HashMap<String, String>();

                                    for (int i = 0; i < heroes.size(); i++) {

                                        for (int s = 0; s < 5; s++) {
                                            if (!heroes.toString().contains(kingName[s])) {
                                                kingData.put(kingName[s], "[0,0]");
                                            }
                                        }

                                        Object str = heroes.get(i);

                                        JSONObject jsonHeroes = JSONObject.parseObject(str.toString());

                                        String name = jsonHeroes.getString("name");//英雄名称
                                        String village = jsonHeroes.getString("village");//英雄所在家乡
                                        String level = jsonHeroes.getString("level");//英雄当前等级
                                        String maxLevel = jsonHeroes.getString("maxLevel");//英雄最高等级

                                        switch (name) {
                                            case "Barbarian King"://蛮王
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                            case "Archer Queen"://女王
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                            case "Grand Warden"://永王
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                            case "Royal Champion"://闰土
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                            case "Battle Machine"://战争机器
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                        }


                                    }
                                    Log.d(TAG, "onResponse: ---kingData:" + kingData);

                                    String data = SpUtils.getString(CocAnalysis.this, "analysisJson");
                                    JSONObject king = JSONObject.parseObject(data);

                                    //蛮王
                                    int mCost = tj(JSONArray.parseArray(king.getString("mking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0),
                                            0
                                    );
                                    int mTime = tj(JSONArray.parseArray(king.getString("mking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0),
                                            1
                                    );
                                    int mTime2 = tj(JSONArray.parseArray(king.getString("mking")), 0, 1);

                                    //女王
                                    int nCost = tj(JSONArray.parseArray(king.getString("nking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0),
                                            0
                                    );
                                    int nTime = tj(JSONArray.parseArray(king.getString("nking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0),
                                            1
                                    );
                                    int nTime2 = tj(JSONArray.parseArray(king.getString("nking")), 0, 1);

                                    //永王
                                    int yCost = tj(JSONArray.parseArray(king.getString("yking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0),
                                            0
                                    );
                                    int yTime = tj(JSONArray.parseArray(king.getString("yking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0),
                                            1
                                    );
                                    int yTime2 = tj(JSONArray.parseArray(king.getString("yking")), 0, 1);

                                    //闰土
                                    int rCost = tj(JSONArray.parseArray(king.getString("rking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0),
                                            0
                                    );
                                    int rTime = tj(JSONArray.parseArray(king.getString("rking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0),
                                            1
                                    );
                                    int rTime2 = tj(JSONArray.parseArray(king.getString("rking")), 0, 1);

                                    //战争机器
                                    int zCost = tj(JSONArray.parseArray(king.getString("zking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0),
                                            0
                                    );
                                    int zTime = tj(JSONArray.parseArray(king.getString("zking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0),
                                            1
                                    );
                                    int zTime2 = tj(JSONArray.parseArray(king.getString("zking")), 0, 1);

                                    Message message = new Message();
                                    message.what = 1;
                                    //然后将消息发送出去
                                    Bundle bundle = new Bundle();
                                    switch (String.valueOf(reduction)) {
                                        case "1.0":
                                            bundle.putInt("三王费用", (int) ((mCost + nCost + rCost) * reduction));
                                            bundle.putInt("永王费用", (int) (yCost * reduction));
                                            bundle.putInt("战争机器费用", (int) (zCost * reduction));
                                            bundle.putInt("五王时间", (int) ((mTime + nTime + yTime + rTime + zTime) * reduction));
                                            bundle.putDouble("五王完成率", ((double)
                                                    (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0) +
                                                    (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0) +
                                                    (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0) +
                                                    (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0) +
                                                    (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0)) /
                                                    ((Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(1) +
                                                            (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(1) +
                                                            (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(1) +
                                                            (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(1) +
                                                            (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(1)) * 100
                                            );
//                                            bundle.putDouble("五王完成率", 100 - (double) ((mTime + nTime + yTime + rTime + zTime) * reduction)
//                                                    / ((mTime2 + nTime2 + yTime2 + rTime2 + zTime2) * reduction) * 100);

                                            //蛮王
                                            bundle.putInt("蛮王等级", (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0));
                                            bundle.putInt("蛮王费用", mCost);
                                            bundle.putInt("蛮王时间", mTime);
                                            bundle.putDouble("蛮王完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(1) * 100);

                                            //女王
                                            bundle.putInt("女王等级", (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0));
                                            bundle.putInt("女王费用", nCost);
                                            bundle.putInt("女王时间", nTime);
                                            bundle.putDouble("女王完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(1) * 100);

                                            //永王
                                            bundle.putInt("永王等级", (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0));
                                            bundle.putInt("永王费用", yCost);
                                            bundle.putInt("永王时间", yTime);
                                            bundle.putDouble("永王完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(1) * 100);

                                            //闰土
                                            bundle.putInt("闰土等级", (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0));
                                            bundle.putInt("闰土费用", rCost);
                                            bundle.putInt("闰土时间", rTime);
                                            bundle.putDouble("闰土完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(1) * 100);

                                            //战争机器
                                            bundle.putInt("战争机器等级", (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0));
                                            bundle.putInt("战争机器费用", zCost);
                                            bundle.putInt("战争机器时间", zTime);
                                            bundle.putDouble("战争机器完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(1) * 100);

                                            break;
                                        default:
                                            bundle.putInt("三王费用", (int) ((int) (mCost + nCost + rCost) - ((mCost + nCost + rCost) * reduction)));
                                            bundle.putInt("永王费用", (int) ((int) yCost - (yCost * reduction)));
                                            bundle.putInt("战争机器费用", (int) ((int) zCost - (zCost * reduction)));
                                            bundle.putInt("五王时间", (int) ((int) (mTime + nTime + yTime + rTime + zTime) - ((mTime + nTime + yTime + rTime + zTime) * reduction)));
                                            bundle.putDouble("五王完成率", ((double)
                                                    (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0) +
                                                    (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0) +
                                                    (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0) +
                                                    (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0) +
                                                    (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0)) /
                                                    ((Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(1) +
                                                            (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(1) +
                                                            (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(1) +
                                                            (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(1) +
                                                            (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(1)) * 100
                                            );
//                                            bundle.putDouble("五王完成率", 100 - (double) ((mTime + nTime + yTime + rTime + zTime) * reduction)
//                                                    / ((mTime2 + nTime2 + yTime2 + rTime2 + zTime2) * reduction) * 100);

                                            //蛮王
                                            bundle.putInt("蛮王等级", (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0));
                                            bundle.putInt("蛮王费用", (int) (mCost - (mCost * reduction)));
                                            bundle.putInt("蛮王时间", (int) (mTime - (mTime * reduction)));
                                            bundle.putDouble("蛮王完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(1) * 100);

                                            //女王
                                            bundle.putInt("女王等级", (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0));
                                            bundle.putInt("女王费用", (int) (nCost - (nCost * reduction)));
                                            bundle.putInt("女王时间", (int) (nTime - (nTime * reduction)));
                                            bundle.putDouble("女王完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(1) * 100);

                                            //永王
                                            bundle.putInt("永王等级", (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0));
                                            bundle.putInt("永王费用", (int) (yCost - (yCost * reduction)));
                                            bundle.putInt("永王时间", (int) (yTime - (yTime * reduction)));
                                            bundle.putDouble("永王完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(1) * 100);

                                            //闰土
                                            bundle.putInt("闰土等级", (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0));
                                            bundle.putInt("闰土费用", (int) (rCost - (rCost * reduction)));
                                            bundle.putInt("闰土时间", (int) (rTime - (rTime * reduction)));
                                            bundle.putDouble("闰土完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(1) * 100);

                                            //战争机器
                                            bundle.putInt("战争机器等级", (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0));
                                            bundle.putInt("战争机器费用", (int) (zCost - (zCost * reduction)));
                                            bundle.putInt("战争机器时间", (int) (zTime - (zTime * reduction)));
                                            bundle.putDouble("战争机器完成率", (double) (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(1) * 100);

                                    }

                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                } catch (
                                        JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        });

                    }
                }.start();

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }

    //UI线程更新
    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {

                Bundle bundle = msg.getData();

                tv_threeKingCost.setText(String.valueOf(bundle.getInt("三王费用") / 10000) + "万");
                tv_ywKingCost.setText(String.valueOf(bundle.getInt("永王费用") / 10000) + "万");
                tv_zzKingCost.setText(String.valueOf(bundle.getInt("战争机器费用") / 10000) + "万");
                tv_kingTime.setText(String.valueOf(bundle.getInt("五王时间") / 60 / 60 / 24) + "天");
                tv_KingPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("五王完成率")) + "%"));

                //蛮王
                tv_mLevel.setText("Lv." + bundle.getInt("蛮王等级"));
                tv_mCost.setText(String.valueOf(bundle.getInt("蛮王费用") / 10000) + "万");
                tv_mTime.setText(String.valueOf(bundle.getInt("蛮王时间") / 60 / 60 / 24) + "天");
                tv_mPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("蛮王完成率")) + "%"));

                //女王
                tv_nLevel.setText("Lv." + bundle.getInt("女王等级"));
                tv_nCost.setText(String.valueOf(bundle.getInt("女王费用") / 10000) + "万");
                tv_nTime.setText(String.valueOf(bundle.getInt("女王时间") / 60 / 60 / 24) + "天");
                tv_nPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("女王完成率")) + "%"));

                //永王
                tv_yLevel.setText("Lv." + bundle.getInt("永王等级"));
                tv_yCost.setText(String.valueOf(bundle.getInt("永王费用") / 10000) + "万");
                tv_yTime.setText(String.valueOf(bundle.getInt("永王时间") / 60 / 60 / 24) + "天");
                tv_yPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("永王完成率")) + "%"));

                //闰土
                tv_rLevel.setText("Lv." + bundle.getInt("闰土等级"));
                tv_rCost.setText(String.valueOf(bundle.getInt("闰土费用") / 10000) + "万");
                tv_rTime.setText(String.valueOf(bundle.getInt("闰土时间") / 60 / 60 / 24) + "天");
                tv_rPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("闰土完成率")) + "%"));

                //战争机器
                tv_zLevel.setText("Lv." + bundle.getInt("战争机器等级"));
                tv_zCost.setText(String.valueOf(bundle.getInt("战争机器费用") / 10000) + "万");
                tv_zTime.setText(String.valueOf(bundle.getInt("战争机器时间") / 60 / 60 / 24) + "天");
                tv_zPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("战争机器完成率")) + "%"));

                pb_loading.setVisibility(View.GONE);
                iv_loading.setVisibility(View.VISIBLE);

                Log.d(TAG, "handleMessage: -----------" + msg);
            }

        }
    };

}
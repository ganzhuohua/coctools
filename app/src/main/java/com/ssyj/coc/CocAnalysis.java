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

    //??????????????????????????????
    private void init() {


        //???????????????
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //???????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // ?????????
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //?????????
        new Handler().postDelayed(new Runnable() {
            public void run() {

                //?????????????????????
                int statusBarHeight = -1;
                //??????status_bar_height?????????ID
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    //????????????ID????????????????????????
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

                //?????????????????????
                TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                    int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                    Log.d(TAG, "onCreate: ---???????????????:" + actionBarHeight);

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

        //??????????????????????????????
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

        //??????spinner?????????id ????????????????????????
        Spinner sp_tag = (Spinner) findViewById(R.id.sp_tag);

        //?????????????????? ????????????????????????????????????
        ArrayList<String> arrayList = new ArrayList<String>();
        //????????????????????????
        for (int i = 0; i < playersTag.length; i++) {
            arrayList.add("??????" + (i + 1) + ":" + SpUtils.getString(this, playersTag[i], "???????????????"));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);//??????????????? ??this--????????? ??android.R.layout.simple_spinner_item--??????????????? ?? arrayList--???????????????
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//???????????????????????????????????? ???????????????????????????????????????
        sp_tag.setAdapter(arrayAdapter);//????????????????????????
        sp_tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//????????????????????????????????????
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//?????????????????????

                //????????????????????????
                for (int i = 0; i < playersTag.length; i++) {

                    if (id == i) {
                        et_tag.setText(SpUtils.getString(CocAnalysis.this, playersTag[i], ""));
                    }

                }

                Toast.makeText(CocAnalysis.this, arrayList.get(position) + id, Toast.LENGTH_LONG).show();//????????????????????????arrayList.get(position)--position--??????????????????????????????
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {//?????????????????????

            }

        });

    }


    //????????????????????????????????????????????????
    public int tj(JSONArray sz, int level, int s) {
        int sum = (int) 0.0;
//        Log.d(TAG, "tj: ----:::"+sz);
        for (int i = level; i < sz.size(); i++) {
            sum += (Integer) JSONArray.parseArray(String.valueOf(sz.get(i))).get(s);
        }
        Log.d(TAG, "tj: ---??????????????????" + sum);
        return sum;
    }

    //?????????????????? ???????????????????????????????????????????????????????????????
    private void analysisJson() {

        if (!"null".equals(SpUtils.getString(CocAnalysis.this, "analysisJson", "null"))) {//???????????????????????????
            Log.d(TAG, "analysisJson: ---???????????????,??????????????????:"
                    + SpUtils.getString(CocAnalysis.this, "analysisJson", "null"));
            return;
        }

        new Thread() {
            public void run() {

                tag = et_tag.getText().toString();

                Log.d(TAG, "run: ---??????????????????:" + tag);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)//????????????????????????
                        .readTimeout(100, TimeUnit.SECONDS)//????????????????????????
                        .writeTimeout(60, TimeUnit.SECONDS)//????????????????????????
                        .build();

                //MD5??????
                String md5 = null;
                try {
                    md5 = Md5Utils.md5(CocAnalysis.this.getResources().getString(R.string.app_name)
                            + CocAnalysis.this.getResources().getString(R.string.gzh));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "run: ---????????????token:" + md5);

                Request request = new Request.Builder()
                        .addHeader("mode", "getsciences")//header???????????????
                        .addHeader("token", md5)
                        .url("https://coctools.top/sciences/")
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e(TAG, "onFailure: ??????: " + e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String res = response.body().string();
                        Log.d(TAG, "onResponse: ---????????????????????????:" + res);

                        if ("200".equals(response.code())) {//??????????????????200
                            Log.d(TAG, "onResponse: ---??????code??????:" + response.code());
                            return;
                        }

                        //??????????????????
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
            case R.id.cardReturn://??????
                finish();
                break;
            case R.id.iv_update:
                new Thread() {
                    public void run() {

                        tag = et_tag.getText().toString();

                        Log.d(TAG, "run: ---??????????????????:" + tag);

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(60, TimeUnit.SECONDS)//????????????????????????
                                .readTimeout(100, TimeUnit.SECONDS)//????????????????????????
                                .writeTimeout(60, TimeUnit.SECONDS)//????????????????????????
                                .build();

                        //MD5??????
                        String md5 = null;
                        try {
                            md5 = Md5Utils.md5(CocAnalysis.this.getResources().getString(R.string.app_name)
                                    + CocAnalysis.this.getResources().getString(R.string.gzh));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "run: ---????????????token:" + md5);

                        Request request = new Request.Builder()
                                .addHeader("mode", "getsciences")//header???????????????
                                .addHeader("token", md5)
                                .url("https://coctools.top/sciences/")
                                .build();

                        Call call = client.newCall(request);

                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.e(TAG, "onFailure: ??????: " + e);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String res = response.body().string();
                                Log.d(TAG, "onResponse: ---????????????????????????:" + res);

                                if ("200".equals(response.code())) {//??????????????????200
                                    Log.d(TAG, "onResponse: ---??????code??????:" + response.code());
                                    return;
                                }

                                //??????????????????
                                SpUtils.setString(CocAnalysis.this, "analysisJson", res);
                            }
                        });
                    }
                }.start();
                Toast.makeText(CocAnalysis.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_00_reduction://?????????
                reduction = 1.0;
                tv_00_reduction.setTextColor(this.getResources().getColor(R.color.players_max));
                tv_10_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_15_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_20_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                break;
            case R.id.tv_10_reduction://??????10
                reduction = 0.1;
                tv_00_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_10_reduction.setTextColor(this.getResources().getColor(R.color.players_max));
                tv_15_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_20_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                break;
            case R.id.tv_15_reduction://??????15
                reduction = 0.15;
                tv_00_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_10_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_15_reduction.setTextColor(this.getResources().getColor(R.color.players_max));
                tv_20_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                break;
            case R.id.tv_20_reduction://??????20
                reduction = 0.2;
                tv_00_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_10_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_15_reduction.setTextColor(this.getResources().getColor(R.color.players_black));
                tv_20_reduction.setTextColor(this.getResources().getColor(R.color.players_max));
                break;
            case R.id.btn_analysis://????????????

                //??????????????? ????????????????????????????????????????????????????????????
                analysisJson();

                pb_loading.setVisibility(View.VISIBLE);
                iv_loading.setVisibility(View.GONE);

                Log.d(TAG, "onClick: ---??????:" + reduction);

                new Thread() {
                    public void run() {

                        tag = et_tag.getText().toString();

                        Log.d(TAG, "run: ---?????????????????????" + tag);

                        OkHttpClient client = new OkHttpClient.Builder()
                                //.connectTimeout(10, TimeUnit.SECONDS)
                                //.readTimeout(15, TimeUnit.SECONDS)
                                .connectTimeout(60, TimeUnit.SECONDS)//????????????????????????
                                .readTimeout(100, TimeUnit.SECONDS)//????????????????????????
                                .writeTimeout(60, TimeUnit.SECONDS)//????????????????????????
                                .build();

                        //MD5??????
                        String md5 = null;
                        try {
                            md5 = Md5Utils.md5(CocAnalysis.this.getResources().getString(R.string.app_name)
                                    + CocAnalysis.this.getResources().getString(R.string.gzh));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "run: ---????????????token:" + md5);

                        Request request = new Request.Builder()
                                .addHeader("mode", "getplayers")//header???????????????
                                .addHeader("token", md5)
                                .get()
                                .url("https://coctools.top/players/" + tag)
                                .build();

                        Call call = client.newCall(request);

                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.e(TAG, "onFailure: ????????????:" + e);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                String res = response.body().string();
                                Log.d(TAG, "onResponse: ---????????????????????????:" + res);

                                if (response.code() != 200) {
                                    Log.d(TAG, "onResponse: ---????????????-???????????????->" + response.code());
                                    return;
                                }

                                if (res.contains("???????????????")) {
                                    Log.d(TAG, "onResponse: ---????????????-???????????????->" + res);
                                    return;
                                }

                                try {
                                    JSONObject json = JSONObject.parseObject(res);

                                    String kingName[] = {"Barbarian King", "Archer Queen", "Grand Warden", "Royal Champion", "Battle Machine"};

                                    //????????????
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

                                        String name = jsonHeroes.getString("name");//????????????
                                        String village = jsonHeroes.getString("village");//??????????????????
                                        String level = jsonHeroes.getString("level");//??????????????????
                                        String maxLevel = jsonHeroes.getString("maxLevel");//??????????????????

                                        switch (name) {
                                            case "Barbarian King"://??????
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                            case "Archer Queen"://??????
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                            case "Grand Warden"://??????
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                            case "Royal Champion"://??????
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                            case "Battle Machine"://????????????
                                                kingData.put(name, "[" + level + "," + maxLevel + "]");
                                                break;
                                        }


                                    }
                                    Log.d(TAG, "onResponse: ---kingData:" + kingData);

                                    String data = SpUtils.getString(CocAnalysis.this, "analysisJson");
                                    JSONObject king = JSONObject.parseObject(data);

                                    //??????
                                    int mCost = tj(JSONArray.parseArray(king.getString("mking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0),
                                            0
                                    );
                                    int mTime = tj(JSONArray.parseArray(king.getString("mking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0),
                                            1
                                    );
                                    int mTime2 = tj(JSONArray.parseArray(king.getString("mking")), 0, 1);

                                    //??????
                                    int nCost = tj(JSONArray.parseArray(king.getString("nking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0),
                                            0
                                    );
                                    int nTime = tj(JSONArray.parseArray(king.getString("nking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0),
                                            1
                                    );
                                    int nTime2 = tj(JSONArray.parseArray(king.getString("nking")), 0, 1);

                                    //??????
                                    int yCost = tj(JSONArray.parseArray(king.getString("yking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0),
                                            0
                                    );
                                    int yTime = tj(JSONArray.parseArray(king.getString("yking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0),
                                            1
                                    );
                                    int yTime2 = tj(JSONArray.parseArray(king.getString("yking")), 0, 1);

                                    //??????
                                    int rCost = tj(JSONArray.parseArray(king.getString("rking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0),
                                            0
                                    );
                                    int rTime = tj(JSONArray.parseArray(king.getString("rking")),
                                            (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0),
                                            1
                                    );
                                    int rTime2 = tj(JSONArray.parseArray(king.getString("rking")), 0, 1);

                                    //????????????
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
                                    //???????????????????????????
                                    Bundle bundle = new Bundle();
                                    switch (String.valueOf(reduction)) {
                                        case "1.0":
                                            bundle.putInt("????????????", (int) ((mCost + nCost + rCost) * reduction));
                                            bundle.putInt("????????????", (int) (yCost * reduction));
                                            bundle.putInt("??????????????????", (int) (zCost * reduction));
                                            bundle.putInt("????????????", (int) ((mTime + nTime + yTime + rTime + zTime) * reduction));
                                            bundle.putDouble("???????????????", ((double)
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
//                                            bundle.putDouble("???????????????", 100 - (double) ((mTime + nTime + yTime + rTime + zTime) * reduction)
//                                                    / ((mTime2 + nTime2 + yTime2 + rTime2 + zTime2) * reduction) * 100);

                                            //??????
                                            bundle.putInt("????????????", (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0));
                                            bundle.putInt("????????????", mCost);
                                            bundle.putInt("????????????", mTime);
                                            bundle.putDouble("???????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(1) * 100);

                                            //??????
                                            bundle.putInt("????????????", (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0));
                                            bundle.putInt("????????????", nCost);
                                            bundle.putInt("????????????", nTime);
                                            bundle.putDouble("???????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(1) * 100);

                                            //??????
                                            bundle.putInt("????????????", (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0));
                                            bundle.putInt("????????????", yCost);
                                            bundle.putInt("????????????", yTime);
                                            bundle.putDouble("???????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(1) * 100);

                                            //??????
                                            bundle.putInt("????????????", (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0));
                                            bundle.putInt("????????????", rCost);
                                            bundle.putInt("????????????", rTime);
                                            bundle.putDouble("???????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(1) * 100);

                                            //????????????
                                            bundle.putInt("??????????????????", (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0));
                                            bundle.putInt("??????????????????", zCost);
                                            bundle.putInt("??????????????????", zTime);
                                            bundle.putDouble("?????????????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(1) * 100);

                                            break;
                                        default:
                                            bundle.putInt("????????????", (int) ((int) (mCost + nCost + rCost) - ((mCost + nCost + rCost) * reduction)));
                                            bundle.putInt("????????????", (int) ((int) yCost - (yCost * reduction)));
                                            bundle.putInt("??????????????????", (int) ((int) zCost - (zCost * reduction)));
                                            bundle.putInt("????????????", (int) ((int) (mTime + nTime + yTime + rTime + zTime) - ((mTime + nTime + yTime + rTime + zTime) * reduction)));
                                            bundle.putDouble("???????????????", ((double)
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
//                                            bundle.putDouble("???????????????", 100 - (double) ((mTime + nTime + yTime + rTime + zTime) * reduction)
//                                                    / ((mTime2 + nTime2 + yTime2 + rTime2 + zTime2) * reduction) * 100);

                                            //??????
                                            bundle.putInt("????????????", (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0));
                                            bundle.putInt("????????????", (int) (mCost - (mCost * reduction)));
                                            bundle.putInt("????????????", (int) (mTime - (mTime * reduction)));
                                            bundle.putDouble("???????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Barbarian King")).get(1) * 100);

                                            //??????
                                            bundle.putInt("????????????", (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0));
                                            bundle.putInt("????????????", (int) (nCost - (nCost * reduction)));
                                            bundle.putInt("????????????", (int) (nTime - (nTime * reduction)));
                                            bundle.putDouble("???????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Archer Queen")).get(1) * 100);

                                            //??????
                                            bundle.putInt("????????????", (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0));
                                            bundle.putInt("????????????", (int) (yCost - (yCost * reduction)));
                                            bundle.putInt("????????????", (int) (yTime - (yTime * reduction)));
                                            bundle.putDouble("???????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Grand Warden")).get(1) * 100);

                                            //??????
                                            bundle.putInt("????????????", (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0));
                                            bundle.putInt("????????????", (int) (rCost - (rCost * reduction)));
                                            bundle.putInt("????????????", (int) (rTime - (rTime * reduction)));
                                            bundle.putDouble("???????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(0)
                                                    / (Integer) JSONArray.parseArray(kingData.get("Royal Champion")).get(1) * 100);

                                            //????????????
                                            bundle.putInt("??????????????????", (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0));
                                            bundle.putInt("??????????????????", (int) (zCost - (zCost * reduction)));
                                            bundle.putInt("??????????????????", (int) (zTime - (zTime * reduction)));
                                            bundle.putDouble("?????????????????????", (double) (Integer) JSONArray.parseArray(kingData.get("Battle Machine")).get(0)
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

    //UI????????????
    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {

                Bundle bundle = msg.getData();

                tv_threeKingCost.setText(String.valueOf(bundle.getInt("????????????") / 10000) + "???");
                tv_ywKingCost.setText(String.valueOf(bundle.getInt("????????????") / 10000) + "???");
                tv_zzKingCost.setText(String.valueOf(bundle.getInt("??????????????????") / 10000) + "???");
                tv_kingTime.setText(String.valueOf(bundle.getInt("????????????") / 60 / 60 / 24) + "???");
                tv_KingPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("???????????????")) + "%"));

                //??????
                tv_mLevel.setText("Lv." + bundle.getInt("????????????"));
                tv_mCost.setText(String.valueOf(bundle.getInt("????????????") / 10000) + "???");
                tv_mTime.setText(String.valueOf(bundle.getInt("????????????") / 60 / 60 / 24) + "???");
                tv_mPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("???????????????")) + "%"));

                //??????
                tv_nLevel.setText("Lv." + bundle.getInt("????????????"));
                tv_nCost.setText(String.valueOf(bundle.getInt("????????????") / 10000) + "???");
                tv_nTime.setText(String.valueOf(bundle.getInt("????????????") / 60 / 60 / 24) + "???");
                tv_nPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("???????????????")) + "%"));

                //??????
                tv_yLevel.setText("Lv." + bundle.getInt("????????????"));
                tv_yCost.setText(String.valueOf(bundle.getInt("????????????") / 10000) + "???");
                tv_yTime.setText(String.valueOf(bundle.getInt("????????????") / 60 / 60 / 24) + "???");
                tv_yPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("???????????????")) + "%"));

                //??????
                tv_rLevel.setText("Lv." + bundle.getInt("????????????"));
                tv_rCost.setText(String.valueOf(bundle.getInt("????????????") / 10000) + "???");
                tv_rTime.setText(String.valueOf(bundle.getInt("????????????") / 60 / 60 / 24) + "???");
                tv_rPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("???????????????")) + "%"));

                //????????????
                tv_zLevel.setText("Lv." + bundle.getInt("??????????????????"));
                tv_zCost.setText(String.valueOf(bundle.getInt("??????????????????") / 10000) + "???");
                tv_zTime.setText(String.valueOf(bundle.getInt("??????????????????") / 60 / 60 / 24) + "???");
                tv_zPercentage.setText(String.valueOf(String.format("%.1f", bundle.getDouble("?????????????????????")) + "%"));

                pb_loading.setVisibility(View.GONE);
                iv_loading.setVisibility(View.VISIBLE);

                Log.d(TAG, "handleMessage: -----------" + msg);
            }

        }
    };

}
package com.ssyj.coc;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.security.NoSuchAlgorithmException;
import java.security.SecurityPermission;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of App Widget functionality.
 */
public class CocAppWidget extends AppWidgetProvider {

    private static final String TAG = "CocAppWidget";

    long startTime = System.currentTimeMillis(); //获取开始时间

    long endTime; //获取结束时间

    //个人标签
    private String playerTag = "null";
    //商品序号
    private String goodsIndex = "null";
    //储存个人标签
    private String playerTags[] = {"playersTag1", "playersTag2", "playersTag3", "playersTag4", "playersTag5", "playersTag6"};
    //储存商品序号
    private String goodIndexs[] = {"goodsIndex1", "goodsIndex2", "goodsIndex3", "goodsIndex4", "goodsIndex5", "goodsIndex6"};

    @RequiresApi(api = Build.VERSION_CODES.N)
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String[] str) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.coc_app_widget);

        //桌面部件透明设置
        if (SpUtils.getBoolean(context, "cocWidgetBgTmColor", false)) {//透明
            views.setViewVisibility(R.id.cocAppWidgetColor1, View.GONE);
        } else {//不透明
            views.setViewVisibility(R.id.cocAppWidgetColor1, View.VISIBLE);
        }

        //设置刷新或前进或后退时的对应个人标签
        switch (str[0]) {
            case "刷新":
                loading(context, views, "刷新", str[1]);//str[1]是当前标签序号
                break;
            case "前进":
                loading(context, views, "前进", str[1]);//str[1]是当前标签序号
                break;
            case "后退":
                loading(context, views, "后退", str[1]);//str[1]是当前标签序号
                break;
        }

        //个人信息
        players(context, appWidgetManager, appWidgetIds, views);
        //鱼情信息
        finsh(context, appWidgetManager, appWidgetIds, views);
        //商品信息
        goods(context, appWidgetManager, appWidgetIds, views);
        //点击事件
        click(context, appWidgetManager, appWidgetIds, views);

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    //通过当前个人标签序号获取对应的个人标签
    private void loading(Context context, RemoteViews views, String str, String index) {
        switch (str) {
            case "刷新":
            case "前进":
            case "后退":
                switch (index) {//设置个人标签和商品序号
                    case "1":
                        this.playerTag = SpUtils.getString(context, playerTags[0], "null");
                        this.goodsIndex = SpUtils.getString(context, goodIndexs[0], "0");
                        break;
                    case "2":
                        this.playerTag = SpUtils.getString(context, playerTags[1], "null");
                        this.goodsIndex = SpUtils.getString(context, goodIndexs[1], "0");
                        break;
                    case "3":
                        this.playerTag = SpUtils.getString(context, playerTags[2], "null");
                        this.goodsIndex = SpUtils.getString(context, goodIndexs[2], "0");
                        break;
                    case "4":
                        this.playerTag = SpUtils.getString(context, playerTags[3], "null");
                        this.goodsIndex = SpUtils.getString(context, goodIndexs[3], "0");
                        break;
                    case "5":
                        this.playerTag = SpUtils.getString(context, playerTags[4], "null");
                        this.goodsIndex = SpUtils.getString(context, goodIndexs[4], "0");
                        break;
                    case "6":
                        this.playerTag = SpUtils.getString(context, playerTags[5], "null");
                        this.goodsIndex = SpUtils.getString(context, goodIndexs[5], "0");
                        break;
                }
                views.setViewVisibility(R.id.pb_loading, View.VISIBLE);//刷新转动可视
                views.setViewVisibility(R.id.iv_loading, View.GONE);//刷新图片隐藏
                break;
        }
    }

    //用于前进或后退时重新设置当前标签序号
    private void qjAndHt(Context context, String str) {
        //获取当前所在的个人标签序号
        String index = SpUtils.getString(context, "playersIndex", "1");
        switch (str) {
            case "前进"://满足两个条件才能前进：1.当前个人标签序号等于所设置序号 2.前进完的个人标签不为空 。 如果成立就前进
                if ("1".equals(index) & !"".equals(SpUtils.getString(context, playerTags[1], ""))) {
                    SpUtils.setString(context, "playersIndex", "2");
                } else if ("2".equals(index) & !"".equals(SpUtils.getString(context, playerTags[2], ""))) {
                    SpUtils.setString(context, "playersIndex", "3");
                } else if ("3".equals(index) & !"".equals(SpUtils.getString(context, playerTags[3], ""))) {
                    SpUtils.setString(context, "playersIndex", "4");
                } else if ("4".equals(index) & !"".equals(SpUtils.getString(context, playerTags[4], ""))) {
                    SpUtils.setString(context, "playersIndex", "5");
                } else if ("5".equals(index) & !"".equals(SpUtils.getString(context, playerTags[5], ""))) {
                    SpUtils.setString(context, "playersIndex", "6");
                } else {
                    Toast.makeText(context, "标签到顶", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "qjAndHt: ---前进:标签到顶");
                }
                break;
            case "后退"://满足两个条件才能后退：1.当前个人标签序号等于所设置序号 2.后退完的个人标签不为空 。 如果成立就后退
                if ("6".equals(index) & !"".equals(SpUtils.getString(context, playerTags[4], ""))) {
                    SpUtils.setString(context, "playersIndex", "5");
                } else if ("5".equals(index) & !"".equals(SpUtils.getString(context, playerTags[3], ""))) {
                    SpUtils.setString(context, "playersIndex", "4");
                } else if ("4".equals(index) & !"".equals(SpUtils.getString(context, playerTags[2], ""))) {
                    SpUtils.setString(context, "playersIndex", "3");
                } else if ("3".equals(index) & !"".equals(SpUtils.getString(context, playerTags[1], ""))) {
                    SpUtils.setString(context, "playersIndex", "2");
                } else if ("2".equals(index) & !"".equals(SpUtils.getString(context, playerTags[0], ""))) {
                    SpUtils.setString(context, "playersIndex", "1");
                } else {
                    Toast.makeText(context, "标签到底", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "qjAndHt: ---后退:标签到底");
                }
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //获取桌面部件的ID
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), CocAppWidget.class.getName()));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.coc_app_widget);

        //点击事件
        click(context, appWidgetManager, appWidgetIds, views);

        switch (intent.getAction()) {
            case "R.id.ll_loading"://刷新
                Log.d(TAG, "onReceive: ---刷新-点击成功:" + intent.getAction());

                updateAppWidget(context, appWidgetManager, appWidgetIds,
                        new String[]{"刷新", SpUtils.getString(context, "playersIndex", "1")});
                break;
            case "R.id.iv_qj"://前进
                Log.d(TAG, "onReceive: ---前进-点击成功:" + intent.getAction());

                //用于前进时重新设置当前标签序号
                qjAndHt(context, "前进");
                updateAppWidget(context, appWidgetManager, appWidgetIds,
                        new String[]{"前进", SpUtils.getString(context, "playersIndex", "1")});
                break;
            case "R.id.iv_ht"://后退
                Log.d(TAG, "onReceive: ---后退-点击成功:" + intent.getAction());

                //用于后退时重新设置当前标签序号
                qjAndHt(context, "后退");
                updateAppWidget(context, appWidgetManager, appWidgetIds,
                        new String[]{"后退", SpUtils.getString(context, "playersIndex", "1")});
                break;
            case "R.id.fl_onclick"://防止点击
                break;
            default:
                Log.d(TAG, "onReceive: ---未知:" + intent.getAction());
        }

    }

    //widget被添加 || 更新时调用
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d(TAG, "onUpdate: ---部件添加");

        //初始化
        SpUtils.setString(context, "playersIndex", "1");//重新放置桌面部件，默认设置个人标签序号为1
        //第一次初始化商品日期,如果本地未保存商品日期，就进行保存
        if ("null".equals(SpUtils.getString(context, "goodsDate", "null"))) {
            SimpleDateFormat df = new SimpleDateFormat("dd");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间
            SpUtils.setString(context, "goodsDate", date);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.coc_app_widget);

        //点击事件
        click(context, appWidgetManager, appWidgetIds, views);

        updateAppWidget(context, appWidgetManager, appWidgetIds, new String[]{"刷新", SpUtils.getString(context, "playersIndex")});
    }

    //widget被删除时调用
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    //第一个widget被添加时调用
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        //获取桌面部件的ID
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), CocAppWidget.class.getName()));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.coc_app_widget);

        //个人信息
        players(context, appWidgetManager, appWidgetIds, views);
        //鱼情信息
        finsh(context, appWidgetManager, appWidgetIds, views);
        //商品信息
        goods(context, appWidgetManager, appWidgetIds, views);
        //点击事件
        click(context, appWidgetManager, appWidgetIds, views);

    }

    //最后一个widget被删除时调用
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    //点击事件
    private void click(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,
                       RemoteViews views) {
        Intent intent = new Intent(context, CocAppWidget.class);

        //刷新
        intent.setAction("R.id.ll_loading");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 200, intent, 0);
        views.setOnClickPendingIntent(R.id.ll_loading, pendingIntent);

        //防止点击
        intent.setAction("R.id.fl_onclick");
        pendingIntent = PendingIntent.getBroadcast(context, 200, intent, 0);
        views.setOnClickPendingIntent(R.id.fl_onclick, pendingIntent);

        //前进
        intent.setAction("R.id.iv_qj");
        pendingIntent = PendingIntent.getBroadcast(context, 200, intent, 0);
        views.setOnClickPendingIntent(R.id.iv_qj, pendingIntent);

        //后退
        intent.setAction("R.id.iv_ht");
        pendingIntent = PendingIntent.getBroadcast(context, 200, intent, 0);
        views.setOnClickPendingIntent(R.id.iv_ht, pendingIntent);

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    //coc个人信息
    private void players(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,
                         RemoteViews views) {

        //如果传过来的标签为null停止往下执行
        if ("null".equals(playerTag)) {
            views.setTextViewText(R.id.tv_nameTag, "null");
            views.setTextColor(R.id.tv_nameTag, context.getResources().getColor(R.color.players_error));
            return;
        }

        new Thread() {
            public void run() {

                //String tag = "99GGQVR8C";
                //String tag = "YYCUC";
                //String tag = "YL8YCPUGR";

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                        .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
                        .writeTimeout(60, TimeUnit.SECONDS)//设置写的超时时间
                        .build();

                //MD5加密
                String md5 = null;
                try {
                    md5 = Md5Utils.md5(context.getResources().getString(R.string.app_name)
                            + context.getResources().getString(R.string.gzh));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "run: ---个人信息token:" + md5);

                Request request = new Request.Builder()
                        .addHeader("mode", "getplayers")//header头信息必带
                        .addHeader("token", md5)
                        .get()
                        .url("https://coctools.top/players/" + playerTag)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e(TAG, "onFailure: ---个人信息okhttp错误: " + e);
                    }

                    @SuppressLint({"ResourceAsColor", "ResourceType"})
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String res = response.body().string();
                        Log.d(TAG, "onResponse: ---个人信息返回内容" + res);

                        //访问错误l
                        if ("200".equals(response.code())) {
                            views.setTextViewText(R.id.tv_nameTag, "code:" + response.code());
                            views.setTextColor(R.id.tv_nameTag, context.getResources().getColor(R.color.players_error));

                            Log.e(TAG, "onResponse: ---" + "code:" + response.code());

                            for (int appWidgetId : appWidgetIds) {
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }
                            return;
                        }

                        //mode or token验证不正确
                        if (res.contains("不正确")) {
                            views.setTextViewText(R.id.tv_nameTag, "mode or token:不正确");
                            views.setTextColor(R.id.tv_nameTag, context.getResources().getColor(R.color.players_error));
                            for (int appWidgetId : appWidgetIds) {
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }
                            ;
                            Log.e(TAG, "onResponse: ---mode or token:不正确");
                            return;
                        }

                        //判断res返回的信息里面是否包含code
                        if (res.contains("code")) {
                            try {
//                                JSONObject json = new JSONObject(res);
//                                String code = json.getString("code");
//                                String error = json.getString("error");

                                JSONObject json = JSONObject.parseObject(res);
                                String code = json.getString("code");
                                String error = json.getString("error");
                                views.setTextViewText(R.id.tv_nameTag, "code:" + code + "/error:" + error);
                                views.setTextColor(R.id.tv_nameTag, context.getResources().getColor(R.color.players_error));

                                Log.e(TAG, "onResponse: ---" + "code:" + code + "/error:" + error);

                                for (int appWidgetId : appWidgetIds) {
                                    appWidgetManager.updateAppWidget(appWidgetId, views);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            //JSONObject json = new JSONObject(res);

                            JSONObject json = JSONObject.parseObject(res);

                            //获取名称和标签
                            String name = json.getString("name");
                            String tag = json.getString("tag");
                            views.setTextViewText(R.id.tv_nameTag, name + "-" + tag);
                            views.setTextColor(R.id.tv_nameTag, context.getResources().getColor(R.color.players_black));

                            //段位图片
                            String ivUrl = json.getJSONObject("league").getJSONObject("iconUrls").getString("medium");
                            views.setImageViewBitmap(R.id.iv_trophies, Utils.getBitmap(ivUrl));

                            //主世界奖杯,夜世界奖杯
                            String trophies = json.getString("trophies");
                            String versusTrophies = json.getString("versusTrophies");
                            //主世界最高奖杯, 夜市界最高奖杯
                            String bestTrophies = json.getString("bestTrophies");
                            String bestVersusTrophies = json.getString("bestVersusTrophies");
                            views.setTextViewText(R.id.tv_trophy, "奖杯:" + trophies + "/" + versusTrophies + "(" + bestTrophies + "/" + bestVersusTrophies + ")");

                            //玩家等级
                            String level = json.getString("expLevel");
                            views.setTextViewText(R.id.tv_playersLeve, "等级:" + level);

                            //个人进攻/防御胜场
                            String geRenAttackWins = json.getString("attackWins");
                            String geRenDefenseWins = json.getString("defenseWins");
                            views.setTextViewText(R.id.tv_attackDefence, "攻防:" + geRenAttackWins + "/" + geRenDefenseWins);

                            //判断玩家是否加入部落
                            if (res.contains("clan")) {//加入部落

                                JSONObject tribe = json.getJSONObject("clan");//部落
                                String tribeTAg = tribe.getString("tag");//部落标签
                                String tribeName = tribe.getString("name");//部落名称
                                String tribeLevel = tribe.getString("clanLevel");//部落等级
                                String tribeImage = tribe.getJSONObject("badgeUrls").getString("large");//部落图片
                                //部落职位(member:成员, admin:长老, coLeader:副首领, leader:首领)
                                String tribeRole = json.getString("role");

                                //职位
                                String Role = tribeRole;

                                //英文职位转中文职位
                                switch (tribeRole) {
                                    case "member":
                                        Role = "成员";
                                        break;
                                    case "admin":
                                        Role = "长老";
                                        break;
                                    case "coLeader":
                                        Role = "副首领";
                                        break;
                                    case "leader":
                                        Role = "首领";
                                        break;
                                    default:
                                        Log.e(TAG, "---错误：部落职位:" + tribeRole);
                                }

                                views.setTextViewText(R.id.tv_clan,
                                        "部落:" + tribeTAg + "-" + tribeName + "(Lv." + tribeLevel + ")" + Role);
                                views.setImageViewBitmap(R.id.iv_clan, Utils.getBitmap(tribeImage));
                            } else {//未加入部落
                                views.setTextViewText(R.id.tv_clan, "部落:未加入");
                                views.setImageViewResource(R.id.iv_clan, R.drawable.kb);
                            }

                            //个人捐兵,个人收兵
                            String donations = json.getString("donations");
                            String dnationsReceived = json.getString("donationsReceived");
                            views.setTextViewText(R.id.tv_giveCollect, "捐收:" + donations + "/" + dnationsReceived);

                            //个人胜利之星
                            String warStars = json.getString("warStars");
                            views.setTextViewText(R.id.tv_warStars, "胜利之星:" + warStars);

                            //对抗赛胜场
                            String versusBattleWins = json.getString("versusBattleWins");
                            views.setTextViewText(R.id.tv_versusBattleWins, "对抗赛胜场:" + versusBattleWins);

                            //个人大本营等级
                            String townHallLevel = json.getString("townHallLevel");
                            String townHallWeaponLevel;//大本营星级
                            //主世界:14本将文字设置成橙色, 1-13本将文字设置成黑色
                            switch (townHallLevel) {
                                case "13":
                                    townHallWeaponLevel = json.getString("townHallWeaponLevel");
                                    views.setTextViewText(R.id.tv_townHallLevel, "Lv." + townHallLevel + "-" + townHallWeaponLevel);
                                    views.setTextColor(R.id.tv_townHallLevel, context.getResources().getColor(R.color.players_black));//黑色
                                    break;
                                case "14":
                                    townHallWeaponLevel = json.getString("townHallWeaponLevel");
                                    views.setTextViewText(R.id.tv_townHallLevel, "Lv." + townHallLevel + "-" + townHallWeaponLevel);
                                    views.setTextColor(R.id.tv_townHallLevel, context.getResources().getColor(R.color.players_max));//橙色
                                    break;
                                default:
                                    views.setTextViewText(R.id.tv_townHallLevel, "Lv." + townHallLevel);
                                    views.setTextColor(R.id.tv_townHallLevel, context.getResources().getColor(R.color.players_black));//黑色
                            }

                            String builderHallLevel = "未解锁";
                            //判断玩家夜世界是否解锁
                            if (res.contains("builderHallLevel")) {//已解锁
                                //geRenBuilderHallLevel:夜世界大本营等级
                                builderHallLevel = json.getString("builderHallLevel");
                            }

                            //夜世界:9本将文字设置成橙色, 1-8本将文字设置成黑色
                            switch (builderHallLevel) {
                                case "未解锁":
                                    views.setTextViewText(R.id.tv_builderHallLevel, "未解锁");
                                    views.setTextColor(R.id.tv_builderHallLevel, context.getResources().getColor(R.color.players_black));
                                    break;
                                case "9":
                                    views.setTextViewText(R.id.tv_builderHallLevel, "Lv." + builderHallLevel);
                                    views.setTextColor(R.id.tv_builderHallLevel, context.getResources().getColor(R.color.players_max));
                                    break;
                                default:
                                    views.setTextViewText(R.id.tv_builderHallLevel, "Lv." + builderHallLevel);
                                    views.setTextColor(R.id.tv_builderHallLevel, context.getResources().getColor(R.color.players_black));
                            }

                            //根据大本等级设置大本营图标
                            if (townHallLevel.equals("1")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th1);
                            } else if (townHallLevel.equals("2")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th2);
                            } else if (townHallLevel.equals("3")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th3);
                            } else if (townHallLevel.equals("4")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th4);
                            } else if (townHallLevel.equals("5")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th5);
                            } else if (townHallLevel.equals("6")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th6);
                            } else if (townHallLevel.equals("7")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th7);
                            } else if (townHallLevel.equals("8")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th8);
                            } else if (townHallLevel.equals("9")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th9);
                            } else if (townHallLevel.equals("10")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th10);
                            } else if (townHallLevel.equals("11")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th11);

                                //12本
                            } else if (townHallLevel.equals("12") & json.getString("townHallWeaponLevel").equals("1")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th12_1);
                            } else if (townHallLevel.equals("12") & json.getString("townHallWeaponLevel").equals("2")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th12_2);
                            } else if (townHallLevel.equals("12") & json.getString("townHallWeaponLevel").equals("3")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th12_3);
                            } else if (townHallLevel.equals("12") & json.getString("townHallWeaponLevel").equals("4")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th12_4);
                            } else if (townHallLevel.equals("12") & json.getString("townHallWeaponLevel").equals("5")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th12_5);

                                //13本
                            } else if (townHallLevel.equals("13") & json.getString("townHallWeaponLevel").equals("1")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th13_1);
                            } else if (townHallLevel.equals("13") & json.getString("townHallWeaponLevel").equals("2")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th13_2);
                            } else if (townHallLevel.equals("13") & json.getString("townHallWeaponLevel").equals("3")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th13_3);
                            } else if (townHallLevel.equals("13") & json.getString("townHallWeaponLevel").equals("4")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th13_4);
                            } else if (townHallLevel.equals("13") & json.getString("townHallWeaponLevel").equals("5")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th13_5);

                                //14本
                            } else if (townHallLevel.equals("14") & json.getString("townHallWeaponLevel").equals("1")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th14_1);
                            } else if (townHallLevel.equals("14") & json.getString("townHallWeaponLevel").equals("2")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th14_2);
                            } else if (townHallLevel.equals("14") & json.getString("townHallWeaponLevel").equals("3")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th14_3);
                            } else if (townHallLevel.equals("14") & json.getString("townHallWeaponLevel").equals("4")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th14_4);
                            } else if (townHallLevel.equals("14") & json.getString("townHallWeaponLevel").equals("5")) {
                                views.setImageViewResource(R.id.iv_townHallLevel, R.drawable.th14_5);

                            } else {
                                Log.e(TAG, "---主世界大本图标设置错误:" + townHallLevel);
                            }

                            //根据夜世界大本等级设置大本营图标
                            switch (builderHallLevel) {
                                case "未解锁"://夜世界未解锁时的默认设置
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh1);
                                    break;
                                case "1":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh1);
                                    break;
                                case "2":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh2);
                                    break;
                                case "3":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh3);
                                    break;
                                case "4":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh4);
                                    break;
                                case "5":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh5);
                                    break;
                                case "6":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh6);
                                    break;
                                case "7":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh7);
                                    break;
                                case "8":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh8);
                                    break;
                                case "9":
                                    views.setImageViewResource(R.id.iv_builderHallLevel, R.drawable.bh9);
                                    break;
                                default:
                                    Log.e(TAG, "---夜世界大本图标设置错误:" + builderHallLevel);
                            }

                            boolean superIS = false;

                            //兵种信息
                            JSONArray troops = json.getJSONArray("troops");

                            //储存兵种ID
                            Map<String, Integer> troopsId = new HashMap<String, Integer>();
                            troopsId.put("Barbarian", R.id.tv_Barbarian);//野蛮人
                            troopsId.put("Archer", R.id.tv_Archer);//弓箭手
                            troopsId.put("Goblin", R.id.tv_Goblin);//哥布林
                            troopsId.put("Giant", R.id.tv_Giant);//巨人
                            troopsId.put("Wall Breaker", R.id.tv_WallBreaker);//炸弹人
                            troopsId.put("Balloon", R.id.tv_Balloon);//气球兵
                            troopsId.put("Wizard", R.id.tv_Wizard);//法师
                            troopsId.put("Healer", R.id.tv_Healer);//天使
                            troopsId.put("Dragon", R.id.tv_Dragon);//飞龙
                            troopsId.put("P.E.K.K.A", R.id.tv_PEKKA);//皮卡超人
                            troopsId.put("zBaby Dragon", R.id.tv_z_BabyDragon);//飞龙宝宝
                            troopsId.put("Miner", R.id.tv_Miner);//矿工
                            troopsId.put("Yeti", R.id.tv_Yeti);//大雪怪
                            troopsId.put("Electro Dragon", R.id.tv_ElectroDragon);//雷龙
                            troopsId.put("Dragon Rider", R.id.tv_DragonRider);//龙骑士

                            troopsId.put("Minion", R.id.tv_Minion);//亡灵
                            troopsId.put("Hog Rider", R.id.tv_HogRider);//野猪
                            troopsId.put("Valkyrie", R.id.tv_Valkyrie);//武神
                            troopsId.put("Golem", R.id.tv_Golem);//戈仑石人
                            troopsId.put("Witch", R.id.tv_Witch);//女巫
                            troopsId.put("Lava Hound", R.id.tv_LavaHound);//熔岩猎犬
                            troopsId.put("Bowler", R.id.tv_Bowler);//巨石投手
                            troopsId.put("Ice Golem", R.id.tv_IceGolem);//戈仑冰人
                            troopsId.put("Headhunter", R.id.tv_Headhunter);//英雄猎手

                            troopsId.put("Super Barbarian", R.drawable.super_ymr);//超级野蛮人
                            troopsId.put("Super Archer", R.drawable.super_gjs);//超级弓箭手
                            troopsId.put("Super Wall Breaker", R.drawable.super_zdr);//超级炸弹人
                            troopsId.put("Super Giant", R.drawable.super_pz);//超级巨人
                            troopsId.put("Sneaky Goblin", R.drawable.super_gbl);//超级哥布林
                            troopsId.put("Inferno Dragon", R.drawable.super_dyfl);//地狱飞龙
                            troopsId.put("Super Valkyrie", R.drawable.super_ws);//超级武神
                            troopsId.put("Super Witch", R.drawable.super_nw);//超级女巫
                            troopsId.put("Ice Hound", R.drawable.super_hblq);//寒冰猎犬
                            troopsId.put("Super Wizard", R.drawable.super_fs);//超级法师
                            troopsId.put("Super Minion", R.drawable.super_wl);//超级亡灵
                            troopsId.put("Rocket Balloon", R.drawable.super_hjqqb);//火箭气球兵

                            troopsId.put("Raged Barbarian", R.id.tv_RagedBarbarian);//狂暴野蛮人
                            troopsId.put("Sneaky Archer", R.id.tv_SneakyArcher);//隐秘弓箭手
                            troopsId.put("Beta Minion", R.id.tv_BetaMinion);//异变亡灵
                            troopsId.put("Boxer Giant", R.id.tv_BoxerGiant);//巨人拳击手
                            troopsId.put("Bomber", R.id.tv_Bomber);//炸弹兵
                            troopsId.put("Super P.E.K.K.A", R.id.tv_SuperPEKKA);//超级皮卡
                            troopsId.put("Cannon Cart", R.id.tv_CannonCart);//加农炮战车
                            troopsId.put("Drop Ship", R.id.tv_DropShip);//骷髅气球
                            troopsId.put("yBaby Dragon", R.id.tv_y_BabyDragon);//飞龙宝宝
                            troopsId.put("Night Witch", R.id.tv_NightWitch);//暗夜女巫
                            troopsId.put("Hog Glider", R.id.tv_HogGlider);//野猪飞骑

                            troopsId.put("Wall Wrecker", R.id.tv_WallWrecker);//攻城车
                            troopsId.put("Battle Blimp", R.id.tv_BattleBlimp);//攻城飞艇
                            troopsId.put("Stone Slammer", R.id.tv_StoneSlammer);//攻城气球
                            troopsId.put("Siege Barracks", R.id.tv_SiegeBarracks);//攻城训练营
                            troopsId.put("Log Launcher", R.id.tv_LogLauncher);//攻城滚木车

                            troopsId.put("L.A.S.S.I", R.id.tv_LASSI);//狗
                            troopsId.put("Mighty Yak", R.id.tv_MightyYak);//牛
                            troopsId.put("Electro Owl", R.id.tv_ElectroOwl);//鸟
                            troopsId.put("Unicorn", R.id.tv_Unicorn);//马

                            //superLevel用于储存超级兵种对应的普通兵种当前等级
                            Map<String, String> superLevel = new HashMap<String, String>();
                            //superLevel用于储存超级兵种对应的普通兵种最高等级
                            Map<String, String> superMaxLevel = new HashMap<String, String>();

                            //先清除兵种信息
                            for (String key : troopsId.keySet()) {
                                Log.d(TAG, "onResponse: ---兵种信息清除:" + key);
                                //清除普通兵种和战宠
                                views.setTextViewText(troopsId.get(key), "N");
                                views.setTextColor(troopsId.get(key), context.getResources().getColor(R.color.players_black));

                                //超级兵种清除
                                views.setImageViewResource(R.id.iv_super1, R.drawable.tm);
                                views.setTextViewText(R.id.tv_superLevel1, "");
                                views.setImageViewResource(R.id.iv_super2, R.drawable.tm);
                                views.setTextViewText(R.id.tv_superLevel2, "");
                            }

                            //设置兵种信息
                            //for (int i = 0; i < troops.length(); i++) {

                            for (int i = 0; i < troops.size(); i++) {
                                Object str = troops.get(i);
                                //JSONObject jsonTroops = new JSONObject(str.toString());

                                JSONObject jsonTroops = JSONObject.parseObject(str.toString());

                                name = jsonTroops.getString("name");//兵种名称
                                String village = jsonTroops.getString("village");//兵种所在家乡
                                level = jsonTroops.getString("level");//当前等级
                                String maxLevel = jsonTroops.getString("maxLevel");//最高等级

                                //存储超级兵种当前和最高等级
                                switch (name) {
                                    case "Barbarian"://野蛮人
                                        superLevel.put("Super Barbarian", level);
                                        superMaxLevel.put("Super Barbarian", maxLevel);
                                        break;
                                    case "Archer"://弓箭手
                                        superLevel.put("Super Archer", level);
                                        superMaxLevel.put("Super Archer", maxLevel);
                                        break;
                                    case "Wall Breaker"://炸弹人
                                        superLevel.put("Super Wall Breaker", level);
                                        superMaxLevel.put("Super Wall Breaker", maxLevel);
                                        break;
                                    case "Giant"://巨人
                                        superLevel.put("Super Giant", level);
                                        superMaxLevel.put("Super Giant", maxLevel);
                                        break;
                                    case "Goblin"://哥布林
                                        superLevel.put("Sneaky Goblin", level);
                                        superMaxLevel.put("Sneaky Goblin", maxLevel);
                                        break;
                                    case "Baby Dragon"://飞龙宝宝
                                        if ("home".equals(village)) {//家乡的飞龙宝宝等级
                                            superLevel.put("Inferno Dragon", level);
                                            superMaxLevel.put("Inferno Dragon", maxLevel);
                                        }
                                        break;
                                    case "Valkyrie"://武神
                                        superLevel.put("Super Valkyrie", level);
                                        superMaxLevel.put("Super Valkyrie", maxLevel);
                                        break;
                                    case "Witch"://女巫
                                        superLevel.put("Super Witch", level);
                                        superMaxLevel.put("Super Witch", maxLevel);
                                        break;
                                    case "Lava Hound"://熔岩猎犬
                                        superLevel.put("Ice Hound", level);
                                        superMaxLevel.put("Ice Hound", maxLevel);
                                        break;
                                    case "Wizard"://法师
                                        superLevel.put("Super Wizard", level);
                                        superMaxLevel.put("Super Wizard", maxLevel);
                                        break;
                                    case "Minion"://亡灵
                                        superLevel.put("Super Minion", level);
                                        superMaxLevel.put("Super Minion", maxLevel);
                                        break;
                                    case "Balloon"://火箭气球兵
                                        superLevel.put("Rocket Balloon", level);
                                        superMaxLevel.put("Rocket Balloon", maxLevel);
                                        break;
                                }

                                //解锁超级兵
                                if (jsonTroops.toString().contains("superTroopIsActive")) {
                                    if (superIS == false) {//解锁超级兵种1
                                        superIS = true;

                                        views.setImageViewResource(R.id.iv_super1, troopsId.get(name));
                                        views.setTextViewText(R.id.tv_superLevel1, superLevel.get(name));
                                        Log.d(TAG, "onResponse: ---冲击波" + superLevel.get(name));
                                        //超级兵种等级颜色设置
                                        if (superMaxLevel.get(name).equals(superLevel.get(name))) {//达到最高等级,颜色为橙色
                                            views.setTextColor(R.id.tv_superLevel1, context.getResources().getColor(R.color.players_max));
                                        } else {//默认颜色黑色
                                            views.setTextColor(R.id.tv_superLevel1, context.getResources().getColor(R.color.players_black));
                                        }
                                    } else if (superIS == true) {//解锁超级兵种2
                                        superIS = false;

                                        views.setImageViewResource(R.id.iv_super2, troopsId.get(name));
                                        views.setTextViewText(R.id.tv_superLevel2, superLevel.get(name));

                                        //超级兵种等级颜色设置
                                        if (superMaxLevel.get(name).equals(superLevel.get(name))) {//达到最高等级,颜色为橙色
                                            views.setTextColor(R.id.tv_superLevel2, context.getResources().getColor(R.color.players_max));
                                        } else {//默认颜色黑色
                                            views.setTextColor(R.id.tv_superLevel2, context.getResources().getColor(R.color.players_black));
                                        }
                                    }
                                }

                                //主世界与夜世界的飞龙宝宝名称相同,所以在此做判断进行区分
                                if ("Baby Dragon".equals(name) & "home".equals(village)) {
                                    name = "zBaby Dragon";//主世界飞龙宝宝
                                    Log.d(TAG, "onResponse: ---主世界飞龙宝宝");
                                } else if ("Baby Dragon".equals(name) & "builderBase".equals(village)) {
                                    name = "yBaby Dragon";//夜世界飞龙宝宝
                                    Log.d(TAG, "onResponse: ---夜世界飞龙宝宝");
                                }

                                views.setTextViewText(troopsId.get(name), level);
                                //兵种等级颜色设置
                                if (maxLevel.equals(level)) {//达到最高等级,颜色为橙色
                                    views.setTextColor(troopsId.get(name), context.getResources().getColor(R.color.players_max));
                                } else {//默认颜色黑色
                                    views.setTextColor(troopsId.get(name), context.getResources().getColor(R.color.players_black));
                                }

                                Log.d(TAG, "onResponse: ---兵种信息:" + str);
                            }

                            //英雄信息
                            JSONArray heroes = json.getJSONArray("heroes");

                            Log.d(TAG, "onResponse: ---英雄信息" + heroes);

                            //储存英雄ID
                            Map<String, Integer> heroesId = new HashMap<String, Integer>();
                            heroesId.put("Barbarian King", R.id.tv_BarbarianKing);//蛮王
                            heroesId.put("Archer Queen", R.id.tv_ArcherQueen);//女王
                            heroesId.put("Grand Warden", R.id.tv_GrandWarden);//永王
                            heroesId.put("Royal Champion", R.id.tv_RoyalChampion);//闰土
                            heroesId.put("Battle Machine", R.id.tv_BattleMachine);//战争机器

                            //清除英雄信息
                            for (String key : heroesId.keySet()) {
                                Log.d(TAG, "onResponse: ---英雄信息清除:" + key);
                                views.setTextViewText(heroesId.get(key), "N");
                                views.setTextColor(heroesId.get(key), context.getResources().getColor(R.color.players_black));
                            }

                            //设置英雄信息
                            //for (int i = 0; i < heroes.length(); i++) {

                            for (int i = 0; i < heroes.size(); i++) {
                                Object str = heroes.get(i);
                                //JSONObject jsonHeroes = new JSONObject(str.toString());

                                JSONObject jsonHeroes = JSONObject.parseObject(str.toString());

                                name = jsonHeroes.getString("name");//英雄名称
                                String village = jsonHeroes.getString("village");//英雄所在家乡
                                level = jsonHeroes.getString("level");//英雄当前等级
                                String maxLevel = jsonHeroes.getString("maxLevel");//英雄最高等级

                                Log.d(TAG, "onResponse: ---英雄信息" + level);

                                views.setTextViewText(heroesId.get(name), level);

                                //英雄等级颜色设置
                                if (maxLevel.equals(level)) {//达到最高等级,颜色为橙色
                                    views.setTextColor(heroesId.get(name), context.getResources().getColor(R.color.players_max));
                                } else {//默认颜色黑色
                                    views.setTextColor(heroesId.get(name), context.getResources().getColor(R.color.players_black));
                                }

                                Log.d(TAG, "onResponse: ---英雄信息:" + str);
                            }

                            //法术信息
                            JSONArray spells = json.getJSONArray("spells");

                            //储存法术ID
                            Map<String, Integer> spellsId = new HashMap<String, Integer>();
                            spellsId.put("Lightning Spell", R.id.tv_LightningSpell);//雷电法术
                            spellsId.put("Healing Spell", R.id.tv_HealingSpell);//治疗法术
                            spellsId.put("Rage Spell", R.id.tv_RageSpell);//狂暴法术
                            spellsId.put("Jump Spell", R.id.tv_JumpSpell);//弹跳法术
                            spellsId.put("Freeze Spell", R.id.tv_FreezeSpell);//冰冻法术
                            spellsId.put("Poison Spell", R.id.tv_PoisonSpell);//毒药法术
                            spellsId.put("Earthquake Spell", R.id.tv_EarthquakeSpell);//地震法术
                            spellsId.put("Haste Spell", R.id.tv_HasteSpell);//急速法术
                            spellsId.put("Clone Spell", R.id.tv_CloneSpell);//镜像法术
                            spellsId.put("Skeleton Spell", R.id.tv_SkeletonSpell);//骷髅法术
                            spellsId.put("Invisibility Spell", R.id.tv_InvisibilitySpell);//隐身法术
                            spellsId.put("Bat Spell", R.id.tv_BatSpell);//蝙蝠法术

                            //清除法术信息
                            for (String key : spellsId.keySet()) {
                                Log.d(TAG, "onResponse: ---法术信息清除:" + key);
                                views.setTextViewText(spellsId.get(key), "N");
                                views.setTextColor(spellsId.get(key), context.getResources().getColor(R.color.players_black));
                            }

                            //设置法术信息
                            //for (int i = 0; i < spells.length(); i++) {

                            for (int i = 0; i < spells.size(); i++) {

                                Object str = spells.get(i);
                                //JSONObject jsonSpells = new JSONObject(str.toString());

                                JSONObject jsonSpells = JSONObject.parseObject(str.toString());

                                name = jsonSpells.getString("name");//法术名称
                                String village = jsonSpells.getString("village");//法术所在家乡
                                level = jsonSpells.getString("level");//法术当前等级
                                String maxLevel = jsonSpells.getString("maxLevel");//法术最高等级

                                views.setTextViewText(spellsId.get(name), level);

                                //法术等级颜色设置
                                if (maxLevel.equals(level)) {//达到最高等级,颜色为橙色
                                    views.setTextColor(spellsId.get(name), context.getResources().getColor(R.color.players_max));
                                } else {//默认颜色黑色
                                    views.setTextColor(spellsId.get(name), context.getResources().getColor(R.color.players_black));
                                }

                                Log.d(TAG, "onResponse: ---法术信息:" + str);
                            }

                            Log.d(TAG, "onResponse: ---个人信息设置完成" + ivUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: JOSNObject解析错误:" + e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        views.setViewVisibility(R.id.pb_loading, View.GONE);//刷新转动隐藏
                        views.setViewVisibility(R.id.iv_loading, View.VISIBLE);//刷新图片可视
                        //设置村庄容量：村庄：1/6
                        views.setTextViewText(R.id.tv_capacity, "村庄：" + SpUtils.getString(context, "playersIndex") + "    /    6");

                        //个人信息完成花费时间
                        endTime = System.currentTimeMillis();//结束时间
                        Double time = Double.parseDouble(String.format("%.2f", Double.valueOf(endTime - startTime) / 1000));//保留2位小数
                        views.setTextViewText(R.id.tv_ms, time + "s");
                        if (time <= 5) {//运行速度良好
                            views.setTextColor(R.id.tv_ms, context.getResources().getColor(R.color.players_upperlimit));
                        } else if (time > 5 & time <= 10) {//运行速度普通
                            views.setTextColor(R.id.tv_ms, context.getResources().getColor(R.color.players_max));
                        } else {//运行速度差
                            views.setTextColor(R.id.tv_ms, context.getResources().getColor(R.color.players_error));
                        }
                        Log.d(TAG, "onResponse: ---个人信息完成花费时间:" + (endTime - startTime) + "ms");

                        for (int appWidgetId : appWidgetIds) {
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        }
                    }
                });

            }
        }.start();

    }

    //coc鱼情信息
    private void finsh(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,
                       RemoteViews views) {
        new Thread() {
            public void run() {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                        .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
                        .writeTimeout(60, TimeUnit.SECONDS)//设置写的超时时间
                        .build();

                Request request = new Request.Builder()
                        .get()
                        .url("http://clashofclansforecaster.com/STATS.json")
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e(TAG, "onFailure: ---鱼情okhttp错误: " + e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String res = response.body().string();
                        Log.d(TAG, "onResponse: ---鱼情返回内容:" + res);

                        if ("200".equals(response.code())) {//状态码不等于200
                            views.setTextViewText(R.id.tv_fish, "code:" + response.code());
                            views.setTextColor(R.id.tv_fish, context.getResources().getColor(R.color.players_error));

                            for (int appWidgetId : appWidgetIds) {
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }

                            Log.d(TAG, "onResponse: ---鱼情code错误:" + response.code());
                        }

                        try {
                            //JSONObject json = new JSONObject(res);

                            JSONObject json = JSONObject.parseObject(res);

                            //int fish = json.getInt("lootIndexString");

                            String fish = json.getString("lootIndexString");

                            String color = json.getString("fgColor");
                            Log.d(TAG, "onResponse: ---鱼情:" + fish);
                            views.setTextViewText(R.id.tv_fish, fish + "/10");
                            views.setTextColor(R.id.tv_fish, Color.parseColor("#" + color));

                            //设置鱼情箭头图片
                            if (Double.parseDouble(fish) > 5) {//向上箭头
                                @SuppressLint("ResourceType")//R.drawable.png资源图片转Bitmap图片
                                Bitmap mBitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.jt1));
                                views.setImageViewBitmap(R.id.iv_fish, Utils.tintBitmap(mBitmap, Color.parseColor("#" + color)));
                            } else {//向下箭头
                                @SuppressLint("ResourceType")//R.drawable.png资源图片转Bitmap图片
                                Bitmap mBitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.jt2));
                                views.setImageViewBitmap(R.id.iv_fish, Utils.tintBitmap(mBitmap, Color.parseColor("#" + color)));
                            }

                            Log.d(TAG, "onResponse: ---鱼情信息设置完成");

                            for (int appWidgetId : appWidgetIds) {
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }.start();
    }

    //设置商品控件
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void goodsIv(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, RemoteViews views) {

        SimpleDateFormat df = new SimpleDateFormat("dd");//设置日期格式
        String time = df.format(new Date());// new Date()获取当前系统时间

        int id[] = {R.id.iv_goods1, R.id.iv_goods2, R.id.iv_goods3};

        String goodsJson = SpUtils.getString(context, "goodsJson", "null");
        JSONArray json = JSONArray.parseArray(goodsJson);

        Log.d(TAG, "goods: ---商品当前序号:" + goodsIndex);

        int s = 0;
        if (time.equals(SpUtils.getString(context, "goodsDate", "null"))) {

            for (int i = 0; i < 3; i++) {
                switch (String.valueOf(i)) {
                    case "0":
                        s = 0;
                        break;
                    case "1":
                        s = 2;
                        break;
                    case "2":
                        s = 4;
                        break;
                }
                Log.d(TAG, "goods: ---序号:" + s);

                //商品序号不能为”“
                if ("".equals(goodsIndex) | "null".equals(goodsIndex)) {
                    goodsIndex = "0";
                    Log.d(TAG, "goodsIv: ---商品序号为\"\",以替换成0");
                }

                String goods = String.valueOf(
                        json.getJSONArray(
                                Integer.valueOf(goodsIndex)
                        ).getString(s)
                );
                Log.d(TAG, "goods: ---商品:" + goods);
                switch (goods) {
                    case "":
                        views.setImageViewResource(id[i], R.drawable.kb3);
                        break;
                    case "建筑工人药水":
                        views.setImageViewResource(id[i], R.drawable.ys_jzgr);
                        break;
                    case "资源药水":
                        views.setImageViewResource(id[i], R.drawable.ys_zy);
                        break;
                    case "训练药水":
                        views.setImageViewResource(id[i], R.drawable.ys_xl);
                        break;
                    case "时光钟楼药水":
                        views.setImageViewResource(id[i], R.drawable.ys_sgzl);
                        break;
                    case "暗黑重油符石":
                        views.setImageViewResource(id[i], R.drawable.fs_ahzy);
                        break;
                    case "建筑大师圣水符石":
                        views.setImageViewResource(id[i], R.drawable.fs_jzdsss);
                        break;
                    case "圣水符石":
                        views.setImageViewResource(id[i], R.drawable.fs_ss);
                        break;
                    case "建筑大师金币符石":
                        views.setImageViewResource(id[i], R.drawable.fs_jzdsjb);
                        break;
                    case "金币符石":
                        views.setImageViewResource(id[i], R.drawable.fs_jb);
                        break;
                    case "万能之书":
                        views.setImageViewResource(id[i], R.drawable.s_wn);
                        break;
                    case "建筑之书":
                        views.setImageViewResource(id[i], R.drawable.s_jz);
                        break;
                    case "英雄之书":
                        views.setImageViewResource(id[i], R.drawable.s_yx);
                        break;
                    case "法术之书":
                        views.setImageViewResource(id[i], R.drawable.s_fs);
                        break;
                    case "战斗之书":
                        views.setImageViewResource(id[i], R.drawable.s_zd);
                        break;
                    case "英雄药水":
                        views.setImageViewResource(id[i], R.drawable.ys_yx);
                        break;
                    case "研究药水":
                        views.setImageViewResource(id[i], R.drawable.ys_yj);
                        break;
                    case "力量药水":
                        views.setImageViewResource(id[i], R.drawable.ys_ll);
                        break;
                    case "超级药水":
                        views.setImageViewResource(id[i], R.drawable.ys_cj);
                        break;
                    case "铲子":
                        views.setImageViewResource(id[i], R.drawable.cz);
                        break;
                    case "戒指":
                        views.setImageViewResource(id[i], R.drawable.jz);
                        break;
                    default:
                        Log.e(TAG, "run: ---商品控件设置出错序号:" + i);
                }

                //商品免费显示
                goods = String.valueOf(
                        json.getJSONArray(
                                Integer.valueOf(goodsIndex)
                        ).getString(i)
                );
                switch (goods) {
                    case "免费":
                        views.setViewVisibility(R.id.tv_goodFree, View.VISIBLE);//商品免费橙色底线显示
                        break;
                }
            }

        } else {//另一天

            for (int i = 0; i < goodIndexs.length; i++) {

                //多余商品序号跳过
                switch (SpUtils.getString(context, goodIndexs[i], "null")) {
                    case "null":
                        continue;
                }

                //获取当前商品序号
                String Date = SpUtils.getString(context, goodIndexs[i], "null");

                switch (Date) {//商品序号一周期重置
                    case "39":
                        SpUtils.setString(context, goodIndexs[i], "1");
                        Toast.makeText(context, "商品一周期已轮完！", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "goodsIv: ---商品一周期已轮完！");
                        break;
                    default://商品序号增一，为第二天商品序号
                        if (!Date.equals("")) {
                            SpUtils.setString(context, goodIndexs[i],
                                    String.valueOf(Integer.valueOf(Date) + 1)
                            );
                        }
                }

            }

            //到了第二天保存第二天的日期
            SpUtils.setString(context, "goodsDate", time);
            Log.d(TAG, "goodsIv: ---第二天商品日期已保存");

            //因为第二天的商品序号以进行改动并保存，所以需要重新获取最新的商品序号
            goodsIndex = SpUtils.getString(context, goodIndexs[Integer.parseInt(SpUtils.getString(context, "playersIndex", "1")) - 1]);

            for (int i = 0; i < 3; i++) {

                switch (String.valueOf(i)) {
                    case "0":
                        s = 0;
                        break;
                    case "1":
                        s = 2;
                        break;
                    case "2":
                        s = 4;
                        break;
                }
                Log.d(TAG, "goods: ---序号:" + s);


                //商品序号不能为”“
                if ("".equals(goodsIndex)) {
                    goodsIndex = "0";
                    Log.d(TAG, "goodsIv: ---商品序号为\"\",以替换成0");
                }

                String goods = String.valueOf(
                        json.getJSONArray(
                                Integer.valueOf(goodsIndex)
                        ).getString(s)
                );
                Log.d(TAG, "goods: ---商品:" + goods);
                switch (goods) {
                    case "":
                        views.setImageViewResource(id[i], R.drawable.kb3);
                        break;
                    case "建筑工人药水":
                        views.setImageViewResource(id[i], R.drawable.ys_jzgr);
                        break;
                    case "资源药水":
                        views.setImageViewResource(id[i], R.drawable.ys_zy);
                        break;
                    case "训练药水":
                        views.setImageViewResource(id[i], R.drawable.ys_xl);
                        break;
                    case "时光钟楼药水":
                        views.setImageViewResource(id[i], R.drawable.ys_sgzl);
                        break;
                    case "暗黑重油符石":
                        views.setImageViewResource(id[i], R.drawable.fs_ahzy);
                        break;
                    case "建筑大师圣水符石":
                        views.setImageViewResource(id[i], R.drawable.fs_jzdsss);
                        break;
                    case "圣水符石":
                        views.setImageViewResource(id[i], R.drawable.fs_ss);
                        break;
                    case "建筑大师金币符石":
                        views.setImageViewResource(id[i], R.drawable.fs_jzdsjb);
                        break;
                    case "金币符石":
                        views.setImageViewResource(id[i], R.drawable.fs_jb);
                        break;
                    case "万能之书":
                        views.setImageViewResource(id[i], R.drawable.s_wn);
                        break;
                    case "建筑之书":
                        views.setImageViewResource(id[i], R.drawable.s_jz);
                        break;
                    case "英雄之书":
                        views.setImageViewResource(id[i], R.drawable.s_yx);
                        break;
                    case "法术之书":
                        views.setImageViewResource(id[i], R.drawable.s_fs);
                        break;
                    case "战斗之书":
                        views.setImageViewResource(id[i], R.drawable.s_zd);
                        break;
                    case "英雄药水":
                        views.setImageViewResource(id[i], R.drawable.ys_yx);
                        break;
                    case "研究药水":
                        views.setImageViewResource(id[i], R.drawable.ys_yj);
                        break;
                    case "力量药水":
                        views.setImageViewResource(id[i], R.drawable.ys_ll);
                        break;
                    case "超级药水":
                        views.setImageViewResource(id[i], R.drawable.ys_cj);
                        break;
                    case "铲子":
                        views.setImageViewResource(id[i], R.drawable.cz);
                        break;
                    case "戒指":
                        views.setImageViewResource(id[i], R.drawable.jz);
                        break;
                    default:
                        Log.e(TAG, "run: ---商品控件设置出错序号:" + i);
                }

                //商品免费显示
                goods = String.valueOf(
                        json.getJSONArray(
                                Integer.valueOf(goodsIndex)
                        ).getString(i)
                );
                switch (goods) {
                    case "免费":
                        views.setViewVisibility(R.id.tv_goodFree, View.VISIBLE);//商品免费橙色底线显示
                        break;
                }
            }
        }

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    //coc商品信息
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void goods(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,
                       RemoteViews views) {

        //商品免费橙色底线隐藏
        views.setViewVisibility(R.id.tv_goodFree, View.GONE);

        if (!"null".equals(SpUtils.getString(context, "goodsJson", "null"))) {//判断商品信息不为空
            Log.d(TAG, "goods: ---商品信息已保存,取出保存内容:" + SpUtils.getString(context, "goodsJson", "null"));
            //设置商品控件
            goodsIv(context, appWidgetManager, appWidgetIds, views);
            Log.d(TAG, "goodsIv: ---商品信息设置完成");
            return;
        }

        new Thread() {
            public void run() {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                        .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
                        .writeTimeout(60, TimeUnit.SECONDS)//设置写的超时时间
                        .build();

                //MD5加密
                String md5 = null;
                try {
                    md5 = Md5Utils.md5(context.getResources().getString(R.string.app_name)
                            + context.getResources().getString(R.string.gzh));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "run: ---商品信息token:" + md5);

                Request request = new Request.Builder()
                        .addHeader("mode", "getgoods")//header头信息必带
                        .addHeader("token", md5)
                        .get()
                        .url("https://coctools.top/goods/")
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e(TAG, "onFailure: ---商品okhttp错误: " + e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String res = response.body().string();
                        Log.d(TAG, "onResponse: ---商品返回内容:" + res);

                        if ("200".equals(response.code())) {//状态码不等于200
                            Log.d(TAG, "onResponse: ---商品code错误:" + response.code());
                        }

                        //保存商品信息到本地
                        SpUtils.setString(context, "goodsJson", res);

                        Log.d(TAG, "onResponse: ---商品信息已保存");

                        //设置商品控件
                        goodsIv(context, appWidgetManager, appWidgetIds, views);
                        Log.d(TAG, "goodsIv: ---商品信息设置完成");
                    }
                });
            }
        }.start();

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
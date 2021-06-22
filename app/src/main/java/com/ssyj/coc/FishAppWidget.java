package com.ssyj.coc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of App Widget functionality.
 */
public class FishAppWidget extends AppWidgetProvider {

    private static final String TAG = "FishAppWidget";

    private static String LOADING_FISH_ACTTON = "com.ssyj.coc.loading.fish.onclick";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        //橙色
        String cColor = "#FF6D00";
        //红色
        String hColor = "#FF5252";
        //黑色
        String hsColos = "#212121";

        RemoteViews views2 = new RemoteViews(context.getPackageName(), R.layout.fish_app_widget);

        new Thread() {
            @Override
            public void run() {
                super.run();

                OkHttpClient client = new OkHttpClient.Builder()
                        //.connectTimeout(10, TimeUnit.SECONDS)
                        //.readTimeout(15, TimeUnit.SECONDS)
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
                        Log.e(TAG, "onFailure: --------------鱼情: " + e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        String res = response.body().string();
                        Log.d(TAG, "onResponse: -------------鱼情: " + res);

                        if (response.code() != 200) {

                            views2.setTextViewText(R.id.tv_nameTag, "400");
                            views2.setTextColor(R.id.textView_fish2, Color.parseColor(hColor));

                            appWidgetManager.updateAppWidget(appWidgetId, views2);

                            Log.d(TAG, "onResponse: -------------------鱼情-状态码错误->" + response.code());

                        } else {//状态码正确, code:200
                            try {

                                JSONObject json = new JSONObject(res);
                                String fish = json.getString("lootIndexString");
                                String color = json.getString("fgColor");
                                views2.setTextViewText(R.id.textView_fish2, fish + "/10");
                                views2.setTextColor(R.id.textView_fish2, Color.parseColor("#" + color));

                                appWidgetManager.updateAppWidget(appWidgetId, views2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
        }.start();

//        //发送点击事件广播
//        Intent intent = new Intent(LOADING_FISH_ACTTON);
//        //添加应用包名
//        intent.setPackage(context.getPackageName());
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //views.setOnClickPendingIntent(R.id.iv_loading, pendingIntent);

        Intent intent = new Intent();
        //注意这个intent构造的是显式intent，直接将这个广播发送给MyAppWidgetProvider，使用Action的方式接收不到
        intent.setClass(context, FishAppWidget.class);

        intent.setData(Uri.parse("hx:" + R.id.linearLayout_loading_fish));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views2.setOnClickPendingIntent(R.id.linearLayout_loading_fish, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views2);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), FishAppWidget.class.getName()));

        Uri data = intent.getData();
        int resID = -1;
        if (data != null) {
            resID = Integer.parseInt(data.getSchemeSpecificPart());
        }

        switch (resID) {
            case R.id.linearLayout_loading_fish:
                Log.d(TAG, "onReceive: --------------------加载-点击成功--------------------");
                for (int i = 0; i < ids.length; i++) {
                    updateAppWidget(context, appWidgetManager, ids[i]);
                }
                Toast.makeText(context, "刷新ing", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.d(TAG, "onReceive: ---------------------未知：" + resID + "\\" + data);
        }

//        switch (action) {
//            case "android.appwidget.action.APPWIDGET_UPDATE":
//                Log.i(TAG, "onReceive: " + action);
//                break;
//            case "com.ssyj.coc.loading.fish.onclick":
//
//                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//                int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), FishAppWidget.class.getName()));
//
//                for (int i = 0; i < ids.length; i++) {
//                    updateAppWidget(context, appWidgetManager, ids[i]);
//                }
//                Toast.makeText(context, "刷新ing", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                Log.i(TAG, "onReceive: 未知action: " + action);
//        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
package com.ssyj.coc;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CocService extends Service {

    private static final String CHANNEL_ID_STRING = "com.ssyj.coc";
    private Timer timer = null;
    private static final String TAG = "CocService";
    public boolean runing = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("trans", "onStartCommand");
        new Thread() {
            public void run() {
                int i = 0;
                while (runing) {
                    i++;
                    Log.d(TAG, "run: -------------------Service-" + i + "--------------");

//                    OkHttpClient client = new OkHttpClient.Builder()
//                            //.connectTimeout(10, TimeUnit.SECONDS)
//                            //.readTimeout(15, TimeUnit.SECONDS)
//                            .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
//                            .readTimeout(100, TimeUnit.SECONDS)//设置读取超时时间
//                            .writeTimeout(60, TimeUnit.SECONDS)//设置写的超时时间
//                            .build();
//
//                    Request request = new Request.Builder()
//                            .get()
//                            .url("http://clashofclansforecaster.com/STATS.json")
//                            .build();
//
//                    Call call = client.newCall(request);
//
//                    call.enqueue(new Callback() {
//                        @Override
//                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                            Log.e(TAG, "onFailure: 鱼情: " + e);
//                        }
//
//                        @Override
//                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//
//                            String res = response.body().string();
//                            Log.d(TAG, "onResponse: ---------------鱼情: " + res);
//
//                            RemoteViews views = new RemoteViews(getPackageName(), R.layout.coc_app_widget);
//
//                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(CocService.this);
//                            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getPackageName(), CocAppWidget.class.getName()));
//
//                            if (response.code() != 200) {
//
//                                views.setTextViewText(R.id.tv_nameTag, "400");
//                                //views.setTextColor(R.id.textView_fish, Color.parseColor(hColor));
//
//                                for (int i = 0; i < ids.length; i++) {
//                                    appWidgetManager.updateAppWidget(ids[i], views);
//                                }
//
//                                Log.d(TAG, "onResponse: -----------鱼情-状态码错误->" + response.code());
//
//                            } else {//状态码正确, code:200
//                                try {
//                                    //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.coc_app_widget);
//                                    JSONObject json = new JSONObject(res);
//                                    String fish = json.getString("lootIndexString");
//                                    String color = json.getString("fgColor");
//                                    views.setTextViewText(R.id.textView_fish, fish + "/10hhhh");
//                                    views.setTextColor(R.id.textView_fish, Color.parseColor("#" + color));
//
//                                    for (int i = 0; i < ids.length; i++) {
//                                        appWidgetManager.updateAppWidget(ids[i], views);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//
//                        }
//                    });

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }

            }

            ;
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, "COC", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            startForeground(1, notification);
        }
        //stopForeground(true);//移除通知栏里的通知（不会关闭Service服务）

        runing = true;
    }

    @Override
    public void onDestroy() {
        runing = false;
        super.onDestroy();
//        if () {
//            //开启服务兼容
//            Intent intent = new Intent(this, CocService.class);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                this.startForegroundService(intent);
//            } else {
//                this.startService(intent);
//            }
//        }
        Log.d(TAG, "onDestroy: --------------------停止CocService-------------------");
    }

}

package com.ssyj.coc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class ClanAppWidget extends AppWidgetProvider {

    private static final String TAG = "ClanAppWidget";

    private static final String LIST_ONCLICK = "listOnclick";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // 获取Widget的组件名
        ComponentName thisWidget = new ComponentName(context, ClansWidgetService.class);
        // 创建一个RemoteView
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clan_app_widget);
        // 把这个Widget绑定到RemoteViewsService
        Intent intent = new Intent(context, ClansWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        ArrayList list = new ArrayList();
        for (int i = 1; i <= 50; i++) {
            list.add("部落:" + i);
        }
        intent.putStringArrayListExtra("ArrayList", list);

        //设置适配器
        views.setRemoteAdapter(R.id.lv_clans, intent);
        //设置当显示的widget_list为空显示的View remoteViews.setEmptyView();

        //列表点击事件
        Intent listIntent = new Intent();
        listIntent.setComponent(new ComponentName(context, ClanAppWidget.class));
        listIntent.setAction(LIST_ONCLICK);
        listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, listIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 设置intent模板
        views.setPendingIntentTemplate(R.id.lv_clans, pendingIntent);

        // 桌面部件刷新
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (LIST_ONCLICK.equals(action)) {

            // 接受“ListView”的点击事件的广播
            int type = intent.getIntExtra("Type", 0);

            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            Log.d(TAG, "onReceive: ---子列表点击事件:" + type);

//            switch (type) {
//                case 0:
//                    Toast.makeText(context, "item" + index, Toast.LENGTH_SHORT).show();
//                    break;
//                case 1:
//                    Toast.makeText(context, "lock" + index, Toast.LENGTH_SHORT).show();
//                    break;
//                case 2:
//                    Toast.makeText(context, "unlock" + index, Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    Log.d(TAG, "onReceive: ---intent其他未知标识:" + action);
//            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
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
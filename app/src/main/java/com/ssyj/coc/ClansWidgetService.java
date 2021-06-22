package com.ssyj.coc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ClansWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ClansWidgetFactory(this.getApplicationContext(), intent);
    }

    public static class ClansWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

        private static final String TAG = "ClansWidgetFactory";

        private final Context mContext;
        public static ArrayList<String> mList = new ArrayList<>();

        public ClansWidgetFactory(Context context, Intent intent) {
            mContext = context;
            mList = intent.getStringArrayListExtra("ArrayList");

            Log.d(TAG, "ClansWidgetFactory: 列表信息：" + intent.getStringArrayListExtra("ArrayList"));

        }

        @Override
        public void onCreate() {
            if (mList.size() == 0) {
                for (int i = 1; i <= 50; i++) {
                    mList.add("item" + i);
                }
            }
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            mList.clear();
        }

        @Override
        public int getCount() {
            if (String.valueOf(mList.size()).equals("null")) {
                return 0;
            }
            return mList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            if (position < 0 || position >= mList.size()) {
                return null;
            }

            String content = mList.get(position);
            // 创建在当前索引位置要显示的View
            final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item_clans);

            // 设置要显示的内容
            rv.setTextViewText(R.id.tv_clans_nameTag, content);
            Log.d(TAG, "getViewAt: ---列表数据：" + content);

            //子列表点击事件
            Intent intent = new Intent();// 填充Intent，填充在AppWdigetProvider中创建的PendingIntent
            // 传入点击行的数据
            intent.putExtra("Type", position);
            intent.setComponent(new ComponentName("com.ssyj.coc", "ClanAppWidget.class"));
            rv.setOnClickFillInIntent(R.id.tv_clans_nameTag, intent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

}

package com.ssyj.coc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

public class CocHelp extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CocHelp";

    private LinearLayout linearCardSetTagZtl, linearCardSetTagBtl;

    private CardView cardReturn, cardDiy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coc_help);

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
                    Log.d(TAG, "onCreate: ----------------标题栏高度---------------" + actionBarHeight);

                    cardDiy = findViewById(R.id.cardDiy);
                    linearCardSetTagZtl = findViewById(R.id.linearCardSetTagZtl);
                    linearCardSetTagBtl = findViewById(R.id.linearCardSetTagBtl);

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

        cardReturn = findViewById(R.id.cardReturn);
        cardReturn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cardReturn://返回页面
                finish();
                break;
        }
    }
}
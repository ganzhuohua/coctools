package com.ssyj.coc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CocAbout extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CocAbout";

    boolean linearLogbln = false, linearCodeDatabln = false;

    private LinearLayout linearCardSetTagZtl, linearCardSetTagBtl, linearDeveloper, linearUpdateLog,
            linearQ, linearLog, linearCodeData, linearDataCode, linearCocData, linearokHttp,
            linearFish, linearImages;
    private CardView cardReturn, cardDiy;
    private TextView appCode;
    private ImageView imageUpdateData, imageCodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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

        //获取版本号
        int versioncode = 0;
        try {
            PackageManager pm = this.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            String versionName = pi.versionName;
            //versioncode = pi.versionCode;
            Log.d(TAG, "onCreate: ----------版本号-----------" + versionName);
            appCode = findViewById(R.id.textAppCode);
            appCode.setText("版本号：" + versionName);

        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }

        cardReturn = findViewById(R.id.cardReturn);
        cardReturn.setOnClickListener(this);

        linearDeveloper = findViewById(R.id.linearDeveloper);
        linearDeveloper.setOnClickListener(this);

        linearUpdateLog = findViewById(R.id.linearUpdateLog);
        linearUpdateLog.setOnClickListener(this);
        linearQ = findViewById(R.id.linearQ);
        linearQ.setOnClickListener(this);
        linearLog = findViewById(R.id.linearLog);
        linearLog.setOnClickListener(this);

        linearCodeData = findViewById(R.id.linearCodeData);
        linearCodeData.setOnClickListener(this);
        linearDataCode = findViewById(R.id.linearDataCode);
        linearDataCode.setOnClickListener(this);

        linearCocData = findViewById(R.id.linearCocData);
        linearCocData.setOnClickListener(this);

        linearFish = findViewById(R.id.linearFish);
        linearFish.setOnClickListener(this);

        linearokHttp = findViewById(R.id.linearOkHttp);
        linearokHttp.setOnClickListener(this);

        imageCodeData = findViewById(R.id.imageCodeData);
        imageCodeData.setOnClickListener(this);

        imageUpdateData = findViewById(R.id.imageUpdateData);
        imageUpdateData.setOnClickListener(this);

        linearImages = findViewById(R.id.linearImages);
        linearImages.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Uri uri;
        Intent intent;
        ObjectAnimator animator;
        switch (v.getId()) {
            case R.id.cardReturn://返回页面
                finish();
                break;
            case R.id.linearDeveloper://开发者
                if (checkApkExist(this, "com.tencent.mobileqq")) {
                    String QQ = "mqqwpa://im/chat?chat_type=wpa&uin=2285623734&version=1";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(QQ)));
                } else {
                    Toast.makeText(this, "本机未安装QQ应用", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.linearUpdateLog://更新日志
                if (linearLogbln == false) {
                    linearLogbln = true;
                    //旋转动画
                    animator = ObjectAnimator.ofFloat(imageUpdateData, "rotation", 0f, 90f);
                    animator.setDuration(150);
                    animator.start();
                    linearLog.setVisibility(View.VISIBLE);
                    //透明动画
                    animator = ObjectAnimator.ofFloat(linearLog, "alpha", 0f, 1f);
                    animator.setDuration(1000);
                    animator.start();
                } else {
                    linearLogbln = false;
                    //旋转动画
                    animator = ObjectAnimator.ofFloat(imageUpdateData, "rotation", 90f, 0f);
                    animator.setDuration(150);
                    animator.start();
                    linearLog.setVisibility(View.GONE);
                }
                break;
            case R.id.linearQ://交流Q群
                if (checkApkExist(this, "com.tencent.mobileqq")) {
                    String QQ = "mqqwpa://im/chat?chat_type=group&uin=779238028&version=1";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(QQ)));
                } else {
                    Toast.makeText(this, "本机未安装QQ应用", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.linearCodeData://代码/数据来源
                if (linearCodeDatabln == false) {
                    linearCodeDatabln = true;
                    //旋转动画
                    animator = ObjectAnimator.ofFloat(imageCodeData, "rotation", 0f, 90f);
                    animator.setDuration(150);
                    animator.start();
                    linearDataCode.setVisibility(View.VISIBLE);
                    //透明动画
                    animator = ObjectAnimator.ofFloat(linearDataCode, "alpha", 0f, 1f);
                    animator.setDuration(1000);
                    animator.start();
                } else {
                    linearCodeDatabln = false;
                    //旋转动画
                    animator = ObjectAnimator.ofFloat(imageCodeData, "rotation", 90f, 0f);
                    animator.setDuration(150);
                    animator.start();
                    linearDataCode.setVisibility(View.GONE);
                }
                break;
            case R.id.linearCocData://Coc数据来源
                uri = Uri.parse("https://www.clashofstats.com/");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.linearFish://Coc鱼情来源
                uri = Uri.parse("http://clashofclansforecaster.com/");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.linearImages://COC图片来源
                uri = Uri.parse("https://coc.guide/");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.linearOkHttp://OkHttp来源
                uri = Uri.parse("https://github.com/square/okhttp");    //设置跳转的网站
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }

    //查询本机是否安装相应APP
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
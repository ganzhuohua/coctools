package com.ssyj.coc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private boolean tagbln = false;

    private EditText editLog;
    private CardView cardTag, cardSetTag, cardCs, cardAnalysis;
    private LinearLayout linearZtl, linearStartY, linearStopY,linearAbout, linearCocHelp,cocAppWidgetColor;
    private ObjectAnimator animator;
    private TextView textTag1, textTag2, textTag3;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);

        //状态栏字体图标灰色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 沉浸式
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //获取状态栏高度
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.d(TAG, "onCreate: ----------------状态栏高度--------------" + statusBarHeight);

        //获取标题栏高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;
        Log.d(TAG, "onCreate: -----------------屏幕高度---------------" + heightPixels);

        linearZtl = findViewById(R.id.linearMainZtl);

        //取当前的布局参数
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) linearZtl.getLayoutParams();
        linearParams.height = statusBarHeight;
        linearZtl.setLayoutParams(linearParams);

        cardTag = findViewById(R.id.cardTag);
        cardTag.setOnClickListener(this);

        cardSetTag = findViewById(R.id.cardSetTag);
        cardSetTag.setOnClickListener(this);

        cardCs = findViewById(R.id.cv_qqLogin);
        cardCs.setOnClickListener(this);

        linearStartY = findViewById(R.id.linearStartY);

        linearStopY = findViewById(R.id.linearStopY);
        linearStopY.setOnClickListener(this);//防止点击穿透

        textTag1 = findViewById(R.id.textTag1);
        textTag2 = findViewById(R.id.textTag2);
        textTag3 = findViewById(R.id.textTag3);

        linearAbout = findViewById(R.id.linearAbout);
        linearAbout.setOnClickListener(this);

        linearCocHelp = findViewById(R.id.linearCocHelp);
        linearCocHelp.setOnClickListener(this);

        cardAnalysis = findViewById(R.id.cardAnalysis);
        cardAnalysis.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        sp = getSharedPreferences("cocData", Context.MODE_PRIVATE);
        editor = sp.edit();

        String Tag1 = sp.getString("playersTag1", null);
        String Tag2 = sp.getString("playersTag2", null);
        String Tag3 = sp.getString("playersTag3", null);

        if (!(Tag1 == null)) {
            if (Tag1.equals("")) {
                textTag1.setText("");
            } else {
                textTag1.setText("#" + Tag1);
            }
        }
        if (!(Tag2 == null)) {
            if (Tag2.equals("")) {
                textTag2.setText("");
            } else {
                textTag2.setText("#" + Tag2);
            }
        }
        if (!(Tag3 == null)) {
            if (Tag3.equals("")) {
                textTag3.setText("");
            } else {
                textTag3.setText("#" + Tag3);
            }
        }

    }

    //点击事件
    @Override
    public void onClick(View v) {

        //获取SharedPreferences对象
        //Context context = "com.ssyj.coc";
        //cocData:储存时生成的xml名称, Context.MODE_PRIVATE:默认打开方式(共4种)
        sp = getSharedPreferences("cocData", Context.MODE_PRIVATE);
        editor = sp.edit();

        switch (v.getId()) {
            case R.id.cardTag:
                if (tagbln == false) {

                    tagbln = true;

                    cardTag.setCardBackgroundColor(ContextCompat.getColor(this, R.color.ls));

                    //透明动画
                    animator = ObjectAnimator.ofFloat(linearStartY, "alpha", 0, 1);
                    animator.setDuration(550);
                    animator.start();

                    //位移动画
                    animator = ObjectAnimator.ofFloat(linearStopY, "translationY", 0, linearStopY.getTop() + linearStartY.getHeight());
                    animator.setDuration(600);
                    animator.start();

                } else {

                    tagbln = false;

                    cardTag.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hs));

                    //透明动画
                    animator = ObjectAnimator.ofFloat(linearStartY, "alpha", 1, 0);
                    animator.setDuration(550);
                    animator.start();

                    //位移动画
                    animator = ObjectAnimator.ofFloat(linearStopY, "translationY", linearStopY.getTop() + linearStartY.getHeight(), 0);
                    animator.setDuration(600);
                    animator.start();

                }
                break;
            case R.id.cardSetTag:
                startActivity(new Intent(this, CardSetTag.class));
                Toast.makeText(MainActivity.this, "设置Tag", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cv_qqLogin:

                startActivity(new Intent(this, Qq.class));

                Intent intent = new Intent(this, CocService.class);
                this.stopService(intent);//停止服务Coc
                Toast.makeText(this, "已停止服务", Toast.LENGTH_SHORT).show();
                break;
            case R.id.linearCocHelp:
                startActivity(new Intent(this, CocHelp.class));
                Toast.makeText(MainActivity.this, "帮助", Toast.LENGTH_SHORT).show();
                break;
            case R.id.linearAbout:
                startActivity(new Intent(this, CocAbout.class));
                Toast.makeText(MainActivity.this, "关于", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cardAnalysis:
                startActivity(new Intent(this, CocAnalysis.class));
                Toast.makeText(MainActivity.this, "分析", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.button_geRenTagPreserve:
                try {
                    Process process = Runtime.getRuntime().exec("logcat -d");
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    StringBuilder log = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        // log.append(line + \n);
                        if (line.contains("CocService") | line.contains("CocAppWidget") | line.contains("FishAppWidget")) {
                            log.append(line + "\n\n");
                        }
                    }
                    editLog = (EditText) findViewById(R.id.editLog);
                    editLog.setText(log.toString());
                } catch (IOException e) {
                }
                break;
        }
        return false;
    }

}
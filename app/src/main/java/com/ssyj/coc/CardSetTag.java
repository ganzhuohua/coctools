package com.ssyj.coc;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class CardSetTag extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CardSetTag";
    private LinearLayout linearCardSetTagZtl, linearCardSetTagBtl;
    private CardView cardDiy, cardReturn, cardPreserve;
    private EditText editTag1, editTag2, editTag3, editData, editIndex1, editIndex2, editIndex3,
            et_tag4, et_tag5, et_tag6,
            et_index4, et_index5, et_index6;

    private ImageView imageIndex, imageGoods1, imageGoods2, imageGoods3;
    private TextView text_help1, text_help2;
    private Switch cocWidgetBgTmColor;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_set_tag);

        //沉浸状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
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

        cardPreserve = findViewById(R.id.cardPreserve);
        cardPreserve.setOnClickListener(this);

        editTag1 = findViewById(R.id.editTag1);
        editTag2 = findViewById(R.id.editTag2);
        editTag3 = findViewById(R.id.editTag3);
        et_tag4 = findViewById(R.id.et_tag4);
        et_tag5 = findViewById(R.id.et_tag5);
        et_tag6 = findViewById(R.id.et_tag6);

        //editData = findViewById(R.id.editData);
        editIndex1 = findViewById(R.id.editIndex1);
        editIndex2 = findViewById(R.id.editIndex2);
        editIndex3 = findViewById(R.id.editIndex3);
        et_index4 = findViewById(R.id.et_index4);
        et_index5 = findViewById(R.id.et_index5);
        et_index6 = findViewById(R.id.et_index6);

        text_help1 = findViewById(R.id.text_help1);
        text_help1.setOnClickListener(this);
        text_help2 = findViewById(R.id.text_help2);
        text_help2.setOnClickListener(this);

        imageGoods1 = findViewById(R.id.iv_goods1);
        imageGoods1.setOnClickListener(this);
        imageGoods2 = findViewById(R.id.iv_goods2);
        imageGoods2.setOnClickListener(this);
        imageGoods3 = findViewById(R.id.iv_goods3);
        imageGoods3.setOnClickListener(this);

        cocWidgetBgTmColor = findViewById(R.id.switch_cocWidgetBgTmColor);
        cocWidgetBgTmColor.setOnClickListener(this);

        cocWidgetBgTmColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    editor.putBoolean("cocWidgetBgTmColor", true);
                    Toast.makeText(CardSetTag.this, "开启", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("cocWidgetBgTmColor", false);
                    Toast.makeText(CardSetTag.this, "关闭", Toast.LENGTH_SHORT).show();
                }

                editor.commit();
            }
        });

        //修改编辑框底线颜色
        editTag1.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        editTag2.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        editTag3.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        //editData.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        editIndex1.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        editIndex2.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        editIndex3.getBackground().mutate().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    protected void onResume() {
        super.onResume();

        sp = getSharedPreferences("cocData", Context.MODE_PRIVATE);
        editor = sp.edit();

        if (sp.getBoolean("cocWidgetBgTmColor", false)) {
            cocWidgetBgTmColor.setChecked(true);
        } else {
            cocWidgetBgTmColor.setChecked(false);
        }

        String Tag1 = sp.getString("playersTag1", null);
        String Tag2 = sp.getString("playersTag2", null);
        String Tag3 = sp.getString("playersTag3", null);

        String Tag4 = SpUtils.getString(CardSetTag.this, "playersTag4", null);
        String Tag5 = SpUtils.getString(CardSetTag.this, "playersTag5", null);
        String Tag6 = SpUtils.getString(CardSetTag.this, "playersTag6", null);

//        editData.setText(sp.getString("cocGoodsData", ""));
        editIndex1.setText(sp.getString("goodsIndex1", ""));
        editIndex2.setText(sp.getString("goodsIndex2", ""));
        editIndex3.setText(sp.getString("goodsIndex3", ""));

        et_index4.setText(SpUtils.getString(CardSetTag.this, "goodsIndex4", ""));
        et_index5.setText(SpUtils.getString(CardSetTag.this, "goodsIndex5", ""));
        et_index6.setText(SpUtils.getString(CardSetTag.this, "goodsIndex6", ""));

        Log.d(TAG, "onResume: ------商品序号：" + Tag1 + sp.getString("goodIndex1", ""));

        if (!(Tag1 == null) && !Tag1.equals("")) {
            editTag1.setText("#" + Tag1);
        }
        if (!(Tag2 == null) && !Tag2.equals("")) {
            editTag2.setText("#" + Tag2);
        }
        if (!(Tag3 == null) && !Tag3.equals("")) {
            editTag3.setText("#" + Tag3);
        }
        if (!(Tag4 == null) && !Tag4.equals("")) {
            et_tag4.setText("#" + Tag4);
        }
        if (!(Tag5 == null) && !Tag5.equals("")) {
            et_tag5.setText("#" + Tag5);
        }
        if (!(Tag6 == null) && !Tag6.equals("")) {
            et_tag6.setText("#" + Tag6);
        }


    }

    @Override
    public void onClick(View v) {

        sp = getSharedPreferences("cocData", Context.MODE_PRIVATE);
        editor = sp.edit();

        switch (v.getId()) {
            case R.id.cardReturn://返回页面
                finish();
                break;
            case R.id.cardPreserve://保存标签
                String Tag1 = editTag1.getText().toString();
                String Tag2 = editTag2.getText().toString();
                String Tag3 = editTag3.getText().toString();
                String Tag4 = et_tag4.getText().toString();
                String Tag5 = et_tag5.getText().toString();
                String Tag6 = et_tag6.getText().toString();


//                editor.putString("cocGoodsData", editData.getText().toString());
                editor.putString("goodsIndex1", editIndex1.getText().toString());
                editor.putString("goodsIndex2", editIndex2.getText().toString());
                editor.putString("goodsIndex3", editIndex3.getText().toString());
                SpUtils.setString(CardSetTag.this, "goodsIndex4", et_index4.getText().toString());
                SpUtils.setString(CardSetTag.this, "goodsIndex5", et_index5.getText().toString());
                SpUtils.setString(CardSetTag.this, "goodsIndex6", et_index6.getText().toString());


                if (Tag1.contains("#")) {
                    editor.putString("playersTag1", Tag1.substring(1));
                } else {
                    editor.putString("playersTag1", Tag1);
                }
                if (Tag2.contains("#")) {
                    editor.putString("playersTag2", Tag2.substring(1));
                } else {
                    editor.putString("playersag2", Tag2);
                }
                if (Tag3.contains("#")) {
                    editor.putString("playersTag3", Tag3.substring(1));
                } else {
                    editor.putString("playersTag3", Tag3);
                }
                if (Tag4.contains("#")) {
                    SpUtils.setString(CardSetTag.this, "playersTag4", Tag4.substring(1));
                } else {
                    Log.d(TAG, "onClick: ------" + Tag4);
                    editor.putString("playersTag4", Tag4);
                }
                if (Tag5.contains("#")) {
                    SpUtils.setString(CardSetTag.this, "playersTag5", Tag5.substring(1));
                } else {
                    SpUtils.setString(CardSetTag.this, "playersTag5", Tag5);
                }
                if (Tag6.contains("#")) {
                    SpUtils.setString(CardSetTag.this, "playersTag6", Tag6.substring(1));
                } else {
                    SpUtils.setString(CardSetTag.this, "playersTag6", Tag6);
                }
                if (editor.commit()) {
                    Toast.makeText(CardSetTag.this, "保存成功！", Toast.LENGTH_SHORT).show();
                }


//                Tag1 = editTag1.getText().toString();
//                Tag2 = editTag2.getText().toString();
//                Tag3 = editTag3.getText().toString();

                Tag1 = sp.getString("playersTag1", "");
                Tag2 = sp.getString("playersTag2", "");
                Tag3 = sp.getString("playersTag3", "");
                Tag4 = sp.getString("playersTag4", "");
                Tag5 = sp.getString("playersTag5", "");
                Tag6 = sp.getString("playersTag6", "");

//                editData.setText(sp.getString("cocGoodsData", ""));
                editIndex1.setText(sp.getString("goodsIndex1", ""));
                editIndex2.setText(sp.getString("goodsIndex2", ""));
                editIndex3.setText(sp.getString("goodsIndex3", ""));
                et_index4.setText(sp.getString("goodsIndex4", ""));
                et_index5.setText(sp.getString("goodsIndex5", ""));
                et_index6.setText(sp.getString("goodsIndex6", ""));


                if (Tag1.equals("")) {
                    editor.putString("playersTag1", Tag2);
                    editTag1.setText("#" + Tag2);
                    editor.putString("playersTag2", Tag3);
                    editTag2.setText("#" + Tag3);
                    editor.putString("playersTag3", Tag4);
                    editTag3.setText("#" + Tag4);
                    editor.putString("playersTag4", Tag5);
                    et_tag4.setText("#" + Tag5);
                    editor.putString("playersTag5", Tag6);
                    et_tag5.setText("#" + Tag6);
                    editor.putString("playersTag6", "");
                    et_tag6.setText("");
                }
                if (Tag2.equals("")) {
                    editor.putString("playersTag2", Tag3);
                    editTag2.setText("#" + Tag3);
                    editor.putString("playersTag3", Tag4);
                    editTag3.setText("#" + Tag4);
                    editor.putString("playersTag4", Tag5);
                    et_tag4.setText("#" + Tag5);
                    editor.putString("playersTag5", Tag6);
                    et_tag5.setText("#" + Tag6);
                    editor.putString("playersTag6", "");
                    et_tag6.setText("");
                }
                if (Tag3.equals("")) {
                    editor.putString("playersTag3", Tag4);
                    editTag3.setText("#" + Tag4);
                    editor.putString("playersTag4", Tag5);
                    et_tag4.setText("#" + Tag5);
                    editor.putString("playersTag5", Tag6);
                    et_tag5.setText("#" + Tag6);
                    editor.putString("playersTag6", "");
                    et_tag6.setText("");
                }
                if (Tag4.equals("")) {
                    editor.putString("playersTag4", Tag5);
                    et_tag4.setText("#" + Tag5);
                    editor.putString("playersTag5", Tag6);
                    et_tag5.setText("#" + Tag6);
                    editor.putString("playersTag6", "");
                    et_tag6.setText("");
                }
                if (Tag5.equals("")) {
                    editor.putString("playersTag5", Tag6);
                    et_tag5.setText("#" + Tag6);
                    editor.putString("playersTag6", "");
                    et_tag6.setText("");
                }
                if (editor.commit()) {
                    Log.d(TAG, "onClick: 标签整理成功！");
                }

                break;
            case R.id.text_help1:
                /* @setIcon 设置对话框图标
                 * @setTitle 设置对话框标题
                 * @setMessage 设置对话框消息提示
                 * setXXX方法返回Dialog对象，因此可以链式设置属性
                 */
                final AlertDialog normalDialog = new AlertDialog.Builder(this)
                        //normalDialog.setIcon(R.drawable.coc_tm);
                        .setTitle(" 问：关于小米手机点击刷新自动打开APP问题解决办法")
                        .setMessage("答：目前解决方法就是关闭MIUI优化(小白不要乱试)，但是关闭之后可能会有很多功能出现问题，请慎重考虑！" +
                                "\n关闭MIUI优化在开发者设置中" +
                                "\n\n关闭MIUI之后所造成的任何问题与本软件无关！！！" +
                                "\n本软件仅提供解决方法！！！" +
                                "\n关闭方法:请百度自行解决！" +
                                "\n\n方法提供者：傻狍子")
                        .setPositiveButton("已了解",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //...To-do
                                    }
                                })
                        // 显示
                        .show();
                normalDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                break;
            case R.id.text_help2:
                startActivity(new Intent(this, CocHelp.class));
                break;
            case R.id.iv_goods1:
            case R.id.iv_goods2:
            case R.id.iv_goods3:

                final View dialogView = LayoutInflater.from(this).inflate(R.layout.set_tag_goods, null);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("商人序号")
                        .setView(dialogView)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                        .show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
//                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                break;
//            case R.id.switch_cocWidgetBgTmColor:
//
//                Boolean swBln = sp.getBoolean("cocWidgetBgTmColor", false);
//
//                if(swBln){
//
//                }
//
//                cocWidgetBgTmColor.setChecked();
//
//                break;
            default:
        }
    }
}
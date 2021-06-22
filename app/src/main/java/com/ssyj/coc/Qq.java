package com.ssyj.coc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.DefaultUiListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Qq extends AppCompatActivity {

    private static final String TAG = "Qq";
    private static final String APP_ID = "101951640";//官方获取的APPID
    private Tencent mTencent;
    private BaseUIListener mIUiListener;
    private ImageView qqlogin;
    private UserInfo mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq);

        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, Qq.this.getApplicationContext());
        initView();
        qqlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIUiListener = new BaseUIListener();

                if (!mTencent.isSessionValid()) {
                    mTencent.login(Qq.this, "all", mIUiListener);
                }

            }
        });


        initView();
    }

    //配置
    private void initView() {
        qqlogin = (ImageView) findViewById(R.id.iv_qqLogin);
    }

    private class MyIUiListener implements IUiListener {

        public MyIUiListener() {
            super();
        }

        @Override
        public void onComplete(Object response) {
            Log.d(TAG, "onComplete: ---QQ用户信息:" + response.toString());

            try {
                JSONObject json = new JSONObject(response.toString());

                TextView tv_name = findViewById(R.id.tv_name);
                tv_name.setText(json.getString("nickname"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(UiError uiError) {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onWarning(int i) {

        }
    }

    private class BaseUIListener implements IUiListener {

        //完成
        @Override
        public void onComplete(Object obj) {
            Log.d(TAG, "onComplete: ---QQ返回内容:" + obj);

            try {
                JSONObject json = (JSONObject) obj;
                String openID = json.getString("openid");
                String accessToken = json.getString("access_token");
                String expires = json.getString("expires_in");
                //下面两个方法非常重要，否则会出现client request's parameters are invalid, invalid openid
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);

                mInfo = new UserInfo(Qq.this, mTencent.getQQToken());
                mInfo.getUserInfo(new MyIUiListener());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //错误
        @Override
        public void onError(UiError e) {
            Log.d(TAG, "---QQ错误-onError:" + "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        //取消
        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel: ---QQ取消");
        }

        @Override
        public void onWarning(int i) {
            Log.d(TAG, "onWarning: ---" + i);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
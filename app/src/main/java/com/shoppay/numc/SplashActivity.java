package com.shoppay.numc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.shoppay.numc.tools.CleanMSG;
import com.shoppay.numc.tools.LogUtils;
import com.shoppay.numc.tools.ObtainSystemLanguage;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.ui.*;

import java.util.Locale;

public class SplashActivity extends Activity {

    private static final int GO_MAIN = 1000;
    private static final int GO_HOME = 2000;

    /**
     * Handler:跳转到不同界面
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_MAIN:
                    goMain();
                    break;
                case GO_HOME:
                    goHome();

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        ObtainSystemLanguage.obainLanguage(getApplicationContext());
    }


    private boolean IsLogin() {
        String phone = PreferenceHelper.readString(getApplicationContext(),
                "shoppay", "account", "123");
        Log.d("xx", phone);
        if (phone.equals("123")) {
            return false;
        } else {
            return false;
        }
    }


    private void init() {
        if (IsLogin()) {
            Message msg = mHandler.obtainMessage();
            msg.what = GO_HOME;
            mHandler.sendMessageDelayed(msg, 1000);
        } else {
            CleanMSG.cleanSharedPreference(getApplicationContext());
            Message msg = mHandler.obtainMessage();
            msg.what = GO_MAIN;
            mHandler.sendMessageDelayed(msg, 1000);
        }
    }

    private void goMain() {
        Intent intent = new Intent(SplashActivity.this, com.shoppay.numc.ui.LoginActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
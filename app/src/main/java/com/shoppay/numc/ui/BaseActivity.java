package com.shoppay.numc.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import com.shoppay.numc.http.PermissionListener;
import com.shoppay.numc.tools.DialogUtil;
import com.shoppay.numc.tools.LogUtils;
import com.shoppay.numc.tools.ObtainSystemLanguage;
import com.shoppay.numc.tools.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by songxiaotao on 2018/1/9.
 */

public abstract class BaseActivity extends Activity {
    private static PermissionListener mListener;
    public Dialog dialog;
    public Resources res;
    public static Activity ac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLocaleLanguage();
        dialog = DialogUtil.loadingDialog(BaseActivity.this, 1);
        res = getResources();
        init();
    }

    protected void init() {
    }
    private void initLocaleLanguage() {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        //获取屏幕参数
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        String language = ObtainSystemLanguage.obainLanguage(getApplicationContext());
        LogUtils.d("xxx", language);
        //设置本地语言
        switch (language) {
            case "zh":
                configuration.locale = Locale.CHINA;
                break;
            case "en":
                configuration.locale = Locale.ENGLISH;
                break;
            case "vi":
                configuration.locale = new Locale("vi");
                break;
        }
        resources.updateConfiguration(configuration, displayMetrics);
    }
    /*防止系统字体影响到app的字体*/
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 申请权限
     */
    public static void requestRuntimePermissions(
            String[] permissions, PermissionListener listener) {
        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        // 遍历每一个申请的权限，把没有通过的权限放在集合中
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(ac, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            } else {
                mListener.granted();
            }
        }
        // 申请权限
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(ac,
                    permissionList.toArray(new String[permissionList.size()]), 1);
        }
    }

    /**
     * 申请后的处理
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            List<String> deniedList = new ArrayList<>();
            // 遍历所有申请的权限，把被拒绝的权限放入集合
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    mListener.granted();
                } else {
                    deniedList.add(permissions[i]);
                }
            }
            if (!deniedList.isEmpty()) {
                mListener.denied(deniedList);
            }
        }
    }
}
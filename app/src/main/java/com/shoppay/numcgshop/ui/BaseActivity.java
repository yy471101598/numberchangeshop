package com.shoppay.numcgshop.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.modle.ImpObtainCurrency;
import com.shoppay.numcgshop.modle.ImpObtainPaytype;
import com.shoppay.numcgshop.nbean.Currency;
import com.shoppay.numcgshop.nbean.PayType;
import com.shoppay.numcgshop.tools.DialogUtil;
import com.shoppay.numcgshop.tools.ObtainSystemLanguage;
import com.shoppay.numcgshop.tools.PreferenceHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by songxiaotao on 2018/1/9.
 */

public abstract class BaseActivity extends AppCompatActivity {
    //    private static PermissionListener mListener;
    public Dialog dialog;
    public Resources res;
    public static Activity ac;
    public List<PayType> paylist = new ArrayList<>();
    public List<Currency> currlist = new ArrayList<>();
    public MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ac = this;
        initLocaleLanguage();
        dialog = DialogUtil.loadingDialog(BaseActivity.this, 1);
        res = getResources();

        app = (MyApplication) getApplication();
        if (app.getPayType().size() == 0) {
            ImpObtainPaytype paytype = new ImpObtainPaytype();
            paytype.obtainPayType(ac, new InterfaceBack() {
                @Override
                public void onResponse(Object response) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<PayType>>() {
                    }.getType();
                    List<PayType> sllist = gson.fromJson(response.toString(), listType);
                    paylist.addAll(sllist);
                }

                @Override
                public void onErrorResponse(Object msg) {

                }
            });

        } else {
            paylist.addAll(app.getPayType());
        }

        if (app.getCurrency().size() == 0) {
            ImpObtainCurrency currency = new ImpObtainCurrency();
            currency.obtainCurrency(ac, new InterfaceBack() {
                @Override
                public void onResponse(Object response) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Currency>>() {
                    }.getType();
                    List<Currency> sllist = gson.fromJson(response.toString(), listType);
                    currlist.addAll(sllist);
                }

                @Override
                public void onErrorResponse(Object msg) {

                }
            });
        } else {
            currlist.addAll(app.getCurrency());
        }
    }

    private void initLocaleLanguage() {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        //获取屏幕参数
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        String language = ObtainSystemLanguage.obainLanguage(getApplicationContext());
        //设置本地语言
        switch (language) {
            case "zh":
                configuration.locale = Locale.CHINA;
                PreferenceHelper.write(ac, "numc", "lagavage", "zh");
                break;
            case "en":
                configuration.locale = Locale.ENGLISH;
                PreferenceHelper.write(ac, "numc", "lagavage", "en");
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

//    /**
//     * 申请权限
//     */
//    public static void requestRuntimePermissions(
//            String[] permissions, PermissionListener listener) {
//        mListener = listener;
//        List<String> permissionList = new ArrayList<>();
//        // 遍历每一个申请的权限，把没有通过的权限放在集合中
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(ac, permission) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                permissionList.add(permission);
//            } else {
//                mListener.granted();
//            }
//        }
//        // 申请权限
//        if (!permissionList.isEmpty()) {
//            ActivityCompat.requestPermissions(ac,
//                    permissionList.toArray(new String[permissionList.size()]), 1);
//        }
//    }
//
//    /**
//     * 申请后的处理
//     */
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults.length > 0) {
//            List<String> deniedList = new ArrayList<>();
//            // 遍历所有申请的权限，把被拒绝的权限放入集合
//            for (int i = 0; i < grantResults.length; i++) {
//                int grantResult = grantResults[i];
//                if (grantResult == PackageManager.PERMISSION_GRANTED) {
//                    mListener.granted();
//                } else {
//                    deniedList.add(permissions[i]);
//                }
//            }
//            if (!deniedList.isEmpty()) {
//                mListener.denied(deniedList);
//            }
//        }
//    }
}

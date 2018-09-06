package com.shoppay.numc;

import android.app.Application;
import android.content.Context;

import com.shoppay.numc.bean.SystemQuanxian;
import com.shoppay.numc.crash.CrashHandler;
import com.shoppay.numc.nbean.Currency;
import com.shoppay.numc.nbean.PayType;
import com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt;
import com.sunmi.payservice.hardware.aidl.HardwareOpt;

import java.util.ArrayList;
import java.util.List;

import sunmi.paylib.SunmiPayKernel;
import sunmi.payservicelib.SunmiPayService;

/**
 * Created by songxiaotao on 2017/7/8.
 */

public class MyApplication extends Application {
    /*****************
     * SUNMI设备
     *****************/
    private static SunmiPayKernel sSunmiPayKernel;  //SUNMI支付SDK操作核心对象
    /**
     * 获取读卡模块
     */
    public static ReadCardOpt sReadCardOpt;
    public static Context context;
    //SUNMI P1N设备
    public static HardwareOpt sHardwareOpt;
    private SunmiPayService mSunmiPayService;
    private List<Currency> currency = new ArrayList<>();
    private List<PayType> payType = new ArrayList<>();
    private SystemQuanxian sysquanxian;

    @Override
    public void onCreate() {
        super.onCreate();
//        conn();
        conn_sunmiP1();
        context = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);
//        BlockCanary.install(this, new AppBlockCanaryContext()).start();
    }

    public SystemQuanxian getSysquanxian() {
        return sysquanxian;
    }

    public void setSystemQuanxian(SystemQuanxian systemQuanxian) {
        this.sysquanxian = systemQuanxian;
    }

    public List<PayType> getPayType() {
        return payType;
    }

    public void setPayType(List<PayType> payType) {
        this.payType.clear();
        this.payType.addAll(payType);
    }

    public List<Currency> getCurrency() {
        return currency;
    }

    public void setCurrency(List<Currency> currency) {
        this.currency.clear();
        this.currency.addAll(currency);
    }

    /**
     * SUNMI-P1N连接读卡支付SDK
     */
    private void conn_sunmiP1() {
        mSunmiPayService = SunmiPayService.getInstance();
        mSunmiPayService.connectPayService(getApplicationContext(), connCallback);
    }

    /**
     * SUNMI-P1N连接状态回调
     */
    private SunmiPayService.ConnCallback connCallback = new SunmiPayService.ConnCallback() {
        @Override
        public void onServiceConnected() {
            sHardwareOpt = mSunmiPayService.mHardwareOpt;
        }

        @Override
        public void onServiceDisconnected() {

        }
    };

    /**
     * SUNMI连接支付SDK
     */
    private void conn() {
        sSunmiPayKernel = SunmiPayKernel.getInstance();
        sSunmiPayKernel.connectPayService(getApplicationContext(), mConnCallback);
    }

    /**
     * SUNMI连接状态回调
     */
    private SunmiPayKernel.ConnCallback mConnCallback = new SunmiPayKernel.ConnCallback() {
        @Override
        public void onServiceConnected() {
            try {
                sReadCardOpt = sSunmiPayKernel.mReadCardOpt;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };
}

package com.shoppay.numc.modle;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shoppay.numc.R;
import com.shoppay.numc.http.ContansUtils;
import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.tools.LogUtils;
import com.shoppay.numc.tools.MD5Util;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.tools.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2018/9/5.
 */

public class ImpFabiDuihuan {
    public void fabiDuihuan(final Activity ac, final Dialog dialog, int FepID, int UserID,String password, int FromCurrency, int ToCurrency, String Money,
                            final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);


        RequestParams params = new RequestParams();
        params.put("UserID", UserID);
        params.put("password", password);
        params.put("FepID", FepID);
        params.put("LoginUserID",  PreferenceHelper.readInt(ac, "shoppay", "userid", 0));
        params.put("FromCurrency", FromCurrency+"");
        params.put("ToCurrency", ToCurrency+"");
        params.put("Money", Money);
        JSONObject jso = new JSONObject();
        try {
            jso.put("UserID".toLowerCase(), UserID);
            jso.put("password".toLowerCase(), password);
            jso.put("FepID".toLowerCase(), FepID);
            jso.put("FromCurrency".toLowerCase(), FromCurrency+"");
            jso.put("ToCurrency".toLowerCase(), ToCurrency+"");
            jso.put("Money".toLowerCase(), Money);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("xxjson", jso.toString());
        params.put("HMAC", MD5Util.md5(jso.toString() + "bankbosscc").toUpperCase());
        LogUtils.d("xxmap", params.toString());
        client.post(ContansUtils.BASE_URL + "pos/Fep.ashx", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxRechargeS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            ToastUtils.showToast(ac, jso.getString("msg"));
                        } else {
                            ToastUtils.showToast(ac, jso.getString("enmsg"));
                        }
                        back.onResponse("");
                        //打印
//                                            if (jsonObject.getInt("printNumber") == 0) {
//                                                finish();
//                                            } else {
//                                                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                                                if (bluetoothAdapter.isEnabled()) {
//                                                    BluetoothUtil.connectBlueTooth(MyApplication.context);
//                                                    BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
//                                                    ActivityStack.create().finishActivity(VipRechargeActivity.class);
//                                                } else {
//                                                    ActivityStack.create().finishActivity(VipRechargeActivity.class);
//                                                }
//                                            }
                    } else {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            ToastUtils.showToast(ac, jso.getString("msg"));
                        } else {
                            ToastUtils.showToast(ac, jso.getString("enmsg"));
                        }

                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ac, ac.getResources().getString(R.string.fabiduhuifalse), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, ac.getResources().getString(R.string.fabiduhuifalse), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

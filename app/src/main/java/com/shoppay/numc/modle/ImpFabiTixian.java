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

public class ImpFabiTixian {
    public void fabiTixian(final Activity ac, final Dialog dialog, int WitdrawID, int UserID, String PassWord, String Currency, int Country, String BankName, String AccountAgent, String AccountBranch,
                           String AccountName, String AccountNumber, int IDType, String IDCard, String Mobile, String Money, String Remark,
                           final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);


        RequestParams params = new RequestParams();
        params.put("WitdrawID", WitdrawID);
        params.put("UserID", UserID);
        params.put("PassWord", PassWord);
        params.put("Currency", Currency);
        params.put("Country", Country);
        params.put("BankName", BankName);
        params.put("AccountAgent", AccountAgent);
        params.put("AccountBranch", AccountBranch);
        params.put("AccountName", AccountName);
        params.put("AccountNumber", AccountNumber);
        params.put("IDType", IDType);
        params.put("IDCard", IDCard);
        params.put("Mobile", Mobile);
        params.put("Money", Money);
        JSONObject jso = new JSONObject();
        try {
            jso.put("UserID".toLowerCase(), UserID);
            jso.put("WitdrawID".toLowerCase(), WitdrawID);
            jso.put("Currency".toLowerCase(), Currency);
            jso.put("BankName".toLowerCase(), BankName);
            jso.put("AccountName".toLowerCase(), AccountName);
            jso.put("AccountNumber".toLowerCase(), AccountNumber);
            jso.put("Money".toLowerCase(), Money);
            jso.put("Remark".toLowerCase(), Remark.equals("") ? "无" : Remark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("xxjson", jso.toString());
        params.put("HMAC", MD5Util.md5(jso.toString() + "bankbosscc").toUpperCase());
        LogUtils.d("xxmap", params.toString());
        client.post(ContansUtils.BASE_URL + "pos/Witdraw.ashx", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxTixianS", new String(responseBody, "UTF-8"));
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
                    Toast.makeText(ac, ac.getResources().getString(R.string.fabitxfalse), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, ac.getResources().getString(R.string.fabitxfalse), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

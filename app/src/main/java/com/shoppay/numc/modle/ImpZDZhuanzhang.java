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
import com.shoppay.numc.tools.NewDayinTools;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.tools.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2018/9/5.
 */

public class ImpZDZhuanzhang {
    public void zdZhuanzhang(final Activity ac, final Dialog dialog, int TransferID, int UserID, String password, int StockCode, String AccountName, String AccountNumber, String Money, String Remark,
                             final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);


        RequestParams params = new RequestParams();
        params.put("TransferID", TransferID);
        params.put("UserID", UserID);
        params.put("password", password);
        params.put("StockCode", StockCode);
        params.put("LoginUserID", PreferenceHelper.readInt(ac, "shoppay", "userid", 0));
        params.put("AccountName", AccountName);
        params.put("AccountNumber", AccountNumber);
        params.put("Money", Money);
        params.put("Remark", Remark.equals("") ? "无" : Remark);
        JSONObject jso = new JSONObject();
        try {
            jso.put("TransferID", TransferID);
            jso.put("UserID", UserID);
            jso.put("password", password);
            jso.put("StockCode", StockCode);
            jso.put("AccountName", AccountName);
            jso.put("AccountNumber", AccountNumber);
            jso.put("Money", Money);
            jso.put("Remark", Remark.equals("") ? "无" : Remark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("xxjson", jso.toString());
        params.put("HMAC", MD5Util.md5(jso.toString().toLowerCase() + "bankbosscc").toUpperCase());
        LogUtils.d("xxmap", params.toString());
        client.post(ContansUtils.BASE_URL + "pos/CoinTransfer.ashx", params, new AsyncHttpResponseHandler() {
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
                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        NewDayinTools.dayin(ac,jsonObject,back);
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
                    Toast.makeText(ac, ac.getResources().getString(R.string.zhidianzzfalse), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                LogUtils.d("xxe", error.getMessage());
                dialog.dismiss();
                Toast.makeText(ac, ac.getResources().getString(R.string.zhidianzzfalse), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

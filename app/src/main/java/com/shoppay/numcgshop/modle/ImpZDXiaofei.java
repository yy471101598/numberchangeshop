package com.shoppay.numcgshop.modle;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.http.ContansUtils;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.tools.LogUtils;
import com.shoppay.numcgshop.tools.MD5Util;
import com.shoppay.numcgshop.tools.NewDayinTools;
import com.shoppay.numcgshop.tools.PreferenceHelper;
import com.shoppay.numcgshop.tools.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2018/9/5.
 */

public class ImpZDXiaofei {
    public void zdXiaofei(final Activity ac, final Dialog dialog, int ConsumeID, int UserID, String PassWord, int StockCode,String Money,
                            final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("ConsumeID", ConsumeID);
        params.put("UserID", UserID);
        params.put("PassWord", PassWord);
//        PayType string	//0为指点，其他直接调用币种编号
        params.put("StockCode", StockCode);
        params.put("LoginUserID",  PreferenceHelper.readInt(ac, "shoppay", "userid", 0));
        params.put("Money", Money);
        JSONObject jso = new JSONObject();
        try {
            jso.put("UserID", UserID);
            jso.put("LoginUserID",PreferenceHelper.readInt(ac, "shoppay", "userid", 0));
            jso.put("ConsumeID", ConsumeID);
            jso.put("StockCode", StockCode);
            jso.put("Money", Money);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("xxjson", jso.toString());
        params.put("HMAC", MD5Util.md5(jso.toString().toLowerCase() + "bankbosscc").toUpperCase());
        LogUtils.d("xxmap", params.toString());
        client.post(ContansUtils.BASE_URL + "pos/Consume.ashx", params, new AsyncHttpResponseHandler() {
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
                    Toast.makeText(ac, ac.getResources().getString(R.string.zdxffalse), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, ac.getResources().getString(R.string.zdxffalse), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

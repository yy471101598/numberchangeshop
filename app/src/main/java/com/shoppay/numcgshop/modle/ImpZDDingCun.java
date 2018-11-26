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

public class ImpZDDingCun {
    public void zdDingcun(final Activity ac, final Dialog dialog, int DepositID, int UserID,String password, int StockCode, int Maturity, String Money,
                            final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);


        RequestParams params = new RequestParams();
        params.put("DepositID", DepositID);
        params.put("UserID", UserID);
        params.put("password", password);
        params.put("StockCode", StockCode);
        params.put("LoginUserID",  PreferenceHelper.readInt(ac, "shoppay", "userid", 0));
        params.put("Maturity", Maturity);
        params.put("Money", Money);
        JSONObject jso = new JSONObject();
        try {
            jso.put("DepositID", DepositID);
            jso.put("UserID", UserID);
            jso.put("password", password);
            jso.put("StockCode",StockCode);
            jso.put("Maturity", Maturity);
            jso.put("Money", Money);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("xxjson", jso.toString());
        params.put("HMAC", MD5Util.md5(jso.toString().toLowerCase() + "bankbosscc").toUpperCase());
        LogUtils.d("xxmap", params.toString());
        client.post(ContansUtils.BASE_URL + "pos/CoinDeposit.ashx", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxdingcunS", new String(responseBody, "UTF-8"));
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
                    Toast.makeText(ac, ac.getResources().getString(R.string.zhidiandingcunfalse), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                LogUtils.d("xxe", error.getMessage());
                Toast.makeText(ac, ac.getResources().getString(R.string.zhidiandingcunfalse), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

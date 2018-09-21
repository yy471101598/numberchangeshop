package com.shoppay.numc.modle;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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

public class ImpVipRecharge {
    public void vipRecharge(final Activity ac, final Dialog dialog, int RechargeID, int UserID, String password, int CurrencyID, int PayTypeID, String Money,
                            final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        LogUtils.d("xxmoney", Money + "");
        RequestParams params = new RequestParams();
        params.put("RechargeID", RechargeID);
        params.put("UserID", UserID);
        params.put("password", password);
        params.put("CurrencyID", CurrencyID);
        params.put("LoginUserID", PreferenceHelper.readInt(ac, "shoppay", "userid", 0));
        params.put("PayTypeID", PayTypeID);
        params.put("Money", Money);
        JSONObject jso = new JSONObject();
        try {
            jso.put("RechargeID", RechargeID);
            jso.put("UserID", UserID);
            jso.put("password", password);
            jso.put("CurrencyID", CurrencyID);
            jso.put("PayTypeID", PayTypeID);
            jso.put("Money", Money);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("xxjson", jso.toString());
        params.put("HMAC", MD5Util.md5(jso.toString().toLowerCase() + "bankbosscc").toUpperCase());
        LogUtils.d("xxmap", params.toString());
        client.post(ContansUtils.BASE_URL + "pos/Recharge.ashx", params, new AsyncHttpResponseHandler() {
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
                        NewDayinTools.dayin(jsonObject,back);
                    } else {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            ToastUtils.showToast(ac, jso.getString("msg"));
                        } else {
                            ToastUtils.showToast(ac, jso.getString("enmsg"));
                        }

                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ac, ac.getResources().getString(R.string.rechargefalse), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, ac.getResources().getString(R.string.rechargefalse), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width / 2 + 1);
        int newheight = (height / 2 + 1);
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newheight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix,
                true);
        return newbm;
    }
}

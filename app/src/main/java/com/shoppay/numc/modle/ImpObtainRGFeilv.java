package com.shoppay.numc.modle;

import android.app.Activity;
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

public class ImpObtainRGFeilv {
    public void obtainDuihuanHuilv(final Activity ac, int FromCurrency, int ToStockCode,
                                   final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("FromCurrency", FromCurrency + "");
        params.put("ToStockCode", ToStockCode + "");
        JSONObject jso = new JSONObject();
        try {
            jso.put("FromCurrency".toLowerCase(), FromCurrency + "");
            jso.put("ToStockCode".toLowerCase(), ToStockCode + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("xxjson", jso.toString());
        params.put("HMAC", MD5Util.md5(jso.toString() + "bankbosscc").toUpperCase());
        LogUtils.d("xxmap", params.toString());
        client.post(ContansUtils.BASE_URL + "pos/CoinSubscribeRate.ashx", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxCurrS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        back.onResponse(new String(responseBody, "UTF-8"));
                    } else {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            ToastUtils.showToast(ac, jso.getString("msg"));
                        } else {
                            ToastUtils.showToast(ac, jso.getString("enmsg"));
                        }
                        back.onErrorResponse("");
                    }
                } catch (Exception e) {
                    back.onErrorResponse("");
                    Toast.makeText(ac, ac.getResources().getString(R.string.zdrgflfalse), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                back.onErrorResponse("");
                Toast.makeText(ac, ac.getResources().getString(R.string.zdrgflfalse), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.shoppay.numcgshop.modle;

import android.app.Activity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shoppay.numcgshop.http.ContansUtils;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.tools.LogUtils;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2018/9/5.
 */

public class ImpObtainCountry {
    public void obtainCountry(final Activity ac,
                               final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        client.get(ContansUtils.BASE_URL + "pos/Country.ashx", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxQuxianS", new String(responseBody, "UTF-8"));
                    JSONObject json=new JSONObject(new String(responseBody,"UTF-8"));

                    back.onResponse(json.getString("vdata"));
                } catch (Exception e) {
                    back.onErrorResponse("");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                back.onErrorResponse("");
            }
        });
    }
}

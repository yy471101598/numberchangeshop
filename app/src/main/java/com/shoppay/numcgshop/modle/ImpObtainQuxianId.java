package com.shoppay.numcgshop.modle;

import android.app.Activity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.http.ContansUtils;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.tools.LogUtils;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2018/9/5.
 */

public class ImpObtainQuxianId {
    public void obtainQuxianId(final Activity ac,
                               final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        client.get(ContansUtils.BASE_URL + "pos/WitdrawID.ashx", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxQuxianS", new String(responseBody, "UTF-8"));
                    //witdrawid
                    back.onResponse(new String(responseBody, "UTF-8"));
                } catch (Exception e) {
                    back.onErrorResponse("");
                    Toast.makeText(ac, ac.getResources().getString(R.string.qxcodefalse), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                back.onErrorResponse("");
                Toast.makeText(ac, ac.getResources().getString(R.string.qxcodefalse), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

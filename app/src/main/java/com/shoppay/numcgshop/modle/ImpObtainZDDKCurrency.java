package com.shoppay.numcgshop.modle;

import android.app.Activity;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.http.ContansUtils;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.tools.LogUtils;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2018/9/5.
 */

public class ImpObtainZDDKCurrency {
    public void obtainZDdkCurrency(final Activity ac,int Maturity,
                               final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("Maturity", Maturity);
        client.post(ContansUtils.BASE_URL + "pos/CoinLoanCurrency.ashx",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxCurrS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    back.onResponse(jso.getString("vdata"));
                } catch (Exception e) {
                    back.onErrorResponse("");
                    Toast.makeText(ac, ac.getResources().getString(R.string.currno), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                back.onErrorResponse("");
                Toast.makeText(ac, ac.getResources().getString(R.string.currno), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

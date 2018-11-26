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

public class ImpObtainZDDHShopmsg {
    public void obtainZDDHShopmsg(final Activity ac, String StaID, int StockCode,
                                  final InterfaceBack back) {

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("StaID", StaID);
        params.put("StockCode", StockCode);
        client.post(ContansUtils.BASE_URL + "pos/CoinExchangeProduct.ashx", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxShopS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getString("flag").equals("1")) {
                        back.onResponse(new String(responseBody, "UTF-8"));
                    } else {
                        back.onErrorResponse("");
                    }
                } catch (Exception e) {
                    back.onErrorResponse("");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                back.onErrorResponse("");
                Toast.makeText(ac, ac.getResources().getString(R.string.dhshopmsgfalse), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.shoppay.numc.http;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shoppay.numc.tools.ActivityStack;
import com.shoppay.numc.tools.LogUtils;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.tools.ToastUtils;
import com.shoppay.numc.ui.LoginActivity;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2018/6/5.
 */

public class AsyncHttpUtls {
    public static void postHttp(final Activity ac, final Dialog dialog, String url, RequestParams map, final InterfaceBack back) {
        AsyncHttpClient client = new AsyncHttpClient();
//        client.addHeader("uid", PreferenceHelper.readString(ac, "carapp", "uid", ""));
//        client.addHeader("token", PreferenceHelper.readString(ac, "carapp", "token", ""));
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        LogUtils.d("xxurl", url);
        LogUtils.d("xxmap", map.toString());
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxmsg", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        back.onResponse(jso.getString("vdata"));
                    } else {
                        back.onErrorResponse("");
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            ToastUtils.showToast(ac, jso.getString("msg"));
                        } else {
                            ToastUtils.showToast(ac, jso.getString("enmsg"));
                        }
                    }
                } catch (Exception e) {
                    back.onErrorResponse("");
                    dialog.dismiss();
                    e.printStackTrace();
                    ToastUtils.showToast(ac, "服务异常，请稍后再试");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                LogUtils.d("xxE", error.toString());
                ToastUtils.showToast(ac, "服务异常，请稍后再试");
            }
        });
    }

    public static void postDayinHttp(final Activity ac, final Dialog dialog, String url, RequestParams map, final InterfaceBack back) {
        AsyncHttpClient client = new AsyncHttpClient();
//        client.addHeader("uid", PreferenceHelper.readString(ac, "carapp", "uid", ""));
//        client.addHeader("token", PreferenceHelper.readString(ac, "carapp", "token", ""));
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        LogUtils.d("xxurl", url);
        LogUtils.d("xxmap", map.toString());
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxmsg", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        back.onResponse(jso.getString("vdata"));
                    } else {
                        back.onErrorResponse("");
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            ToastUtils.showToast(ac, jso.getString("msg"));
                        } else {
                            ToastUtils.showToast(ac, jso.getString("enmsg"));
                        }
                    }
                } catch (Exception e) {
                    back.onErrorResponse("");
                    dialog.dismiss();
                    e.printStackTrace();
                    ToastUtils.showToast(ac, "服务异常，请稍后再试");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                ToastUtils.showToast(ac, "服务异常，请稍后再试");
            }
        });
    }

    public static void getHttp(final Activity ac, final Dialog dialog, String url, final InterfaceBack back) {
        AsyncHttpClient client = new AsyncHttpClient();
//        client.addHeader("uid", PreferenceHelper.readString(ac, "carapp", "uid", ""));
//        client.addHeader("token", PreferenceHelper.readString(ac, "carapp", "token", ""));
        LogUtils.d("xxurl", url);
        client.get(ac, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxmsg", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        back.onResponse(jso.getString("vdata"));
                    } else {
                        back.onErrorResponse("");
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            ToastUtils.showToast(ac, jso.getString("msg"));
                        } else {
                            ToastUtils.showToast(ac, jso.getString("enmsg"));
                        }
                    }
                } catch (Exception e) {
                    back.onErrorResponse("");
                    dialog.dismiss();
                    e.printStackTrace();
                    ToastUtils.showToast(ac, "服务异常，请稍后再试");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                ToastUtils.showToast(ac, "服务异常，请稍后再试");
            }
        });
    }


    public static void getHttpNoDialog(final Activity ac, String url, final InterfaceBack back) {
        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(ac);
//        client.setCookieStore(myCookieStore);
        LogUtils.d("xxurl", url);
        client.addHeader("uid", PreferenceHelper.readString(ac, "carapp", "uid", ""));
        client.addHeader("token", PreferenceHelper.readString(ac, "carapp", "token", ""));
        client.get(ac, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxmsg", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        back.onResponse(jso.getString("data"));
                    } else {
                        if (jso.getInt("status") == 106) {
                            PreferenceHelper.write(ac, "carapp", "token", "");
                            ActivityStack.create().finishAllActivity();
                            Intent intent = new Intent(ac, LoginActivity.class);
                            ac.startActivity(intent);
                        }
                        ToastUtils.showToast(ac, jso.getString("resmsg"));
                        back.onErrorResponse("");
                    }
                } catch (Exception e) {
                    back.onErrorResponse("");
                    e.printStackTrace();
                    ToastUtils.showToast(ac, "服务异常，请稍后再试");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                back.onErrorResponse("");

                ToastUtils.showToast(ac, "服务异常，请稍后再试");
            }
        });
    }
}

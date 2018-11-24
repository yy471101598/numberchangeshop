package com.shoppay.numc.ui.web;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.tools.LogUtils;
import com.shoppay.numc.tools.NewDayinTools;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.ui.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class PayUtils {
    private static final int FILECHOOSER_RESULTCODE = 333;
    protected static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 211;
    private static ValueCallback<Uri> mUploadMessage;
    public static ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public static void webPayUtils(final Context ac, final Dialog dialog,
                                   final WebView shop_web, String url) {
        WebSettings seting = shop_web.getSettings();
        // 设置与Js交互的权限
        seting.setJavaScriptEnabled(true);
        seting.setAllowFileAccess(true);
        seting.setAllowContentAccess(true);
        // shop_web.setVisibility(View.INVISIBLE);

        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        shop_web.addJavascriptInterface(new AndroidtoJs(), "lee");//AndroidtoJS类对象映射到js的test对象
        shop_web.setWebChromeClient(new WebChromeClient() {
            // Android>=5.0调用这个方法
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> uploadFile,
                                             FileChooserParams fileChooserParams) {
                mUploadMessageForAndroid5 = uploadFile;
                Intent contentSelectionIntent = new Intent(
                        Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT,
                        contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "文件选择");

                ((Activity) ac).startActivityForResult(chooserIntent,
                        FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
                return true;
            }

            // js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
            // Android > 4.1.1 调用这个方法
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadFile,
                                        String acceptType, String capture) {
                mUploadMessage = uploadFile;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                ((Activity) ac).startActivityForResult(intent,
                        FILECHOOSER_RESULTCODE);
            }

            // 3.0 + 调用这个方法
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadFile,
                                        String acceptType) {
                mUploadMessage = uploadFile;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                ((Activity) ac).startActivityForResult(intent,
                        FILECHOOSER_RESULTCODE);
            }

            //
            // // Android < 3.0 调用这个方法
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadFile) {
                mUploadMessage = uploadFile;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                ((Activity) ac).startActivityForResult(intent,
                        FILECHOOSER_RESULTCODE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                // TODO Auto-generated method stub
                super.onReceivedTitle(view, title);
//                tv_title.setText(title);
            }

        });
        shop_web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                // http://o2o.cloudkay.net/ViewHome/HomeIndex
                // PreferenceHelper.write(ac, "pull", "shopurl", url);
//                tv_title.setText(view.getTitle());
                super.onPageFinished(view, url);
                dialog.dismiss();

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("url", url);
                PreferenceHelper.write(MyApplication.context,"numchange","weburl",url);
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    ac.startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        // 在js中调用本地java方法
        shop_web.loadUrl(url);

    }

    // 继承自Object类
    public static class AndroidtoJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void funAndroid(String msg) {
            try {
                JSONObject jso = new JSONObject(msg);
                JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                NewDayinTools.dayin(MyApplication.context, jsonObject, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {

                    }

                    @Override
                    public void onErrorResponse(Object msg) {

                    }
                });
            } catch (JSONException e) {
                LogUtils.d("xxer", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

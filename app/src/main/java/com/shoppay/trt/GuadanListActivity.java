package com.shoppay.trt;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.trt.adapter.GuadanListAdapter;
import com.shoppay.trt.adapter.LeftAdapter;
import com.shoppay.trt.bean.GuadanListMsg;
import com.shoppay.trt.bean.GuadanPay;
import com.shoppay.trt.bean.ShopClass;
import com.shoppay.trt.http.InterfaceBack;
import com.shoppay.trt.tools.BluetoothUtil;
import com.shoppay.trt.tools.DateUtils;
import com.shoppay.trt.tools.DayinUtils;
import com.shoppay.trt.tools.DialogUtil;
import com.shoppay.trt.tools.LogUtils;
import com.shoppay.trt.tools.PreferenceHelper;
import com.shoppay.trt.tools.UrlTools;
import com.shoppay.trt.wxcode.MipcaActivityCapture;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import static com.shoppay.trt.R.id.tv_money;

/**
 * Created by Administrator on 2018/8/21 0021.
 */

public class GuadanListActivity extends Activity {

    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.listview)
    ListView listview;
    private Activity ac;
    private Dialog dialog;
    private List<GuadanListMsg> list;
    private GuadanListAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pay = (GuadanPay) msg.obj;
            if (pay.type.equals("")) {
                obtainGuadanlistClass();
            } else {
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 333);
            }
        }
    };
    private String orderAccount;
    private Dialog paydialog;
    private MyApplication app;
    private GuadanPay pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guadanlist);
        ButterKnife.bind(this);
        ac = this;
        app = (MyApplication) getApplication();
        tvTitle.setText("挂单列表");
        dialog = DialogUtil.loadingDialog(GuadanListActivity.this, 1);
        paydialog = DialogUtil.payloadingDialog(GuadanListActivity.this, 1);
        list = new ArrayList<>();
        adapter = new GuadanListAdapter(ac, list, dialog, app, handler);
        listview.setAdapter(adapter);
        obtainGuadanlistClass();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 333:
                if (resultCode == RESULT_OK) {
                    pay(data.getStringExtra("codedata"));
                }
                break;
        }
    }

    private void pay(String codedata) {
        paydialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("auth_code", codedata);
        map.put("UserID", PreferenceHelper.readString(ac, "shoppay", "UserID", ""));
//        （1会员充值7商品消费9快速消费11会员充次）
        map.put("ordertype", 7);
        map.put("account", pay.orderAccount);
        map.put("money", pay.money);
//        0=现金 1=银联 2=微信 3=支付宝
        switch (pay.type) {
            case "wx":
                map.put("payType", 2);
                break;
            case "zfb":
                map.put("payType", 3);
                break;
        }
        client.setTimeout(120 * 1000);
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "PayOnLine");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    paydialog.dismiss();
                    LogUtils.d("xxpayS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {

                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        DayinUtils.dayin(jsonObject.getString("printContent"));
                        if (jsonObject.getInt("printNumber") == 0) {
                        } else {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                            } else {
                            }
                        }
                        jiesuan();
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    paydialog.dismiss();
                    Toast.makeText(ac, "支付失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                paydialog.dismiss();
                Toast.makeText(ac, "支付失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void jiesuan() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("OrderID", pay.orderId);
        if (pay.type.equals("wx")) {
            params.put("payType", 2);
        } else {
            params.put("payType", 3);
        }
        params.put("UserPwd", "");

        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "StaySettle");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        dialog.dismiss();
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        if (jsonObject.getInt("printNumber") == 0) {
                        } else {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                            } else {
                            }
                        }
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                }
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "挂单完成失败，请重新完成",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtainGuadanlistClass() {
        dialog.show();
        list.clear();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("UserID", PreferenceHelper.readString(ac, "shoppay", "UserID", ""));
        map.put("UserShopID", PreferenceHelper.readString(ac, "shoppay", "ShopID", ""));
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "GetStayList");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxguadanlistS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<GuadanListMsg>>() {
                        }.getType();
                        List<GuadanListMsg> slist = gson.fromJson(jso.getString("vdata"), listType);
                        list.addAll(slist);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "获取挂单列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.rl_left)
    public void onViewClicked() {
        finish();
    }
}



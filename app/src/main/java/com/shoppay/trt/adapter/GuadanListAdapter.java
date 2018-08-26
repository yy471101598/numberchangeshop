package com.shoppay.trt.adapter;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.trt.MyApplication;
import com.shoppay.trt.R;
import com.shoppay.trt.bean.GuadanListMsg;
import com.shoppay.trt.bean.GuadanPay;
import com.shoppay.trt.bean.ShopClass;
import com.shoppay.trt.http.InterfaceBack;
import com.shoppay.trt.tools.BluetoothUtil;
import com.shoppay.trt.tools.DayinUtils;
import com.shoppay.trt.tools.GuadanCompleteDialog;
import com.shoppay.trt.tools.LogUtils;
import com.shoppay.trt.tools.NoDoubleClickListener;
import com.shoppay.trt.tools.PreferenceHelper;
import com.shoppay.trt.tools.StringUtil;
import com.shoppay.trt.tools.UrlTools;
import com.shoppay.trt.wxcode.MipcaActivityCapture;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class GuadanListAdapter extends BaseAdapter {
    private Context context;
    private List<GuadanListMsg> list;
    private LayoutInflater inflater;
    private Dialog dialog;
    private Handler handler;
    private MyApplication app;

    public GuadanListAdapter(Context context, List<GuadanListMsg> list, Dialog dialog, MyApplication app, Handler handler) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<GuadanListMsg>();
        } else {
            this.list = list;
        }
        inflater = LayoutInflater.from(context);
        this.dialog = dialog;
        this.app = app;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_guadanlist, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final GuadanListMsg home = list.get(position);
        vh.tvCode.setText(home.OrderAccount);
        vh.tvVipcard.setText(home.MemCard);
        vh.tvVipname.setText(home.MemName);
        vh.tvOrderstate.setText(home.OrderType);
        vh.tvOrderstyle.setText(home.Typetext);
        vh.tvZhmoney.setText(StringUtil.twoNum(home.DiscountMoney));
        vh.tvComplete.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                GuadanCompleteDialog.jiesuanDialog(app, home.MemCard.equals("0") ? false : true, dialog, context, 1, Double.parseDouble(home.DiscountMoney), home.OrderID, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        Message msg = handler.obtainMessage();
                        GuadanPay pay = new GuadanPay();
                        pay.money = home.DiscountMoney;
                        pay.orderId = home.OrderID;
                        if (response.toString().equals("wxpay")) {
                            pay.type = "wx";
                            msg.obj = pay;
                            msg.what=222;
                            handler.sendMessage(msg);
                        } else if (response.toString().equals("zfbpay")) {
                            pay.type = "zfb";
                            msg.obj = pay;
                            msg.what=222;
                            handler.sendMessage(msg);
                        } else {
                            pay.type = "";
                            msg.obj = pay;
                            msg.what=222;
                            handler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onErrorResponse(Object msg) {

                    }
                });
            }
        });
        vh.tvDetail.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                Message message=handler.obtainMessage();
                message.what=111;
                message.obj=home;
                handler.sendMessage(message);
            }
        });
        vh.vipTvDayin.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                dayin(home.OrderID);
            }
        });
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.tv_code)
        TextView tvCode;
        @Bind(R.id.tv_vipcard)
        TextView tvVipcard;
        @Bind(R.id.tv_vipname)
        TextView tvVipname;
        @Bind(R.id.tv_orderstate)
        TextView tvOrderstate;
        @Bind(R.id.tv_orderstyle)
        TextView tvOrderstyle;
        @Bind(R.id.vip_tv_dayin)
        TextView vipTvDayin;
        @Bind(R.id.tv_detail)
        TextView tvDetail;
        @Bind(R.id.tv_complete)
        TextView tvComplete;
        @Bind(R.id.tv_zhmoney)
        TextView tvZhmoney;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    private void dayin(String orderid) {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("UserID", PreferenceHelper.readString(context, "shoppay", "UserID", ""));
        map.put("UserShopID", PreferenceHelper.readString(context, "shoppay", "ShopID", ""));
        map.put("OrderID", orderid);
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(context, "?Source=3", "SecondPrinting");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxcompleteS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
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
                        Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "获取打印小票信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(context, "获取打印小票信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

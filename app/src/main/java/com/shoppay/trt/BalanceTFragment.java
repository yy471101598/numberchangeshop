package com.shoppay.trt;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.trt.adapter.RightAdapter;
import com.shoppay.trt.bean.Shop;
import com.shoppay.trt.bean.ShopCar;
import com.shoppay.trt.bean.ShopChose;
import com.shoppay.trt.bean.ShopClass;
import com.shoppay.trt.bean.Zhekou;
import com.shoppay.trt.db.DBAdapter;
import com.shoppay.trt.http.InterfaceBack;
import com.shoppay.trt.tools.CommonUtils;
import com.shoppay.trt.tools.DialogUtil;
import com.shoppay.trt.tools.LogUtils;
import com.shoppay.trt.tools.PreferenceHelper;
import com.shoppay.trt.tools.ShopPcNumChoseDialog;
import com.shoppay.trt.tools.StringUtil;
import com.shoppay.trt.tools.UrlTools;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class BalanceTFragment extends Fragment {

    public static final String TAG = "BalanceFragment";
    private TextView tv_no, tv_loading;
    private ListView listView;
    private RightAdapter adapter;
    private List<Shop> list;
    private String classid = "";
    private Dialog dialog;
    private DBAdapter dbAdapter;
    private Intent intent;
    private Context context;
    private ShopChangeReceiver shopchangeReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_balance, null);
        tv_no = (TextView) view.findViewById(R.id.fragment_no);
        tv_loading = (TextView) view.findViewById(R.id.fragment_loading);
        listView = (ListView) view.findViewById(R.id.fragment_listview);
        context = getActivity();
        dialog = DialogUtil.loadingDialog(getActivity(), 1);
        dbAdapter = DBAdapter.getInstance(getActivity());
        intent = new Intent("com.shoppay.wy.numberchange");
        //得到数据
        classid = getArguments().getString("classid");
        if (classid.equals("all")) {
            list = (ArrayList<Shop>) getArguments().getSerializable(TAG);
            if (list.size() > 0) {
                adapter = new RightAdapter(getActivity(), list);
                listView.setAdapter(adapter);
            } else {
                tv_no.setVisibility(View.VISIBLE);
            }
        } else {
            obtainShop();
        }
//        obtainShop();

        // 注册广播
        shopchangeReceiver = new ShopChangeReceiver();
        IntentFilter iiiff = new IntentFilter();
        iiiff.addAction("com.shoppay.wy.shopcarchange");
        getActivity().registerReceiver(shopchangeReceiver, iiiff);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (PreferenceHelper.readBoolean(context, "shoppay", "isSan", true)) {

                    final Shop home = (Shop) adapterView.getItemAtPosition(i);
                    final ShopCar dbshop = dbAdapter.getShopCar(home.GoodsID);
                    if (dbshop == null) {
                        ShopPcNumChoseDialog.numchoseDialog(getActivity(), 1, home.GoodsName, 0, Integer.parseInt(home.Number), home.GoodsType, "", new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                LogUtils.d("xxre", new Gson().toJson(response));
                                ShopChose chose = (ShopChose) response;
                                int num = Integer.parseInt(chose.num);
                                String pihao = chose.pihao;
                                if (PreferenceHelper.readBoolean(getActivity(), "shoppay", "isSan", true)) {
                                    insertShopCar(PreferenceHelper.readBoolean(context, "shoppay", "isSan", true), null, home, num, pihao);

                                } else {
                                    obtainShopZhekou(home, num, pihao);

                                }

                            }

                            @Override
                            public void onErrorResponse(Object msg) {

                            }
                        });
                    } else {
                        ShopPcNumChoseDialog.numchoseDialog(getActivity(), 1, home.GoodsName, dbshop.count, Integer.parseInt(home.Number), home.GoodsType, dbshop.batchnumber, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                ShopChose chose = (ShopChose) response;
                                int num = Integer.parseInt(chose.num);
                                String pihao = chose.pihao;
                                if (PreferenceHelper.readBoolean(getActivity(), "shoppay", "isSan", true)) {
                                    insertShopCar(PreferenceHelper.readBoolean(context, "shoppay", "isSan", true), null, home, num, pihao);

                                } else {
                                    Zhekou zk = new Zhekou();
                                    zk.DiscountPrice = dbshop.discount;
                                    zk.GoodsPoint = dbshop.pointPercent;
                                    insertShopCar(PreferenceHelper.readBoolean(context, "shoppay", "isSan", true), zk, home, num, pihao);

                                }

                            }

                            @Override
                            public void onErrorResponse(Object msg) {

                            }
                        });
                    }
                } else {
                    if (PreferenceHelper.readString(context, "shoppay", "memid", "").equals("")) {
                        Toast.makeText(context, "请输入正确的会员信息", Toast.LENGTH_SHORT).show();
                    } else {
                        final Shop home = (Shop) adapterView.getItemAtPosition(i);
                        final ShopCar dbshop = dbAdapter.getShopCar(home.GoodsID);
                        if (dbshop == null) {
                            ShopPcNumChoseDialog.numchoseDialog(getActivity(), 1, home.GoodsName, 0, Integer.parseInt(home.Number), home.GoodsType, "", new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    LogUtils.d("xxre", new Gson().toJson(response));
                                    ShopChose chose = (ShopChose) response;
                                    int num = Integer.parseInt(chose.num);
                                    String pihao = chose.pihao;
                                    if (PreferenceHelper.readBoolean(getActivity(), "shoppay", "isSan", true)) {
                                        insertShopCar(PreferenceHelper.readBoolean(context, "shoppay", "isSan", true), null, home, num, pihao);

                                    } else {
                                        obtainShopZhekou(home, num, pihao);

                                    }

                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        } else {
                            ShopPcNumChoseDialog.numchoseDialog(getActivity(), 1, home.GoodsName, dbshop.count, Integer.parseInt(home.Number), home.GoodsType, dbshop.batchnumber, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    ShopChose chose = (ShopChose) response;
                                    int num = Integer.parseInt(chose.num);
                                    String pihao = chose.pihao;
                                    if (PreferenceHelper.readBoolean(getActivity(), "shoppay", "isSan", true)) {
                                        insertShopCar(PreferenceHelper.readBoolean(context, "shoppay", "isSan", true), null, home, num, pihao);

                                    } else {
                                        Zhekou zk = new Zhekou();
                                        zk.DiscountPrice = dbshop.discount;
                                        zk.GoodsPoint = dbshop.pointPercent;
                                        insertShopCar(PreferenceHelper.readBoolean(context, "shoppay", "isSan", true), zk, home, num, pihao);

                                    }

                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        }
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(shopchangeReceiver);
    }

    private class ShopChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.notifyDataSetChanged();
        }
    }
    private void obtainShopZhekou(final Shop shop, final int num, final String pihao) {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        if (PreferenceHelper.readBoolean(context, "shoppay", "isSan", true)) {
            params.put("memid", "0");
        } else {
            params.put("memid", PreferenceHelper.readString(context, "shoppay", "memid", "0"));
        }
        params.put("GoodsCode", shop.GoodsCode);
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(context, "?Source=3", "GetGoodsInfos");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxshopzkS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Zhekou>>() {
                        }.getType();
                        List<Zhekou> zhekoulist = gson.fromJson(jso.getString("vdata"), listType);
                        //加入购物车
                        insertShopCar(PreferenceHelper.readBoolean(context, "shoppay", "isSan", true), zhekoulist.get(0), shop, num, pihao);

                    } else {
                        Toast.makeText(context, "获取商品折扣失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "获取商品折扣失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                LogUtils.d("xxshopzkE", new String(responseBody));
                Toast.makeText(context, "获取商品折扣失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertShopCar(Boolean isSan, Zhekou zk, Shop shop, int num, String pihao) {
        //加入购物车
        List<ShopCar> li = new ArrayList<ShopCar>();
        ShopCar shopCar = new ShopCar();
        shopCar.account = PreferenceHelper.readString(context, "shoppay", "account", "123");
        shopCar.count = num;
        if (isSan) {
            shopCar.discount = shop.GoodsPrice;
            shopCar.discountmoney = StringUtil.twoNum(Double.parseDouble(shop.GoodsPrice) * num + "");
            shopCar.point = 0;
            shopCar.pointPercent = "0";
            shopCar.goodspoint = 0;
        } else {
            double dimoney = num * Double.parseDouble(zk.DiscountPrice);
            shopCar.discount = zk.DiscountPrice;
            shopCar.discountmoney = StringUtil.twoNum(dimoney + "");
            shopCar.point = Double.parseDouble(CommonUtils.multiply(zk.GoodsPoint, num + ""));
            shopCar.pointPercent = zk.GoodsPoint;
            shopCar.goodspoint = Integer.parseInt(zk.GoodsPoint);
        }
        shopCar.goodsid = shop.GoodsID;
        shopCar.goodsclassid = shop.GoodsClassID;
        shopCar.goodsType = shop.GoodsType;
        shopCar.price = shop.GoodsPrice;
        shopCar.shopname = shop.GoodsName;
        shopCar.batchnumber = pihao;
        shopCar.maxnum = shop.Number;
        li.add(shopCar);
        dbAdapter.insertShopCar(li);
        adapter.notifyDataSetChanged();
//		intent.putExtra("shopclass",shop.GoodsClassID);
//		intent.putExtra("num",num+"");
        getActivity().sendBroadcast(intent);
    }

    private void obtainShop() {
        dialog.show();
        if (list != null) {
            list.clear();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getActivity());
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("UserID", PreferenceHelper.readString(getActivity(), "shoppay", "UserID", ""));
        map.put("UserShopID", PreferenceHelper.readString(getActivity(), "shoppay", "ShopID", ""));
        map.put("ClassID", classid);
        LogUtils.d("xxshopparams", map.toString());
        String url = UrlTools.obtainUrl(getActivity(), "?Source=3", "ClassGetGoods");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxshopS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Shop>>() {
                        }.getType();
                        tv_no.setVisibility(View.GONE);
                        list = gson.fromJson(jso.getString("vdata"), listType);
                        adapter = new RightAdapter(getActivity(), list);
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    tv_no.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "获取商品失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                tv_no.setVisibility(View.VISIBLE);
            }
        });
    }
}

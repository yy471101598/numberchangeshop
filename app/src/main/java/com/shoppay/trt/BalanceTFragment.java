package com.shoppay.trt;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.trt.adapter.LeftAdapter;
import com.shoppay.trt.adapter.RightAdapter;
import com.shoppay.trt.bean.Shop;
import com.shoppay.trt.bean.ShopClass;
import com.shoppay.trt.tools.DialogUtil;
import com.shoppay.trt.tools.LogUtils;
import com.shoppay.trt.tools.PreferenceHelper;
import com.shoppay.trt.tools.UrlTools;

import org.json.JSONObject;

import java.io.Serializable;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_balance, null);
        tv_no = (TextView) view.findViewById(R.id.fragment_no);
        tv_loading = (TextView) view.findViewById(R.id.fragment_loading);
        listView = (ListView) view.findViewById(R.id.fragment_listview);
        dialog = DialogUtil.loadingDialog(getActivity(), 1);
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
        return view;
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

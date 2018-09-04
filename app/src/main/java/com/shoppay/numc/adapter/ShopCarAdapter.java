package com.shoppay.numc.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.numc.R;
import com.shoppay.numc.bean.Shop;
import com.shoppay.numc.bean.ShopCar;
import com.shoppay.numc.bean.Zhekou;
import com.shoppay.numc.db.DBAdapter;
import com.shoppay.numc.tools.CommonUtils;
import com.shoppay.numc.tools.DialogUtil;
import com.shoppay.numc.tools.LogUtils;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.tools.StringUtil;
import com.shoppay.numc.tools.UrlTools;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ShopCarAdapter extends BaseAdapter {
    private Context context;
    private List<ShopCar> list;
    private LayoutInflater inflater;
    private Intent intent;
    private Dialog dialog;
    private DBAdapter dbAdapter;

    public ShopCarAdapter(Context context, List<ShopCar> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<ShopCar>();
        } else {
            this.list = list;
        }
        inflater = LayoutInflater.from(context);
        intent = new Intent("com.shoppay.wy.shopcarchange");
        dialog = DialogUtil.loadingDialog(context, 1);
        dbAdapter = DBAdapter.getInstance(context);
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
        final ViewHolder vh;
//		if (convertView == null) {
        convertView = inflater.inflate(R.layout.item_gwcar, null);
        vh = new ViewHolder();
        vh.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        vh.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
        vh.item_tv_numph = (EditText) convertView.findViewById(R.id.item_tv_numph);
        vh.img_del = (ImageView) convertView.findViewById(R.id.item_iv_del);
        vh.img_add = (ImageView) convertView.findViewById(R.id.item_iv_add);
        vh.et_pihao = (EditText) convertView.findViewById(R.id.et_pihao);
        convertView.setTag(vh);
//		} else {
//			vh = (ViewHolder) convertView.getTag();
//		}
        final ShopCar home = list.get(position);
        vh.tv_money.setText("￥" + home.discountmoney);
        vh.tv_name.setText("品名：" + home.shopname);
        vh.item_tv_numph.setText(home.count + "");
        if (!home.batchnumber.equals("")) {
            vh.et_pihao.setText(home.batchnumber);
        }
        vh.img_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.parseInt(vh.item_tv_numph.getText().toString());
                if (num > 0) {
                    num = num - 1;
                    vh.item_tv_numph.setText(num + "");
                    home.count = num;
                    double dimoney = num * Double.parseDouble(home.discount);
                    home.discountmoney = StringUtil.twoNum(dimoney + "");
                    home.batchnumber = vh.et_pihao.getText().toString();
                    vh.tv_money.setText("￥" + home.discountmoney);
                    insertShopCar(home);
                }

            }
        });
        vh.img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int shopnum = Integer.parseInt(vh.item_tv_numph.getText().toString());
                shopnum = shopnum + 1;
                if (home.goodsType.equals("1")) {
                    vh.item_tv_numph.setText(shopnum + "");
                    home.count = shopnum;
                    double dimoney = shopnum * Double.parseDouble(home.discount);
                    home.discountmoney = StringUtil.twoNum(dimoney + "");
                    home.batchnumber = vh.et_pihao.getText().toString();
                    vh.tv_money.setText("￥" + home.discountmoney);
                    insertShopCar(home);
                } else {
                    int maxnum = Integer.parseInt(home.maxnum);
                    if (shopnum > maxnum) {
                        shopnum = maxnum;
                        vh.item_tv_numph.setText(shopnum + "");
                        Toast.makeText(context, "该商品的最大库存量为" + maxnum, Toast.LENGTH_SHORT).show();
                    } else {
                        vh.item_tv_numph.setText(shopnum + "");
                        home.count = shopnum;
                        double dimoney = shopnum * Double.parseDouble(home.discount);
                        home.discountmoney = StringUtil.twoNum(dimoney + "");
                        home.batchnumber = vh.et_pihao.getText().toString();
                        vh.tv_money.setText("￥" + home.discountmoney);
                        insertShopCar(home);
                    }
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView tv_name, tv_money;
        ImageView img_del, img_add;
        EditText et_pihao, item_tv_numph;
    }


    private void obtainShopZhekou(final Shop shop) {
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
                        insertShopCar(PreferenceHelper.readBoolean(context, "shoppay", "isSan", true), zhekoulist.get(0), shop, 1);

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

    private void insertShopCar(ShopCar shopCar) {
        List<ShopCar> li = new ArrayList<ShopCar>();
        li.add(shopCar);
        dbAdapter.insertShopCar(li);
        context.sendBroadcast(intent);
    }

    private void insertShopCar(Boolean isSan, Zhekou zk, Shop shop, int num) {
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
        li.add(shopCar);
        dbAdapter.insertShopCar(li);
//		intent.putExtra("shopclass",shop.GoodsClassID);
//		intent.putExtra("num",num+"");
        context.sendBroadcast(intent);
    }
}

package com.shoppay.numcgshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.bean.LipinMsg;
import com.shoppay.numcgshop.db.DBAdapter;
import com.shoppay.numcgshop.tools.LogUtils;
import com.shoppay.numcgshop.tools.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ZhidianDuihuanAdapter extends BaseAdapter {
    private Context context;
    private List<LipinMsg> list;
    private LayoutInflater inflater;
    private Intent intent;
    private DBAdapter dbAdapter;

    public ZhidianDuihuanAdapter(Context context, List<LipinMsg> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<LipinMsg>();
        } else {
            this.list = list;
        }
        inflater = LayoutInflater.from(context);
        dbAdapter = DBAdapter.getInstance(context);
        intent = new Intent("com.shoppay.wy.jifenduihuan");
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
        convertView = inflater.inflate(R.layout.item_jifenduihuan, null);
        vh = new ViewHolder(convertView);
        convertView.setTag(vh);
        final LipinMsg home = list.get(position);
        if (PreferenceHelper.readString(context, "numc", "lagavage", "zh").equals("zh")) {
            vh.itemTvShopname.setText(home.title);
        } else {
            vh.itemTvShopname.setText(home.entitle);
        }
        vh.itemTvJifen.setText(home.price);
        vh.itemTvKucunnum.setText(home.stock);
        LogUtils.d("xxst", home.stockcode);
         LipinMsg dbshop = dbAdapter.getJifenShop(home.staid);
        if (dbshop == null) {
            vh.itemTvNum.setText("0");
        } else {
            if (!dbshop.num.equals("0")) {
                vh.itemIvDel.setVisibility(View.VISIBLE);
                vh.itemTvNum.setVisibility(View.VISIBLE);
                vh.itemTvNum.setText(dbshop.num);
            }
        }
        vh.itemIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.parseInt(vh.itemTvNum.getText().toString());
                if (num == 0) {
                    vh.itemTvNum.setVisibility(View.VISIBLE);
                    vh.itemIvDel.setVisibility(View.VISIBLE);
                }
                LipinMsg dbshop = dbAdapter.getJifenShop(home.staid);
                if (dbshop == null) {
                    num = 1;
                    vh.itemTvNum.setText(num + "");
                    insertJifenShopCar(home, num);
                } else {
                    num = num + 1;
                    if (Integer.parseInt(home.stock) < num) {
                        num = num - 1;
                        Toast.makeText(context, context.getResources().getString(R.string.lipinnumbigkucun) + home.stock, Toast.LENGTH_SHORT).show();
                    }
                    vh.itemTvNum.setText(num + "");
                    insertJifenShopCar(home, num);
                }

            }
        });

        vh.itemIvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.parseInt(vh.itemTvNum.getText().toString());
                num = num - 1;
                if (num == 0) {
                    PreferenceHelper.write(context, "shoppay", "ischoaseItemjifen", false);
                    vh.itemIvDel.setVisibility(View.GONE);
                    vh.itemTvNum.setVisibility(View.GONE);
                }
                vh.itemTvNum.setText(num + "");
                insertJifenShopCar(home, num);
            }
        });
        return convertView;
    }

    private void insertJifenShopCar(LipinMsg shop, int num) {
        //加入购物车
        List<LipinMsg> li = new ArrayList<LipinMsg>();
        LipinMsg shopCar = new LipinMsg();
        shopCar.num = num + "";
        shopCar.staid = shop.staid;
        shopCar.stockcode = shop.stockcode;
        shopCar.price = shop.price;
        shopCar.title = shop.title;
        shopCar.entitle = shop.entitle;
        li.add(shopCar);
        dbAdapter.insertJifenShopCar(li);
        context.sendBroadcast(intent);
    }

    class ViewHolder {
        @Bind(R.id.item_tv_shopname)
        TextView itemTvShopname;
        @Bind(R.id.item_tv_kucunnum)
        TextView itemTvKucunnum;
        @Bind(R.id.item_tv_money)
        TextView itemTvMoney;
        @Bind(R.id.item_tv_jifen)
        TextView itemTvJifen;
        @Bind(R.id.item_iv_add)
        ImageView itemIvAdd;
        @Bind(R.id.item_tv_num)
        TextView itemTvNum;
        @Bind(R.id.item_iv_del)
        ImageView itemIvDel;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

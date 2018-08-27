package com.shoppay.trt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.trt.R;
import com.shoppay.trt.bean.ShopTjMsg;
import com.shoppay.trt.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShopTjAdapter extends BaseAdapter {
    private Context context;
    private List<ShopTjMsg> list;
    private LayoutInflater inflater;


    public ShopTjAdapter(Context context, List<ShopTjMsg> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<ShopTjMsg>();
        } else {
            this.list = list;
        }
        inflater = LayoutInflater.from(context);

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
            convertView = inflater.inflate(R.layout.item_shoptj, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final ShopTjMsg home = list.get(position);
        vh.tvShopname.setText(home.GoodsName);
        vh.tvShopcode.setText(home.GoodsCode);
        vh.tvXsnum.setText(home.TotalNumber);
        vh.tvXsmoney.setText(StringUtil.twoNum(home.TotalMoney));
        vh.tvTcmoney.setText(StringUtil.twoNum(home.StaffTotalMoney));
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.tv_shopname)
        TextView tvShopname;
        @Bind(R.id.tv_shopcode)
        TextView tvShopcode;
        @Bind(R.id.tv_xsnum)
        TextView tvXsnum;
        @Bind(R.id.tv_xsmoney)
        TextView tvXsmoney;
        @Bind(R.id.tv_tcmoney)
        TextView tvTcmoney;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

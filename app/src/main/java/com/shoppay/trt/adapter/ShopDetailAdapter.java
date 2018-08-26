package com.shoppay.trt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.trt.R;
import com.shoppay.trt.bean.OrderDetailMsg;
import com.shoppay.trt.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShopDetailAdapter extends BaseAdapter {
    private Context context;
    private List<OrderDetailMsg> list;
    private LayoutInflater inflater;

    public ShopDetailAdapter(Context context, List<OrderDetailMsg> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<OrderDetailMsg>();
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
            convertView = inflater.inflate(R.layout.item_shopxiaofeijilu, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final OrderDetailMsg home = list.get(position);
        vh.tvName.setText("品名："+home.GoodsName);
        vh.tvMoney.setText("￥" + StringUtil.twoNum(home.OrderDetailDiscountPrice));
        vh.tvNum.setText("数量：" + home.OrderDetailNumber);
        vh.tvPihao.setText("批号：" + home.BatchNumber);
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_num)
        TextView tvNum;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.tv_pihao)
        TextView tvPihao;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

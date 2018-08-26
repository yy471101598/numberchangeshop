package com.shoppay.trt.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.trt.MyApplication;
import com.shoppay.trt.R;
import com.shoppay.trt.bean.OrderDetailMsg;
import com.shoppay.trt.bean.XiaofeiRecordNew;
import com.shoppay.trt.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class YinpianDetailAdapter extends BaseAdapter {
    private Context context;
    private List<OrderDetailMsg> list;
    private LayoutInflater inflater;

    public YinpianDetailAdapter(Context context, List<OrderDetailMsg> list) {
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
            convertView = inflater.inflate(R.layout.item_yinpianjilu, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final OrderDetailMsg home = list.get(position);
        vh.tvName.setText(home.GoodsName);
        vh.tvMoney.setText("￥" + StringUtil.twoNum(home.OrderDetailDiscountPrice));
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_money)
        TextView tvMoney;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

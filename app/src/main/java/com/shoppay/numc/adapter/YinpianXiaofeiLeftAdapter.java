package com.shoppay.numc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppay.numc.R;
import com.shoppay.numc.bean.YinpianMsg;

import java.util.ArrayList;
import java.util.List;

public class YinpianXiaofeiLeftAdapter extends BaseAdapter {
    private Context context;
    private List<YinpianMsg> list;
    private LayoutInflater inflater;

    public YinpianXiaofeiLeftAdapter(Context context, List<YinpianMsg> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<YinpianMsg>();
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
            convertView = inflater.inflate(R.layout.item_balanceleft, null);
            vh = new ViewHolder();
            vh.tv_name = (TextView) convertView
                    .findViewById(R.id.tv);
            vh.tv_num = (TextView) convertView
                    .findViewById(R.id.tv_num);
            vh.rl_item = (RelativeLayout) convertView
                    .findViewById(R.id.rl_item);
            vh.rl_num = (RelativeLayout) convertView
                    .findViewById(R.id.rl_num);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.rl_num.setVisibility(View.GONE);
        final YinpianMsg home = list.get(position);
        vh.tv_name.setText(home.GoodsName);
        return convertView;
    }

    class ViewHolder {
        TextView tv_name, tv_num;
        RelativeLayout rl_item, rl_num;
    }
}

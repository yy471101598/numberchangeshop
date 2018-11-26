package com.shoppay.numcgshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.nbean.HomeMsg;
import com.shoppay.numcgshop.tools.PreferenceHelper;
import com.shoppay.numcgshop.view.GridviewItemLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeshopAdapter extends BaseAdapter {
    private Context context;
    private List<HomeMsg> list;
    private LayoutInflater inflater;

    public HomeshopAdapter(Context context, List<HomeMsg> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<HomeMsg>();
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
            convertView = inflater.inflate(R.layout.item_homeshop, null);
            vh = new ViewHolder();
            vh.iv_img = (ImageView) convertView
                    .findViewById(R.id.iv_item);
            vh.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_item);
            vh.rl_bg=convertView.findViewById(R.id.rl_bg);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final HomeMsg home = list.get(position);
        if (PreferenceHelper.readString(context, "numc", "lagavage", "zh").equals("zh")) {
            vh.tv_name.setText(home.Title);
        } else {
            vh.tv_name.setText(home.EnTitle);
        }
        vh.iv_img.setBackgroundResource(obtainIconId(home.Icon));
        return convertView;
    }

    public int obtainIconId(String icon) {
        int iconId = R.mipmap.icon_01;
        switch (icon.toLowerCase()) {
            case "icon_01.png":
                iconId = R.mipmap.icon_01;
                break;
            case "icon_02.png":
                iconId = R.mipmap.icon_02;
                break;
            case "icon_03.png":
                iconId = R.mipmap.icon_03;
                break;
            case "icon_04.png":
                iconId = R.mipmap.icon_04;
                break;
            case "icon_05.png":
                iconId = R.mipmap.icon_05;
                break;
            case "icon_06.png":
                iconId = R.mipmap.icon_06;
                break;
            case "icon_07.png":
                iconId = R.mipmap.icon_07;
                break;
            case "icon_08.png":
                iconId = R.mipmap.icon_08;
                break;
            case "icon_09.png":
                iconId = R.mipmap.icon_09;
                break;
            case "icon_10.png":
                iconId = R.mipmap.icon_10;
                break;
            case "icon_11.png":
                iconId = R.mipmap.icon_11;
                break;
            case "icon_12.png":
                iconId = R.mipmap.icon_12;
                break;
            case "icon_13.png":
                iconId = R.mipmap.icon_13;
                break;
            case "icon_14.png":
                iconId = R.mipmap.icon_14;
                break;
            case "icon_15.png":
                iconId = R.mipmap.icon_15;
                break;
            case "icon_16.png":
                iconId = R.mipmap.icon_16;
                break;
            case "icon_17.png":
                iconId = R.mipmap.icon_17;
                break;
            case "icon_18.png":
                iconId = R.mipmap.icon_18;
                break;
            case "icon_19.png":
                iconId = R.mipmap.icon_19;
                break;
            case "icon_20.png":
                iconId = R.mipmap.icon_20;
                break;
            case "icon_21.png":
                iconId = R.mipmap.icon_21;
                break;
            case "icon_22.png":
                iconId = R.mipmap.icon_22;
                break;
            case "icon_23.png":
                iconId = R.mipmap.icon_23;
                break;
            case "icon_24.png":
                iconId = R.mipmap.icon_24;
                break;
            case "icon_25.png":
                iconId = R.mipmap.icon_25;
                break;
            case "icon_26.png":
                iconId = R.mipmap.icon_26;
                break;
            case "icon_27.png":
                iconId = R.mipmap.icon_27;
                break;
            case "icon_28.png":
                iconId = R.mipmap.icon_28;
                break;
            case "icon_29.png":
                iconId = R.mipmap.icon_29;
                break;
            case "icon_30.png":
                iconId = R.mipmap.icon_30;
                break;

        }
        return iconId;
    }

    class ViewHolder {
        ImageView iv_img;
        TextView tv_name;
        GridviewItemLayout rl_bg;
    }
}

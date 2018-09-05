package com.shoppay.numc.nadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoppay.numc.R;
import com.shoppay.numc.nbean.HomeMsg;
import com.shoppay.numc.tools.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends BaseAdapter {
    private Context context;
    private List<HomeMsg> list;
    private LayoutInflater inflater;

    public HomeAdapter(Context context, List<HomeMsg> list) {
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
            convertView = inflater.inflate(R.layout.item_home, null);
            vh = new ViewHolder();
            vh.iv_img = (ImageView) convertView
                    .findViewById(R.id.iv_item);
            vh.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_item);
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
        return R.mipmap.shopxiaofei;
    }

    class ViewHolder {
        ImageView iv_img;
        TextView tv_name;
    }
}

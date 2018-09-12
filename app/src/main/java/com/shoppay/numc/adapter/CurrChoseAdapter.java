package com.shoppay.numc.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.numc.R;
import com.shoppay.numc.bean.Dengji;
import com.shoppay.numc.nbean.Currency;
import com.shoppay.numc.tools.PreferenceHelper;

import java.util.List;

/**
 * Created by songxiaotao on 2017/7/2.
 */

public class CurrChoseAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<Currency> items;
    private Context context;

    public CurrChoseAdapter(Context context, List<Currency> items) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_timechose, null);
            holder = new ViewHolder();
            holder.tv_car = (TextView) convertView
                    .findViewById(R.id.item_carlist_car);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Currency item = items.get(position);
        if (PreferenceHelper.readString(context, "numc", "lagavage", "zh").equals("zh")) {
            holder.tv_car.setText(item.CurrencyName);
        } else {
            holder.tv_car.setText(item.EnCurrencyName);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tv_car;
    }
}

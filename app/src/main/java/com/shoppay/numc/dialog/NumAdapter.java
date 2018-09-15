package com.shoppay.numc.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppay.numc.R;
import com.shoppay.numc.bean.HomeNum;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by songxiaotao on 2017/8/16.
 */

public class NumAdapter extends BaseAdapter {
    private List<HomeNum> list;
    private Context context;
    private LayoutInflater inflater;

    public NumAdapter(Context context, List<HomeNum> list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null) {
            view = inflater.inflate(R.layout.item_numjianpan, null);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        HomeNum ts = list.get(i);
        if (ts.num.equals("*")||ts.num.equals("#")) {
            vh.rlNum.setVisibility(View.GONE);
            vh.rlTuxing.setVisibility(View.VISIBLE);
            vh.tvTuxing.setText(ts.num);
        } else {
            vh.rlNum.setVisibility(View.VISIBLE);
            vh.rlTuxing.setVisibility(View.GONE);
            vh.tvNum.setText(ts.num);
            vh.tvEn.setText(ts.ennum);
        }
        return view;
    }


    static class ViewHolder {
        @Bind(R.id.tv_tuxing)
        TextView tvTuxing;
        @Bind(R.id.rl_tuxing)
        RelativeLayout rlTuxing;
        @Bind(R.id.tv_num)
        TextView tvNum;
        @Bind(R.id.tv_en)
        TextView tvEn;
        @Bind(R.id.rl_num)
        RelativeLayout rlNum;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

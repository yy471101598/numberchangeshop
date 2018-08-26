package com.shoppay.trt.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppay.trt.R;
import com.shoppay.trt.bean.YinpianMsg;

import java.util.ArrayList;
import java.util.List;

public class YinpianXiaofeiRightAdapter extends BaseAdapter {
    private Context context;
    private List<YinpianMsg> list;
    private LayoutInflater inflater;
    private Handler handler;

    public YinpianXiaofeiRightAdapter(Context context, List<YinpianMsg> list, Handler handler) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<YinpianMsg>();
        } else {
            this.list = list;
        }
        inflater = LayoutInflater.from(context);
        this.handler = handler;
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
//        if (convertView == null) {
        convertView = inflater.inflate(R.layout.item_yinpian, null);
        vh = new ViewHolder();
        vh.tv_name = (TextView) convertView
                .findViewById(R.id.tv_name);
        vh.et_money = (EditText) convertView
                .findViewById(R.id.et_money);
        vh.img_delete = (RelativeLayout) convertView
                .findViewById(R.id.img_delete);
        convertView.setTag(vh);
//        } else {
        vh = (ViewHolder) convertView.getTag();
//        }
        final YinpianMsg home = list.get(position);
        vh.tv_name.setText(home.GoodsName);
        if(!home.money.equals("")){
            vh.et_money.setText(home.money);
        }
        vh.et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(".")) {
                    home.money = editable.toString();
                    Message msg = handler.obtainMessage();
                    msg.what = 22;
                    msg.obj = home;
                    handler.sendMessage(msg);
                }
            }
        });
        vh.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = handler.obtainMessage();
                msg.what = 33;
                msg.obj = home;
                handler.sendMessage(msg);
            }
        });
        return convertView;
    }
    class ViewHolder {
        TextView tv_name;
        EditText et_money;
        RelativeLayout img_delete;
    }
}

package com.shoppay.numcgshop.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.bean.HomeNum;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.tools.NoDoubleClickListener;
import com.shoppay.numcgshop.tools.ToastUtils;
import com.shoppay.numcgshop.view.MyGridViews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/9 0009.
 */

public class PwdDialog {

    public static Dialog pwdDialog(final Context context, final String pwd,
                                   int showingLocation, final InterfaceBack handle) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_pwd, null);
        RelativeLayout rl_cancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        RelativeLayout rl_confirm = (RelativeLayout) view.findViewById(R.id.rl_confirm);
        RelativeLayout rl_retrest = (RelativeLayout) view.findViewById(R.id.rl_retrest);
        final TextView et_pwd = (TextView) view.findViewById(R.id.pwd_et_pwd);
        MyGridViews gridview = (MyGridViews) view.findViewById(R.id.gridview_num);
        NumAdapter adapter = new NumAdapter(context, obtainNum());
        gridview.setAdapter(adapter);
        final StringBuffer numsb = new StringBuffer();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        numsb.append("1");
                        break;
                    case 1:
                        numsb.append("2");
                        break;
                    case 2:
                        numsb.append("3");
                        break;
                    case 3:
                        numsb.append("4");
                        break;
                    case 4:
                        numsb.append("5");
                        break;
                    case 5:
                        numsb.append("6");
                        break;
                    case 6:
                        numsb.append("7");
                        break;
                    case 7:
                        numsb.append("8");
                        break;
                    case 8:
                        numsb.append("9");
                        break;
                    case 9:
                        break;
                    case 10:
                        numsb.append("0");
                        break;
                    case 11:

                        break;
                }
                //键盘赋值
                if (numsb.length() > 0) {
                    et_pwd.setText(numsb.toString());
                } else {
                    et_pwd.setText("");
                }
            }
        });


        dialog = new Dialog(context, R.style.DialogNotitle1);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth - 100, LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.show();
        rl_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_pwd.getText().toString().equals(pwd)) {
                    handle.onResponse(et_pwd.getText().toString());
                    dialog.dismiss();
                } else {
                    ToastUtils.showToast(context, context.getResources().getString(R.string.pwdno));
                }
            }
        });
        rl_cancel.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                handle.onErrorResponse("");
                dialog.dismiss();
            }
        });
        rl_retrest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numsb.length() > 0) {
                    numsb.delete(numsb.length() - 1, numsb.length());
                }
                if (numsb.length() > 0) {
                    et_pwd.setText(numsb.toString());
                } else {
                    et_pwd.setText("");
                }
            }
        });
        Window window = dialog.getWindow();
        switch (showingLocation) {
            case 0:
                window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
                break;
            case 1:
                window.setGravity(Gravity.CENTER);
                break;
            case 2:
                window.setGravity(Gravity.BOTTOM);
                break;
            case 3:
                WindowManager.LayoutParams params = window.getAttributes();
                dialog.onWindowAttributesChanged(params);
                params.x = screenWidth - dip2px(context, 100);// 设置x坐标
                params.gravity = Gravity.TOP;
                params.y = dip2px(context, 45);// 设置y坐标
                Log.d("xx", params.y + "");
                window.setGravity(Gravity.TOP);
                window.setAttributes(params);
                break;
            default:
                window.setGravity(Gravity.CENTER);
                break;
        }
        return dialog;
    }

    public static List<HomeNum> obtainNum() {
        List<HomeNum> list = new ArrayList<>();
        HomeNum h01 = new HomeNum();
        h01.num = "1";
        h01.ennum = "one";
        list.add(h01);
        HomeNum h02 = new HomeNum();
        h02.num = "2";
        h02.ennum = "two";
        list.add(h02);
        HomeNum h03 = new HomeNum();
        h03.num = "3";
        h03.ennum = "three";
        list.add(h03);
        HomeNum h04 = new HomeNum();
        h04.num = "4";
        h04.ennum = "four";
        list.add(h04);
        HomeNum h05 = new HomeNum();
        h05.num = "5";
        h05.ennum = "five";
        list.add(h05);
        HomeNum h06 = new HomeNum();
        h06.num = "6";
        h06.ennum = "six";
        list.add(h06);
        HomeNum h07 = new HomeNum();
        h07.num = "7";
        h07.ennum = "seven";
        list.add(h07);
        HomeNum h08 = new HomeNum();
        h08.num = "8";
        h08.ennum = "eight";
        list.add(h08);
        HomeNum h09 = new HomeNum();
        h09.num = "9";
        h09.ennum = "nine";
        list.add(h09);
        HomeNum h = new HomeNum();
        h.num = "*";
        list.add(h);
        HomeNum h0 = new HomeNum();
        h0.num = "0";
        h0.ennum="zero";
        list.add(h0);
        HomeNum h1 = new HomeNum();
        h1.num = "#";
        list.add(h1);
        return list;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

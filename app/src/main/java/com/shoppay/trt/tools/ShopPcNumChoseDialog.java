package com.shoppay.trt.tools;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppay.trt.R;
import com.shoppay.trt.bean.ShopChose;
import com.shoppay.trt.http.InterfaceBack;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class ShopPcNumChoseDialog {
    public static int shopnum = 0;

    public static Dialog numchoseDialog(final Context context,
                                        int showingLocation, final String name, final int num, final int maxnum, final String type, final String pihao, final InterfaceBack handler) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_addshop, null);
        final RelativeLayout iv_del = (RelativeLayout) view.findViewById(R.id.item_iv_delpm);
        final RelativeLayout iv_add = (RelativeLayout) view.findViewById(R.id.item_iv_addpm);
        final EditText et_num = (EditText) view.findViewById(R.id.item_tv_numpm);
        final TextView tv_name = (TextView) view.findViewById(R.id.tv_pinming);
        final EditText et_pihao = (EditText) view.findViewById(R.id.et_pihao);
        final ImageView rl_cancle = (ImageView) view.findViewById(R.id.img_delete);
        final RelativeLayout rl_confirm = (RelativeLayout) view.findViewById(R.id.rl_confirm);
        dialog = new Dialog(context, R.style.DialogNotitle1);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth - 60, LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.show();
        shopnum = num;
        tv_name.setText(name);
        et_num.setText(num + "");
        et_num.setSelection(String.valueOf(num).length());
        if (!pihao.equals("")) {
            et_pihao.setText(pihao);
        }
        Log.d("xxmax", maxnum + "--------" + type);
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shopnum > 0) {
                    shopnum = shopnum - 1;
                    et_num.setText(shopnum + "");
                    et_num.setSelection(String.valueOf(shopnum).length());
                }
            }
        });
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopnum = shopnum + 1;
                if (type.equals("1")) {
                    et_num.setText(shopnum + "");
                    et_num.setSelection(String.valueOf(shopnum).length());
                } else {
                    if (shopnum > maxnum) {
                        shopnum = maxnum;
                        et_num.setText(shopnum + "");
                        et_num.setSelection(String.valueOf(shopnum).length());
                        Toast.makeText(context, "该商品的最大库存量为" + maxnum, Toast.LENGTH_SHORT).show();
                    } else {
                        et_num.setText(shopnum + "");
                        et_num.setSelection(String.valueOf(shopnum).length());
                    }
                }
            }
        });

        rl_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                handler.onErrorResponse("");
            }
        });
        rl_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("1")) {
                    if (Integer.parseInt(et_num.getText().toString()) > maxnum) {
                        et_num.setText(maxnum + "");
                        et_num.setSelection(String.valueOf(shopnum).length());
                        Toast.makeText(context, "该商品的最大库存量为" + maxnum, Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        ShopChose chose = new ShopChose();
                        chose.num = et_num.getText().toString();
                        chose.pihao = et_pihao.getText().toString();
                        handler.onResponse(chose);
                    }
                } else {
                    dialog.dismiss();
                    ShopChose chose = new ShopChose();
                    chose.num = et_num.getText().toString();
                    chose.pihao = et_pihao.getText().toString();
                    handler.onResponse(chose);
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

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


}

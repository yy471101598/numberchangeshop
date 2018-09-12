package com.shoppay.numc.dialog;

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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.shoppay.numc.R;
import com.shoppay.numc.adapter.DengjiChoseAdapter;
import com.shoppay.numc.bean.Dengji;
import com.shoppay.numc.http.InterfaceBack;

import java.util.List;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by Administrator on 2018/9/9 0009.
 */

public class CurrChoseDialog {
    public static String chose = "";

    public static void currChoseDialog(final Context context, String[] times,
                                    final int showingLocation, final InterfaceBack handler) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_currencychose, null);
        RelativeLayout rl_cancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);
        RelativeLayout rl_confirm = (RelativeLayout) view.findViewById(R.id.rl_confirm);
        NumberPickerView picker = (NumberPickerView) view.findViewById(R.id.picker);
        chose = times[0];
        picker.refreshByNewDisplayedValues(times);
        picker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                String[] content = picker.getDisplayedValues();
                chose = content[newVal - picker.getMinValue()];
            }
        });
        dialog = new Dialog(context, R.style.DialogNotitle1);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.show();

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        rl_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.onResponse(chose);
                dialog.dismiss();
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
                params.x = 10;// 设置x坐标
                params.gravity = Gravity.TOP;
                params.y = 100;// 设置y坐标
                Log.d("xx", params.y + "");
                window.setGravity(Gravity.BOTTOM);
                window.setAttributes(params);
                break;
            default:
                window.setGravity(Gravity.CENTER);
                break;
        }
    }
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

package com.shoppay.numc.tools;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.shoppay.numc.R;
import com.shoppay.numc.adapter.ShopCarAdapter;
import com.shoppay.numc.bean.ShopCar;

import java.util.List;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class ShopCarDialog {

    public static Dialog numchoseDialog(final Context context,
                                        int showingLocation, List<ShopCar> carlist) {

        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_shopcar, null);
        final ListView listview = (ListView) view.findViewById(R.id.listview);
        dialog = new Dialog(context, R.style.DialogNotitle1);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        ShopCarAdapter adapter = new ShopCarAdapter(context, carlist);
        listview.setAdapter(adapter);
        dialog.show();
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
            case 4:
                //设置Dialog从窗体底部弹出
                window.setGravity(Gravity.BOTTOM);

                WindowManager.LayoutParams lp = window.getAttributes();
                lp.y = dip2px(context, 51);//设置Dialog距离底部的距离
                //宽度填满
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                //将属性设置给窗体
                window.setAttributes(lp);
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

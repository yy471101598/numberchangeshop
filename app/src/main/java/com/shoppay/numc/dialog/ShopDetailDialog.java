package com.shoppay.numc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.shoppay.numc.R;
import com.shoppay.numc.adapter.ShopDetailAdapter;
import com.shoppay.numc.bean.OrderDetailMsg;

import java.util.List;

/**
 * Created by Administrator on 2018/8/26 0026.
 */

public class ShopDetailDialog {
    /**
     * 时间选择
     *
     * @param context
     * @param showingLocation 0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
     */
    public static void shopDetailDialog(final Context context, final List<OrderDetailMsg> list,
                                        int showingLocation) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_xiaofeijilunew, null);
        ListView listview = (ListView) view.findViewById(R.id.listview);
        ImageView img = (ImageView) view.findViewById(R.id.img_close);
        ShopDetailAdapter carListAdapter = new ShopDetailAdapter(context, list);
        listview.setAdapter(carListAdapter);
        if (list.size() > 3) {
            ViewGroup.LayoutParams params = listview.getLayoutParams();

            params.height = dip2px(context, 280);
            listview.setLayoutParams(params);


        }
        dialog = new Dialog(context, R.style.DialogNotitle1);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth - dip2px(context, 60), LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.show();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}

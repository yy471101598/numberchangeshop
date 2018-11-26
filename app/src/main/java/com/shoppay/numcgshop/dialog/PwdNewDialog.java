package com.shoppay.numcgshop.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.tools.ToastUtils;

/**
 * Created by Administrator on 2018/9/9 0009.
 */

public class PwdNewDialog {

    public static Dialog pwdDialog(final Context context, final String pwd,
                                   int showingLocation, final InterfaceBack handle) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_pwd, null);
        RelativeLayout rl_confirm = (RelativeLayout) view.findViewById(R.id.pwd_rl_confirm);
        final EditText et_pwd = (EditText) view.findViewById(R.id.pwd_et_pwd);

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
                if(et_pwd.getText().toString().equals(pwd)) {
                    handle.onResponse(et_pwd.getText().toString());
                    dialog.dismiss();
                }else{
                    ToastUtils.showToast(context,context.getResources().getString(R.string.pwdno));
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

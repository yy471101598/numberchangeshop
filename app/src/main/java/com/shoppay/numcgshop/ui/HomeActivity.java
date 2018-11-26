package com.shoppay.numcgshop.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.nadapter.HomeAdapter;
import com.shoppay.numcgshop.nbean.HomeMsg;
import com.shoppay.numcgshop.tools.ActivityStack;
import com.shoppay.numcgshop.tools.DialogUtil;
import com.shoppay.numcgshop.tools.SysUtil;
import com.shoppay.numcgshop.ui.web.CenterWebActivity;
import com.shoppay.numcgshop.view.MyGridViews;

import java.util.List;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class HomeActivity extends BaseActivity {
    private MyGridViews gridViews;
    private List<HomeMsg> list;
    private HomeAdapter adapter;
    private Activity ac;
    private Dialog dialog;
    private long firstTime = 0;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ac = this;
        dialog = DialogUtil.loadingDialog(ac, 1);
        gridViews = (MyGridViews) findViewById(R.id.gridview);
        img = (ImageView) findViewById(R.id.imgview);
        list = (List<HomeMsg>) getIntent().getSerializableExtra("list");
        setimg();
        adapter = new HomeAdapter(ac, list);
        gridViews.setAdapter(adapter);
        gridViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeMsg home = (HomeMsg) adapterView.getItemAtPosition(i);
                if (home.Url.equals("")) {
                    switch (home.Code) {
                        case "F01":
                            Intent recharge = new Intent(ac, VipRechargeActivity.class);
                            recharge.putExtra("title", home.Title);
                            recharge.putExtra("entitle", home.EnTitle);
                            startActivity(recharge);
                            break;
                        case "F02":
                            Intent duihuan = new Intent(ac, FabiDuihuanActivity.class);
                            duihuan.putExtra("title", home.Title);
                            duihuan.putExtra("entitle", home.EnTitle);
                            startActivity(duihuan);
                            break;
                        case "F03":
                            Intent fbzz = new Intent(ac, FabiZhuanzhangActivity.class);
                            fbzz.putExtra("title", home.Title);
                            fbzz.putExtra("entitle", home.EnTitle);
                            startActivity(fbzz);
                            break;
                        case "F04":
                            Intent dc = new Intent(ac, FabiDingcunActivity.class);
                            dc.putExtra("title", home.Title);
                            dc.putExtra("entitle", home.EnTitle);
                            startActivity(dc);
                            break;
                        case "F05":
                            Intent qx = new Intent(ac, FabiQuxianActivity.class);
                            qx.putExtra("title", home.Title);
                            qx.putExtra("entitle", home.EnTitle);
                            startActivity(qx);
                            break;
                        case "F06":
                            Intent dxf = new Intent(ac, ZhidianXiaofeiActivity.class);
                            dxf.putExtra("title", home.Title);
                            dxf.putExtra("entitle", home.EnTitle);
                            startActivity(dxf);
                            break;
                        case "F07":
                            Intent drg = new Intent(ac, ZhidianRengouActivity.class);
                            drg.putExtra("title", home.Title);
                            drg.putExtra("entitle", home.EnTitle);
                            startActivity(drg);
                            break;
                        case "F08":
                            Intent ddc = new Intent(ac, ZhidianDingcunActivity.class);
                            ddc.putExtra("title", home.Title);
                            ddc.putExtra("entitle", home.EnTitle);
                            startActivity(ddc);
                            break;

                        case "F09":
                            Intent ddh = new Intent(ac, ZhidianDuihuanActivity.class);
                            ddh.putExtra("title", home.Title);
                            ddh.putExtra("entitle", home.EnTitle);
                            startActivity(ddh);
                            break;

                        case "F10":
                            Intent dzz = new Intent(ac, ZhidianZhuanzhangActivity.class);
                            dzz.putExtra("title", home.Title);
                            dzz.putExtra("entitle", home.EnTitle);
                            startActivity(dzz);
                            break;
                        case "F11":
                            Intent ddk = new Intent(ac, ZhidianDaikuanActivity.class);
                            ddk.putExtra("title", home.Title);
                            ddk.putExtra("entitle", home.EnTitle);
                            startActivity(ddk);
                            break;
                    }
                } else {
                    Intent web1 = new Intent(ac, CenterWebActivity.class);
                    web1.putExtra("title", home.Title);
                    web1.putExtra("entitle", home.EnTitle);
                    web1.putExtra("typeid", home.TypeID + "");
                    web1.putExtra("url", home.Url);
                    startActivity(web1);
                }

            }
        });

    }

    private void setimg() {
        DisplayMetrics disMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
        int width = disMetrics.widthPixels;
        int height = disMetrics.heightPixels;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.banner);//link the drable image
        SysUtil.setImageBackground(bitmap, img, width, dip2px(ac, 230));
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            long secndTime = System.currentTimeMillis();
            if (secndTime - firstTime > 3000) {
                firstTime = secndTime;
                Toast.makeText(ac, "再按一次退出", Toast.LENGTH_LONG)
                        .show();
            } else {
                ActivityStack.create().AppExit(ac);
            }
            return true;
        }
        return false;
    }

}

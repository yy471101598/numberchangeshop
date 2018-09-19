package com.shoppay.numc.ui;

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

import com.shoppay.numc.R;
import com.shoppay.numc.nadapter.HomeAdapter;
import com.shoppay.numc.nbean.HomeMsg;
import com.shoppay.numc.tools.ActivityStack;
import com.shoppay.numc.tools.DialogUtil;
import com.shoppay.numc.tools.SysUtil;
import com.shoppay.numc.ui.web.CenterWebActivity;
import com.shoppay.numc.view.MyGridViews;

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
                switch (home.Title) {
                    case "会员充值":
                        Intent recharge = new Intent(ac, VipRechargeActivity.class);
                        recharge.putExtra("title",home.Title);
                        recharge.putExtra("entitle",home.EnTitle);
                        startActivity(recharge);
                        break;
                    case "法币兑换":
                        Intent duihuan = new Intent(ac, FabiDuihuanActivity.class);
                        duihuan.putExtra("title",home.Title);
                        duihuan.putExtra("entitle",home.EnTitle);
                        startActivity(duihuan);
                        break;
                    case "法币转帐":
                        Intent fbzz = new Intent(ac, FabiZhuanzhangActivity.class);
                        fbzz.putExtra("title",home.Title);
                        fbzz.putExtra("entitle",home.EnTitle);
                        startActivity(fbzz);
                        break;
                    case "法币定存":
                        Intent dc = new Intent(ac, FabiDingcunActivity.class);
                        dc.putExtra("title",home.Title);
                        dc.putExtra("entitle",home.EnTitle);
                        startActivity(dc);
                        break;
                    case "法币取现":
                        Intent qx = new Intent(ac, FabiQuxianActivity.class);
                        qx.putExtra("title",home.Title);
                        qx.putExtra("entitle",home.EnTitle);
                        startActivity(qx);
                        break;
                    case "指点消费":
                        Intent dxf = new Intent(ac, ZhidianXiaofeiActivity.class);
                        dxf.putExtra("title",home.Title);
                        dxf.putExtra("entitle",home.EnTitle);
                        startActivity(dxf);
                        break;
                    case "指点认购":
                        Intent drg = new Intent(ac, ZhidianRengouActivity.class);
                        drg.putExtra("title",home.Title);
                        drg.putExtra("entitle",home.EnTitle);
                        startActivity(drg);
                        break;
                    case "指点定存":
                        Intent ddc = new Intent(ac, ZhidianDingcunActivity.class);
                        ddc.putExtra("title",home.Title);
                        ddc.putExtra("entitle",home.EnTitle);
                        startActivity(ddc);
                        break;

                    case "指点兑换":
                        Intent ddh = new Intent(ac, ZhidianDuihuanActivity.class);
                        ddh.putExtra("title",home.Title);
                        ddh.putExtra("entitle",home.EnTitle);
                        startActivity(ddh);
                        break;

                    case "指点转帐":
                        Intent dzz = new Intent(ac, ZhidianZhuanzhangActivity.class);
                        dzz.putExtra("title",home.Title);
                        dzz.putExtra("entitle",home.EnTitle);
                        startActivity(dzz);
                        break;
                    case "指点贷款":
                        Intent ddk = new Intent(ac, ZhidianDaikuanActivity.class);
                        ddk.putExtra("title",home.Title);
                        ddk.putExtra("entitle",home.EnTitle);
                        startActivity(ddk);
                        break;
                    case "法币汇率":
                        Intent web = new Intent(ac, CenterWebActivity.class);
                        web.putExtra("title",home.Title);
                        web.putExtra("entitle",home.EnTitle);
                        web.putExtra("typeid",home.TypeID+"");
                        web.putExtra("url",home.Url);
                        startActivity(web);
                        break;
                    case "查询":
                        Intent web1 = new Intent(ac, CenterWebActivity.class);
                        web1.putExtra("title",home.Title);
                        web1.putExtra("entitle",home.EnTitle);
                        web1.putExtra("typeid",home.TypeID+"");
                        web1.putExtra("url",home.Url);
                        startActivity(web1);
                        break;
                }


            }
        });

    }

    private void setimg() {
        DisplayMetrics disMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
        int width = disMetrics.widthPixels;
        int height = disMetrics.heightPixels;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.banner);//link the drable image
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

package com.shoppay.numcgshop.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.adapter.HomeshopAdapter;
import com.shoppay.numcgshop.nbean.HomeMsg;
import com.shoppay.numcgshop.tools.ActivityStack;
import com.shoppay.numcgshop.tools.DialogUtil;
import com.shoppay.numcgshop.tools.PreferenceHelper;
import com.shoppay.numcgshop.ui.web.CenterWebActivity;
import com.shoppay.numcgshop.view.HeaderGridView;

import java.util.List;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class HomeShopActivity extends BaseActivity {
    private HeaderGridView gridViews;
    private List<HomeMsg> list;
    private HomeshopAdapter adapter;
    private Activity ac;
    private Dialog dialog;
    private long firstTime = 0;
    private HomeMsg homeMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeshop);
        ac = this;
        dialog = DialogUtil.loadingDialog(ac, 1);
        gridViews = (HeaderGridView) findViewById(R.id.gridview);
        list = (List<HomeMsg>) getIntent().getSerializableExtra("list");
        homeMsg = (HomeMsg) getIntent().getSerializableExtra("home");
        View view = LayoutInflater.from(ac).inflate(R.layout.header_home, null);
        addHeaderMsg(view, homeMsg);
        gridViews.addHeaderView(view);
        list.remove(0);
        adapter = new HomeshopAdapter(ac, list);
        gridViews.setAdapter(adapter);
        gridViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeMsg home = (HomeMsg) adapterView.getItemAtPosition(i);
                if (null != home) {
                    if (home.Url.equals("")) {
                        switch (home.Code) {
                            case "F13":
                                Intent dxf1 = new Intent(ac, ZhimaXiaofeiActivity.class);
                                dxf1.putExtra("title", home.Title);
                                dxf1.putExtra("entitle", home.EnTitle);
                                startActivity(dxf1);
                                break;
                            case "F14":
                                Intent drg1 = new Intent(ac, ZhidianRengouShopActivity.class);
                                drg1.putExtra("title", home.Title);
                                drg1.putExtra("entitle", home.EnTitle);
                                startActivity(drg1);
                                break;
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
            }
        });

    }

    private void addHeaderMsg(View view, final HomeMsg homeMsg) {
        RelativeLayout rl_bg = view.findViewById(R.id.rl_headerbg);
        ImageView img_icon = view.findViewById(R.id.iv_item);
        TextView tv_name = view.findViewById(R.id.tv_item);
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tv_name.setText(homeMsg.Title);
        } else {
            tv_name.setText(homeMsg.EnTitle);
        }
        img_icon.setBackgroundResource(obtainIconId(homeMsg.Icon));
        rl_bg.setBackgroundColor(Color.parseColor(homeMsg.BgColor));
        rl_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dxf = new Intent(ac, ZhidianXiaofeiShopActivity.class);
                dxf.putExtra("title", homeMsg.Title);
                dxf.putExtra("entitle", homeMsg.EnTitle);
                startActivity(dxf);
            }
        });
    }
    public int obtainIconId(String icon) {
        int iconId = R.mipmap.icon_01;
        switch (icon.toLowerCase()) {
            case "icon_01.png":
                iconId = R.mipmap.icon_01;
                break;
            case "icon_02.png":
                iconId = R.mipmap.icon_02;
                break;
            case "icon_03.png":
                iconId = R.mipmap.icon_03;
                break;
            case "icon_04.png":
                iconId = R.mipmap.icon_04;
                break;
            case "icon_05.png":
                iconId = R.mipmap.icon_05;
                break;
            case "icon_06.png":
                iconId = R.mipmap.icon_06;
                break;
            case "icon_07.png":
                iconId = R.mipmap.icon_07;
                break;
            case "icon_08.png":
                iconId = R.mipmap.icon_08;
                break;
            case "icon_09.png":
                iconId = R.mipmap.icon_09;
                break;
            case "icon_10.png":
                iconId = R.mipmap.icon_10;
                break;
            case "icon_11.png":
                iconId = R.mipmap.icon_11;
                break;
            case "icon_12.png":
                iconId = R.mipmap.icon_12;
                break;
            case "icon_13.png":
                iconId = R.mipmap.icon_13;
                break;
            case "icon_14.png":
                iconId = R.mipmap.icon_14;
                break;
            case "icon_15.png":
                iconId = R.mipmap.icon_15;
                break;
            case "icon_16.png":
                iconId = R.mipmap.icon_16;
                break;
            case "icon_17.png":
                iconId = R.mipmap.icon_17;
                break;
            case "icon_18.png":
                iconId = R.mipmap.icon_18;
                break;
            case "icon_19.png":
                iconId = R.mipmap.icon_19;
                break;
            case "icon_20.png":
                iconId = R.mipmap.icon_20;
                break;
            case "icon_21.png":
                iconId = R.mipmap.icon_21;
                break;
            case "icon_22.png":
                iconId = R.mipmap.icon_22;
                break;
            case "icon_23.png":
                iconId = R.mipmap.icon_23;
                break;
            case "icon_24.png":
                iconId = R.mipmap.icon_24;
                break;
            case "icon_25.png":
                iconId = R.mipmap.icon_25;
                break;
            case "icon_26.png":
                iconId = R.mipmap.icon_26;
                break;
            case "icon_27.png":
                iconId = R.mipmap.icon_27;
                break;
            case "icon_28.png":
                iconId = R.mipmap.icon_28;
                break;
            case "icon_29.png":
                iconId = R.mipmap.icon_29;
                break;
            case "icon_30.png":
                iconId = R.mipmap.icon_30;
                break;
            case "sicon_01.png":
                iconId = R.mipmap.sicon_01;
                break;
            case "sicon_02.png":
                iconId = R.mipmap.sicon_02;
                break;
            case "sicon_03.png":
                iconId = R.mipmap.sicon_03;
                break;
            case "sicon_04.png":
                iconId = R.mipmap.sicon_04;
                break;
            case "sicon_05.png":
                iconId = R.mipmap.sicon_05;
                break;
        }
        return iconId;
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

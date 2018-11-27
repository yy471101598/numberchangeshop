package com.shoppay.numcgshop.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.bean.ZhidianMsg;
import com.shoppay.numcgshop.card.ReadCardOptHander;
import com.shoppay.numcgshop.dialog.CurrChoseDialog;
import com.shoppay.numcgshop.dialog.PwdDialog;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.modle.ImpObtainRGFeilv;
import com.shoppay.numcgshop.modle.ImpObtainRGZhidianList;
import com.shoppay.numcgshop.modle.ImpObtainVipMsg;
import com.shoppay.numcgshop.modle.ImpObtainZDRGCurrency;
import com.shoppay.numcgshop.modle.ImpObtainZDRGId;
import com.shoppay.numcgshop.modle.ImpObtainZDRGYuemoney;
import com.shoppay.numcgshop.modle.ImpZDRengou;
import com.shoppay.numcgshop.nbean.Currency;
import com.shoppay.numcgshop.tools.ActivityStack;
import com.shoppay.numcgshop.tools.CommonUtils;
import com.shoppay.numcgshop.tools.DialogUtil;
import com.shoppay.numcgshop.tools.LogUtils;
import com.shoppay.numcgshop.tools.NoDoubleClickListener;
import com.shoppay.numcgshop.tools.PreferenceHelper;
import com.shoppay.numcgshop.tools.ToastUtils;
import com.shoppay.numcgshop.wxcode.MipcaActivityCapture;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class ZhidianRengouShopActivity extends BaseActivity {
    @Bind(R.id.img_left)
    ImageView imgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout rlRight;
    @Bind(R.id.viprecharge_tv_cardnum)
    TextView viprechargeTvCardnum;
    @Bind(R.id.viprecharge_et_cardnum)
    TextView viprechargeEtCardnum;
    @Bind(R.id.viprecharge_tv_name)
    TextView viprechargeTvName;
    @Bind(R.id.viprecharge_et_name)
    TextView viprechargeEtName;
    @Bind(R.id.viprecharge_tv_bizhong)
    TextView viprechargeTvBizhong;
    @Bind(R.id.viprecharge_et_bingzhong)
    TextView viprechargeEtBingzhong;
    @Bind(R.id.rl_fkcurchose)
    RelativeLayout rlFkcurchose;
    @Bind(R.id.viprecharge_tv_yue)
    TextView viprechargeTvYue;
    @Bind(R.id.viprecharge_et_yue)
    TextView viprechargeEtYue;
    @Bind(R.id.viprecharge_tv_dhbiz)
    TextView viprechargeTvDhbiz;
    @Bind(R.id.viprecharge_et_dhbizhong)
    TextView viprechargeEtDhbizhong;
    @Bind(R.id.rl_dhcurchose)
    RelativeLayout rlDhcurchose;
    @Bind(R.id.vip_tv_money)
    TextView vipTvMoney;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.consumption_rl_money)
    RelativeLayout consumptionRlMoney;
    @Bind(R.id.viprecharge_tv_huilv)
    TextView viprechargeTvHuilv;
    @Bind(R.id.viprecharge_et_huilv)
    TextView viprechargeEtHuilv;
    @Bind(R.id.viprecharge_tv_sxf)
    TextView viprechargeTvSxf;
    @Bind(R.id.viprecharge_et_sxf)
    TextView viprechargeEtSxf;
    @Bind(R.id.viprecharge_tv_xumoney)
    TextView viprechargeTvXumoney;
    @Bind(R.id.viprecharge_et_xumoney)
    TextView viprechargeEtXumoney;
    @Bind(R.id.viprecharge_rl_duihuan)
    RelativeLayout viprechargeRlDuihuan;
    private boolean isSuccess = false;
    private int vipid;
    private String pwd = "";
    private int currid = -1;
    private int dhzhidian = -1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        JSONObject jso = new JSONObject(msg.obj.toString());
                        vipid = jso.getInt("userid");
                        pwd = jso.getString("paypassword");
                        viprechargeEtName.setText(jso.getString("name"));
                        viprechargeEtCardnum.setText(jso.getString("bankcard"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isSuccess = true;
                    break;
                case 2:
                    viprechargeEtCardnum.setText("");
                    viprechargeEtName.setText("");
                    isSuccess = false;
                    break;
            }
        }
    };
    private Activity ac;
    private String editString;
    private String title, entitle;
    private String jisuanHuilv = "";
    private String xueditString;
    private boolean isHuilv = false;
    private List<Currency> fkCurrlist = new ArrayList<>();
    private List<ZhidianMsg> zdlist = new ArrayList<>();
    private String jisuanSxf="0";
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_zhidianrengoushop);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(ZhidianRengouShopActivity.this, 1);
        ActivityStack.create().addActivity(ZhidianRengouShopActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText(entitle);
        }
        initView();
        obtainRgCurr("no");
        obtainRgZhidian("no");
//        viprechargeEtCardnum.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (delayRun != null) {
//                    //每次editText有变化的时候，则移除上次发出的延迟线程
//                    handler.removeCallbacks(delayRun);
//                }
//                editString = editable.toString();
//
//                //延迟800ms，如果不再输入字符，则执行该线程的run方法
//
//                handler.postDelayed(delayRun, 800);
//            }
//        });


        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (currid == -1) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosefkcurr));
                } else if (dhzhidian == -1) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosergzhidian));
                } else if (!isHuilv) {
                    ToastUtils.showToast(ac, res.getString(R.string.rgflfalse));
                } else {
                    //计算所需金额
//                    所需金额（兑换金额* 计算汇率exchangeratetitle*（1+手续费率Poundage））
                    double sxf = CommonUtils.add(1.0, Double.parseDouble(jisuanSxf));
                    LogUtils.d("xxmoey", etMoney.getText().toString() + "-------" + jisuanHuilv);
                    String xu = CommonUtils.multiply(etMoney.getText().toString().equals("") ? "0" : etMoney.getText().toString(), jisuanHuilv);
                    double xumoney = Double.parseDouble(CommonUtils.multiply(xu, sxf + ""));
                    viprechargeEtXumoney.setText(CommonUtils.lasttwo(xumoney));
                }

            }
        });
    }

    private void obtainRgCurr(final String type) {
        ImpObtainZDRGCurrency currency = new ImpObtainZDRGCurrency();
        currency.obtainZDRGCurrency(ac, new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Currency>>() {
                }.getType();
                List<Currency> sllist = gson.fromJson(response.toString(), listType);
                fkCurrlist.addAll(sllist);
                if (type.equals("no")) {

                } else {
                    String[] tft = new String[fkCurrlist.size()];
                    for (int i = 0; i < fkCurrlist.size(); i++) {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            tft[i] = fkCurrlist.get(i).CurrencyName;
                        } else {
                            tft[i] = fkCurrlist.get(i).EnCurrencyName;
                        }
                    }
                    CurrChoseDialog.currChoseDialog(ZhidianRengouShopActivity.this, tft, 2, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            for (Currency curr : fkCurrlist) {
                                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                    if (curr.CurrencyName.equals(response.toString())) {
                                        currid = curr.CurrencyID;
                                    }
                                } else {
                                    if (curr.EnCurrencyName.equals(response.toString())) {
                                        currid = curr.CurrencyID;
                                    }
                                }
                            }
                            viprechargeEtBingzhong.setText(response.toString());
                            dialog.show();
                            ImpObtainZDRGYuemoney yue = new ImpObtainZDRGYuemoney();
                            yue.obtainCurrency(ZhidianRengouShopActivity.this, vipid, currid, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dialog.dismiss();
                                    viprechargeEtYue.setText(response.toString());
                                }

                                @Override
                                public void onErrorResponse(Object msg) {
                                    viprechargeEtYue.setText("");
                                    dialog.dismiss();
                                }
                            });


                        }

                        @Override
                        public void onErrorResponse(Object msg) {

                        }
                    });

                }
            }

            @Override
            public void onErrorResponse(Object msg) {
                if (type.equals("no")) {

                } else {
                    Toast.makeText(ac, ac.getResources().getString(R.string.zdlistfalse), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void obtainRgZhidian(final String type) {
        ImpObtainRGZhidianList rgzd = new ImpObtainRGZhidianList();
        rgzd.obtainRGZdlist(ac, new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ZhidianMsg>>() {
                }.getType();
                List<ZhidianMsg> sllist = gson.fromJson(response.toString(), listType);
                zdlist.addAll(sllist);
                if (type.equals("no")) {

                } else {
                    String[] tft = new String[zdlist.size()];
                    for (int i = 0; i < zdlist.size(); i++) {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            tft[i] = zdlist.get(i).StockCodeName;
                        } else {
                            tft[i] = zdlist.get(i).EnStockCodeName;
                        }
                    }
                    CurrChoseDialog.currChoseDialog(ZhidianRengouShopActivity.this, tft, 2, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            for (ZhidianMsg curr : zdlist) {
                                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                    if (curr.StockCodeName.equals(response.toString())) {
                                        dhzhidian = curr.StockCodeID;
                                    }
                                } else {
                                    if (curr.EnStockCodeName.equals(response.toString())) {
                                        dhzhidian = curr.StockCodeID;
                                    }
                                }
                            }
                            viprechargeEtDhbizhong.setText(response.toString());
                            dialog.show();
                            ImpObtainRGFeilv feilv = new ImpObtainRGFeilv();
                            feilv.obtainDuihuanHuilv(ZhidianRengouShopActivity.this, currid, dhzhidian, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dialog.dismiss();
                                    try {
                                        JSONObject jso = new JSONObject(response.toString());
                                        viprechargeEtHuilv.setText(jso.getString("subscriberatetitle"));
                                        viprechargeEtSxf.setText(CommonUtils.multiply(jso.getString("Poundage"),"100")+"%");
                                        jisuanSxf=jso.getString("Poundage");
                                        jisuanHuilv = jso.getString("subscriberate");
                                        LogUtils.d("xxjisuan", jisuanHuilv);
                                        isHuilv = true;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorResponse(Object msg) {
                                    isHuilv = false;
                                    jisuanHuilv = "";
                                    viprechargeEtHuilv.setText("");
                                    viprechargeEtSxf.setText("");
                                    dialog.dismiss();
                                }
                            });


                        }

                        @Override
                        public void onErrorResponse(Object msg) {

                        }
                    });

                }
            }

            @Override
            public void onErrorResponse(Object msg) {
                if (type.equals("no")) {

                } else {
                    Toast.makeText(ac, ac.getResources().getString(R.string.zdlistfalse), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

//    /**
//     * 延迟线程，看是否还有下一个字符输入
//     */
//    private Runnable delayRun = new Runnable() {
//
//        @Override
//        public void run() {
//            //在这里调用服务器的接口，获取数据
//            ontainVipInfo();
//        }
//    };
//
//    private void ontainVipInfo() {
//        ImpObtainVipMsg vipmsg = new ImpObtainVipMsg();
//        vipmsg.obtainVipMsg(ZhidianRengouActivity.this, editString, new InterfaceBack() {
//            @Override
//            public void onResponse(Object response) {
//                Message msg = handler.obtainMessage();
//                msg.what = 1;
//                msg.obj = response;
//                handler.sendMessage(msg);
//            }
//
//            @Override
//            public void onErrorResponse(Object msg1) {
//                Message msg = handler.obtainMessage();
//                msg.what = 2;
//                handler.sendMessage(msg);
//            }
//        });
//    }


    private void initView() {
        rlFkcurchose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (isSuccess) {
                    if (fkCurrlist.size() == 0) {
                        obtainRgCurr("yes");
                    } else {
                        String[] tft = new String[fkCurrlist.size()];
                        for (int i = 0; i < fkCurrlist.size(); i++) {
                            if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                tft[i] = fkCurrlist.get(i).CurrencyName;
                            } else {
                                tft[i] = fkCurrlist.get(i).EnCurrencyName;
                            }
                        }
                        CurrChoseDialog.currChoseDialog(ZhidianRengouShopActivity.this, tft, 2, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                for (Currency curr : fkCurrlist) {
                                    if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                        if (curr.CurrencyName.equals(response.toString())) {
                                            currid = curr.CurrencyID;
                                        }
                                    } else {
                                        if (curr.EnCurrencyName.equals(response.toString())) {
                                            currid = curr.CurrencyID;
                                        }
                                    }
                                }
                                viprechargeEtBingzhong.setText(response.toString());
                                dialog.show();
                                //兑换指点选择后，又选择了付款币种
                                if (dhzhidian != -1) {
                                    //避免计算错误，每次选择清空输入金额
                                    etMoney.setText("");
                                    ImpObtainRGFeilv feilv = new ImpObtainRGFeilv();
                                    feilv.obtainDuihuanHuilv(ZhidianRengouShopActivity.this, currid, dhzhidian, new InterfaceBack() {
                                        @Override
                                        public void onResponse(Object response) {
                                            dialog.dismiss();
                                            try {
                                                JSONObject jso = new JSONObject(response.toString());
                                                isHuilv = true;
                                                viprechargeEtHuilv.setText(jso.getString("subscriberatetitle"));
                                                viprechargeEtSxf.setText(CommonUtils.multiply(jso.getString("Poundage"),"100")+"%");
                                                jisuanSxf=jso.getString("Poundage");
                                                jisuanHuilv = jso.getString("subscriberate");
                                                LogUtils.d("xxjisuan", jisuanHuilv);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onErrorResponse(Object msg) {
                                            isHuilv = false;
                                            jisuanHuilv = "";
                                            viprechargeEtHuilv.setText("");
                                            viprechargeEtSxf.setText("");
                                            dialog.dismiss();
                                        }
                                    });

                                }
                                ImpObtainZDRGYuemoney yue = new ImpObtainZDRGYuemoney();
                                yue.obtainCurrency(ZhidianRengouShopActivity.this, vipid, currid, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        dialog.dismiss();
                                        viprechargeEtYue.setText(response.toString());
                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {
                                        viprechargeEtYue.setText("");
                                        dialog.dismiss();
                                    }
                                });


                            }

                            @Override
                            public void onErrorResponse(Object msg) {

                            }
                        });
                    }
                } else {
                    ToastUtils.showToast(ac, res.getString(R.string.vipmsgfalse));
                }
            }
        });

        rlDhcurchose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (viprechargeEtBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosefkcurr),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (zdlist.size() == 0) {
                        obtainRgZhidian("yes");
                    } else {
                        String[] tft = new String[zdlist.size()];
                        for (int i = 0; i < zdlist.size(); i++) {
                            if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                tft[i] = zdlist.get(i).StockCodeName;
                            } else {
                                tft[i] = zdlist.get(i).EnStockCodeName;
                            }
                        }
                        CurrChoseDialog.currChoseDialog(ZhidianRengouShopActivity.this, tft, 2, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                for (ZhidianMsg curr : zdlist) {
                                    if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                        if (curr.StockCodeName.equals(response.toString())) {
                                            dhzhidian = curr.StockCodeID;
                                        }
                                    } else {
                                        if (curr.EnStockCodeName.equals(response.toString())) {
                                            dhzhidian = curr.StockCodeID;
                                        }
                                    }
                                }
                                viprechargeEtDhbizhong.setText(response.toString());
                                dialog.show();
                                ImpObtainRGFeilv feilv = new ImpObtainRGFeilv();
                                feilv.obtainDuihuanHuilv(ZhidianRengouShopActivity.this, currid, dhzhidian, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        dialog.dismiss();
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            isHuilv = true;
                                            viprechargeEtHuilv.setText(jso.getString("subscriberatetitle"));
                                            viprechargeEtSxf.setText(CommonUtils.multiply(jso.getString("Poundage"),"100")+"%");
                                            jisuanHuilv = jso.getString("subscriberate");
                                            jisuanSxf=jso.getString("Poundage");
                                            //避免计算错误，每次选择清空输入金额
                                            etMoney.setText("");
                                            LogUtils.d("xxjisuan", jisuanHuilv);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {
                                        isHuilv = false;
                                        jisuanHuilv = "";
                                        viprechargeEtHuilv.setText("");
                                        viprechargeEtSxf.setText("");
                                        dialog.dismiss();
                                    }
                                });


                            }

                            @Override
                            public void onErrorResponse(Object msg) {

                            }
                        });
                    }

                }
            }
        });

        rlRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (ContextCompat.checkSelfPermission(ac, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(ac, Manifest.permission.CAMERA)) {
                        ToastUtils.showToast(ac, "您已经拒绝过一次");
                    }
                    ActivityCompat.requestPermissions(ac, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST_CODE);
                } else {//有权限直接调用系统相机拍照
                    Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                    startActivityForResult(mipca, 111);
                }
            }
        });
        viprechargeRlDuihuan.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!isSuccess) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputvip),
                            Toast.LENGTH_SHORT).show();
                } else if (etMoney.getText().toString() == null || etMoney.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputrengounum),
                            Toast.LENGTH_SHORT).show();
                } else if (viprechargeEtBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosefkcurr),
                            Toast.LENGTH_SHORT).show();
                } else if (viprechargeEtDhbizhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosergzhidian),
                            Toast.LENGTH_SHORT).show();
                } else if (Double.parseDouble(etMoney.getText().toString()) > Double.parseDouble(viprechargeEtYue.getText().toString())) {
                    ToastUtils.showToast(ac, res.getString(R.string.dhbigyue));
                } else if (!isHuilv) {
                    ToastUtils.showToast(ac, res.getString(R.string.huilvfalse));
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        PwdDialog.pwdDialog(ZhidianRengouShopActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                dialog.show();
                                ImpObtainZDRGId duihuanid = new ImpObtainZDRGId();
                                duihuanid.obtainZDRGId(ZhidianRengouShopActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("subscribeid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ImpZDRengou zdrg = new ImpZDRengou();
                                        zdrg.zdRengou(ZhidianRengouShopActivity.this, dialog, rechargeid, vipid, pwd, currid, dhzhidian, etMoney.getText().toString(), new InterfaceBack() {
                                            @Override
                                            public void onResponse(Object response) {
                                                finish();
                                            }

                                            @Override
                                            public void onErrorResponse(Object msg) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {
                                        dialog.dismiss();
                                    }
                                });

                            }

                            @Override
                            public void onErrorResponse(Object msg) {

                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), res.getString(R.string.internet),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                    startActivityForResult(mipca, 111);
                } else {

                    ToastUtils.showToast(this, "请允许打开相机！！");
                }
                break;


            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
                    dialog.show();
                    ImpObtainVipMsg vipmsg = new ImpObtainVipMsg();
                    vipmsg.obtainVipMsg(ZhidianRengouShopActivity.this, data.getStringExtra("codedata"), new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            dialog.dismiss();
                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            msg.obj = response;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onErrorResponse(Object msg1) {
                            dialog.dismiss();
                            Message msg = handler.obtainMessage();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOptHander(new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                dialog.show();
                ImpObtainVipMsg vipmsg = new ImpObtainVipMsg();
                vipmsg.obtainVipMsg(ZhidianRengouShopActivity.this, response.toString(), new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        dialog.dismiss();
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onErrorResponse(Object msg1) {
                        dialog.dismiss();
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                });
            }

            @Override
            public void onErrorResponse(Object msg) {

            }
        });
    }

    @Override
    protected void onStop() {
        try {
            new ReadCardOptHander().overReadCard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onStop();
//        if (delayRun != null) {
//            //每次editText有变化的时候，则移除上次发出的延迟线程
//            handler.removeCallbacks(delayRun);
//        }
    }


    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    @OnClick(R.id.rl_left)
    public void onViewClicked() {
        ActivityStack.create().finishActivity(ZhidianRengouShopActivity.class);
    }

}

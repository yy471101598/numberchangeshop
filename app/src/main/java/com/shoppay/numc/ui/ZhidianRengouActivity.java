package com.shoppay.numc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppay.numc.R;
import com.shoppay.numc.card.ReadCardOpt;
import com.shoppay.numc.dialog.CurrChoseDialog;
import com.shoppay.numc.dialog.PwdDialog;
import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.modle.ImpFabiDuihuan;
import com.shoppay.numc.modle.ImpObtainDuihuanLulv;
import com.shoppay.numc.modle.ImpObtainFabiDuihuanId;
import com.shoppay.numc.modle.ImpObtainVipMsg;
import com.shoppay.numc.modle.ImpObtainYuemoney;
import com.shoppay.numc.nbean.Currency;
import com.shoppay.numc.nbean.PayType;
import com.shoppay.numc.tools.ActivityStack;
import com.shoppay.numc.tools.CommonUtils;
import com.shoppay.numc.tools.DialogUtil;
import com.shoppay.numc.tools.NoDoubleClickListener;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.tools.ToastUtils;
import com.shoppay.numc.wxcode.MipcaActivityCapture;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class ZhidianRengouActivity extends BaseActivity {
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
    EditText viprechargeEtCardnum;
    @Bind(R.id.viprecharge_tv_name)
    TextView viprechargeTvName;
    @Bind(R.id.viprecharge_et_name)
    TextView viprechargeEtName;
    @Bind(R.id.viprecharge_tv_bizhong)
    TextView viprechargeTvBizhong;
    @Bind(R.id.viprecharge_et_bingzhong)
    TextView viprechargeEtBingzhong;
    @Bind(R.id.viprecharge_tv_yue)
    TextView viprechargeTvYue;
    @Bind(R.id.viprecharge_et_yue)
    TextView viprechargeEtYue;
    @Bind(R.id.viprecharge_tv_dhbiz)
    TextView viprechargeTvDhbiz;
    @Bind(R.id.viprecharge_et_dhbizhong)
    TextView viprechargeEtDhbizhong;
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
    @Bind(R.id.rl_dhcurchose)
    RelativeLayout rlDhCurChose;
    @Bind(R.id.rl_fkcurchose)
    RelativeLayout rlFkCurChose;
    private boolean isSuccess = false;
    private int vipid;
    private String pwd = "";
    private int currid = -1;
    private int fkcurrid = -1;
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isSuccess = true;
                    break;
                case 2:
                    viprechargeEtName.setText("");
                    isSuccess = false;
                    break;
            }
        }
    };
    private Activity ac;
    private String editString;
    private PayType paytype;
    private String title, entitle;
    private String jisuanHuilv = "";
    private String xueditString;
    private boolean isHuilv = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_fabiduihuan);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(ZhidianRengouActivity.this, 1);
        ActivityStack.create().addActivity(ZhidianRengouActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText(entitle);
        }
        initView();
        viprechargeEtCardnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                editString = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法

                handler.postDelayed(delayRun, 800);
            }
        });


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
                    ToastUtils.showToast(ac, res.getString(R.string.chosedhcurr));
                } else if (fkcurrid == -1) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosefkcurr));
                } else if (!isHuilv) {
                    ToastUtils.showToast(ac, res.getString(R.string.huilvfalse));
                } else {
                    //计算所需金额
//                    所需金额（兑换金额* 计算汇率exchangeratetitle*（1+手续费率Poundage））
                    double sxf = CommonUtils.add(1.0, Double.parseDouble(viprechargeEtSxf.getText().toString()));
                    String xu = CommonUtils.multiply(etMoney.getText().toString().equals("") ? "0" : etMoney.getText().toString(), jisuanHuilv);
                    double xumoney = Double.parseDouble(CommonUtils.multiply(xu, sxf + ""));
                    viprechargeEtXumoney.setText(xumoney + "");
                }

            }
        });
    }

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            ontainVipInfo();
        }
    };

    private void ontainVipInfo() {
        ImpObtainVipMsg vipmsg = new ImpObtainVipMsg();
        vipmsg.obtainVipMsg(ZhidianRengouActivity.this, editString, new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = response;
                handler.sendMessage(msg);
            }

            @Override
            public void onErrorResponse(Object msg1) {
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }


    private void initView() {
        rlFkCurChose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (isSuccess) {
                    if (currlist.size() > 0) {
                        String[] tft = new String[currlist.size()];
                        for (int i = 0; i < currlist.size(); i++) {
                            if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                tft[i] = currlist.get(i).CurrencyName;
                            } else {
                                tft[i] = currlist.get(i).EnCurrencyName;
                            }
                        }
                        CurrChoseDialog.currChoseDialog(ZhidianRengouActivity.this, tft, 2, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                for (Currency curr : currlist) {
                                    if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                        if (curr.CurrencyName.equals(response.toString())) {
                                            fkcurrid = curr.CurrencyID;
                                        }
                                    } else {
                                        if (curr.EnCurrencyName.equals(response.toString())) {
                                            fkcurrid = curr.CurrencyID;
                                        }
                                    }
                                }
                                viprechargeEtBingzhong.setText(response.toString());
                                dialog.show();
                                if (currid != -1) {
                                    //获取汇率
                                    ImpObtainDuihuanLulv huilv = new ImpObtainDuihuanLulv();
                                    huilv.obtainDuihuanHuilv(ZhidianRengouActivity.this, fkcurrid, currid, new InterfaceBack() {
                                        @Override
                                        public void onResponse(Object response) {
                                            try {
                                                JSONObject jso = new JSONObject(response.toString());
                                                viprechargeEtHuilv.setText(jso.getString("exchangeratetitle"));
                                                viprechargeEtSxf.setText(jso.getString("Poundage"));
                                                jisuanHuilv = jso.getString("exchangerate");
                                                isHuilv = true;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                isHuilv = false;
                                            }

                                        }

                                        @Override
                                        public void onErrorResponse(Object msg) {
                                            viprechargeEtHuilv.setText("");
                                            viprechargeEtSxf.setText("");
                                            jisuanHuilv = "";
                                            isHuilv = false;
                                        }
                                    });
                                }
                                ImpObtainYuemoney yue = new ImpObtainYuemoney();
                                yue.obtainCurrency(ZhidianRengouActivity.this, vipid, fkcurrid, new InterfaceBack() {
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
                    } else {
                        ToastUtils.showToast(ac, res.getString(R.string.currno_chose));
                    }
                } else {
                    ToastUtils.showToast(ac, res.getString(R.string.vipmsgfalse));
                }
            }
        });


        rlDhCurChose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (currlist.size() > 0) {
                    String[] tft = new String[currlist.size()];
                    for (int i = 0; i < currlist.size(); i++) {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            tft[i] = currlist.get(i).CurrencyName;
                        } else {
                            tft[i] = currlist.get(i).EnCurrencyName;
                        }
                    }
                    CurrChoseDialog.currChoseDialog(ZhidianRengouActivity.this, tft, 2, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            for (Currency curr : currlist) {
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
                            viprechargeEtDhbizhong.setText(response.toString());


                            if (fkcurrid != -1) {
                                //获取汇率
                                dialog.show();
                                ImpObtainDuihuanLulv huilv = new ImpObtainDuihuanLulv();
                                huilv.obtainDuihuanHuilv(ZhidianRengouActivity.this, fkcurrid, currid, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        dialog.dismiss();
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            viprechargeEtHuilv.setText(jso.getString("exchangeratetitle"));
                                            viprechargeEtSxf.setText(jso.getString("Poundage"));
                                            jisuanHuilv = jso.getString("exchangerate");
                                            isHuilv = true;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            isHuilv = false;
                                        }

                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {
                                        dialog.dismiss();
                                        viprechargeEtHuilv.setText("");
                                        viprechargeEtSxf.setText("");
                                        jisuanHuilv = "";
                                        isHuilv = false;
                                    }
                                });
                            }
                        }

                        @Override
                        public void onErrorResponse(Object msg) {

                        }
                    });
                } else {
                    ToastUtils.showToast(ac, res.getString(R.string.currno_chose));
                }
            }
        });

        rlRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 111);
            }
        });
        viprechargeRlDuihuan.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!isSuccess) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputvip),
                            Toast.LENGTH_SHORT).show();
                } else if (etMoney.getText().toString() == null || etMoney.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputduihuanmoney),
                            Toast.LENGTH_SHORT).show();
                } else if (viprechargeEtBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosefkcurr),
                            Toast.LENGTH_SHORT).show();
                } else if (viprechargeEtDhbizhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosedhcurr),
                            Toast.LENGTH_SHORT).show();
                } else if (Double.parseDouble(etMoney.getText().toString()) > Double.parseDouble(viprechargeEtYue.getText().toString())) {
                    ToastUtils.showToast(ac, res.getString(R.string.dhbigyue));
                } else if (!isHuilv) {
                    ToastUtils.showToast(ac, res.getString(R.string.huilvfalse));
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        PwdDialog.pwdDialog(ZhidianRengouActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                ImpObtainFabiDuihuanId duihuanid = new ImpObtainFabiDuihuanId();
                                duihuanid.obtainFabiDuihuanId(ZhidianRengouActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("fepid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        dialog.show();
                                        ImpFabiDuihuan duihuan = new ImpFabiDuihuan();
                                        duihuan.fabiDuihuan(ZhidianRengouActivity.this, dialog, rechargeid, vipid, pwd, fkcurrid, currid, etMoney.getText().toString(), new InterfaceBack() {
                                            @Override
                                            public void onResponse(Object response) {
                                                ActivityStack.create().finishActivity(ZhidianRengouActivity.class);
                                            }

                                            @Override
                                            public void onErrorResponse(Object msg) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
                    viprechargeEtCardnum.setText(data.getStringExtra("codedata"));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(viprechargeEtCardnum);
    }

    @Override
    protected void onStop() {
        try {
            new ReadCardOpt().overReadCard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onStop();
        if (delayRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(delayRun);
        }
    }


    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    @OnClick(R.id.rl_left)
    public void onViewClicked() {
        ActivityStack.create().finishActivity(ZhidianRengouActivity.class);
    }

}

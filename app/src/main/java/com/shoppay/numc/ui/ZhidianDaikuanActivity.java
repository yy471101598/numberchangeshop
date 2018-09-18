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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppay.numc.R;
import com.shoppay.numc.bean.Cunqi;
import com.shoppay.numc.bean.ZhidianMsg;
import com.shoppay.numc.card.ReadCardOpt;
import com.shoppay.numc.dialog.CurrChoseDialog;
import com.shoppay.numc.dialog.PwdDialog;
import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.modle.ImpObtainDyZhidianList;
import com.shoppay.numc.modle.ImpObtainVipMsg;
import com.shoppay.numc.modle.ImpObtainZDDKCurrency;
import com.shoppay.numc.modle.ImpObtainZDDKId;
import com.shoppay.numc.modle.ImpObtainZDDKLilv;
import com.shoppay.numc.modle.ImpObtainZDYuemoney;
import com.shoppay.numc.modle.ImpObtainZhidianDcCunqi;
import com.shoppay.numc.modle.ImpObtainZhidianDkCunqi;
import com.shoppay.numc.modle.ImpZDDaikuan;
import com.shoppay.numc.nbean.Currency;
import com.shoppay.numc.tools.ActivityStack;
import com.shoppay.numc.tools.CommonUtils;
import com.shoppay.numc.tools.DialogUtil;
import com.shoppay.numc.tools.NoDoubleClickListener;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.tools.ToastUtils;
import com.shoppay.numc.wxcode.MipcaActivityCapture;

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

import static com.shoppay.numc.tools.CommonUtils.multiply;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class ZhidianDaikuanActivity extends BaseActivity {
    @Bind(R.id.img_left)
    ImageView mImgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout mRlLeft;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout mRlRight;
    @Bind(R.id.tv_cardnum)
    TextView mTvCardnum;
    @Bind(R.id.et_cardnum)
    EditText mEtCardnum;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.et_name)
    TextView mEtName;
    @Bind(R.id.tv_bizhong)
    TextView mTvBizhong;
    @Bind(R.id.et_bingzhong)
    TextView mEtBingzhong;
    @Bind(R.id.rl_dycurchose)
    RelativeLayout mRlDycurchose;
    @Bind(R.id.tv_dkqixian)
    TextView mTvDkqixian;
    @Bind(R.id.et_dkqixian)
    TextView mEtDkqixian;
    @Bind(R.id.rl_dkqixian)
    RelativeLayout mRlDkqixian;
    @Bind(R.id.tv_dkbiz)
    TextView mTvDkbiz;
    @Bind(R.id.et_dkbizhong)
    TextView mEtDkbizhong;
    @Bind(R.id.rl_dkcurchose)
    RelativeLayout mRlDkcurchose;
    @Bind(R.id.tv_yue)
    TextView mTvYue;
    @Bind(R.id.et_yue)
    TextView mEtYue;
    @Bind(R.id.vip_tv_money)
    TextView mVipTvMoney;
    @Bind(R.id.et_money)
    EditText mEtMoney;
    @Bind(R.id.consumption_rl_money)
    RelativeLayout mConsumptionRlMoney;
    @Bind(R.id.tv_huilv)
    TextView mTvHuilv;
    @Bind(R.id.et_dkmoney)
    TextView mEtDkmoney;
    @Bind(R.id.tv_sxf)
    TextView mTvSxf;
    @Bind(R.id.et_lilv)
    TextView mEtLilv;
    @Bind(R.id.tv_xumoney)
    TextView mTvXumoney;
    @Bind(R.id.et_mothhk)
    TextView mEtMothhk;
    @Bind(R.id.rl_duihuan)
    RelativeLayout mRlDuihuan;
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
                        mEtName.setText(jso.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isSuccess = true;
                    break;
                case 2:
                    mEtName.setText("");
                    isSuccess = false;
                    break;
            }
        }
    };
    private Activity ac;
    private String editString;
    private String title, entitle;
    private String jisuanHuilv = "";
    private String mortgagemoney = "";
    private String instalment = "";
    private String xueditString;
    private boolean isHuilv = false;
    private List<Currency> fkCurrlist = new ArrayList<>();
    private List<ZhidianMsg> zdlist = new ArrayList<>();
    private List<Cunqi> cunqilist = new ArrayList<>();
    private String cunqiId = "";

    @Override
    protected void
    onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_zhidiandaikuan);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(ZhidianDaikuanActivity.this, 1);
        ActivityStack.create().addActivity(ZhidianDaikuanActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            mTvTitle.setText(title);
        } else {
            mTvTitle.setText(entitle);
        }
        initView();
        obtainDyZhidian("no");
        mEtCardnum.addTextChangedListener(new TextWatcher() {
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


        mEtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (cunqiId.equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosedkqx));
                } else if (dhzhidian == -1) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosedyzd));
                } else if (currid == -1) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosedkcurr));
                } else if (!isHuilv) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosedkcurr));
                } else {
//                    double sxf = CommonUtils.add(1.0, Double.parseDouble(mEt.getText().toString()));
//                    LogUtils.d("xxmoey", mEtMoney.getText().toString() + "-------" + jisuanHuilv);
//                    String xu = CommonUtils.multiply(mEtMoney.getText().toString().equals("") ? "0" : mEtMoney.getText().toString(), jisuanHuilv);
//                    double xumoney = Double.parseDouble(CommonUtils.multiply(xu, sxf + ""));
//                    mEtMothhk.setText(CommonUtils.lasttwo(xumoney));
                    double lilv = CommonUtils.add(1.0, Double.parseDouble(jisuanHuilv));
                    String xu = multiply(multiply(mEtMoney.getText().toString().equals("") ? "0" : mEtMoney.getText().toString(), lilv + ""), mortgagemoney);
                    mEtDkmoney.setText(CommonUtils.lasttwo(Double.parseDouble(xu)));

                    double mo = CommonUtils.div(Double.parseDouble(CommonUtils.multiply(mEtMoney.getText().toString().equals("") ? "0" : mEtMoney.getText().toString(), mortgagemoney)), Double.parseDouble(instalment), 2);
                    String moth = CommonUtils.multiply(mo + "", lilv + "");
                    mEtMothhk.setText(CommonUtils.lasttwo(Double.parseDouble(moth)));
                }

            }
        });
    }

    private void obtainZdDkCurr(int Maturity) {
        dialog.show();
        ImpObtainZDDKCurrency currency = new ImpObtainZDDKCurrency();
        currency.obtainZDdkCurrency(ac, Maturity, new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Currency>>() {
                }.getType();
                List<Currency> sllist = gson.fromJson(response.toString(), listType);
                fkCurrlist.addAll(sllist);
                String[] tft = new String[fkCurrlist.size()];
                for (int i = 0; i < fkCurrlist.size(); i++) {
                    if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                        tft[i] = fkCurrlist.get(i).CurrencyName;
                    } else {
                        tft[i] = fkCurrlist.get(i).EnCurrencyName;
                    }
                }
                CurrChoseDialog.currChoseDialog(ZhidianDaikuanActivity.this, tft, 2, new InterfaceBack() {
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
                        mEtDkbizhong.setText(response.toString());

                        ImpObtainZDDKLilv lilv = new ImpObtainZDDKLilv();
                        lilv.obtainZDDKLilv(ZhidianDaikuanActivity.this, Integer.parseInt(cunqiId), currid, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                dialog.dismiss();
                                try {
                                    JSONObject jso = new JSONObject(response.toString());
                                    mEtLilv.setText(jso.getString("ratetitle"));
                                    jisuanHuilv = jso.getString("rate");
                                    mortgagemoney = jso.getString("mortgagemoney");
                                    instalment = jso.getString("instalment");
                                    //避免计算错误，每次选择清空输入金额
                                    mEtMoney.setText("");
                                    isHuilv = true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onErrorResponse(Object msg) {
                                isHuilv = false;
                                jisuanHuilv = "";
                                mortgagemoney = "";
                                instalment = "";
                                mEtLilv.setText("");
                                dialog.dismiss();
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

    }


    private void obtainDyZhidian(final String type) {
        ImpObtainDyZhidianList rgzd = new ImpObtainDyZhidianList();
        rgzd.obtainDyZdlist(ac, new InterfaceBack() {
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
                    CurrChoseDialog.currChoseDialog(ZhidianDaikuanActivity.this, tft, 2, new InterfaceBack() {
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
                            mEtBingzhong.setText(response.toString());
                            dialog.show();
                            ImpObtainZDYuemoney zdye = new ImpObtainZDYuemoney();
                            zdye.obtainCurrency(ZhidianDaikuanActivity.this, vipid, dhzhidian, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dialog.dismiss();
                                    mEtYue.setText(response.toString());
                                }

                                @Override
                                public void onErrorResponse(Object msg) {
                                    mEtYue.setText("");
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
        vipmsg.obtainVipMsg(ZhidianDaikuanActivity.this, editString, new InterfaceBack() {
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

    private void obtainCunqi(final String cuid) {
        dialog.show();
        ImpObtainZhidianDkCunqi cunqi = new ImpObtainZhidianDkCunqi();
        cunqi.obtainZddkCunqi(ac, cuid, new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                dialog.dismiss();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Cunqi>>() {
                }.getType();
                List<Cunqi> sllist = gson.fromJson(response.toString(), listType);
                cunqilist.addAll(sllist);
                String[] tft = new String[cunqilist.size()];
                for (int i = 0; i < cunqilist.size(); i++) {
                    if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                        tft[i] = cunqilist.get(i).MaturityName;
                    } else {
                        tft[i] = cunqilist.get(i).EnMaturityName;
                    }
                }
                CurrChoseDialog.currChoseDialog(ZhidianDaikuanActivity.this, tft, 2, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        for (Cunqi curr : cunqilist) {
                            if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                if (curr.MaturityName.equals(response.toString())) {
                                    cunqiId = curr.Maturity;
                                }
                            } else {
                                if (curr.EnMaturityName.equals(response.toString())) {
                                    cunqiId = curr.Maturity;
                                }
                            }
                        }
                        mEtDkqixian.setText(response.toString());


                        if (currid != -1) {
                            dialog.show();
                            ImpObtainZDDKLilv lilv = new ImpObtainZDDKLilv();
                            lilv.obtainZDDKLilv(ZhidianDaikuanActivity.this, Integer.parseInt(cunqiId), currid, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    try {
                                        JSONObject jso = new JSONObject(response.toString());
                                        dialog.dismiss();
                                        mEtLilv.setText(jso.getString("ratetitle"));
                                        jisuanHuilv = jso.getString("rate");
                                        mortgagemoney = jso.getString("mortgagemoney");
                                        instalment = jso.getString("instalment");
                                        //避免计算错误，每次选择清空输入金额
                                        mEtMoney.setText("");
                                        isHuilv = true;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorResponse(Object msg) {
                                    isHuilv = false;
                                    jisuanHuilv = "";
                                    mortgagemoney = "";
                                    instalment = "";
                                    mEtLilv.setText("");
                                    dialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onErrorResponse(Object msg) {

                    }
                });
            }

            @Override
            public void onErrorResponse(Object msg) {
                dialog.dismiss();
                Toast.makeText(ac, ac.getResources().getString(R.string.cunqifalse), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        mRlDycurchose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (isSuccess) {
                    if (zdlist.size() == 0) {
                        obtainDyZhidian("yes");
                    } else {
                        String[] tft = new String[zdlist.size()];
                        for (int i = 0; i < zdlist.size(); i++) {
                            if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                tft[i] = zdlist.get(i).StockCodeName;
                            } else {
                                tft[i] = zdlist.get(i).EnStockCodeName;
                            }
                        }
                        CurrChoseDialog.currChoseDialog(ZhidianDaikuanActivity.this, tft, 2, new InterfaceBack() {
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
                                mEtBingzhong.setText(response.toString());
                                dialog.show();
                                ImpObtainZDYuemoney zdye = new ImpObtainZDYuemoney();
                                zdye.obtainCurrency(ZhidianDaikuanActivity.this, vipid, dhzhidian, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        dialog.dismiss();
                                        mEtYue.setText(response.toString());
                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {
                                        mEtYue.setText("");
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
        mRlDkqixian.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (mEtBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosezhidian),
                            Toast.LENGTH_SHORT).show();
                } else {
                    obtainCunqi(currid + "");
                }
            }
        });
        mRlDkcurchose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (mEtBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosedyzd));
                } else if (mEtDkqixian.getText().toString().equals(res.getString(R.string.chose))) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosedkqx));
                } else {
                    obtainZdDkCurr(Integer.parseInt(cunqiId));
                }
            }
        });

        mRlRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 111);
            }
        });
        mRlDuihuan.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!isSuccess) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputvip),
                            Toast.LENGTH_SHORT).show();
                } else if (mEtMoney.getText().toString() == null || mEtMoney.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputdydiane),
                            Toast.LENGTH_SHORT).show();
                } else if (mEtBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosedyzd),
                            Toast.LENGTH_SHORT).show();
                } else if (mEtDkqixian.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosedkqx),
                            Toast.LENGTH_SHORT).show();
                } else if (mEtDkbizhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosedkcurr),
                            Toast.LENGTH_SHORT).show();
                } else if (Double.parseDouble(mEtMoney.getText().toString()) > Double.parseDouble(mEtYue.getText().toString())) {
                    ToastUtils.showToast(ac, res.getString(R.string.dydianebigyue));
                } else if (!isHuilv) {
                    ToastUtils.showToast(ac, res.getString(R.string.zddklilvfalse));
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        PwdDialog.pwdDialog(ZhidianDaikuanActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                dialog.show();
                                ImpObtainZDDKId duihuanid = new ImpObtainZDDKId();
                                duihuanid.obtainZDDKId(ZhidianDaikuanActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("loanid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ImpZDDaikuan zdrg = new ImpZDDaikuan();
                                        zdrg.zdDaikuan(ZhidianDaikuanActivity.this, dialog, rechargeid, vipid, pwd, Integer.parseInt(cunqiId), dhzhidian, currid, mEtMoney.getText().toString(), new InterfaceBack() {
                                            @Override
                                            public void onResponse(Object response) {
                                                ActivityStack.create().finishActivity(ZhidianDaikuanActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
                    mEtCardnum.setText(data.getStringExtra("codedata"));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(mEtCardnum);
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
        ActivityStack.create().finishActivity(ZhidianDaikuanActivity.class);
    }

}

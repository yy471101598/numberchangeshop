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
import com.shoppay.numc.card.ReadCardOpt;
import com.shoppay.numc.dialog.CurrChoseDialog;
import com.shoppay.numc.dialog.PwdDialog;
import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.modle.ImpFabiDingCun;
import com.shoppay.numc.modle.ImpFabiZhuanzhang;
import com.shoppay.numc.modle.ImpObtainCunqi;
import com.shoppay.numc.modle.ImpObtainCurrency;
import com.shoppay.numc.modle.ImpObtainDcLilv;
import com.shoppay.numc.modle.ImpObtainDingcunCurrency;
import com.shoppay.numc.modle.ImpObtainFabiDCId;
import com.shoppay.numc.modle.ImpObtainFabiZZId;
import com.shoppay.numc.modle.ImpObtainVipMsg;
import com.shoppay.numc.modle.ImpObtainYuemoney;
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

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class FabiDingcunActivity extends BaseActivity {


    @Bind(R.id.img_left)
    ImageView imgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout rlRight;
    @Bind(R.id.tv_cardnum)
    TextView tvCardnum;
    @Bind(R.id.et_cardnum)
    EditText etCardnum;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.et_name)
    TextView etName;
    @Bind(R.id.tv_bizhong)
    TextView tvBizhong;
    @Bind(R.id.et_bingzhong)
    TextView etBingzhong;
    @Bind(R.id.rl_currchose)
    RelativeLayout rlCurrchose;
    @Bind(R.id.tv_cunqi)
    TextView tvCunqi;
    @Bind(R.id.et_cunqi)
    TextView etCunqi;
    @Bind(R.id.rl_cunqichose)
    RelativeLayout rlCunqichose;
    @Bind(R.id.tv_lilv)
    TextView tvLilv;
    @Bind(R.id.et_lilv)
    TextView etLilv;
    @Bind(R.id.tv_yue)
    TextView tvYue;
    @Bind(R.id.et_yue)
    TextView etYue;
    @Bind(R.id.vip_tv_money)
    TextView vipTvMoney;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.consumption_rl_money)
    RelativeLayout consumptionRlMoney;
    @Bind(R.id.rl_dingcuntijiao)
    RelativeLayout rlDingcuntijiao;
    private boolean isSuccess = false;
    private boolean isLilvSuccess = false;
    private int vipid;
    private String pwd = "";
    private int currid;
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
                        etName.setText(jso.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isSuccess = true;
                    break;
                case 2:
                    etName.setText("");
                    isSuccess = false;
                    break;
            }
        }
    };
    private Activity ac;
    private String editString;
    private String title, entitle;
    private List<Currency> dccurrlist = new ArrayList<>();
    private List<Cunqi> cunqilist = new ArrayList<>();
    private String cunqiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_fabidingcun);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(FabiDingcunActivity.this, 1);
        ActivityStack.create().addActivity(FabiDingcunActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText(entitle);
        }
        obtainDcCurr("no");
        initView();


        etCardnum.addTextChangedListener(new TextWatcher() {
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
    }

    private void obtainCunqi(String currid) {
        dialog.show();
        ImpObtainCunqi cunqi = new ImpObtainCunqi();
        cunqi.obtainCunqi(ac, currid, new InterfaceBack() {
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
                CurrChoseDialog.currChoseDialog(FabiDingcunActivity.this, tft, 2, new InterfaceBack() {
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
                        etCunqi.setText(response.toString());
                        //获取定存利率
                        ImpObtainDcLilv lilv = new ImpObtainDcLilv();
                        lilv.obtainCunqi(ac, cunqiId, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {

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
                dialog.dismiss();
                Toast.makeText(ac, ac.getResources().getString(R.string.cunqifalse), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void obtainDcCurr(final String type) {
        ImpObtainDingcunCurrency currency = new ImpObtainDingcunCurrency();
        currency.obtainDcCurrency(ac, new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Currency>>() {
                }.getType();
                List<Currency> sllist = gson.fromJson(response.toString(), listType);
                dccurrlist.addAll(sllist);
                if (type.equals("no")) {

                } else {
                    String[] tft = new String[dccurrlist.size()];
                    for (int i = 0; i < dccurrlist.size(); i++) {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            tft[i] = dccurrlist.get(i).CurrencyName;
                        } else {
                            tft[i] = dccurrlist.get(i).EnCurrencyName;
                        }
                    }
                    CurrChoseDialog.currChoseDialog(FabiDingcunActivity.this, tft, 2, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            for (Currency curr : dccurrlist) {
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
                            etBingzhong.setText(response.toString());
                            dialog.show();
                            ImpObtainYuemoney yue = new ImpObtainYuemoney();
                            yue.obtainCurrency(FabiDingcunActivity.this, vipid, currid, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dialog.dismiss();
                                    etYue.setText(response.toString());
                                }

                                @Override
                                public void onErrorResponse(Object msg) {
                                    etYue.setText("");
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
                    Toast.makeText(ac, ac.getResources().getString(R.string.currno), Toast.LENGTH_SHORT).show();
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
        vipmsg.obtainVipMsg(FabiDingcunActivity.this, editString, new InterfaceBack() {
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
        rlCurrchose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (isSuccess) {
                    if (dccurrlist.size() == 0) {
                        obtainDcCurr("yes");
                    } else {
                        String[] tft = new String[dccurrlist.size()];
                        for (int i = 0; i < dccurrlist.size(); i++) {
                            if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                tft[i] = dccurrlist.get(i).CurrencyName;
                            } else {
                                tft[i] = dccurrlist.get(i).EnCurrencyName;
                            }
                        }
                        CurrChoseDialog.currChoseDialog(FabiDingcunActivity.this, tft, 2, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                for (Currency curr : dccurrlist) {
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
                                etBingzhong.setText(response.toString());
                                dialog.show();
                                ImpObtainYuemoney yue = new ImpObtainYuemoney();
                                yue.obtainCurrency(FabiDingcunActivity.this, vipid, currid, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        dialog.dismiss();
                                        etYue.setText(response.toString());
                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {
                                        etYue.setText("");
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

        rlCunqichose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (etBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosecurr),
                            Toast.LENGTH_SHORT).show();
                } else {
                    obtainCunqi(currid + "");
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
        rlDingcuntijiao.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!isSuccess) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputvip),
                            Toast.LENGTH_SHORT).show();
                } else if (etBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosecurr),
                            Toast.LENGTH_SHORT).show();
                } else if (etCunqi.getText().toString().equals(res.getString(R.string.chose))) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosecunqi));
                } else if (etMoney.getText().toString().equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.inputcunmoney));
                } else if (!isLilvSuccess) {
                    ToastUtils.showToast(ac, res.getString(R.string.lilvfalse));
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        PwdDialog.pwdDialog(FabiDingcunActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                ImpObtainFabiDCId zzid = new ImpObtainFabiDCId();
                                zzid.obtainFabiDCId(FabiDingcunActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("depositid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        dialog.show();
                                        ImpFabiDingCun fbzz = new ImpFabiDingCun();
                                        fbzz.fabiDingcun(FabiDingcunActivity.this, dialog, rechargeid, vipid, pwd, currid, cunqiId, etMoney.getText().toString(), new InterfaceBack() {
                                            @Override
                                            public void onResponse(Object response) {
                                                ActivityStack.create().finishActivity(FabiDingcunActivity.class);
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
                    etCardnum.setText(data.getStringExtra("codedata"));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(etCardnum);
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
        ActivityStack.create().finishActivity(FabiDingcunActivity.class);
    }
}
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppay.numc.R;
import com.shoppay.numc.bean.ZhidianMsg;
import com.shoppay.numc.card.ReadCardOpt;
import com.shoppay.numc.dialog.CurrChoseDialog;
import com.shoppay.numc.dialog.PwdDialog;
import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.modle.ImpObtainDingcunCurrency;
import com.shoppay.numc.modle.ImpObtainRechargeId;
import com.shoppay.numc.modle.ImpObtainVipMsg;
import com.shoppay.numc.modle.ImpObtainXFZhidianList;
import com.shoppay.numc.modle.ImpObtainYuemoney;
import com.shoppay.numc.modle.ImpObtainZDXiaofeiId;
import com.shoppay.numc.modle.ImpObtainZDYuemoney;
import com.shoppay.numc.modle.ImpVipRecharge;
import com.shoppay.numc.modle.ImpZDXiaofei;
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

public class ZhidianXiaofeiActivity extends BaseActivity {

    @Bind(R.id.viprecharge_rl_recharge)
    RelativeLayout viprechargeRlRecharge;
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
    @Bind(R.id.rl_curchose)
    RelativeLayout rlCurchose;
    @Bind(R.id.viprecharge_tv_yue)
    TextView viprechargeTvYue;
    @Bind(R.id.viprecharge_et_yue)
    TextView viprechargeEtYue;
    @Bind(R.id.vip_tv_money)
    TextView vipTvMoney;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.consumption_rl_money)
    RelativeLayout consumptionRlMoney;
    private boolean isSuccess = false;
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
    private String title, entitle;
    private List<ZhidianMsg> zdlist=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_zhidianxiaofei);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(ZhidianXiaofeiActivity.this, 1);
        ActivityStack.create().addActivity(ZhidianXiaofeiActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText(entitle);
        }

        initView();
        obtainXFzhidian("no");
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
    }

    private void obtainXFzhidian( final String type) {
        ImpObtainXFZhidianList currency = new ImpObtainXFZhidianList();
        currency.obtainCurrency(ac, new InterfaceBack() {
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
                    CurrChoseDialog.currChoseDialog(ZhidianXiaofeiActivity.this, tft, 2, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            for (ZhidianMsg curr : zdlist) {
                                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                    if (curr.StockCodeName.equals(response.toString())) {
                                        currid = curr.StockCodeID;
                                    }
                                } else {
                                    if (curr.EnStockCodeName.equals(response.toString())) {
                                        currid = curr.StockCodeID;
                                    }
                                }
                            }
                            viprechargeEtBingzhong.setText(response.toString());
                            dialog.show();
                            ImpObtainZDYuemoney yue = new ImpObtainZDYuemoney();
                            yue.obtainCurrency(ZhidianXiaofeiActivity.this, vipid, currid, new InterfaceBack() {
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
        vipmsg.obtainVipMsg(ZhidianXiaofeiActivity.this, editString, new InterfaceBack() {
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
        rlCurchose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (isSuccess) {
                    if (zdlist.size() == 0) {
                        obtainXFzhidian("yes");
                    } else {
                        String[] tft = new String[zdlist.size()];
                        for (int i = 0; i < zdlist.size(); i++) {
                            if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                tft[i] = zdlist.get(i).StockCodeName;
                            } else {
                                tft[i] = zdlist.get(i).EnStockCodeName;
                            }
                        }
                        CurrChoseDialog.currChoseDialog(ZhidianXiaofeiActivity.this, tft, 2, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                for (ZhidianMsg curr : zdlist) {
                                    if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                        if (curr.StockCodeName.equals(response.toString())) {
                                            currid = curr.StockCodeID;
                                        }
                                    } else {
                                        if (curr.EnStockCodeName.equals(response.toString())) {
                                            currid = curr.StockCodeID;
                                        }
                                    }
                                }
                                viprechargeEtBingzhong.setText(response.toString());
                                dialog.show();
                                ImpObtainZDYuemoney yue = new ImpObtainZDYuemoney();
                                yue.obtainCurrency(ZhidianXiaofeiActivity.this, vipid, currid, new InterfaceBack() {
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
        rlRight.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 111);
            }
        });
        viprechargeRlRecharge.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!isSuccess) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputvip),
                            Toast.LENGTH_SHORT).show();
                } else if (etMoney.getText().toString() == null || etMoney.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputxfzhidian),
                            Toast.LENGTH_SHORT).show();
                } else if (viprechargeEtBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosefkzd),
                            Toast.LENGTH_SHORT).show();
                } else if(etMoney.getText().toString().equals("")){
                    ToastUtils.showToast(ac,res.getString(R.string.inputxfzhidian));
                }else if (Double.parseDouble(etMoney.getText().toString())>Double.parseDouble(viprechargeEtYue.getText().toString())) {
                  ToastUtils.showToast(ac,res.getString(R.string.xfzdbigyue));
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        PwdDialog.pwdDialog(ZhidianXiaofeiActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                dialog.show();
                                ImpObtainZDXiaofeiId rechargeid = new ImpObtainZDXiaofeiId();
                                rechargeid.obtainZDXiaofeiId(ZhidianXiaofeiActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("consumeid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ImpZDXiaofei recharge = new ImpZDXiaofei();
                                        recharge.zdXiaofei(ZhidianXiaofeiActivity.this, dialog, rechargeid, vipid, pwd, currid,etMoney.getText().toString(), new InterfaceBack() {
                                            @Override
                                            public void onResponse(Object response) {
                                                ActivityStack.create().finishActivity(ZhidianXiaofeiActivity.class);
                                                //打印
//                                            if (jsonObject.getInt("printNumber") == 0) {
//                                                finish();
//                                            } else {
//                                                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                                                if (bluetoothAdapter.isEnabled()) {
//                                                    BluetoothUtil.connectBlueTooth(MyApplication.context);
//                                                    BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
//                                                    ActivityStack.create().finishActivity(VipRechargeActivity.class);
//                                                } else {
//                                                    ActivityStack.create().finishActivity(VipRechargeActivity.class);
//                                                }
//                                            }
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
        ActivityStack.create().finishActivity(ZhidianXiaofeiActivity.class);
    }

}

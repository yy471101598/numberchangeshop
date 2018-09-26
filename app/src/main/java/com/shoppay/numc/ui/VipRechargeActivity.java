package com.shoppay.numc.ui;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppay.numc.R;
import com.shoppay.numc.card.ReadCardOpt;
import com.shoppay.numc.dialog.CurrChoseDialog;
import com.shoppay.numc.dialog.PwdDialog;
import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.modle.ImpObtainRechargeId;
import com.shoppay.numc.modle.ImpObtainVipMsg;
import com.shoppay.numc.modle.ImpObtainYuemoney;
import com.shoppay.numc.modle.ImpVipRecharge;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class VipRechargeActivity extends BaseActivity {
    @Bind(R.id.rl_curchose)
    RelativeLayout rlCurChose;
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
    @Bind(R.id.rb_1)
    RadioButton rb1;
    @Bind(R.id.rb_2)
    RadioButton rb2;
    @Bind(R.id.rb_3)
    RadioButton rb3;
    @Bind(R.id.rb_4)
    RadioButton rb4;
    @Bind(R.id.radiogroup)
    RadioGroup radiogroup;
    @Bind(R.id.vip_tv_money)
    TextView vipTvMoney;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.consumption_rl_money)
    RelativeLayout consumptionRlMoney;
    @Bind(R.id.viprecharge_rl_recharge)
    RelativeLayout viprechargeRlRecharge;
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
    private PayType paytype;
    private String title, entitle;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_viprecharge);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(VipRechargeActivity.this, 1);
        ActivityStack.create().addActivity(VipRechargeActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText(entitle);
        }

        handlePayType(paylist);
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
    }

    private void handlePayType(List<PayType> paylist) {
        switch (paylist.size()) {
            case 1:
                rb1.setVisibility(View.VISIBLE);
                paytype = paylist.get(0);
                rb1.setChecked(true);
                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                    rb1.setText(paylist.get(0).PayTypeName);
                } else {
                    rb1.setText(paylist.get(0).EnPayTypeName);
                }
                break;
            case 2:
                rb1.setVisibility(View.VISIBLE);
                rb2.setVisibility(View.VISIBLE);
                paytype = paylist.get(0);
                rb1.setChecked(true);
                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                    rb1.setText(paylist.get(0).PayTypeName);
                    rb2.setText(paylist.get(1).PayTypeName);
                } else {
                    rb1.setText(paylist.get(0).EnPayTypeName);
                    rb2.setText(paylist.get(1).EnPayTypeName);
                }
                break;
            case 3:
                rb1.setVisibility(View.VISIBLE);
                rb2.setVisibility(View.VISIBLE);
                rb3.setVisibility(View.VISIBLE);
                rb1.setChecked(true);
                paytype = paylist.get(0);
                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                    rb1.setText(paylist.get(0).PayTypeName);
                    rb2.setText(paylist.get(1).PayTypeName);
                    rb3.setText(paylist.get(2).PayTypeName);
                } else {
                    rb1.setText(paylist.get(0).EnPayTypeName);
                    rb2.setText(paylist.get(1).EnPayTypeName);
                    rb3.setText(paylist.get(2).EnPayTypeName);
                }
                break;
            case 4:
                rb1.setVisibility(View.VISIBLE);
                rb2.setVisibility(View.VISIBLE);
                rb3.setVisibility(View.VISIBLE);
                rb4.setVisibility(View.VISIBLE);
                paytype = paylist.get(0);
                rb1.setChecked(true);
                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                    rb1.setText(paylist.get(0).PayTypeName);
                    rb2.setText(paylist.get(1).PayTypeName);
                    rb3.setText(paylist.get(2).PayTypeName);
                    rb4.setText(paylist.get(3).PayTypeName);
                } else {
                    rb1.setText(paylist.get(0).EnPayTypeName);
                    rb2.setText(paylist.get(1).EnPayTypeName);
                    rb3.setText(paylist.get(2).EnPayTypeName);
                    rb4.setText(paylist.get(3).EnPayTypeName);
                }
                break;
        }
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
        vipmsg.obtainVipMsg(VipRechargeActivity.this, editString, new InterfaceBack() {
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
        rlCurChose.setOnClickListener(new NoDoubleClickListener() {
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
                        CurrChoseDialog.currChoseDialog(VipRechargeActivity.this, tft, 2, new InterfaceBack() {
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
                                viprechargeEtBingzhong.setText(response.toString());
                                dialog.show();
                                ImpObtainYuemoney yue = new ImpObtainYuemoney();
                                yue.obtainCurrency(VipRechargeActivity.this, vipid, currid, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        dialog.dismiss();
                                        viprechargeEtYue.setText(response.toString());
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
                        ToastUtils.showToast(ac, res.getString(R.string.currno_chose));
                    }
                } else {
                    ToastUtils.showToast(ac, res.getString(R.string.vipmsgfalse));
                }
            }
        });
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_1:
                        paytype = paylist.get(0);
                        break;
                    case R.id.rb_2:
                        paytype = paylist.get(1);
                        break;
                    case R.id.rb_3:
                        paytype = paylist.get(2);
                        break;
                    case R.id.rb_4:
                        paytype = paylist.get(3);
                        break;
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
        viprechargeRlRecharge.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!isSuccess) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputvip),
                            Toast.LENGTH_SHORT).show();
                } else if (etMoney.getText().toString() == null || etMoney.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputmoney),
                            Toast.LENGTH_SHORT).show();
                } else if (viprechargeEtBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosecurr),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        PwdDialog.pwdDialog(VipRechargeActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                dialog.show();
                                ImpObtainRechargeId rechargeid = new ImpObtainRechargeId();
                                rechargeid.obtainRechargeId(VipRechargeActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("rechargeid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ImpVipRecharge recharge = new ImpVipRecharge();
                                        recharge.vipRecharge(VipRechargeActivity.this, dialog, rechargeid, vipid, pwd, currid, paytype.PayTypeID, etMoney.getText().toString(), new InterfaceBack() {
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
        ActivityStack.create().finishActivity(VipRechargeActivity.class);
    }

}

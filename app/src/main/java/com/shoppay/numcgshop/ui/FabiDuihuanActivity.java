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

import com.shoppay.numcgshop.R;
import com.shoppay.numcgshop.card.ReadCardOptHander;
import com.shoppay.numcgshop.dialog.CurrChoseDialog;
import com.shoppay.numcgshop.dialog.PwdDialog;
import com.shoppay.numcgshop.http.InterfaceBack;
import com.shoppay.numcgshop.modle.ImpFabiDuihuan;
import com.shoppay.numcgshop.modle.ImpObtainDuihuanLulv;
import com.shoppay.numcgshop.modle.ImpObtainFabiDuihuanId;
import com.shoppay.numcgshop.modle.ImpObtainVipMsg;
import com.shoppay.numcgshop.modle.ImpObtainYuemoney;
import com.shoppay.numcgshop.nbean.Currency;
import com.shoppay.numcgshop.nbean.PayType;
import com.shoppay.numcgshop.tools.ActivityStack;
import com.shoppay.numcgshop.tools.CommonUtils;
import com.shoppay.numcgshop.tools.DialogUtil;
import com.shoppay.numcgshop.tools.NoDoubleClickListener;
import com.shoppay.numcgshop.tools.PreferenceHelper;
import com.shoppay.numcgshop.tools.ToastUtils;
import com.shoppay.numcgshop.wxcode.MipcaActivityCapture;

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

public class FabiDuihuanActivity extends BaseActivity {
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
    private PayType paytype;
    private String title, entitle;
    private String jisuanHuilv = "";
    private String shouxufei = "";
    private String xueditString;
    private boolean isHuilv = false;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_fabiduihuan);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(FabiDuihuanActivity.this, 1);
        ActivityStack.create().addActivity(FabiDuihuanActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText(entitle);
        }
        initView();
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
                    ToastUtils.showToast(ac, res.getString(R.string.chosedhcurr));
                } else if (fkcurrid == -1) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosefkcurr));
                } else if (!isHuilv) {
                    ToastUtils.showToast(ac, res.getString(R.string.huilvfalse));
                } else {
                    //计算所需金额
//                    所需金额（兑换金额* 计算汇率exchangeratetitle*（1+手续费率Poundage））
                    double sxf = CommonUtils.add(1.0, Double.parseDouble(shouxufei));
                    String xu = CommonUtils.multiply(etMoney.getText().toString().equals("") ? "0" : etMoney.getText().toString(), jisuanHuilv);
                    double xumoney = Double.parseDouble(CommonUtils.multiply(xu, sxf + ""));
                    viprechargeEtXumoney.setText(CommonUtils.lasttwo(xumoney));
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
//        vipmsg.obtainVipMsg(FabiDuihuanActivity.this, editString, new InterfaceBack() {
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
                        CurrChoseDialog.currChoseDialog(FabiDuihuanActivity.this, tft, 2, new InterfaceBack() {
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
                                    huilv.obtainDuihuanHuilv(FabiDuihuanActivity.this, fkcurrid, currid, new InterfaceBack() {
                                        @Override
                                        public void onResponse(Object response) {
                                            try {
                                                JSONObject jso = new JSONObject(response.toString());
                                                isHuilv = true;
                                                viprechargeEtHuilv.setText(jso.getString("exchangeratetitle"));
                                                viprechargeEtSxf.setText(CommonUtils.lasttwo(Double.parseDouble(CommonUtils.multiply(jso.getString("Poundage"), "100"))) + "%");
                                                jisuanHuilv = jso.getString("exchangerate");
                                                shouxufei = jso.getString("Poundage");
                                                //避免计算错误，每次选择清空输入金额
                                                etMoney.setText("");
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
                                            shouxufei = "";
                                            isHuilv = false;
                                        }
                                    });
                                }
                                ImpObtainYuemoney yue = new ImpObtainYuemoney();
                                yue.obtainCurrency(FabiDuihuanActivity.this, vipid, fkcurrid, new InterfaceBack() {
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
                    CurrChoseDialog.currChoseDialog(FabiDuihuanActivity.this, tft, 2, new InterfaceBack() {
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
                                huilv.obtainDuihuanHuilv(FabiDuihuanActivity.this, fkcurrid, currid, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        dialog.dismiss();
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            isHuilv = true;
                                            viprechargeEtHuilv.setText(jso.getString("exchangeratetitle"));
                                            viprechargeEtSxf.setText(CommonUtils.lasttwo(Double.parseDouble(CommonUtils.multiply(jso.getString("Poundage"), "100"))) + "%");
                                            jisuanHuilv = jso.getString("exchangerate");
                                            shouxufei = jso.getString("Poundage");
                                            //避免计算错误，每次选择清空输入金额
                                            etMoney.setText("");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            isHuilv = false;
                                        }

                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {
                                        dialog.dismiss();
                                        isHuilv = false;
                                        viprechargeEtHuilv.setText("");
                                        viprechargeEtSxf.setText("");
                                        jisuanHuilv = "";
                                        shouxufei = "";
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
                        PwdDialog.pwdDialog(FabiDuihuanActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                dialog.show();
                                ImpObtainFabiDuihuanId duihuanid = new ImpObtainFabiDuihuanId();
                                duihuanid.obtainFabiDuihuanId(FabiDuihuanActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("fepid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ImpFabiDuihuan duihuan = new ImpFabiDuihuan();
                                        duihuan.fabiDuihuan(FabiDuihuanActivity.this, dialog, rechargeid, vipid, pwd, fkcurrid, currid, etMoney.getText().toString(), new InterfaceBack() {
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
                    vipmsg.obtainVipMsg(FabiDuihuanActivity.this, data.getStringExtra("codedata"), new InterfaceBack() {
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
                vipmsg.obtainVipMsg(FabiDuihuanActivity.this, response.toString(), new InterfaceBack() {
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
        ActivityStack.create().finishActivity(FabiDuihuanActivity.class);
    }

}

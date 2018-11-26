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
import com.shoppay.numcgshop.modle.ImpFabiZhuanzhang;
import com.shoppay.numcgshop.modle.ImpObtainFabiZZId;
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

public class FabiZhuanzhangActivity extends BaseActivity {

    @Bind(R.id.rl_recharge)
    RelativeLayout rlRecharge;
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
    TextView etCardnum;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.et_name)
    TextView etName;
    @Bind(R.id.tv_bizhong)
    TextView tvBizhong;
    @Bind(R.id.et_bingzhong)
    TextView etBingzhong;
    @Bind(R.id.rl_curchose)
    RelativeLayout rlCurchose;
    @Bind(R.id.tv_skhm)
    TextView tvSkhm;
    @Bind(R.id.et_skhm)
    EditText etSkhm;
    @Bind(R.id.tv_skaccount)
    TextView tvSkaccount;
    @Bind(R.id.et_skaccount)
    EditText etSkaccount;
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
    @Bind(R.id.tv_zzremark)
    TextView tvZzremark;
    @Bind(R.id.et_zzremark)
    EditText etZzremark;
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
                        etName.setText(jso.getString("name"));
                        etCardnum.setText(jso.getString("bankcard"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isSuccess = true;
                    break;
                case 2:
                    etCardnum.setText("");
                    etName.setText("");
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
        setContentView(R.layout.aactivity_fabizhuanzhang);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(FabiZhuanzhangActivity.this, 1);
        ActivityStack.create().addActivity(FabiZhuanzhangActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText(entitle);
        }
        initView();
//        etCardnum.addTextChangedListener(new TextWatcher() {
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
//        vipmsg.obtainVipMsg(FabiZhuanzhangActivity.this, editString, new InterfaceBack() {
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
        rlCurchose.setOnClickListener(new NoDoubleClickListener() {
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
                        CurrChoseDialog.currChoseDialog(FabiZhuanzhangActivity.this, tft, 2, new InterfaceBack() {
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
                                etBingzhong.setText(response.toString());
                                dialog.show();
                                ImpObtainYuemoney yue = new ImpObtainYuemoney();
                                yue.obtainCurrency(FabiZhuanzhangActivity.this, vipid, currid, new InterfaceBack() {
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
                    } else {
                        ToastUtils.showToast(ac, res.getString(R.string.currno_chose));
                    }
                } else {
                    ToastUtils.showToast(ac, res.getString(R.string.vipmsgfalse));
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
        rlRecharge.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!isSuccess) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.inputvip),
                            Toast.LENGTH_SHORT).show();
                } else if (etBingzhong.getText().toString().equals(res.getString(R.string.chose))) {
                    Toast.makeText(getApplicationContext(), res.getString(R.string.chosezzcurr),
                            Toast.LENGTH_SHORT).show();
                } else if (etSkhm.getText().toString().equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.inputskhuming));
                } else if (etSkaccount.getText().toString().equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.inputskaccount));
                } else if (etMoney.getText().toString().equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.inputzzmoney));
                } else if (Double.parseDouble(etMoney.getText().toString()) > Double.parseDouble(etYue.getText().toString())) {
                    ToastUtils.showToast(ac, res.getString(R.string.zzbigyue));
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        PwdDialog.pwdDialog(FabiZhuanzhangActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                dialog.show();
                                ImpObtainFabiZZId zzid = new ImpObtainFabiZZId();
                                zzid.obtainFabiZZId(FabiZhuanzhangActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("transferid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ImpFabiZhuanzhang fbzz = new ImpFabiZhuanzhang();
                                        fbzz.fabiZhuanzhang(FabiZhuanzhangActivity.this, dialog, rechargeid, vipid, pwd, currid, etSkhm.getText().toString(), etSkaccount.getText().toString(), etMoney.getText().toString(), etZzremark.getText().toString(), new InterfaceBack() {
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
                    vipmsg.obtainVipMsg(FabiZhuanzhangActivity.this, data.getStringExtra("codedata"), new InterfaceBack() {
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
                vipmsg.obtainVipMsg(FabiZhuanzhangActivity.this, response.toString(), new InterfaceBack() {
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
        ActivityStack.create().finishActivity(FabiZhuanzhangActivity.class);
    }
}

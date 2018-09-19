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
import com.shoppay.numc.bean.Country;
import com.shoppay.numc.bean.IDTyppe;
import com.shoppay.numc.card.ReadCardOpt;
import com.shoppay.numc.dialog.CurrChoseDialog;
import com.shoppay.numc.dialog.PwdDialog;
import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.modle.ImpFabiTixian;
import com.shoppay.numc.modle.ImpObtainCountry;
import com.shoppay.numc.modle.ImpObtainIDType;
import com.shoppay.numc.modle.ImpObtainQuxianId;
import com.shoppay.numc.modle.ImpObtainQxSxf;
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

public class FabiQuxianActivity extends BaseActivity {
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
    @Bind(R.id.tv_yue)
    TextView tvYue;
    @Bind(R.id.et_yue)
    TextView etYue;
    @Bind(R.id.tv_country)
    TextView tvCountry;
    @Bind(R.id.et_country)
    TextView etCountry;
    @Bind(R.id.rl_countrychose)
    RelativeLayout rlCountrychose;
    @Bind(R.id.tv_bank)
    TextView tvBank;
    @Bind(R.id.et_bank)
    EditText etBank;
    @Bind(R.id.tv_fenhang)
    TextView tvFenhang;
    @Bind(R.id.et_fenhang)
    EditText etFenhang;
    @Bind(R.id.tv_zhihang)
    TextView tvZhihang;
    @Bind(R.id.et_zhihang)
    EditText etZhihang;
    @Bind(R.id.tv_accout)
    TextView tvAccout;
    @Bind(R.id.et_accout)
    EditText etAccout;
    @Bind(R.id.tv_bankaccout)
    TextView tvBankaccout;
    @Bind(R.id.et_bankaccout)
    EditText etBankaccout;
    @Bind(R.id.tv_idtype)
    TextView tvIdtype;
    @Bind(R.id.et_idtype)
    TextView etIdtype;
    @Bind(R.id.rl_idtypechose)
    RelativeLayout rlIdtypechose;
    @Bind(R.id.tv_idnum)
    TextView tvIdnum;
    @Bind(R.id.et_idnum)
    EditText etIdnum;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.vip_tv_money)
    TextView vipTvMoney;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.consumption_rl_money)
    RelativeLayout consumptionRlMoney;
    @Bind(R.id.tv_sxf)
    TextView tvSxf;
    @Bind(R.id.et_sxf)
    TextView etSxf;
    @Bind(R.id.rl_dingcuntijiao)
    RelativeLayout rlDingcuntijiao;
    private boolean isSuccess = false;
    private boolean isSxf = false;
    private int vipid;
    private String pwd = "";
    private int currid;
    private int CountryID;
    private int typeId=-1;
    private boolean isIDtype=false;
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
    private List<Country> countrylist = new ArrayList<>();
    private List<IDTyppe> idtypelist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aactivity_fabiquxian);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(FabiQuxianActivity.this, 1);
        ActivityStack.create().addActivity(FabiQuxianActivity.this);
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText(entitle);
        }
        initView();
        obtainCountry("no");
        obtainIDtype("no");
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

    private void obtainIDtype(final String type) {
        ImpObtainIDType idtype = new ImpObtainIDType();
        idtype.obtainIDType(ac, new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<IDTyppe>>() {
                }.getType();
                List<IDTyppe> sllist = gson.fromJson(response.toString(), listType);
                idtypelist.addAll(sllist);
                if (type.equals("no")) {

                } else {
                    String[] tft = new String[idtypelist.size()];
                    for (int i = 0; i < idtypelist.size(); i++) {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            tft[i] = idtypelist.get(i).TypeName;
                        } else {
                            tft[i] = idtypelist.get(i).EnTypeName;
                        }
                    }
                    CurrChoseDialog.currChoseDialog(FabiQuxianActivity.this, tft, 2, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            for (IDTyppe curr : idtypelist) {
                                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                    if (curr.TypeName.equals(response.toString())) {
                                        typeId = curr.TypeID;
                                    }
                                } else {
                                    if (curr.EnTypeName.equals(response.toString())) {
                                        typeId = curr.TypeID;
                                    }
                                }
                            }
                            etIdtype.setText(response.toString());
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
                    Toast.makeText(ac, ac.getResources().getString(R.string.idtypefalse), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void obtainCountry(final String type) {
        ImpObtainCountry currency = new ImpObtainCountry();
        currency.obtainCountry(ac, new InterfaceBack() {
            @Override
            public void onResponse(Object response) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Country>>() {
                }.getType();
                List<Country> sllist = gson.fromJson(response.toString(), listType);
                countrylist.addAll(sllist);
                if (type.equals("no")) {

                } else {
                    String[] tft = new String[countrylist.size()];
                    for (int i = 0; i < countrylist.size(); i++) {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            tft[i] = countrylist.get(i).CountryName;
                        } else {
                            tft[i] = countrylist.get(i).EnCountryName;
                        }
                    }
                    CurrChoseDialog.currChoseDialog(FabiQuxianActivity.this, tft, 2, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            for (Country curr : countrylist) {
                                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                    if (curr.CountryName.equals(response.toString())) {
                                        CountryID = curr.CountryID;
                                    }
                                } else {
                                    if (curr.EnCountryName.equals(response.toString())) {
                                        CountryID = curr.CountryID;
                                    }
                                }
                            }
                            etCountry.setText(response.toString());
                            dialog.show();
                            ImpObtainQxSxf sxf = new ImpObtainQxSxf();
                            sxf.obtainQxSxf(FabiQuxianActivity.this, CountryID + "", new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dialog.dismiss();
                                    isSxf = true;
                                    etSxf.setText(response.toString());
                                }

                                @Override
                                public void onErrorResponse(Object msg) {
                                    etSxf.setText("");
                                    isSxf = false;
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
                    Toast.makeText(ac, ac.getResources().getString(R.string.countryfalse), Toast.LENGTH_SHORT).show();
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
        vipmsg.obtainVipMsg(FabiQuxianActivity.this, editString, new InterfaceBack() {
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

      rlIdtypechose.setOnClickListener(new NoDoubleClickListener() {
          @Override
          protected void onNoDoubleClick(View view) {
              if (idtypelist.size() == 0) {
                  obtainIDtype("yes");
              } else {
                  String[] tft = new String[idtypelist.size()];
                  for (int i = 0; i < idtypelist.size(); i++) {
                      if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                          tft[i] = idtypelist.get(i).TypeName;
                      } else {
                          tft[i] = idtypelist.get(i).EnTypeName;
                      }
                  }
                  CurrChoseDialog.currChoseDialog(FabiQuxianActivity.this, tft, 2, new InterfaceBack() {
                      @Override
                      public void onResponse(Object response) {
                          for (IDTyppe curr : idtypelist) {
                              if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                  if (curr.TypeName.equals(response.toString())) {
                                      typeId = curr.TypeID;
                                  }
                              } else {
                                  if (curr.EnTypeName.equals(response.toString())) {
                                      typeId = curr.TypeID;
                                  }
                              }
                          }
                          etIdtype.setText(response.toString());
                      }

                      @Override
                      public void onErrorResponse(Object msg) {

                      }
                  });
              }
          }
      });

        rlCountrychose.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (countrylist.size() == 0) {
                    obtainCountry("yes");
                } else {
                    String[] tft = new String[countrylist.size()];
                    for (int i = 0; i < countrylist.size(); i++) {
                        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                            tft[i] = countrylist.get(i).CountryName;
                        } else {
                            tft[i] = countrylist.get(i).EnCountryName;
                        }
                    }
                    CurrChoseDialog.currChoseDialog(FabiQuxianActivity.this, tft, 2, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            for (Country curr : countrylist) {
                                if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
                                    if (curr.CountryName.equals(response.toString())) {
                                        CountryID = curr.CountryID;
                                    }
                                } else {
                                    if (curr.EnCountryName.equals(response.toString())) {
                                        CountryID = curr.CountryID;
                                    }
                                }
                            }
                            etCountry.setText(response.toString());
                            dialog.show();
                            ImpObtainQxSxf sxf = new ImpObtainQxSxf();
                            sxf.obtainQxSxf(FabiQuxianActivity.this, CountryID + "", new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dialog.dismiss();
                                    isSxf = true;
                                    etSxf.setText(response.toString());
                                }

                                @Override
                                public void onErrorResponse(Object msg) {
                                    etSxf.setText("");
                                    isSxf = false;
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
        });

        rlCurrchose.setOnClickListener(new NoDoubleClickListener() {
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
                        CurrChoseDialog.currChoseDialog(FabiQuxianActivity.this, tft, 2, new InterfaceBack() {
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
                                yue.obtainCurrency(FabiQuxianActivity.this, vipid, currid, new InterfaceBack() {
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
                }else if (etCountry.getText().toString().equals(res.getString(R.string.chose))) {
                    ToastUtils.showToast(ac, res.getString(R.string.chosecountry));
                }  else if (etBank.getText().toString().equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.inputbankname));
                } else if (etAccout.getText().toString().equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.inputaccountname));
                } else if (etBankaccout.getText().toString().equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.inputbankaccount));
                }else if (etMoney.getText().toString().equals("")) {
                    ToastUtils.showToast(ac, res.getString(R.string.inputtxmoney));
                } else if (Double.parseDouble(etMoney.getText().toString()) > Double.parseDouble(etYue.getText().toString())) {
                    ToastUtils.showToast(ac, res.getString(R.string.txbigyue));
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        PwdDialog.pwdDialog(FabiQuxianActivity.this, pwd, 1, new InterfaceBack() {
                            @Override
                            public void onResponse(Object response) {
                                dialog.show();

                                ImpObtainQuxianId zzid = new ImpObtainQuxianId();
                                zzid.obtainQuxianId(FabiQuxianActivity.this, new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        int rechargeid = -1;
                                        try {
                                            JSONObject jso = new JSONObject(response.toString());
                                            rechargeid = jso.getInt("witdrawid");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ImpFabiTixian fbzz = new ImpFabiTixian();
                                        fbzz.fabiTixian(FabiQuxianActivity.this, dialog, rechargeid, vipid, pwd, currid,CountryID, etBank.getText().toString(), etFenhang.getText().toString(),etZhihang.getText().toString(),etAccout.getText().toString(), etBankaccout.getText().toString(),typeId,etIdnum.getText().toString(), etPhone.getText().toString(), etMoney.getText().toString(), new InterfaceBack() {
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
        ActivityStack.create().finishActivity(FabiQuxianActivity.class);
    }
}

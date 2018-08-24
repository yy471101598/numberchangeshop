package com.shoppay.trt;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.trt.adapter.YinpianXiaofeiLeftAdapter;
import com.shoppay.trt.adapter.YinpianXiaofeiRightAdapter;
import com.shoppay.trt.bean.Shop;
import com.shoppay.trt.bean.ShopCar;
import com.shoppay.trt.bean.SystemQuanxian;
import com.shoppay.trt.bean.VipInfo;
import com.shoppay.trt.bean.VipInfoMsg;
import com.shoppay.trt.bean.YinpianMsg;
import com.shoppay.trt.bean.Zhekou;
import com.shoppay.trt.card.ReadCardOpt;
import com.shoppay.trt.db.DBAdapter;
import com.shoppay.trt.http.InterfaceBack;
import com.shoppay.trt.tools.BluetoothUtil;
import com.shoppay.trt.tools.CommonUtils;
import com.shoppay.trt.tools.DateUtils;
import com.shoppay.trt.tools.DayinUtils;
import com.shoppay.trt.tools.DialogUtil;
import com.shoppay.trt.tools.LogUtils;
import com.shoppay.trt.tools.NoDoubleClickListener;
import com.shoppay.trt.tools.PreferenceHelper;
import com.shoppay.trt.tools.ShopXiaofeiDialog;
import com.shoppay.trt.tools.StringUtil;
import com.shoppay.trt.tools.UrlTools;
import com.shoppay.trt.wxcode.MipcaActivityCapture;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import static com.shoppay.trt.MyApplication.context;

/**
 * Created by Administrator on 2018/8/23 0023.
 */

public class YinpianXiaofeiActivity extends Activity {
    @Bind(R.id.img_left)
    ImageView imgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout rlRight;
    @Bind(R.id.tv_vip)
    TextView tvVip;
    @Bind(R.id.li_vip)
    LinearLayout liVip;
    @Bind(R.id.tv_san)
    TextView tvSan;
    @Bind(R.id.li_san)
    LinearLayout liSan;
    @Bind(R.id.tabBottomLine)
    ImageView tabBottomLine;
    @Bind(R.id.balance_tv_card)
    TextView balanceTvCard;
    @Bind(R.id.balance_et_card)
    EditText balanceEtCard;
    @Bind(R.id.balance_rl_card)
    RelativeLayout balanceRlCard;
    @Bind(R.id.balance_tv_vipname)
    TextView balanceTvVipname;
    @Bind(R.id.balance_rl_vipname)
    RelativeLayout balanceRlVipname;
    @Bind(R.id.balance_tv_vipdengji)
    TextView balanceTvVipdengji;
    @Bind(R.id.balance_rl_vipdengji)
    RelativeLayout balanceRlVipdengji;
    @Bind(R.id.balance_tv_vipyue)
    TextView balanceTvVipyue;
    @Bind(R.id.balance_rl_vipyue)
    RelativeLayout balanceRlVipyue;
    @Bind(R.id.balance_tv_vipjifen)
    TextView balanceTvVipjifen;
    @Bind(R.id.balance_rl_vipjifen)
    RelativeLayout balanceRlVipjifen;
    @Bind(R.id.li_vipmsg)
    LinearLayout liVipmsg;
    @Bind(R.id.tv_shopcode)
    TextView tvShopcode;
    @Bind(R.id.vip_tv_dingwei)
    TextView vipTvDingwei;
    @Bind(R.id.vip_tv_search)
    TextView vipTvSearch;
    @Bind(R.id.balance_et_shopcode)
    EditText balanceEtShopcode;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.listview_right)
    ListView listviewRight;
    @Bind(R.id.balance_tv_n)
    TextView balanceTvN;
    @Bind(R.id.balance_tv_num)
    TextView balanceTvNum;
    @Bind(R.id.balance_tv_m)
    TextView balanceTvM;
    @Bind(R.id.balance_tv_z)
    TextView balanceTvZ;
    @Bind(R.id.balance_tv_money)
    TextView balanceTvMoney;
    @Bind(R.id.balance_rl_jiesan)
    RelativeLayout balanceRlJiesan;
    @Bind(R.id.balance_rl_guadan)
    RelativeLayout balanceRlGuadan;
    @Bind(R.id.balance_rl_d)
    RelativeLayout balanceRlD;
    private Activity ac;
    private int tabWidth = 0;
    private int curTabIndex = 0;
    private String type = "否";
    private Dialog dialog;
    private Dialog paydialog;
    private MyApplication app;
    private boolean isSuccess = false;
    private String editString;
    private String shopString;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    balanceTvVipname.setText(info.getMemName());
                    balanceTvVipjifen.setText(info.getMemPoint());
                    balanceTvVipyue.setText(info.getMemMoney());
                    balanceTvVipdengji.setText(info.getLevelName());
                    PreferenceHelper.write(ac, "shoppay", "vipcar", balanceEtCard.getText().toString());
                    PreferenceHelper.write(ac, "shoppay", "vipname", balanceTvVipname.getText().toString());
                    PreferenceHelper.write(ac, "shoppay", "memid", info.getMemID());
                    PreferenceHelper.write(ac, "shoppay", "MemMoney", info.getMemMoney() + "");
                    PreferenceHelper.write(ac, "shoppay", "jifenall", info.getMemPoint());
                    isSuccess = true;
                    break;
                case 2:
                    balanceTvVipname.setText("");
                    balanceTvVipjifen.setText("");
                    balanceTvVipyue.setText("");
                    balanceTvVipdengji.setText("");
                    isSuccess = false;
                    break;
                case 3:
                    Zhekou zhekou = (Zhekou) msg.obj;
                    handlerShopMsg(zhekou);
                    break;
                case 4:
                    Toast.makeText(context, "获取商品信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case 22:
                    //饮片money修改
                    YinpianMsg ypmsg= (YinpianMsg) msg.obj;
                    for(int i=0;i<carlist.size();i++){
                        if(ypmsg.GoodsID.equals(carlist.get(i).GoodsID)){
                            //已存在
                            if(ypmsg.money.equals("")||Double.parseDouble(ypmsg.money)==0){
                                //money为空
                                carlist.remove(carlist.get(i));
                            }else{
                                carlist.get(i).money=ypmsg.money;
                            }
                        }else{
                            //不存在
                            if(ypmsg.money.equals("")||Double.parseDouble(ypmsg.money)==0){
                                //money为空
                            }else{
                                carlist.add(ypmsg);
                            }
                        }
                    }
                   double money=0;
                    //底部导航数据修改
                     for(int j=0;j<carlist.size();j++){
                         money=money+Double.parseDouble(carlist.get(j).money);
                     }
                    balanceTvNum.setText(carlist.size()+"");
                    balanceTvMoney.setText(StringUtil.twoNum(money+""));

                    break;
                case 33:
                    //饮片删除
                    YinpianMsg yp= (YinpianMsg) msg.obj;
                    for(int i=0;i<carlist.size();i++){
                        if(yp.GoodsID.equals(carlist.get(i).GoodsID)){
                            carlist.remove(carlist.get(i));
                            //底部导航数据修改
                            double money1=0;
                            for(int j=0;j<carlist.size();j++){
                                money1=money1+Double.parseDouble(carlist.get(j).money);
                            }
                            balanceTvNum.setText(carlist.size()+"");
                            balanceTvMoney.setText(StringUtil.twoNum(money1+""));
                        }
                    }
                    break;
            }
        }
    };
    private String orderAccount = "";
    private SystemQuanxian sysquanxian;
    private String paytype;
    private List<YinpianMsg> leftlist = new ArrayList<>();
    private YinpianXiaofeiLeftAdapter leftAdapter;
    private List<YinpianMsg> rightlist = new ArrayList<>();
    private YinpianXiaofeiRightAdapter rightAdapter;
    private List<YinpianMsg> carlist = new ArrayList<>();
    private void handlerShopMsg(Zhekou zhekou) {
    }

    private void handlerShopMsg(List<Shop> zhekou) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinpianxiaofei);
        ButterKnife.bind(this);
        ac = this;
        inintView();
        app = (MyApplication) getApplication();
        sysquanxian = app.getSysquanxian();
        dialog = DialogUtil.loadingDialog(YinpianXiaofeiActivity.this, 1);
        paydialog = DialogUtil.payloadingDialog(YinpianXiaofeiActivity.this, 1);
        PreferenceHelper.write(ac, "shoppay", "memid", "");
        PreferenceHelper.write(ac, "shoppay", "isSan", true);
        PreferenceHelper.write(ac, "shoppay", "vipcar", "无");
        PreferenceHelper.write(ac, "shoppay", "vipname", "散客");
        PreferenceHelper.write(context, "shoppay", "viptoast", "未查询到会员");


        leftAdapter = new YinpianXiaofeiLeftAdapter(ac, leftlist);
        listview.setAdapter(leftAdapter);
        rightAdapter = new YinpianXiaofeiRightAdapter(ac, rightlist, handler);
        listviewRight.setAdapter(rightAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                     YinpianMsg yp= (YinpianMsg) adapterView.getItemAtPosition(i);
                     for(int j=0;j<rightlist.size();j++){
                         if(!yp.GoodsID.contains(rightlist.get(j).GoodsID)){
                             rightlist.add(yp);
                             rightAdapter.notifyDataSetChanged();
                         }
                     }
            }
        });
        obtainYinpianList();
        balanceEtCard.addTextChangedListener(new TextWatcher() {
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

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            obtainVipInfo();
        }
    };


    private void obtainVipInfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("MemCard", editString);
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "GetMem");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxVipinfoS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        VipInfoMsg infomsg = gson.fromJson(new String(responseBody, "UTF-8"), VipInfoMsg.class);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = infomsg.getVdata().get(0);
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }

    private void obtainYinpianList() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("UserID", PreferenceHelper.readString(ac, "shoppay", "UserID", ""));
        map.put("UserShopID", PreferenceHelper.readString(ac, "shoppay", "ShopID", ""));
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "GetFujia");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxyinpianS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<YinpianMsg>>() {
                        }.getType();
                        List<YinpianMsg> yplist = gson.fromJson(jso.getString("vdata"), listType);
                        leftlist.clear();
                        leftlist.addAll(yplist);
                        leftAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "获取饮片失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
            }
        });
    }


    private void obtainShopMsg() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        if (PreferenceHelper.readBoolean(context, "shoppay", "isSan", true)) {
            params.put("memid", "0");
        } else {
            params.put("memid", PreferenceHelper.readString(context, "shoppay", "memid", "0"));
        }
        params.put("GoodsCode", shopString);
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(context, "?Source=3", "GetGoodsInfos");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxshopzkS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Zhekou>>() {
                        }.getType();
                        List<Zhekou> zhekoulist = gson.fromJson(jso.getString("vdata"), listType);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = zhekoulist.get(0);
                        handler.sendMessage(msg);


                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = 4;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = handler.obtainMessage();
                    msg.what = 4;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Message msg = handler.obtainMessage();
                msg.what = 4;
                handler.sendMessage(msg);
            }
        });
    }

    private void inintView() {
        //		// 初始化tab标识线
        tabBottomLine = (ImageView) findViewById(R.id.tabBottomLine);
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) tabBottomLine
                .getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        lParams.width = dm.widthPixels / 2;
        tabBottomLine.setLayoutParams(lParams);
        int offset = (dm.widthPixels / 2 - tabBottomLine.getWidth()) / 2;// 计算偏移量
        tabBottomLine.setBackgroundColor(getResources().getColor(R.color.theme_red));
        tabWidth = dm.widthPixels / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        tabBottomLine.setImageMatrix(matrix);// 设置动画初始位置
        balanceRlGuadan.setOnClickListener(new NoDoubleClickListener() {
                                               @Override
                                               protected void onNoDoubleClick(View view) {
                                                   if (balanceTvNum.getText().toString().equals("0")) {
                                                       Toast.makeText(getApplicationContext(), "请选择商品",
                                                               Toast.LENGTH_SHORT).show();
                                                   } else {
                                                       if (CommonUtils.checkNet(getApplicationContext())) {
                                                           if (type.equals("否")) {
                                                               if (!isSuccess) {
                                                                   Toast.makeText(ac, "您选择的是会员结算，请确认会员信息是否正确", Toast.LENGTH_SHORT).show();
                                                               } else {
                                                                   //会员挂单
                                                                   guadan();
                                                               }
                                                           } else {
                                                               //散客挂单
                                                               guadan();
                                                           }
                                                       } else {
                                                           //无网络
                                                           Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                                                   Toast.LENGTH_SHORT).show();
                                                       }
                                                   }
                                               }
                                           }

        );
        balanceRlJiesan.setOnClickListener(new NoDoubleClickListener() {
                                               @Override
                                               protected void onNoDoubleClick(View view) {

                                                   if (sysquanxian.isjiesuan == 1) {

                                                       if (balanceTvNum.getText().toString().equals("0")) {
                                                           Toast.makeText(getApplicationContext(), "请选择商品",
                                                                   Toast.LENGTH_SHORT).show();
                                                       } else {
                                                           if (CommonUtils.checkNet(getApplicationContext())) {
                                                               if (type.equals("否")) {
                                                                   if (!isSuccess) {
                                                                       Toast.makeText(ac, "您选择的是会员结算，请确认会员信息是否正确", Toast.LENGTH_SHORT).show();
                                                                   } else {
                                                                       //会员结算
//
                                                                       ShopXiaofeiDialog.jiesuanDialog(app, true, dialog, YinpianXiaofeiActivity.this, 1, "shop", Double.parseDouble(balanceTvMoney.getText().toString()), new InterfaceBack() {
                                                                           @Override
                                                                           public void onResponse(Object response) {
                                                                               if (response.toString().equals("wxpay")) {
                                                                                   paytype = "wx";
                                                                                   Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                                                                                   startActivityForResult(mipca, 333);
                                                                               } else if (response.toString().equals("zfbpay")) {
                                                                                   paytype = "zfb";
                                                                                   Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                                                                                   startActivityForResult(mipca, 333);
                                                                               } else {
                                                                                   finish();
                                                                               }
                                                                           }

                                                                           @Override
                                                                           public void onErrorResponse(Object msg) {

                                                                           }
                                                                       });
                                                                   }
                                                               } else {//散客结算
                                                                   ShopXiaofeiDialog.jiesuanDialog(app, false, dialog, YinpianXiaofeiActivity.this, 1, "shop", Double.parseDouble(balanceTvMoney.getText().toString()), new InterfaceBack() {
                                                                       @Override
                                                                       public void onResponse(Object response) {
                                                                           if (response.toString().equals("wxpay")) {
                                                                               paytype = "wx";
                                                                               Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                                                                               startActivityForResult(mipca, 333);
                                                                           } else if (response.toString().equals("zfbpay")) {
                                                                               paytype = "zfb";
                                                                               Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                                                                               startActivityForResult(mipca, 333);
                                                                           } else {
                                                                               finish();
                                                                           }
                                                                       }

                                                                       @Override
                                                                       public void onErrorResponse(Object msg) {

                                                                       }
                                                                   });
                                                               }
                                                           } else {
                                                               Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                                                       Toast.LENGTH_SHORT).show();
                                                           }
                                                       }
                                                   } else {
                                                       Toast.makeText(getApplicationContext(), "暂无结算权限",
                                                               Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           }

        );


        vipTvDingwei.setOnClickListener(new

                                                NoDoubleClickListener() {
                                                    @Override
                                                    protected void onNoDoubleClick(View view) {
                                                        Intent duihuan = new Intent(ac, MipcaActivityCapture.class);
                                                        startActivityForResult(duihuan, 222);
                                                    }
                                                }

        );
        vipTvSearch.setOnClickListener(new View.OnClickListener()

                                       {
                                           @Override
                                           public void onClick(View view) {
                                               if (balanceEtShopcode.getText().toString().equals("")) {
                                                   Toast.makeText(ac, "请输入搜索条件", Toast.LENGTH_SHORT).show();
                                               } else {
                                                   obtainSearchShopMsg();
                                               }
                                           }
                                       }

        );
    }

    private void obtainSearchShopMsg() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        if (PreferenceHelper.readBoolean(context, "shoppay", "isSan", true)) {
            params.put("MemID", "");
        } else {
            params.put("MemID", PreferenceHelper.readString(context, "shoppay", "memid", ""));
        }
        params.put("UserID", PreferenceHelper.readString(ac, "shoppay", "UserID", ""));
        params.put("UserShopID", PreferenceHelper.readString(ac, "shoppay", "ShopID", ""));
        params.put("SType", 1);
        params.put("GoodsCode", balanceEtShopcode.getText().toString());
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(context, "?Source=3", "GetGoods");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxshopzkS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Shop>>() {
                        }.getType();
                        List<Shop> zhekoulist = gson.fromJson(jso.getString("vdata"), listType);
                        handlerShopMsg(zhekoulist);


                    } else {
                        Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "获取商品信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(context, "获取商品信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.rl_left, R.id.rl_right, R.id.li_vip, R.id.li_san})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.rl_right:
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 111);
                break;
            case R.id.li_vip:
                changeTabItem(0);
                type = "否";
                PreferenceHelper.write(ac, "shoppay", "isSan", false);
                liVipmsg.setVisibility(View.VISIBLE);
                PreferenceHelper.write(ac, "shoppay", "memid", "");
                balanceEtCard.setText("");
                balanceTvVipdengji.setText("");
                balanceTvVipjifen.setText("");
                balanceTvVipyue.setText("");
                balanceTvVipname.setText("");
                balanceTvNum.setText("0");
                balanceTvMoney.setText("0");
                break;
            case R.id.li_san:
                changeTabItem(1);
                type = "是";
                liVipmsg.setVisibility(View.GONE);
                balanceEtCard.setText("");
                balanceTvVipdengji.setText("");
                balanceTvVipjifen.setText("");
                balanceTvVipyue.setText("");
                balanceTvVipname.setText("");
                balanceTvNum.setText("0");
                balanceTvMoney.setText("0");
                PreferenceHelper.write(ac, "shoppay", "isSan", true);
                PreferenceHelper.write(ac, "shoppay", "memid", "");
                PreferenceHelper.write(ac, "shoppay", "vipcar", "无");
                PreferenceHelper.write(ac, "shoppay", "vipname", "散客");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
                    balanceEtCard.setText(data.getStringExtra("codedata"));
                }
                break;
            case 222:
                if (resultCode == RESULT_OK) {
                    balanceEtShopcode.setText(data.getStringExtra("codedata"));
                    obtainShopMsg();
                }
                break;
            case 333:
                if (resultCode == RESULT_OK) {
                    pay(data.getStringExtra("codedata"));
                }
                break;
        }
    }

    private void pay(String codedata) {
        paydialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("auth_code", codedata);
        map.put("UserID", PreferenceHelper.readString(ac, "shoppay", "UserID", ""));
//        （1会员充值7商品消费9快速消费11会员充次）
        map.put("ordertype", 7);
        orderAccount = DateUtils.getCurrentTime("yyyyMMddHHmmss");
        map.put("account", orderAccount);
        map.put("money", balanceTvMoney.getText().toString());
//        0=现金 1=银联 2=微信 3=支付宝
        switch (paytype) {
            case "wx":
                map.put("payType", 2);
                break;
            case "zfb":
                map.put("payType", 3);
                break;
        }
        client.setTimeout(120 * 1000);
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "PayOnLine");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    paydialog.dismiss();
                    LogUtils.d("xxpayS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {

                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        DayinUtils.dayin(jsonObject.getString("printContent"));
                        if (jsonObject.getInt("printNumber") == 0) {
                        } else {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                            } else {
                            }
                        }
                        jiesuan(orderAccount);
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    paydialog.dismiss();
                    Toast.makeText(ac, "支付失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                paydialog.dismiss();
                Toast.makeText(ac, "支付失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void jiesuan(String orderNum) {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        final DBAdapter dbAdapter = DBAdapter.getInstance(context);
        List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
        List<ShopCar> shoplist = new ArrayList<>();
        double yfmoney = 0.0;
        double zfmoney = 0.0;
        int point = 0;
        int num = 0;
        for (ShopCar numShop : list) {
            if (numShop.count == 0) {
            } else {
                shoplist.add(numShop);
                zfmoney = CommonUtils.add(zfmoney, Double.parseDouble(numShop.discountmoney));
                yfmoney = CommonUtils.add(yfmoney, Double.parseDouble(CommonUtils.multiply(numShop.count + "", numShop.price)));
                num = num + numShop.count;
                point = point + (int) numShop.point;
            }
        }
        RequestParams params = new RequestParams();
        params.put("MemID", PreferenceHelper.readString(context, "shoppay", "memid", ""));
        params.put("OrderAccount", orderNum);
        params.put("TotalMoney", yfmoney);
        params.put("DiscountMoney", zfmoney);
        params.put("OrderPoint", point);
        switch (paytype) {
            case "wx":
                params.put("payType", 2);
                break;
            case "zfb":
                params.put("payType", 3);
                break;
        }
        params.put("UserPwd", "");
        params.put("GlistCount", shoplist.size());
        LogUtils.d("xxparams", shoplist.size() + "");
        for (int i = 0; i < shoplist.size(); i++) {
            LogUtils.d("xxparams", shoplist.get(i).discount);
            params.put("Glist[" + i + "][GoodsID]", shoplist.get(i).goodsid);
            params.put("Glist[" + i + "][number]", shoplist.get(i).count);
            params.put("Glist[" + i + "][GoodsPoint]", point);
            params.put("Glist[" + i + "][discountedprice]", shoplist.get(i).discount);
            params.put("Glist[" + i + "][Price]", shoplist.get(i).discountmoney);
            params.put("Glist[" + i + "][GoodsPrice]", shoplist.get(i).price);
            params.put("Glist[" + i + "][batchnumber]", shoplist.get(i).batchnumber);
        }
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(context, "?Source=3", "GoodsExpense");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        if (jsonObject.getInt("printNumber") == 0) {
                            dbAdapter.deleteShopCar();
                        } else {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                                dbAdapter.deleteShopCar();
                            } else {
                                dbAdapter.deleteShopCar();
                            }
                        }
                        finish();
                    } else {
                        Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                }
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(context, "结算失败，请重新结算",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void guadan() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        final DBAdapter dbAdapter = DBAdapter.getInstance(context);
        List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
        List<ShopCar> shoplist = new ArrayList<>();
        double yfmoney = 0.0;
        double zfmoney = 0.0;
        int point = 0;
        int num = 0;
        for (ShopCar numShop : list) {
            if (numShop.count == 0) {
            } else {
                shoplist.add(numShop);
                zfmoney = CommonUtils.add(zfmoney, Double.parseDouble(numShop.discountmoney));
                yfmoney = CommonUtils.add(yfmoney, Double.parseDouble(CommonUtils.multiply(numShop.count + "", numShop.price)));
                num = num + numShop.count;
                point = point + (int) numShop.point;
            }
        }
        RequestParams params = new RequestParams();
        params.put("MemID", PreferenceHelper.readString(context, "shoppay", "memid", ""));
        params.put("OrderAccount", DateUtils.getCurrentTime("yyyyMMddHHmmss"));
        params.put("TotalMoney", yfmoney);
        params.put("DiscountMoney", zfmoney);
        params.put("GlistCount", shoplist.size());
        LogUtils.d("xxparams", shoplist.size() + "");
        for (int i = 0; i < shoplist.size(); i++) {
            LogUtils.d("xxparams", shoplist.get(i).discount);
            params.put("Glist[" + i + "][GoodsID]", shoplist.get(i).goodsid);
            params.put("Glist[" + i + "][number]", shoplist.get(i).count);
            params.put("Glist[" + i + "][GoodsPoint]", point);
            params.put("Glist[" + i + "][batchnumber]", shoplist.get(i).batchnumber);
            params.put("Glist[" + i + "][discountedprice]", shoplist.get(i).discount);
            params.put("Glist[" + i + "][goodstype]", shoplist.get(i).goodsType);
            params.put("Glist[" + i + "][GoodsPrice]", shoplist.get(i).price);
        }
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(context, "?Source=3", "Stay");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        if (jsonObject.getInt("printNumber") == 0) {
                            dbAdapter.deleteShopCar();
                        } else {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                                dbAdapter.deleteShopCar();
                            } else {
                                dbAdapter.deleteShopCar();
                            }
                        }
                        finish();
                    } else {
                        Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                }
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(context, "挂单失败，请重新挂单",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 切换tab选项
    private void changeTabItem(int index) {
        Animation animation = new TranslateAnimation(curTabIndex * tabWidth,
                index * tabWidth, 0, 0);
        curTabIndex = index;
        animation.setFillAfter(true);
        animation.setDuration(300);
        tabBottomLine.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 动画结束,更新文字选中状态
//                updateTabTextStatus(curTabIndex);
            }
        });
    }


}

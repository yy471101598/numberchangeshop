package com.shoppay.trt;

import android.app.Activity;
import android.app.Dialog;
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
import com.shoppay.trt.adapter.XiaofeijiluNewAdapter;
import com.shoppay.trt.bean.Dengji;
import com.shoppay.trt.bean.GuadanListMsg;
import com.shoppay.trt.bean.OrderDetailMsg;
import com.shoppay.trt.bean.VipInfo;
import com.shoppay.trt.bean.VipInfoMsg;
import com.shoppay.trt.bean.XiaofeiRecordNew;
import com.shoppay.trt.card.ReadCardOpt;
import com.shoppay.trt.dialog.DateHmChoseDialog;
import com.shoppay.trt.dialog.OrderTypeDialog;
import com.shoppay.trt.dialog.ShopDetailDialog;
import com.shoppay.trt.dialog.YinpianDetailDialog;
import com.shoppay.trt.http.InterfaceBack;
import com.shoppay.trt.tools.DateUtils;
import com.shoppay.trt.tools.DialogUtil;
import com.shoppay.trt.tools.LogUtils;
import com.shoppay.trt.tools.NoDoubleClickListener;
import com.shoppay.trt.tools.PreferenceHelper;
import com.shoppay.trt.tools.UrlTools;
import com.shoppay.trt.wxcode.MipcaActivityCapture;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2018/8/25 0025.
 */

public class XiaofeiRecordNewActivity extends Activity {
    @Bind(R.id.img_left)
    ImageView imgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout rlRight;
    @Bind(R.id.tv_shopname)
    TextView tvShopname;
    @Bind(R.id.et_shopname)
    EditText etShopname;
    @Bind(R.id.tv_cardnum)
    TextView tvCardnum;
    @Bind(R.id.et_cardnum)
    EditText etCardnum;
    @Bind(R.id.tv_starttime)
    TextView tvStarttime;
    @Bind(R.id.tv_endtime)
    TextView tvEndtime;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.rl_typechose)
    RelativeLayout rlTypechose;
    @Bind(R.id.tv_search)
    TextView tvSearch;
    @Bind(R.id.listview)
    ListView listview;
    private Activity ac;
    private XiaofeijiluNewAdapter adapter;
    private Dialog dialog;
    private String type = "";
    private List<XiaofeiRecordNew> list;
    private boolean isSuccess = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 111:
                    final XiaofeiRecordNew record = (XiaofeiRecordNew) msg.obj;
                    obtainDetailClass(record.OrderID);
                    break;
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    isSuccess = true;
                    break;
                case 2:
                    isSuccess = false;
                    break;
            }
        }
    };
    private String editString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaofeijilunew);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(XiaofeiRecordNewActivity.this, 1);
        tvEndtime.setText(DateUtils.getCurrentTime("yyyy-MM-dd HH:mm"));
        tvStarttime.setText(DateUtils.getDateBefore(new Date(), 3));
        tvTitle.setText("消费记录");
        nodoubleClick();
        list = new ArrayList<>();
        adapter = new XiaofeijiluNewAdapter(ac, list, dialog, handler);
        listview.setAdapter(adapter);
        obtainRecordListClass();
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

    private void nodoubleClick() {
        tvSearch.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (!etCardnum.getText().toString().equals("")) {
                    if (isSuccess) {
                        obtainRecordListClass();
                    } else {
                        Toast.makeText(ac, "请输入正确的会员卡号", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    obtainRecordListClass();
                }
            }
        });
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

    private void obtainRecordListClass() {
        dialog.show();
        list.clear();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("UserID", PreferenceHelper.readString(ac, "shoppay", "UserID", ""));
        map.put("UserShopID", PreferenceHelper.readString(ac, "shoppay", "ShopID", ""));
        map.put("StartTime", tvStarttime.getText().toString().replace(" ", "_") + ":59");
        map.put("EndTime", tvEndtime.getText().toString().replace(" ", "_") + ":59");
        map.put("GoodsCode", etShopname.getText().toString());
        map.put("type", type);//（1商品消费2饮片消费）
        map.put("MemCard", etCardnum.getText().toString());
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "OrderLogListGet");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxrecordS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<XiaofeiRecordNew>>() {
                        }.getType();
                        List<XiaofeiRecordNew> slist = gson.fromJson(jso.getString("vdata"), listType);
                        if (slist.size() == 0) {
                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        } else {
                            list.addAll(slist);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "获取消费记录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "获取消费记录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void obtainDetailClass(String orderid) {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("UserID", PreferenceHelper.readString(ac, "shoppay", "UserID", ""));
        map.put("UserShopID", PreferenceHelper.readString(ac, "shoppay", "ShopID", ""));
        map.put("OrderID", orderid);
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "OrderLogDetail");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxdetailS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<OrderDetailMsg>>() {
                        }.getType();
                        List<OrderDetailMsg> slist = gson.fromJson(jso.getString("vdata"), listType);
                        if (type.equals("商品消费")) {
                            ShopDetailDialog.shopDetailDialog(XiaofeiRecordNewActivity.this, slist, 1);
                        } else {
                            YinpianDetailDialog.yinpianDetailDialog(XiaofeiRecordNewActivity.this, slist, 1);
                        }
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "获取详情失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "获取详情失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 222:
                if (resultCode == RESULT_OK) {
                    etCardnum.setText(data.getStringExtra("codedata"));
                }
                break;
        }
    }

    @OnClick({R.id.rl_left, R.id.rl_right, R.id.tv_starttime, R.id.tv_endtime, R.id.rl_typechose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.rl_right:
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 222);
                break;
            case R.id.tv_starttime:
                DateHmChoseDialog.datehmChoseDialog(ac, 2, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        tvStarttime.setText(response.toString());
                    }

                    @Override
                    public void onErrorResponse(Object msg) {

                    }
                });
                break;
            case R.id.tv_endtime:
                DateHmChoseDialog.datehmChoseDialog(ac, 2, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        tvEndtime.setText(response.toString());
                    }

                    @Override
                    public void onErrorResponse(Object msg) {

                    }
                });
                break;
            case R.id.rl_typechose:
                List<Dengji> dlist = new ArrayList<>();
                Dengji d1 = new Dengji();
                d1.LevelID = "1";
                d1.LevelName = "商品消费";
                dlist.add(d1);
                Dengji d2 = new Dengji();
                d2.LevelID = "2";
                d2.LevelName = "饮片消费";
                dlist.add(d2);
                OrderTypeDialog.typeChoseDialog(XiaofeiRecordNewActivity.this, dlist, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        Dengji dj = (Dengji) response;
                        tvType.setText(dj.LevelName);
                        type = dj.LevelID;
                    }

                    @Override
                    public void onErrorResponse(Object msg) {

                    }
                });
                break;
        }
    }
}

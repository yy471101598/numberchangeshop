package com.shoppay.trt;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
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
import com.shoppay.trt.adapter.ShopTjAdapter;
import com.shoppay.trt.bean.Dengji;
import com.shoppay.trt.bean.ShopTjMsg;
import com.shoppay.trt.dialog.DateHmChoseDialog;
import com.shoppay.trt.dialog.OrderTypeDialog;
import com.shoppay.trt.http.InterfaceBack;
import com.shoppay.trt.tools.DateUtils;
import com.shoppay.trt.tools.DialogUtil;
import com.shoppay.trt.tools.LogUtils;
import com.shoppay.trt.tools.NoDoubleClickListener;
import com.shoppay.trt.tools.PreferenceHelper;
import com.shoppay.trt.tools.StringUtil;
import com.shoppay.trt.tools.UrlTools;

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

public class ShopTongjiActivity extends Activity {

    @Bind(R.id.img_left)
    ImageView imgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout rlRight;
    @Bind(R.id.et_shopname)
    EditText etShopname;
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
    @Bind(R.id.tv_tcmoney)
    TextView tvTcmoney;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.tv_xsmoney)
    TextView tv_xsmoney;
    private Activity ac;
    private ShopTjAdapter adapter;
    private Dialog dialog;
    private String type = "";
    private List<ShopTjMsg> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoptongji);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(ShopTongjiActivity.this, 1);
        tvEndtime.setText(DateUtils.getCurrentTime("yyyy-MM-dd HH:mm"));
        tvStarttime.setText(DateUtils.getDateBefore(new Date(), 3));
        tvTitle.setText("商品统计");
        nodoubleClick();
        list = new ArrayList<>();
        adapter = new ShopTjAdapter(ac, list);
        listview.setAdapter(adapter);
        obtainRecordListClass();

    }

    private void nodoubleClick() {
        tvSearch.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                obtainRecordListClass();
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
        map.put("type", type);//（0商品消费1饮片消费）
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "GoodsSale");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxrecordS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 0) {
                        JSONObject jo=jso.getJSONObject("vdata");
                        Gson gson = new Gson();
                        tvTcmoney.setText(StringUtil.twoNum(jo.getString("totalstaffmoney")));
                        tv_xsmoney.setText(StringUtil.twoNum(jo.getString("totalsalesmoney")));
                        Type listType = new TypeToken<List<ShopTjMsg>>() {
                        }.getType();
                        List<ShopTjMsg> slist = gson.fromJson(jo.getString("GoodsSale"), listType);
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
                    Toast.makeText(ac, "获取商品统计失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "获取商品统计失败", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @OnClick({R.id.rl_left, R.id.tv_starttime, R.id.tv_endtime, R.id.rl_typechose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
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
                d1.LevelID = "0";
                d1.LevelName = "商品";
                dlist.add(d1);
                Dengji d2 = new Dengji();
                d2.LevelID = "1";
                d2.LevelName = "饮片";
                dlist.add(d2);
                OrderTypeDialog.typeChoseDialog(ShopTongjiActivity.this, dlist, 1, new InterfaceBack() {
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

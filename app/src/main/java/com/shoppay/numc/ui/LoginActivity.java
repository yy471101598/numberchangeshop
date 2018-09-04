package com.shoppay.numc.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.numc.HomeActivity;
import com.shoppay.numc.MyApplication;
import com.shoppay.numc.R;
import com.shoppay.numc.bean.QuanxianManage;
import com.shoppay.numc.bean.SystemQuanxian;
import com.shoppay.numc.http.ContansUtils;
import com.shoppay.numc.tools.ActivityStack;
import com.shoppay.numc.tools.CommonUtils;
import com.shoppay.numc.tools.DialogUtil;
import com.shoppay.numc.tools.LogUtils;
import com.shoppay.numc.tools.MD5Util;
import com.shoppay.numc.tools.NoDoubleClickListener;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.tools.SysUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class LoginActivity extends BaseActivity {
    private RelativeLayout rl_login;
    private EditText et_account, et_pwd;
    private CheckBox cb;
    private Activity ac;
    private Dialog dialog;
    private ImageView img;
    File file;
    private QuanxianManage menuquanxian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ac = this;
        ActivityStack.create().addActivity(ac);
        initView();
        if (PreferenceHelper.readBoolean(ac, "shoppay", "remember", false)) {
            cb.setChecked(true);
            et_account.setText(PreferenceHelper.readString(ac, "shoppay", "account", "123"));
            et_pwd.setText(PreferenceHelper.readString(ac, "shoppay", "pwd", "123"));
        }


        dialog = DialogUtil.loadingDialog(ac, 1);
        setimg();
        file = new File(Environment.getExternalStorageDirectory(),
                "error.log");
        LogUtils.d("xxe", res.getString(R.string.laguage));
    }

    public String str2HexStr(String str) {
        byte[] bytes = str.getBytes();
        // 如果不是宽类型的可以用Integer
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString(16);
    }

    private void loadError(String s) {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("error", s);

        Log.d("xx", s);
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "?Source=3&Method=logError", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("xxLogS", "sd");
                try {
                    file.delete();
                    Log.d("xxLogS", new String(responseBody, "UTF-8"));
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("xxLogE", error.getMessage());
            }
        });
    }

    private void setimg() {
        DisplayMetrics disMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
        int width = disMetrics.widthPixels;
        int height = disMetrics.heightPixels;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.banner);//link the drable image
        SysUtil.setImageBackground(bitmap, img, width, dip2px(ac, 170));
    }


    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void initView() {
        img = (ImageView) findViewById(R.id.imgview);
        rl_login = (RelativeLayout) findViewById(R.id.rl_login);
        et_account = (EditText) findViewById(R.id.et_login_phone);
        et_pwd = (EditText) findViewById(R.id.et_login_pwd);
        cb = (CheckBox) findViewById(R.id.login_cb);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    LogUtils.d("xx", "checked");
                    PreferenceHelper.write(ac, "shoppay", "remember", b);
                }
            }
        });

        rl_login.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (et_account.getText().toString().equals("")
                        || et_account.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入账号",
                            Toast.LENGTH_SHORT).show();
                } else if (et_pwd.getText().toString().equals("")
                        || et_pwd.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入密码",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        login();
                        if (file.exists()) {
                            if (Environment.getExternalStorageState().equals(
                                    Environment.MEDIA_MOUNTED)) {
                                try {
                                    FileInputStream inputStream = new FileInputStream(file);
                                    byte[] b = new byte[inputStream.available()];
                                    inputStream.read(b);
                                    loadError(new String(b));
                                } catch (Exception e) {
                                }
                            } else {
                                // 此时SDcard不存在或者不能进行读写操作的
                            }

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void login() {
        PreferenceHelper.write(ac, "shoppay", "account", et_account.getText().toString());
        PreferenceHelper.write(ac, "shoppay", "pwd", et_pwd.getText().toString());
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("UserName", et_account.getText().toString());
        params.put("PassWord", et_pwd.getText().toString());
        JSONObject jso = new JSONObject();
        try {
            jso.put("username", et_account.getText().toString());
            jso.put("password", et_pwd.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.d("xxjson", jso.toString());
        params.put("HMAC", MD5Util.MD5(jso.toString() + "bankbosscc").toUpperCase());
        LogUtils.d("xxmap", params.toString());
        client.post(ContansUtils.BASE_URL + "pos/login.ashx", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxLoginS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        MyApplication myApplication = (MyApplication) getApplication();
                        PreferenceHelper.write(ac, "shoppay", "account", et_account.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "pwd", et_pwd.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "userid", jso.getString("userid"));
                        Intent intent = new Intent(ac, HomeActivity.class);
                        intent.putExtra("quanxian", menuquanxian);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ac, "登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "登录失败，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
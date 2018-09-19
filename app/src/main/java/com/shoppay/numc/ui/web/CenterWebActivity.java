package com.shoppay.numc.ui.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppay.numc.R;
import com.shoppay.numc.card.ReadCardOpt;
import com.shoppay.numc.http.InterfaceBack;
import com.shoppay.numc.modle.ImpObtainVipMsg;
import com.shoppay.numc.tools.ActivityStack;
import com.shoppay.numc.tools.LogUtils;
import com.shoppay.numc.tools.PreferenceHelper;
import com.shoppay.numc.ui.BaseActivity;

/**
 * 个人中心跳转URL页面
 *
 * @author Administrator
 */
@SuppressLint("SetJavaScriptEnabled")
public class CenterWebActivity extends BaseActivity {
    private WebView web;
    private static final int FILECHOOSER_RESULTCODE = 333;
    protected static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 211;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private TextView title_tv;
    private RelativeLayout getBack;
    private Activity ac;
    private String title, entitle;
    private String uri, typeid;
    private RelativeLayout rl_no;
    private EditText et_card;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    rl_no.setVisibility(View.GONE);
                    web.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    break;
            }
        }
    };
    private String editString;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market);
        ac = this;
        ActivityStack.create().addActivity(ac);
        this.initView();
        Intent it = getIntent();
        uri = getIntent().getStringExtra("url");
        typeid = getIntent().getStringExtra("typeid");
        title = getIntent().getStringExtra("title");
        entitle = getIntent().getStringExtra("entitle");
        LogUtils.d("xxurl",uri);
        if (PreferenceHelper.readString(ac, "numc", "lagavage", "zh").equals("zh")) {
            title_tv.setText(title);
        } else {
            title_tv.setText(entitle);
        }

        if (typeid.equals("2")) {
            rl_no.setVisibility(View.VISIBLE);
            web.setVisibility(View.GONE);
        } else {
            rl_no.setVisibility(View.GONE);
            web.setVisibility(View.VISIBLE);
        }
        PayUtils.webPayUtils(ac, web, uri);


        et_card.addTextChangedListener(new TextWatcher() {
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
        this.getBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (web.canGoBack()) {
                    web.goBack();
                } else {
                    ActivityStack.create().finishActivity(ac);
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
        vipmsg.obtainVipMsg(CenterWebActivity.this, editString, new InterfaceBack() {
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
        // TODO Auto-generated method stub
        this.web = (WebView) findViewById(R.id.markrtweb);
        this.title_tv = (TextView) findViewById(R.id.tv_title);
        this.title_tv.setVisibility(View.VISIBLE);
        this.getBack = (RelativeLayout) findViewById(R.id.rl_left);
        et_card = (EditText) findViewById(R.id.et_card);
        rl_no = (RelativeLayout) findViewById(R.id.rl_no);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (web.canGoBack()) {
                web.goBack();
            } else {
                ActivityStack.create().finishActivity(ac);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(et_card);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == this.mUploadMessage)
                return;
            Uri result = intent == null || resultCode != ac.RESULT_OK ? null
                    : intent.getData();
            this.mUploadMessage.onReceiveValue(result);
            this.mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            Uri result = (intent == null || resultCode != ac.RESULT_OK) ? null
                    : intent.getData();
            if (result != null) {
                this.mUploadMessageForAndroid5
                        .onReceiveValue(new Uri[]{result});
            } else {
                this.mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            this.mUploadMessageForAndroid5 = null;
        }
    }


}

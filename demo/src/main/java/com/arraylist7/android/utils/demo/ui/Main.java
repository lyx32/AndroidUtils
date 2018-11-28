package com.arraylist7.android.utils.demo.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.UiUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.Params;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.broadcast.ActivityBroadcast;
import com.arraylist7.android.utils.broadcast.BaseBroadcastReceiver;
import com.arraylist7.android.utils.demo.App;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.adapter.DemoAdapter;
import com.arraylist7.android.utils.demo.base.Base;
import com.arraylist7.android.utils.demo.model.DemoModel;
import com.arraylist7.android.utils.inter.IOperator;
import com.arraylist7.android.utils.listener.BaseBroadcastReceiverListener;
import com.arraylist7.android.utils.listener.PermissionListener;
import com.arraylist7.android.utils.widget.NEditText;
import com.arraylist7.android.utils.widget.NRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends Base {

    @Views(R.id.ui_main_textView1)
    private TextView textView1;
    @Views(R.id.ui_main_editText_1)
    private NEditText editText1;
    @Views(R.id.ui_main_editText_2)
    private NEditText editText2;
    @Views(R.id.ui_main_button1)
    private Button button1;
    @Views(R.id.ui_main_button2)
    private Button button2;
    @Views(R.id.ui_main_button3)
    private Button button3;
    @Views(R.id.ui_main_recyclerView1)
    private NRecyclerView recyclerView1;

    // 获取从Launch页面点击按钮传过来的random参数
    @Params("random")
    private String random;


    private DemoAdapter adapter = null;
    private BaseBroadcastReceiver receiver = null;

    public static void instance(Activity from, boolean finish) {
        IntentUtils.activity(from, Main.class, finish);
    }


    public static void instance(Activity from, String random, boolean finish) {
        Bundle bundle = new Bundle();
        bundle.putString("random", random);
        IntentUtils.activity(from, Main.class, bundle, finish);
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_main;
    }

    @Override
    public void initWidget() {
        ViewUtils.inject(activity);
        // 弹出的键盘，点击右下角跳转到指定EditText
        editText1.setNextView(editText2);
        // 绑定键盘右下角响应对应按钮，默认 IME_ACTION_DONE, IME_ACTION_GO,  IME_ACTION_SEARCH, IME_ACTION_SEND, IME_ACTION_UNSPECIFIED
        editText2.setOnSubmitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.showLong(App.getContext(), "点击了键盘右下角按钮");
            }
        });

        recyclerView1.setVertical(true);
        recyclerView1.setAdapter(adapter = new DemoAdapter(R.layout.ui_main_item, this));
    }

    @Override
    public void initData() {
        textView1.setText("从lunch过来的参数为：" + random);
        DemoModel model = null;
        List<DemoModel> list = new ArrayList<>();
        String[] img = new String[]{
                "https://assets-cdn.github.com/images/modules/site/logos/airbnb-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/sap-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/ibm-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/google-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/paypal-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/bloomberg-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/spotify-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/swift-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/facebook-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/node-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/nasa-logo.png",
                "https://assets-cdn.github.com/images/modules/site/logos/walmart-logo.png"
        };
        for (int i = 1; i < 100; i++) {
            model = new DemoModel();
            model.id = i + "";
            model.name = "name-" + i;
            model.dateTime = StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss.SSS");
            model.picUrl = img[i % img.length]+"?random="+i;
            list.add(model);
            model = null;
        }
        adapter.addData(list);
        adapter.updataUI();
    }

    @Override
    public void initListener() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.requestPermission(1001, new String[]{Manifest.permission.CAMERA}, new PermissionListener() {
                    @Override
                    public void permissionRequestSuccess(String[] permissions) {
                        UiUtils.showLong(App.getContext(), "你同意了相机权限");
                    }

                    @Override
                    public void permissionRequestFail(String[] permissions) {
                        UiUtils.showLong(App.getContext(), "你拒绝了相机权限");
                    }
                });
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.requestPermission(1001, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.VIBRATE, Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.READ_PHONE_NUMBERS}, new PermissionListener() {
                    @Override
                    public void permissionRequestSuccess(String[] permissions) {
                        StringBuffer buffer = new StringBuffer("你同意了以下权限：");
                        for (String permission : permissions) {
                            buffer.append("[" + permission + "]，");

                        }
                        UiUtils.showLong(App.getContext(), buffer.toString());
                    }

                    @Override
                    public void permissionRequestFail(String[] permissions) {
                        StringBuffer buffer = new StringBuffer("你拒绝了以下权限：");
                        for (String permission : permissions) {
                            buffer.append("[" + permission + "]，");
                        }
                        UiUtils.showLong(App.getContext(), buffer.toString());
                    }
                });
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 以下3种广播基本满足所有需求，如果不能满足
                ActivityBroadcast.sendLoginSuccess(App.getContext(), "login_success_custom_key", "登录成功");
                ActivityBroadcast.sendLoginOut(App.getContext(), "login_out_custom_key", "注销登录");
                ActivityBroadcast.sendData(App.getContext(), "action_custom_key", "value1");
                // 则使用
                if (null == receiver) {
                    receiver = new BaseBroadcastReceiver();
                    receiver.registerReceiver(Main.this, "broadcastReceiver_custom_action", new BaseBroadcastReceiverListener() {
                        @Override
                        public void onReceiver(Context context, String action, Map<String, Serializable> data) {
                            if ("broadcastReceiver_custom_action".equals(action)) {
                                LogUtils.e("收到自定义广播action的广播，广播参数：" + data.get("action_custom_key"));
                            }
                        }
                    });
                }
                ActivityBroadcast.send(App.getContext(), "broadcastReceiver_custom_action", "action_custom_key", "value1");
            }
        });
    }


    @Override
    public void onNetChange(NetState state) {
        super.onNetChange(state);
        LogUtils.e("当前网络状态：" + state);
    }

    @Override
    public void onScreenLock(boolean isLock) {
        super.onScreenLock(isLock);
        LogUtils.e("是否锁定屏幕：" + isLock);
    }

    @Override
    public void onLoginSuccess(Map<String, Serializable> data) {
        super.onLoginSuccess(data);
        LogUtils.e("收到登录成功广播，广播参数：" + data.get("login_success_custom_key"));
    }

    @Override
    public void onLoginOut(Map<String, Serializable> data) {
        super.onLoginOut(data);
        LogUtils.e("收到注销登录广播，广播参数：" + data.get("login_out_custom_key"));
    }

    @Override
    public void onReceivedData(Map<String, Serializable> data) {
        super.onReceivedData(data);
        LogUtils.e("接收通用广播，广播参数：" + data.get("action_custom_key"));
    }

    @Override
    public void finish() {
        if (null != receiver)
            receiver.unRegisterReceiver(this);
        super.finish();
    }
}

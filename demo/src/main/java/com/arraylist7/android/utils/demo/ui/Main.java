package com.arraylist7.android.utils.demo.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arraylist7.android.utils.HTMLUtils;
import com.arraylist7.android.utils.IOUtils;
import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.TypefaceUtils;
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
import com.arraylist7.android.utils.listener.BaseBroadcastReceiverListener;
import com.arraylist7.android.utils.widget.NEditText;
import com.arraylist7.android.utils.widget.NRecyclerView;

import java.io.IOException;
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
    // 绑定view 并且将random参数设置给tag和text
    @Views(value = R.id.ui_main_button3, setTag = "random", setText = "random")
    private Button button3;
    @Views(R.id.ui_main_button4)
    private Button button4;
    // 由于NRecyclerView 不是继承自TextView，所以setText不会生效，但是setTag会生效
    @Views(value = R.id.ui_main_recyclerView1, setText = "random", setTag = "random")
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
    public void initStatusBar() {
        StatusBarUtils.setDeepColor(this, getResources().getColor(R.color.colorPrimary));
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
        recyclerView1.setListDivider(StringUtils.randomColor(), 5);
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
            model.picUrl = img[i % img.length] + "?random=" + i;
            list.add(model);
            model = null;
        }
        adapter.addData(list);
        adapter.updateUI();

        try {
            String cq_qq_com = IOUtils.getString(App.getContext().getAssets().open("cq.qq.com_2018-12-21.html"), "GBK").toString();
            // 找到所有type="text" 的input标签
            List<String> inputs = HTMLUtils.findInputTag(cq_qq_com, new String[]{"type"}, new String[]{"text"});
            // 找到class="channel-title"的h3节点及h3节点下的内容
            List<String> h3_class = HTMLUtils.findHtmlTag(cq_qq_com, "h3", new String[]{"class"}, new String[]{"channel-title"}, true);
            // 提取所有img的src值
            List<String> srcs = HTMLUtils.filterHtmlTag(cq_qq_com, "img", new String[]{"src"});
            for (String item : inputs) {
                LogUtils.e("input=" + item);
            }
            for (String item : h3_class) {
                LogUtils.e("h3_class=" + item);
            }
            for (String item : srcs) {
                LogUtils.e("srcs=" + item);
            }
//            LogUtils.e("11111111",HTMLUtils.clearHTMLTag("<html><li>111</li><li id='222'>222</li><div><li>333</li></div><div><a><li>444</li><li id='5' /></a></div>6</html>","li",true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initListener() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = button1.getTag();
                if (StringUtils.contains(tag, "1"))
                    tag = "fonts/2.ttf";
                else if (StringUtils.contains(tag, "2"))
                    tag = "fonts/3.ttf";
                else
                    tag = "fonts/1.ttf";
                button1.setTag(tag);
                TypefaceUtils.setAssetsDefaultFont(App.getContext(), tag + "");
                Fonts.instance(activity, false);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypefaceUtils.clearDefaultFont(App.getContext());
                Fonts.instance(activity, false);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("button3.tag=" + button3.getTag());
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
                ActivityBroadcast.send(App.getContext(), "broadcastReceiver_custom_action", "action_custom_key", "value" + StringUtils.random(100000, 999999));
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

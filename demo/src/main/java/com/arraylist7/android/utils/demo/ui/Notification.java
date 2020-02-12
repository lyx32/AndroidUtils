package com.arraylist7.android.utils.demo.ui;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.NotifyUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.base.Base;

public class Notification extends Base {

    @Views(R.id.ui_notifi_button1)
    private Button button1;
    @Views(R.id.ui_notifi_button2)
    private Button button2;
    @Views(R.id.ui_notifi_button3)
    private Button button3;
    @Views(R.id.ui_notifi_button4)
    private Button button4;

    public static void instance(Activity from, boolean finish) {
        IntentUtils.activity(from, Notification.class, finish);
    }


    @Override
    public int getLayoutId() {
        return R.layout.ui_notification;
    }

    @Override
    public void initWidget() {
        ViewUtils.inject(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyUtils.showText(activity, NotifyUtils.getChannelId("低版本","低版本",""), R.mipmap.ic_android_32, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_android_64),"我是普通通知", "普通通知111111111111",null, Html.class);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyUtils.showText(activity, R.mipmap.ic_android_32, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_android_64),"我是普通通知2", "普通通知2222222", null,Html.class);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyUtils.showPicture(activity, NotifyUtils.getChannelId("低版本","低版本",""), R.mipmap.ic_android_32, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_android_64),"我是普通通知", "普通通知111111111111", BitmapFactory.decodeResource(getResources(),R.mipmap.ui_notifi_1), null,Html.class);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyUtils.download(activity, Uri.parse("http://gyxza3.eymlz.com/a31/rj_zhoux1/57manhua.apk"), "我是普通通知", "普通通知111111111111");
            }
        });
    }
}

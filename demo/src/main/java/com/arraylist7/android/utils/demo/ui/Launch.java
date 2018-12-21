package com.arraylist7.android.utils.demo.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arraylist7.android.utils.AnimatorUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.TypefaceUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.demo.App;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.base.Base;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Launch extends Base {

    @Views(R.id.ui_launch_button)
    private Button button;

    private long backClickTime = 1L;

    @Override
    public boolean onCreate2(Bundle savedInstanceState) {
//        if (StringUtils.random(10, 19) >= 15L) {
//            return true;
//        } else {
//            Main.instance(this, true);
//            return false;
//        }
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_launch;
    }

    @Override
    public void initWidget() {
        ViewUtils.inject(activity);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - backClickTime > 1000) {
                    AnimatorUtils.shakeAnimation(button);
                    backClickTime= System.currentTimeMillis();
                }else {
                    Main.instance(activity, "随机数：" + StringUtils.random(100000, 999999), true);
                }
            }
        });
    }
}

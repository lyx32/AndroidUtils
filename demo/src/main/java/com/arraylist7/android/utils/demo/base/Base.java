package com.arraylist7.android.utils.demo.base;

import android.os.Bundle;
import android.os.Message;

import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.base.BaseAppCompatActivity;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.handler.NHandler;

public abstract class Base extends BaseAppCompatActivity {

    @Override
    public void initStatusBar() {
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public boolean onCreate2(Bundle savedInstanceState) {
        return true;
    }

    @Override
    public void handlerMsg(NHandler handler, Message msg) {

    }
}

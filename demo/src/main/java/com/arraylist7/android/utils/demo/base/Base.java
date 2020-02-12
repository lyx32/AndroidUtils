package com.arraylist7.android.utils.demo.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.arraylist7.android.utils.base.BaseAppCompatActivity;
import com.arraylist7.android.utils.handler.NHandler;

public abstract class Base extends BaseAppCompatActivity {

    @Override
    public boolean onCreate2(Bundle savedInstanceState) {
        return true;
    }

    @Override
    public void handlerMsg(Context context, NHandler handler, Message msg) {

    }
}

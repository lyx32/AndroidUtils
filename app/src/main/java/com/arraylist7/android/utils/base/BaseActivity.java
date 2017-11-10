package com.arraylist7.android.utils.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;

import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.handler.NHandler;
import com.arraylist7.android.utils.inter.IData;
import com.arraylist7.android.utils.inter.IHandler;

/**
 * Created by Administrator on 2016/6/29.
 */
public abstract class BaseActivity extends Activity implements IData,IHandler {

    protected Bundle bundle;
    protected NHandler handler;
    protected BaseActivity activity;


    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(getLayoutId());
        activity = this;
        handler = new NHandler(this);
        bundle = getIntent().getBundleExtra(IntentUtils.DATA_BUNDLE_KEY);
        onCreate2(savedInstanceState);
        initWidget();
        initStatusBar();
        readerDatabase();
        initListener();
        initData();
    }

    public abstract void onCreate2(Bundle savedInstanceState);

    @Override
    public void readerDatabase() {

    }

    @Override
    public void initStatusBar() {
        StatusBarUtils.setTranslucent(this);
    }


    @Override
    public void finish() {
        super.finish();
    }


    public void hideKeyboard(View view) {
        if (view != null) {
            IBinder iBinder = view.getWindowToken();
            if (null != iBinder) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}

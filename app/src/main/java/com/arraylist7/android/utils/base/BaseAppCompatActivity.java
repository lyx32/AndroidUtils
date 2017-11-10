package com.arraylist7.android.utils.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.R;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.handler.NHandler;
import com.arraylist7.android.utils.inter.IData;
import com.arraylist7.android.utils.inter.IHandler;

public abstract class BaseAppCompatActivity extends AppCompatActivity implements IData,IHandler {


    protected Bundle bundle;
    protected NHandler handler;
    protected BaseAppCompatActivity activity;

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

package com.arraylist7.android.utils.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.inter.IData;

/**
 * Created by Administrator on 2016/6/29.
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements IData {


    protected Bundle bundle;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(getLayoutId());
        bundle = getIntent().getBundleExtra(IntentUtils.DATA_BUNDLE_KEY);
        ViewUtils.inject(this);
        initWidget();
        initStatusBar();
        readerDatabase();
        initListener();
        initData();
    }

    @Override
    public void readerDatabase() {

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

package com.arraylist7.android.utils.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arraylist7.android.utils.R;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.inter.IData;

public abstract class BaseAppCompatActivity extends AppCompatActivity implements IData {

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(getLayoutId());
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

    @Override
    public void initStatusBar() {
        StatusBarUtils.setColor(this,R.color.colorPrimary);
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

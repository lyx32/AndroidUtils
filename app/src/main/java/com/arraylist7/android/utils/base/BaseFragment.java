package com.arraylist7.android.utils.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.arraylist7.android.utils.ClassUtils;
import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.handler.NHandler;
import com.arraylist7.android.utils.inter.IActivity;
import com.arraylist7.android.utils.inter.IHandler;
import com.arraylist7.android.utils.inter.INetChange;
import com.arraylist7.android.utils.inter.IOperator;
import com.arraylist7.android.utils.inter.IScreen;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public abstract class BaseFragment extends Fragment implements IActivity,IHandler, IOperator, INetChange, IScreen {
    protected BaseFragment that;
    protected NHandler hanlder;
    protected Bundle bundle;
    protected Context context;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(getLayoutId(), container, false);
        that = this;
        hanlder = new NHandler(this);
        context = getActivity().getApplicationContext();
        bundle = savedInstanceState;
        onCreate(root);
        initWidget();
        readerDatabase();
        initData();
        initListener();
        return root;
    }

    protected abstract void onCreate(View root);

    @Override
    public void readerDatabase() {

    }


    public void hideKeyboard(View view) {
        if (view != null) {
            IBinder iBinder = view.getWindowToken();
            if (null != iBinder) {
                InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    // http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            ClassUtils.setValue(Fragment.class,"mChildFragmentManager",null);
        } catch (Throwable throwable) {
        }
    }

    @Override
    public void onNetChange(NetState state) {

    }

    @Override
    public void onLoginSuccess(Map<String, Serializable> data) {

    }

    @Override
    public void onLoginOut(Map<String, Serializable> data) {

    }

    @Override
    public void onReceivedData(Map<String, Serializable> data) {

    }

    @Override
    public void onScreenLock(boolean isLock) {

    }
}

package com.arraylist7.android.utils.base;

import android.app.Activity;
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
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.handler.NHandler;
import com.arraylist7.android.utils.inter.IData;
import com.arraylist7.android.utils.inter.IHandler;

import java.lang.reflect.Field;

public abstract class BaseLazyFragment extends Fragment implements IData, IHandler {

    private View rootView;
    private boolean isInit = false;
    private boolean isVisibility = false;
    private LayoutInflater inflater;
    private ViewGroup container;

    protected BaseLazyFragment that;
    protected NHandler hanlder;
    protected Bundle bundle;
    protected Context context;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        that = this;
        hanlder = new NHandler(this);
        context = getActivity().getApplicationContext();
        bundle = this.getArguments();
        if (!isVisibility && !isInit && rootView == null)
            return new View(context);
        else
            rootView = onCreateView(inflater, container);
        return rootView;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        if (null == inflater || null == container)
            return null;
        View view = inflater.inflate(getLayoutId(), container, false);
        onCreate(view);
        initWidget();
        readerDatabase();
        initData();
        initListener();
        isInit = true;
        return view;
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisibility = isVisibleToUser;
        if (isVisibility) {
            if (!isInit && rootView == null)
                onCreateView(inflater, container);
            onResumeLazy();
        }
        if (isInit && rootView != null) {
            if (isVisibility) {
                isStart = true;
                onStartLazy();
            } else {
                isStart = false;
                onStopLazy();
            }
        }
    }

    @Deprecated
    @Override
    public final void onStart() {
        super.onStart();
        if (isInit && !isStart && isVisibility) {
            isStart = true;
            onStartLazy();
        }
    }

    @Deprecated
    @Override
    public final void onStop() {
        super.onStop();
        if (isInit && isStart && isVisibility) {
            isStart = false;
            onStopLazy();
        }
    }

    @Override
    @Deprecated
    public final void onResume() {
        super.onResume();
        if (isInit) {
            onResumeLazy();
        }
    }

    @Override
    @Deprecated
    public final void onPause() {
        super.onPause();
        if (isInit) {
            onPauseLazy();
        }
    }

    @Override
    @Deprecated
    public final void onDestroyView() {
        super.onDestroyView();
        if (isInit) {
            onDestroyViewLazy();
        }
        isInit = false;
    }

    private boolean isStart = false;

    protected void onStartLazy() {

    }

    protected void onStopLazy() {

    }

    protected void onResumeLazy() {

    }

    protected void onPauseLazy() {

    }

    protected void onDestroyViewLazy() {

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
}

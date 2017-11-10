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
import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.handler.NHandler;
import com.arraylist7.android.utils.inter.IData;
import com.arraylist7.android.utils.inter.IHandler;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public abstract class BaseFragment extends Fragment implements IData {
    protected BaseFragment that;
    protected Bundle bundle;
    protected Context context;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(getLayoutId(), container, false);
        that = this;
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

}

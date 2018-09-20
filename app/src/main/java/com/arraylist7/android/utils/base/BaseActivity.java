package com.arraylist7.android.utils.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.broadcast.NetReceiver;
import com.arraylist7.android.utils.handler.NHandler;
import com.arraylist7.android.utils.inter.IActivity;
import com.arraylist7.android.utils.inter.IHandler;
import com.arraylist7.android.utils.inter.INetChange;
import com.arraylist7.android.utils.inter.IOperator;
import com.arraylist7.android.utils.inter.IScreen;
import com.arraylist7.android.utils.listener.PermissionListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/29.
 */
public abstract class BaseActivity extends Activity implements IActivity, IHandler,IOperator,INetChange,IScreen {

    protected Bundle bundle;
    protected NHandler handler;
    protected BaseActivity activity;
    private HashMap<String, PermissionListener> permissionMap = new HashMap<>();


    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        this.setContentView(getLayoutId());
        super.onCreate(savedInstanceState);
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


    public void hideKeyboard(@NonNull View view) {
        if (view != null) {
            IBinder iBinder = view.getWindowToken();
            if (null != iBinder) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    public boolean havePermission(@NonNull String permission) {
        return havePermission(new String[]{permission})[0];
    }

    public boolean[] havePermission(@NonNull String[] permissions) {
        boolean[] booleans = new boolean[permissions.length];
        if (Build.VERSION.SDK_INT >= 23) {
            for (int i = 0; i < permissions.length; i++) {
                booleans[i] = PackageManager.PERMISSION_GRANTED == this.checkSelfPermission(permissions[i]);
            }
        }else{
            for (int i = 0; i < permissions.length; i++) {
                booleans[i] = true;
            }
        }
        return booleans;
    }

    public void requestPermission(@NonNull int requestCode, @NonNull String[] permissions, @Nullable PermissionListener listener) {
        if (null == listener)
            listener = new PermissionListener() {
                @Override
                public void permissionRequestSuccess(String[] permissions) {

                }

                @Override
                public void permissionRequestFail(String[] permissions) {

                }
            };
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> request = new ArrayList<>();
            for (String permission : permissions) {
                if (PackageManager.PERMISSION_GRANTED != this.checkSelfPermission(permission)) {
                    request.add(permission);
                }
            }
            if (0 == request.size()) {
                listener.permissionRequestSuccess(permissions);
            } else {
                permissionMap.put(requestCode + "", listener);
                this.requestPermissions(request.toArray(new String[]{}), requestCode);
            }
        } else {
            listener.permissionRequestSuccess(permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionMap.containsKey(requestCode + "")) {
            PermissionListener listener = permissionMap.get(requestCode + "");
            List<String> success = new ArrayList<>();
            List<String> fail = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int result = grantResults[i];
                if (PackageManager.PERMISSION_GRANTED == result)
                    success.add(permission);
                else
                    fail.add(permission);
            }
            if (0 != success.size())
                listener.permissionRequestSuccess(success.toArray(new String[]{}));
            if (0 != fail.size())
                listener.permissionRequestFail(fail.toArray(new String[]{}));
            success.clear();
            fail.clear();
            permissionMap.remove(requestCode + "");
        }
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
    public void onNetChange(NetState state) {

    }

    @Override
    public void onScreenOnOrOff(boolean isOn) {

    }
}

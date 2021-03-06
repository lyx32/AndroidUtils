package com.arraylist7.android.utils.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.handler.NHandler;
import com.arraylist7.android.utils.inter.IActivity;
import com.arraylist7.android.utils.inter.IHandler;
import com.arraylist7.android.utils.inter.INetChange;
import com.arraylist7.android.utils.inter.IOperator;
import com.arraylist7.android.utils.inter.IScreen;
import com.arraylist7.android.utils.listener.PermissionListener;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAppCompatActivity extends AppCompatActivity implements IActivity, IHandler, IOperator, INetChange, IScreen {

    protected boolean isShow = false;
    protected Bundle bundle;
    protected NHandler handler;
    protected BaseAppCompatActivity activity;
    private Map<Integer, SoftReference<View>> findViewByIdCache = new HashMap<>();
    private HashMap<String, PermissionListener> permissionMap = new HashMap<>();


    public void openActivity(Activity from, String title, boolean finish) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        IntentUtils.activity(from, this.getClass(), bundle, finish);
    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        this.setContentView(getLayoutId());
        super.onCreate(savedInstanceState);
        activity = this;
        handler = new NHandler(this, this);
        bundle = getIntent().getExtras();
        if (onCreate2(savedInstanceState)) {
            initWidget();
            initListener();
            initData();
        }
    }


    public abstract boolean onCreate2(Bundle savedInstanceState);


    @Override
    protected void onStart() {
        super.onStart();
        isShow = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isShow = false;
    }

    @Override
    public void finish() {
        findViewByIdCache.clear();
        permissionMap.clear();
        if (null != activity)
            activity = null;
        if (null != handler)
            handler.removeCallbacksAndMessages(null);
        if (null != bundle)
            bundle.clear();
        super.finish();
    }


    public View getRootView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
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

    public boolean havePermission(@NonNull String permission) {
        return havePermission(new String[]{permission})[0];
    }

    public boolean[] havePermission(@NonNull String[] permissions) {
        boolean[] booleans = new boolean[permissions.length];
        if (Build.VERSION.SDK_INT >= 23) {
            for (int i = 0; i < permissions.length; i++) {
                booleans[i] = PackageManager.PERMISSION_GRANTED == this.checkSelfPermission(permissions[i]);
            }
        } else {
            for (int i = 0; i < permissions.length; i++) {
                booleans[i] = true;
            }
        }
        return booleans;
    }

    /**
     * 动态请求权限
     *
     * @param requestCode 请求的requestCode
     * @param permission  Manifest.permission.XXX （注：在部分手机上需要在AndroidManifest.xml申请了，才能弹出请求权限的dialog）
     * @param listener
     */
    public void requestPermission(@NonNull int requestCode, @NonNull String permission, @Nullable PermissionListener listener) {
        requestPermission(requestCode, StringUtils.asArray(permission), listener);
    }

    /**
     * 动态请求权限
     *
     * @param requestCode 请求的requestCode
     * @param permissions Manifest.permission.XXX （注：在部分手机上需要在AndroidManifest.xml申请了，才能弹出请求权限的dialog）
     * @param listener
     */
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
            List<String> success = new ArrayList<>();
            for (String permission : permissions) {
                if (PackageManager.PERMISSION_GRANTED != this.checkSelfPermission(permission)) {
                    request.add(permission);
                } else {
                    success.add(permission);
                }
            }
            if (0 == request.size()) {
                listener.permissionRequestSuccess(permissions);
            } else {
                permissionMap.put(requestCode + "", listener);
                this.requestPermissions(request.toArray(new String[]{}), requestCode);
                listener.permissionRequestSuccess(success.toArray(new String[]{}));
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
        List<Fragment> list = this.getSupportFragmentManager().getFragments();
        for (Fragment f : list) {
            if (f instanceof IActivity)
                ((IOperator) f).onLoginSuccess(data);
        }
    }

    @Override
    public void onLoginOut(Map<String, Serializable> data) {
        List<Fragment> list = this.getSupportFragmentManager().getFragments();
        for (Fragment f : list) {
            if (f instanceof IActivity)
                ((IOperator) f).onLoginOut(data);
        }
    }

    @Override
    public void onReceivedData(Map<String, Serializable> data) {
        List<Fragment> list = this.getSupportFragmentManager().getFragments();
        for (Fragment f : list) {
            if (f instanceof IActivity)
                ((IOperator) f).onReceivedData(data);
        }
    }


    @Override
    public void onNetChange(NetState state) {
        List<Fragment> list = this.getSupportFragmentManager().getFragments();
        for (Fragment f : list) {
            if (f instanceof INetChange)
                ((INetChange) f).onNetChange(state);
        }
    }

    @Override
    public void onScreenLock(boolean isLock) {
        List<Fragment> list = this.getSupportFragmentManager().getFragments();
        for (Fragment f : list) {
            if (f instanceof IScreen)
                ((IScreen) f).onScreenLock(isLock);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            getSupportFragmentManager().getFragments().clear();
            IntentUtils.finish(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        SoftReference<View> soft = findViewByIdCache.get(Integer.valueOf(id));
        if (null != soft && null != soft.get()) {
            return (T) soft.get();
        }
        View view = super.findViewById(id);
        if (null != view) {
            findViewByIdCache.put(Integer.valueOf(id), new SoftReference<>(view));
        }
        return (T) view;
    }
}

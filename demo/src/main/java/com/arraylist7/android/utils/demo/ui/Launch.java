package com.arraylist7.android.utils.demo.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arraylist7.android.utils.AnimatorUtils;
import com.arraylist7.android.utils.CacheUtils;
import com.arraylist7.android.utils.IOUtils;
import com.arraylist7.android.utils.OtherUtils;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.UiUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.demo.App;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.base.Base;
import com.arraylist7.android.utils.listener.PermissionListener;

import java.io.File;
import java.io.IOException;

public class Launch extends Base {

    @Views(R.id.ui_launch_anim)
    private Button anim;
    @Views(R.id.ui_launch_install)
    private Button install;
    @Views(R.id.ui_launch_permisstion)
    private Button permisstion;
    @Views(R.id.ui_launch_intent)
    private Button intent;


    private long backClickTime = 1L;

    @Override
    public boolean onCreate2(Bundle savedInstanceState) {
//        if (StringUtils.random(10, 19) >= 15L) {
//            return true;
//        } else {
//            Main.instance(this, true);
//            return false;
//        }
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.ui_launch;
    }

    @Override
    public void initStatusBar() {
        StatusBarUtils.setColor(activity, StringUtils.randomColor());
    }

    @Override
    public void initWidget() {
        ViewUtils.inject(activity);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
        anim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - backClickTime > 1000) {
                    AnimatorUtils.shakeAnimation(anim);
                    backClickTime = System.currentTimeMillis();
                } else {
                    AnimatorUtils.clickAnimation(anim);
                }
            }
        });
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在部分手机上权限需要在
                requestPermission(1000, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionListener() {
                    @Override
                    public void permissionRequestSuccess(String[] permissions) {
                        String fileName = "demo-release-unsigned.apk";
                        File installFile = new File(CacheUtils.getStorageDirectory() + "/" + fileName);
                        if (installFile.exists()) {
                            OtherUtils.install(App.getContext(), installFile);
                        } else {
                            try {
                                IOUtils.readAndWriteAndClose(getAssets().open(fileName), IOUtils.fileOut(CacheUtils.getStorageDirectory() + "/" + fileName));
                                OtherUtils.install(App.getContext(), new File(CacheUtils.getStorageDirectory() + "/" + fileName));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void permissionRequestFail(String[] permissions) {

                    }
                });
            }
        });

        permisstion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在部分手机上需要在 AndroidManifest.xml 先声明，不然不会弹出授权框而直接拒绝
                // 在部分手机上 BLUETOOTH_ADMIN 则会被拒绝
                activity.requestPermission(1001, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.VIBRATE, Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.BLUETOOTH_ADMIN}, new PermissionListener() {
                    @Override
                    public void permissionRequestSuccess(String[] permissions) {
                        StringBuffer buffer = new StringBuffer("你同意了以下权限：");
                        for (String permission : permissions) {
                            buffer.append("[" + permission + "]，");
                        }
                        UiUtils.showLong(App.getContext(), buffer.toString());
                    }

                    @Override
                    public void permissionRequestFail(String[] permissions) {
                        StringBuffer buffer = new StringBuffer("你拒绝了以下权限：");
                        for (String permission : permissions) {
                            buffer.append("[" + permission + "]，");
                        }
                        UiUtils.showLong(App.getContext(), buffer.toString());
                    }
                });
            }
        });

        intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.instance(activity, StringUtils.random(100000, 999999) + "", true);
            }
        });
    }

}


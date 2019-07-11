package com.arraylist7.android.utils.demo.ui;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arraylist7.android.utils.AnimatorUtils;
import com.arraylist7.android.utils.CacheUtils;
import com.arraylist7.android.utils.FileUtils;
import com.arraylist7.android.utils.HTMLUtils;
import com.arraylist7.android.utils.HttpUtils;
import com.arraylist7.android.utils.IOUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.OtherUtils;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.UiUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.demo.App;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.base.Base;
import com.arraylist7.android.utils.http.HttpRequest;
import com.arraylist7.android.utils.http.HttpResponse;
import com.arraylist7.android.utils.http.callback.HttpListenerImpl;
import com.arraylist7.android.utils.http.excep.HttpException;
import com.arraylist7.android.utils.listener.PermissionListener;
import com.arraylist7.android.utils.widget.RoundImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Launch extends Base {

    @Views(R.id.ui_launch_anim)
    private Button anim;
    @Views(R.id.ui_launch_install)
    private Button install;
    @Views(R.id.ui_launch_permisstion)
    private Button permisstion;
    @Views(R.id.ui_launch_http)
    private Button http;
    @Views(R.id.ui_launch_intent)
    private Button intent;
    @Views(R.id.ui_launch_roundImageView)
    private RoundImageView roundImageView;

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


        String newFilePath = CacheUtils.createAppRootDir("android_utils_folder") + "/1.html";
        try {
            FileUtils.copyFile(getAssets().open("cq.qq.com_2018-12-21.html"), newFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cq_qq_com = FileUtils.readerFile(newFilePath, "gbk");
        LogUtils.e("本地文件大小：" + cq_qq_com.length());
        LogUtils.e("-------------------------");
        String[] array = FileUtils.readerTopLines(newFilePath,30);
        LogUtils.e(array[0]);
        LogUtils.e(array[7]);
        LogUtils.e(array[15]);

        // 找到所有type="text" 的input标签
        List<String> inputs = HTMLUtils.findInputTag(cq_qq_com, new String[]{"type"}, new String[]{"text"});
        // 找到class="channel-title"的h3节点及h3节点下的内容
        List<String> h3_class = HTMLUtils.findHtmlTag(cq_qq_com, "h3", new String[]{"class"}, new String[]{"channel-title"}, true);
        // 提取所有img的src值
        List<String> srcs = HTMLUtils.filterHtmlTag(cq_qq_com, "img", new String[]{"src"});
        for (String item : inputs) {
            LogUtils.e("input=" + item);
        }
        for (String item : h3_class) {
            LogUtils.e("h3_class=" + item);
        }
        for (String item : srcs) {
            LogUtils.e("srcs=" + item);
        }
    }

    @Override
    public void initListener() {

        http.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtils.request(new HttpRequest("www.qq.com", "GBK"), new HttpListenerImpl() {
                    @Override
                    public void onStart(HttpRequest request) {
                        LogUtils.e("开始请求=" + request.getUrl()+"    "+request.getMethod());
                    }

                    @Override
                    public void onSuccess(String html, HttpResponse response) {
                        LogUtils.e("请求成功，提取所有 p http状态=" + response.getHttpStatusCode());
                        List<String> imgList = HTMLUtils.findHtmlTag(html, "p", null, null, true);
                        for (String img : imgList)
                            LogUtils.e(img);
                    }

                    @Override
                    public void onException(HttpException exception) {
                        super.onException(exception);
                    }

                    @Override
                    public void onEnd() {
                        LogUtils.e("请求结束");
                    }
                });
            }
        });

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
                // 在部分手机上需要在 AndroidManifest.xml 先声明，不然不会弹出授权框而直接拒绝
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
                // 因为BLUETOOTH_ADMIN没有在AndroidManifest.xml 声明。所以在部分手机上 BLUETOOTH_ADMIN 会被拒绝
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


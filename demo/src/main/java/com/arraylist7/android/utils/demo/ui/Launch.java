package com.arraylist7.android.utils.demo.ui;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arraylist7.android.utils.AnimatorUtils;
import com.arraylist7.android.utils.CacheUtils;
import com.arraylist7.android.utils.DeviceUtils;
import com.arraylist7.android.utils.FileUtils;
import com.arraylist7.android.utils.HTMLUtils;
import com.arraylist7.android.utils.IOUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.OtherUtils;
import com.arraylist7.android.utils.StatusBarUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.ThreadUtils;
import com.arraylist7.android.utils.UiUtils;
import com.arraylist7.android.utils.ViewUtils;
import com.arraylist7.android.utils.annotation.LayoutBind;
import com.arraylist7.android.utils.annotation.Views;
import com.arraylist7.android.utils.demo.App;
import com.arraylist7.android.utils.demo.R;
import com.arraylist7.android.utils.demo.base.Base;
import com.arraylist7.android.utils.demo.model.DemoModel;
import com.arraylist7.android.utils.listener.PermissionListener;
import com.arraylist7.android.utils.listener.ThreadStateListener;
import com.arraylist7.android.utils.model.SimInfo;
import com.arraylist7.android.utils.widget.RoundImageView;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@LayoutBind(R.layout.ui_launch)
public class Launch extends Base {

    @Views(R.id.ui_launch_anim)
    private Button anim;
    @Views(R.id.ui_launch_install)
    private Button install;
    @Views(R.id.ui_launch_permisstion)
    private Button permisstion;
    @Views(R.id.ui_launch_http)
    private Button http;
    @Views(R.id.ui_launch_thread)
    private Button thread;
    @Views(R.id.ui_launch_sim)
    private Button sim;
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
    public void initWidget() {
        ViewUtils.inject(activity);
        StatusBarUtils.setColor(activity, Color.parseColor("#cf1234"));
    }

    @Override
    public void initData() {


        String newFilePath = CacheUtils.getPublicDir(activity,"android_utils_folder") + "/1.html";
        try {
            FileUtils.copyFile(getAssets().open("cq.qq.com_2018-12-21.html"), newFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cq_qq_com = FileUtils.readerFile(newFilePath, "gbk");
        LogUtils.e("本地文件大小：" + cq_qq_com.length());
        LogUtils.e("-------------------------");
        String[] array = FileUtils.readerTopLines(newFilePath, 30);
        LogUtils.e(array[0]);
        LogUtils.e(array[7]);
        LogUtils.e(array[15]);

        // 找到所有type="text" 的input标签
        List<String> inputs = HTMLUtils.findInputTag(cq_qq_com, new String[]{"type"}, new String[]{"text"});
        // 找到class="channel-title"的h3节点及h3节点下的内容
        List<String> h3_class = HTMLUtils.findHtmlTag(cq_qq_com, "h3", new String[]{"class"}, new String[]{"channel-title"}, true);
        // 提取所有img的src值
        List<String> srcs = HTMLUtils.findImg_Src(cq_qq_com);
        // 提取所有img的src值
        List<String> spans = HTMLUtils.findHtmlTag(cq_qq_com,"span",true);
        for (String item : inputs) {
            LogUtils.e("input=" + item);
        }
        for (String item : h3_class) {
            LogUtils.e("h3_class=" + item);
        }
        for (String item : srcs) {
            LogUtils.e("srcs=" + item);
        }
        for (String item : spans) {
            LogUtils.e("span_content=" + HTMLUtils.findContent(item));
        }



        DemoModel model = new DemoModel();
        model.id = 1 + "";
        model.name = "name-" + 1;
        model.dateTime = StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss.SSS");
        model.picUrl = "http://s.img.mix.sina.com.cn/auto/resize?img=http%3A%2F%2Fwww.sinaimg.cn%2Fdy%2Fslidenews%2F1_img%2F2017_13%2F86104_823262_955160.jpg&size=100_100";


    }

    @Override
    public void initListener() {

        http.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HttpRequest request = new HttpRequest("https://www.qq.com/", "gb2312", "GET");
//                request.addPostParameter("time",StringUtils.getDateTimeNow("yyyy-MM-dd HH:mm:ss"));
//                HttpUtils.request(request, new HttpListenerImpl() {
//                    @Override
//                    public void onSuccess(String html, HttpResponse response) {
//                        LogUtils.e("[" + html + "]");
//                    }
//                });
            }
        });

        thread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ThreadUtils.submit(activity, new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("线程1开始执行");
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000, new ThreadStateListener() {
                    @Override
                    public void done(Object threadReturnValue) {
                        LogUtils.e("线程1执行完毕：" + threadReturnValue);
                    }

                    @Override
                    public void timeout() {
                        LogUtils.e("线程1超时");
                    }

                    @Override
                    public void exception(Exception e) {

                    }
                });
                ThreadUtils.submit(activity, new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("线程2开始执行");
                        try {
                            TimeUnit.SECONDS.sleep(7);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000, new ThreadStateListener() {
                    @Override
                    public void done(Object threadReturnValue) {
                        LogUtils.e("线程2执行完毕");
                    }

                    @Override
                    public void timeout() {
                        LogUtils.e("线程2超时");
                    }

                    @Override
                    public void exception(Exception e) {

                    }
                });
                ThreadUtils.submit(activity, new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        LogUtils.e("线程3开始执行");
                        return "线程3返回值：" + System.currentTimeMillis();
                    }
                }, 5000, new ThreadStateListener<String>() {
                    @Override
                    public void done(String threadReturnValue) {
                        LogUtils.e("线程3执行完毕：" + threadReturnValue);
                    }

                    @Override
                    public void timeout() {
                    }

                    @Override
                    public void exception(Exception e) {

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
                        File installFile = new File(CacheUtils.getPrivateDirectory(activity) + "/" + fileName);
                        if (installFile.exists()) {
                            OtherUtils.install(App.getContext(), installFile);
                        } else {
                            try {
                                IOUtils.readAndWriteAndClose(getAssets().open(fileName), IOUtils.fileOut(CacheUtils.getPrivateDirectory(activity) + "/" + fileName));
                                OtherUtils.install(App.getContext(), new File(CacheUtils.getPrivateDirectory(activity) + "/" + fileName));
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

        sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.requestPermission(1002, Manifest.permission.READ_PHONE_STATE, new PermissionListener() {
                    @Override
                    public void permissionRequestSuccess(String[] permissions) {
                        SimInfo simInfo = DeviceUtils.getSimInfo(activity);
                        LogUtils.e("拨号卡iccid=" + simInfo.iccid1
                                + "\n拨号卡运营商=" + simInfo.networkOperatorName1 + "\n\n"
                                + "\n卡2iccid=" + simInfo.iccid2
                                + "\n卡2运营商=" + simInfo.networkOperatorName2);
                    }

                    @Override
                    public void permissionRequestFail(String[] permissions) {
                        UiUtils.showLong(activity, "权限被拒绝不能读取sim卡信息");
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


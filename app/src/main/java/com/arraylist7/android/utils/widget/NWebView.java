package com.arraylist7.android.utils.widget;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;

import com.arraylist7.android.utils.CacheUtils;
import com.arraylist7.android.utils.FileUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.NetState;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.UiUtils;
import com.arraylist7.android.utils.listener.WebViewListener;

import java.io.File;
import java.util.Map;


public class NWebView extends WebView {


    private String title;
    private AlertDialog dialog;
    private boolean isCanDownload = true;
    private String[] canDownloadType = new String[]{"doc", "xls", "xlsx", "pdf", "apk", "jpg", "png", "webp", "gif", "zip", "rar", "mp3", "mp4", "txt"};
    private String[] canDownloadMime = new String[]{"application/msword", "application/vnd.ms-excel", "application/pdf", "application/vnd.android", "image/jpeg", "image/png", "image/webp", "image/gif", "application/zip", "application/x-rar-compressed", "audio/x-mpeg", "video/mp4", "text/plain", "application/octet-stream"};


    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if (0 == downloadId)
                    return;
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(android.content.Context.DOWNLOAD_SERVICE);
                String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                if (StringUtils.isNullOrEmpty(type)) {
                    type = "*/*";
                }
                Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                if (uri != null) {
                    Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
                    handlerIntent.setDataAndType(uri, type);
                    context.startActivity(handlerIntent);
                }
            }
        }
    };


    public NWebView(Context context) {
        this(context, null);
    }

    public NWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget();
        initListener();
    }

    private void initWidget() {
        WebSettings webSettings = this.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
        if (UiUtils.isConnected(this.getContext()) != NetState.NET_NO) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        this.requestFocusFromTouch();
        this.removeJavascriptInterface("searchBoxJavaBridge_");
        this.removeJavascriptInterface("accessibility");
        this.removeJavascriptInterface("accessibilityTraversal");


        webSettings.setJavaScriptEnabled(true);  //支持js
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webView的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(false);  //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows();  //多窗口
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setNeedInitialFocus(true); //当webView调用requestFocus时为webView设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);

        webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
        webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能
        webSettings.setAppCacheMaxSize(8 * 1024 * 1024);
        webSettings.setDatabasePath(CacheUtils.getWebCachePath(this.getContext()));
        webSettings.setAppCachePath(CacheUtils.getWebCachePath(this.getContext()));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        this.getContext().registerReceiver(downloadReceiver, intentFilter);


        setWebViewListener(new WebViewListener());
    }

    private void initListener() {
        this.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                LogUtils.d("请求下载：" + url);
                LogUtils.d("是否允许下载=" + isCanDownload);
                LogUtils.d("userAgent=" + userAgent);
                LogUtils.d("contentDisposition=" + contentDisposition);
                LogUtils.d("mimeType=" + mimeType);
                LogUtils.d("contentLength=" + contentLength);

                if (!isCanDownload) return;

                boolean isAccept = false;
                for (String mime : canDownloadMime) {
                    if (mime.equalsIgnoreCase(mimeType)) {
                        isAccept = true;
                        break;
                    }
                }
                if (!isAccept) return;
                String gbkUrl = StringUtils.decode(url);
                String fileName = "";
                String[] disposition = contentDisposition.split(";");
                if (null != disposition && disposition.length >= 2)
                    fileName = StringUtils.decode(disposition[1].replace("filename=", ""));
                else
                    fileName = FileUtils.getFileName(gbkUrl);
                String downloadFileType = ("application/octet-stream".equalsIgnoreCase(mimeType) ? FileUtils.getFileFormat(fileName) : mimeType.split("/")[1]);
                // 如果mime类型没有匹配的则判断后缀名
                if (!isAccept) {
                    for (String type : canDownloadType) {
                        if (downloadFileType.toLowerCase().endsWith(type)) {
                            isAccept = true;
                            break;
                        }
                    }
                    if (!isAccept) return;
                }

                String fileType = "文件类型：" + downloadFileType;
                String fileSize = "文件大小：" + FileUtils.formatFileSize(contentLength);
                String msg = fileName + "\n\n" + fileSize + "\n" + fileType;
                final String downloadFileName = fileName;
                showDownloadDialog("您是否想要下载：" + msg, "立即下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setAllowedOverMetered(true);
                        request.setVisibleInDownloadsUi(true);
                        request.setAllowedOverRoaming(true);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName);
                        ((DownloadManager) NWebView.this.getContext().getSystemService(android.content.Context.DOWNLOAD_SERVICE)).enqueue(request);
                    }
                }, "取消", null);
            }
        });

        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            //=========HTML5定位==========================================================
            //需要先加入权限
            //<uses-permission android:name="android.permission.INTERNET"/>
            //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
            //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
            @Override
            public void onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt();
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
            //=========HTML5定位==========================================================

            //=========多窗口的问题==========================================================
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(view);
                resultMsg.sendToTarget();
                return true;
            }

            //=========多窗口的问题==========================================================
            //获取Web页中的title用来设置自己界面中的title
            //当加载出错的时候，比如无网络，这时onReceiveTitle中获取的标题为 找不到该网页,
            //因此建议当触发onReceiveError时，不要使用获取到的title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                NWebView.this.title = title;
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                //
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                LogUtils.d("onJsAlert({\"url\":\"" + url + "\",\"message\":\"" + message + "\"})");
                return true;
            }

            //处理confirm弹出框
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                LogUtils.d("onJsPrompt({\"url\":\"" + url + "\",\"message\":\"" + message + "\",\"defaultValue\":\"" + defaultValue + "\"})");
                return true;
            }

            //处理prompt弹出框
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                LogUtils.d("onJsConfirm({\"url\":\"" + url + "\",\"message\":\"" + message + "\"})");
                return true;
            }
        });


//        this.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
//                if (result != null) {
//                    int type = result.getType();
//                    //判断点击类型如果是图片
//                    if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
//
//                    } else if (type == WebView.HitTestResult.PHONE_TYPE) {

//                    }
//                }
//                return false;
//            }
//        });
    }

    public boolean setCanDownload() {
        return isCanDownload;
    }

    public void setCanDownload(boolean download) {
        setCanDownload(download, null, null);
    }

    /**
     * 设置是否允许下载
     *
     * @param download        是否允许下载
     * @param canDownloadType 允许下载的文件后缀名 （优先判断mimetype，在判断后缀名）
     * @param canDownloadMime 允许下载的文件mimetype（注：有些文件下载是使用流方式下载，所以最好配套canDownloadType一起）
     */
    public void setCanDownload(boolean download, String[] canDownloadType, String[] canDownloadMime) {
        this.isCanDownload = download;
        if (!StringUtils.isNullOrEmpty(canDownloadType))
            this.canDownloadType = canDownloadType;
        if (!StringUtils.isNullOrEmpty(canDownloadMime)) {
            Map<String, String> map = StringUtils.asMap(canDownloadMime);
            if (!map.values().contains("application/octet-stream")) {
                map.put(System.currentTimeMillis() + "", "application/octet-stream");
            }
            this.canDownloadMime = map.values().toArray(new String[]{});
        }
    }


    public void clearData() {
        this.clearFormData();
        this.clearMatches();
        this.clearHistory();
    }

    public void clearDeepData() {
        clearData();
        this.clearData();
        this.clearCache(true);
        FileUtils.deleteFile(new File(CacheUtils.getWebCachePath(this.getContext())));
    }


    public void onPause() {
        super.onPause();
        this.pauseTimers();
    }

    public void onResume() {
        super.onResume();
        this.resumeTimers();
    }

    public void onDestroy() {
        if (null != dialog) {
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }
        this.getContext().unregisterReceiver(downloadReceiver);
        if (this != null) {
            this.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            this.clearHistory();
            ((ViewGroup) this.getParent()).removeView(this);
            this.destroy();
        }
    }

    private void showDownloadDialog(String message, String determineText, final DialogInterface.OnClickListener determine, String cancelText, final DialogInterface.OnClickListener cancel) {
        if (null != dialog) {
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }
        dialog = new AlertDialog.Builder(this.getContext()).create();
        dialog.setTitle("文件下载");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        if (!StringUtils.isNullOrEmpty(determineText)) {
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, determineText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (null != determine)
                        determine.onClick(dialogInterface, i);
                    dialogInterface.dismiss();
                }
            });
        }
        if (!StringUtils.isNullOrEmpty(cancelText)) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (null != cancel)
                        cancel.onClick(dialogInterface, i);
                    dialogInterface.dismiss();
                }
            });
        }

        dialog.show();
    }

    public void setWebViewListener(WebViewListener webViewListener) {
        this.setWebViewClient(webViewListener);
    }

    public String getPageTitle() {
        return this.title;
    }
}

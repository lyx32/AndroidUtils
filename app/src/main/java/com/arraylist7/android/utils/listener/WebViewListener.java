package com.arraylist7.android.utils.listener;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arraylist7.android.utils.IntentUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.R;
import com.arraylist7.android.utils.widget.NWebView;

public class WebViewListener extends WebViewClient {

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (null != view) view.getSettings().setBlockNetworkImage(true);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.d("请求的url=" + url);
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (!url.matches("^[0-9a-zA-Z\\.\\-]+://")) {
                try {
                    if (url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(url.startsWith("tel:") ? url : "tel:" + url));
                        view.getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        view.getContext().startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else
                view.loadUrl("http://" + url);
        } else
            view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (null != view)
            view.getSettings().setBlockNetworkImage(false);
        super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        LogUtils.d("getStatusCode     " + errorResponse.getStatusCode());
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        url = url.toLowerCase();
        if (!hasAd(view.getContext(), url)) {
            return super.shouldInterceptRequest(view, url);//正常加载
        } else {
            return new WebResourceResponse(null, null, null);//含有广告资源屏蔽请求
        }
    }

    public boolean hasAd(Context context, String url) {
        String[] adUrls = context.getResources().getStringArray(R.array.androidUtils_url_block);
        for (String adUrl : adUrls) {
            if (url.contains(adUrl)) {
                return true;
            }
        }
        return false;
    }
}

package com.arraylist7.android.utils.http.callback;

import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.http.HttpRequest;
import com.arraylist7.android.utils.http.HttpResponse;
import com.arraylist7.android.utils.http.excep.HttpException;

public class HttpListenerImpl implements IHttpListener {

    @Override
    public void onStart(HttpRequest request) {
    }

    @Override
    public void onSuccess(String html,HttpResponse response) {

    }

    @Override
    public void onException(HttpException exception) {

    }

    @Override
    public void onEnd() {
    }
}

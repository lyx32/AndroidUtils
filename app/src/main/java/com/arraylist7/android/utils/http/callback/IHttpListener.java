package com.arraylist7.android.utils.http.callback;

import com.arraylist7.android.utils.http.HttpRequest;
import com.arraylist7.android.utils.http.HttpResponse;
import com.arraylist7.android.utils.http.excep.HttpErrorException;
import com.arraylist7.android.utils.http.excep.HttpException;

public interface IHttpListener {

    public void onStart(HttpRequest request);

    public void onSuccess(String html,HttpResponse response);

    public void onException(HttpException exception);

    public void onEnd();

}

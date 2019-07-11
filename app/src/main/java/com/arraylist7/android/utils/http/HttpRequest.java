package com.arraylist7.android.utils.http;

import com.arraylist7.android.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

    private String url;
    private String method = "GET";
    private int requestTimeout = 15000;
    private boolean isKeepCookie = true;
    private int readResponseTimeout = 15000;
    private List<String> cookies = new ArrayList<>();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, Object> postParameter = new HashMap<>();
    private List<File> files = new ArrayList<>();

    private String charset = "UTF-8";
    private String accept = "*/*";
    private String acceptEncoding = "gzip, deflate, br";
    private String userAgent = "Mozilla/5.0 (Windows NT 6.1; W…) Gecko/20100101 Firefox/67.0";


    public HttpRequest(String url, String charset) {
        this(url, charset, "GET");
    }

    public HttpRequest(String url, String charset, String method) {

        if(!url.startsWith("http")) {
            url = "http://"+url;
        }

        this.url = url;
        this.charset = charset;
        this.method = method;
    }


    public HttpRequest addCookie(String cookies) {
        addCookie(StringUtils.asList(cookies));
        return this;
    }

    public HttpRequest addCookie(List<String> cookies) {
        this.cookies.addAll(cookies);
        return this;
    }


    public HttpRequest addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }


    public HttpRequest addFile(String fullPath) {
        return addFile(new File(fullPath));
    }

    public HttpRequest addFile(File file) {
        this.files.add(file);
        return this;
    }

    public HttpRequest addPostParameter(String key, Object val) {
        this.postParameter.put(key, val);
        return this;
    }

    public boolean isKeepCookie() {
        return isKeepCookie;
    }

    public void setKeepCookie(boolean keepCookie) {
        isKeepCookie = keepCookie;
    }

    public List<String> getCookies() {
        return cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public List<File> getFiles() {
        return files;
    }

    public Map<String, Object> getPostParameter() {
        return postParameter;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method.toUpperCase();
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getReadResponseTimeout() {
        return readResponseTimeout;
    }

    public void setReadResponseTimeout(int readResponseTimeout) {
        this.readResponseTimeout = readResponseTimeout;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}

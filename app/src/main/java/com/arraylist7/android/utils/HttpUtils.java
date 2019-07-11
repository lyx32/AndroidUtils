package com.arraylist7.android.utils;

import android.app.DownloadManager;
import android.os.Build;
import android.util.Log;

import com.arraylist7.android.utils.http.HttpRequest;
import com.arraylist7.android.utils.http.HttpResponse;
import com.arraylist7.android.utils.http.callback.HttpListenerImpl;
import com.arraylist7.android.utils.http.callback.IHttpListener;
import com.arraylist7.android.utils.http.excep.HttpErrorException;
import com.arraylist7.android.utils.http.excep.HttpException;
import com.arraylist7.android.utils.http.excep.HttpTimeoutException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class HttpUtils {


    private static ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(3);
    private static Map<String, List<String>> cookies = new HashMap<>();
    private static IHttpListener globalListener = new HttpListenerImpl();

    static {
        threadPool.setMaximumPoolSize(7);
        threadPool.setKeepAliveTime(30, TimeUnit.SECONDS);
    }


    public static HttpResponse request(HttpRequest request) {
        if (null != globalListener)
            globalListener.onStart(request);
        HttpURLConnection conn = null;
        HttpResponse response = null;
        try {
            conn = getConn(request);
            response = getResponse(request, conn);
            if (null != globalListener)
                globalListener.onSuccess(response.getResult(), response);
        } catch (HttpException e) {
            e.printStackTrace();
            if (null != globalListener)
                globalListener.onException(e);
        }
        if (null != globalListener)
            globalListener.onEnd();
        return response;
    }

    public static void request(final HttpRequest request, final IHttpListener listener) {
        if (null != globalListener)
            globalListener.onStart(request);
        if (null != listener)
            listener.onStart(request);
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                HttpResponse response = null;
                try {
                    conn = getConn(request);
                    response = getResponse(request, conn);
                    if (null != globalListener)
                        globalListener.onSuccess(response.getResult(), response);
                    if (null != listener)
                        listener.onSuccess(response.getResult(), response);
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (null != globalListener)
                        globalListener.onException(e);
                    if (null != listener)
                        listener.onException(e);
                }
                if (null != globalListener)
                    globalListener.onEnd();
                if (null != listener)
                    listener.onEnd();
            }
        });
    }


    private static HttpURLConnection getConn(HttpRequest request) throws HttpException {
        URL url = null;
        URLConnection connection = null;
        try {
            url = new URL(request.getUrl());
            connection = url.openConnection();
        } catch (IOException e) {
            throw new HttpErrorException("请求服务器异常", e);
        }
        HttpURLConnection conn = null;
        if (request.getUrl().startsWith("https://")) {
            conn = (HttpsURLConnection) connection;
        } else {
            conn = (HttpURLConnection) connection;
        }
        conn.setUseCaches(true);

        conn.addRequestProperty("Accept", request.getAccept());
        conn.addRequestProperty("AcceptEncoding", request.getAcceptEncoding());
        conn.addRequestProperty("UserAgent", request.getUserAgent());
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
        conn.setRequestProperty("Pragma", "no-cache");


        Map<String, String> headers = request.getHeaders();
        for (String key : headers.keySet()) {
            conn.addRequestProperty(key, headers.get(key));
        }
        String domain = getDomain(request.getUrl());
        List<String> cookie = getCookies(domain);
//        conn.addRequestProperty("HOST", url.getHost());
        StringBuffer cookiesBuffer = new StringBuffer();
        if (StringUtils.len(cookie) > 0) {
            for (String item : cookie)
                cookiesBuffer.append(item + ";");
            for (String item : request.getCookies())
                cookiesBuffer.append(item + ";");
        }
        if (0 < cookiesBuffer.length())
            conn.addRequestProperty("Cookie", cookiesBuffer.toString());
        try {
            conn.setRequestMethod(request.getMethod());
        } catch (ProtocolException e) {
            throw new HttpErrorException("无效的请求方式：" + request.getMethod(), e);
        }
        conn.setConnectTimeout(request.getRequestTimeout());
        conn.setReadTimeout(request.getReadResponseTimeout());
        conn.setInstanceFollowRedirects(true);


        conn.setDoInput(true);
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            conn.addRequestProperty("Content-Type", "text/html");
        }
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            conn.setDoOutput(true);
            if (0 == StringUtils.len(request.getFiles())) {
                conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                StringBuffer paramster = new StringBuffer();
                Map<String, Object> params = request.getPostParameter();
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    paramster.append(key + "=" + params.get(key));
                }
                OutputStream out = null;
                try {
                    out = conn.getOutputStream();
                    out.write(paramster.toString().getBytes(request.getCharset()));
                } catch (IOException e) {
                    IOUtils.close(out);
                }
            } else {
                String uuid = UUID.randomUUID().toString(); //边界标识 随机生成
                String prefix = "--", line_end = "\r\n";
                conn.addRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + uuid);
                OutputStream out = null;
                try {
                    out = conn.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                DataOutputStream dos = new DataOutputStream(out);

                StringBuffer sb = new StringBuffer();
                Map<String, Object> params = request.getPostParameter();
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    sb.setLength(0);
                    sb.append(prefix + uuid + line_end);
                    sb.append("Content-Disposition: form-data; name=\"" + key + "\"; " + line_end + params.get(keys) + line_end);
                    try {
                        dos.write(sb.toString().getBytes());
                        dos.write(line_end.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (File file : request.getFiles()) {
                    sb.setLength(0);
                    sb.append(prefix + uuid + line_end);
                    sb.append("Content-Disposition: form-data; name=\"files\"; filename=\"" + file.getName() + "\"" + line_end);
                    if (Build.VERSION.SDK_INT >= 26) {
                        try {
                            String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
                            sb.append("Content-Type: " + contentType + line_end);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    sb.append(line_end);
                    InputStream fin = null;
                    try {
                        dos.write(sb.toString().getBytes());
                        IOUtils.readAndWriterAndNoClose(fin = new FileInputStream(file), dos);
                        dos.write(line_end.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        IOUtils.close(fin);
                    }
                }
                try {
                    byte[] end_data = (prefix + uuid + prefix + line_end).getBytes(request.getCharset());
                    dos.write(end_data);
                    dos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.close(dos);
                }
            }
        }
        return conn;
    }

    private static HttpResponse getResponse(HttpRequest request, HttpURLConnection connection) throws HttpException {
        HttpResponse response = new HttpResponse();

        try {
            LogUtils.e("code=" + connection.getResponseCode());
            if (302 == connection.getResponseCode()) {
                //如果会重定向，保存302重定向地址，以及Cookies,然后重新发送请求(模拟请求)
                String location = connection.getHeaderField("Location");
                String cookies = connection.getHeaderField("Set-Cookie");
                LogUtils.e("location=" + location + "      cookies=" + cookies);
                request.setUrl(location);
                if (!StringUtils.isNullOrEmpty(cookies))
                    request.addCookie(cookies);
                return getResponse(request, getConn(request));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String charset = "UTF-8";

        Pattern pattern = Pattern.compile("charset=\\S*");
        Matcher matcher = pattern.matcher(connection.getContentType());
        if (matcher.find()) {
            charset = matcher.group().replace("charset=", "");
        }
        response.setCharset(charset);
        InputStreamReader inputStream = null;
        BufferedReader reader = null;
        try {
            response.setHttpStatusCode(connection.getResponseCode());
            response.setContentLength(connection.getContentLength());
            InputStream connIn = null;
            if (connection.getResponseCode() >= 400)
                connIn = connection.getErrorStream();
            else
                connIn = connection.getInputStream();

            inputStream = new InputStreamReader(connIn, charset);
            reader = new BufferedReader(inputStream);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            response.setResult(sb.toString());
        } catch (IOException e) {
            throw new HttpTimeoutException("获取返回失败", e);
        } finally {
            IOUtils.close(inputStream);
            IOUtils.close(reader);
        }
        Map<String, List<String>> header = connection.getHeaderFields();
        response.setHeaders(header);
        Set<String> headerKeys = header.keySet();
        List<String> responseCookies = new ArrayList<>();
        for (String key : headerKeys) {
            if ("Set-Cookie".equalsIgnoreCase(key)) {
                List<String> headerItems = header.get(key);
                for (String c : headerItems) {
                    responseCookies.add(c.split(";")[0]);
                }
            }
        }

        if (request.isKeepCookie()) {
            String domain = getDomain(request.getUrl());
            List<String> list = new ArrayList<>();
            List<String> localCookies = getCookies(domain);
            Map<String, String> saveCookies = new HashMap<>();
            Map<String, String> updateCookies = new HashMap<>();
            for (String item : localCookies) {
                saveCookies.put(item.split("=")[0], item);
            }
            for (String item : responseCookies) {
                String key = item.split("=")[0];
                updateCookies.put(key, item);
                list.add(item);
            }

            for (String key : saveCookies.keySet()) {
                if (!updateCookies.containsKey(key))
                    list.add(saveCookies.get(key));
            }
            updateCookies.clear();
            saveCookies.clear();
            updateCookies(domain, list);
        }
        response.setCookies(responseCookies);
        return response;
    }

    private static String getDomain(String url) {
        if (StringUtils.isNullOrEmpty(url))
            return "";

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        int index = url.replace("://", ",,,").indexOf("/");
        if (-1 == index)
            index = url.length();
        return url.substring(0, index);
    }

    private static List<String> getCookies(String domain) {
        List<String> cookie = null;
        if (StringUtils.isNullOrEmpty(domain)) {
            cookie = new ArrayList<>();
        } else {
            cookie = cookies.get(domain);
            if (null == cookie) {
                cookie = new ArrayList<>();
                cookies.put(domain, cookie);
            }
        }
        return cookie;
    }

    private static void updateCookies(String domain, List<String> newCookies) {
        if (!StringUtils.isNullOrEmpty(domain)) {
            cookies.remove(domain);
            cookies.put(domain, newCookies);
        }
    }

}


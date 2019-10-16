package com.arraylist7.android.utils;

import android.content.Context;

import com.arraylist7.android.utils.listener.ThreadStateListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadUtils {

    private static final long DEFAULT_TIMEOUT = 15000;
    private static ExecutorService execThreadPools = new ThreadPoolExecutor(0, 128, DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
    private static volatile Map<Context, List<Future<?>>> map = new HashMap<>();

    private ThreadUtils() {
    }


    public static void submit(Context tag, Runnable runnable) {
        submit(tag, new FutureTask(runnable, null), DEFAULT_TIMEOUT);
    }

    public static void submit(Context tag, Runnable runnable, long timeout) {
        submit(tag, new FutureTask(runnable, null), timeout, null);
    }

    public static <T> void submit(Context tag, Runnable runnable, long timeout, ThreadStateListener<T> listener) {
        submit(tag, new FutureTask(runnable, null), timeout, listener);
    }

    public static <T> void submit(Context tag, Callable<T> callable) {
        submit(tag, new FutureTask(callable), DEFAULT_TIMEOUT);
    }

    public static <T> void submit(Context tag, Callable<T> callable, long timeout) {
        submit(tag, new FutureTask(callable), timeout);
    }

    public static <T> void submit(Context tag, Callable<T> callable, ThreadStateListener<T> listener) {
        submit(tag, new FutureTask(callable), DEFAULT_TIMEOUT, listener);
    }


    public static <T> void submit(Context tag, Callable<T> callable, long timeout, ThreadStateListener<T> listener) {
        submit(tag, new FutureTask(callable), timeout, listener);
    }


    private static <T> void submit(final Context tag, final FutureTask<T> futureTask, final long timeout, final ThreadStateListener<T> listener) {
        if (null == futureTask)
            return;
        if (!map.containsKey(tag)) {
            map.put(tag, new ArrayList<Future<?>>());
        }
        execThreadPools.execute(new Runnable() {
            @Override
            public void run() {
                map.get(tag).add(futureTask);
                try {
                    execThreadPools.execute(futureTask);
                    T resultObject = (T) futureTask.get(timeout, TimeUnit.MILLISECONDS);
                    listener.done(resultObject);
                } catch (Exception e) {
                    futureTask.cancel(true);
                    if (e instanceof TimeoutException) {
                        listener.timeout();
                    } else {
                        listener.exception(e);
                    }
                } finally {
                    map.get(tag).remove(futureTask);
                }
            }
        });
    }


    public static void cancel(Context tag) {
        synchronized (map) {
            List<Future<?>> list = map.get(tag);
            if (null != list) {
                for (Future<?> future : list) {
                    if (!future.isDone()) {
                        future.cancel(true);
                    }
                }
            }
            map.remove(tag);
        }
    }
}
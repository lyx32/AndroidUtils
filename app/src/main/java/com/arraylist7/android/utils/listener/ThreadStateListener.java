package com.arraylist7.android.utils.listener;

/**
 * 线程状态监听
 */
public interface ThreadStateListener<T> {

    /**
     * 线程执行完毕
     * @param threadReturnValue 线程返回值
     */
    public void done(T threadReturnValue);



    /**
     * 线程超时
     */
    public void timeout();

    /**
     * 线程内部异常
     */
    public void exception(Exception e);

}

package com.pipedog.hermes.executor.base;

import com.google.gson.Gson;
import com.pipedog.hermes.request.Request;
import com.pipedog.hermes.cache.ICacheStorage;
import com.pipedog.hermes.utils.ThreadUtils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

/**
 * @author liang
 * @time 2022/05/31
 * @desc 请求执行器抽象类（网络请求是一个具有时序性的操作，虽然会涉及到线程切换，
 *      但一般情况不会出现资源竞态访问的情况，因此，这里的大部分代码都没有进行过
 *      多的线程安全保护，但却可以安全的执行）
 */
public abstract class AbstractExecutor {

    public static final int HTTP_STATUS_OK = 200;

    public interface ExecutorListener {
        void onResult(boolean success, String error);
    }

    protected Request request;
    protected OkHttpClient okHttpClient;
    protected Call okCall;
    protected Callback okCallback;
    protected Gson gson;
    protected ICacheStorage cacheStorage;
    protected ExecutorListener executorListener;
    protected int currentRetryTimes = 0;

    public AbstractExecutor(OkHttpClient okHttpClient, Request request) {
        this.okHttpClient = okHttpClient;
        this.request = request;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setCacheStorage(ICacheStorage cacheStorage) {
        this.cacheStorage = cacheStorage;
    }

    public void setExecutorListener(ExecutorListener listener) {
        this.executorListener = listener;
    }

    public boolean isExecuted() {
        if (okCall == null) {
            return false;
        }
        return okCall.isExecuted();
    }

    public void cancel() {
        if (okCall != null) {
            okCall.cancel();
        }

        executorListener.onResult(false, "User cancel!");
    }

    public boolean isCanceled() {
        if (okCall == null) {
            return false;
        }
        return okCall.isCanceled();
    }

    public abstract void execute();


    // PROTECTED METHODS

    protected void executeOnCallbackThread(Runnable r) {
        if (request.isCallbackOnMainThread()) {
            ThreadUtils.runInMainThread(r);
        } else {
            r.run();
        }
    }

    /**
     * 自动重试
     * @return  true - 将要进行重试，不要给 Request 进行回调
     *          false - 不进行重试（或重试完成），可以对 Request 进行回调
     */
    protected boolean autoRetryIfNeeded() {
        if (currentRetryTimes < request.getAutoRetryTimes()) {
            currentRetryTimes ++;
            okCall.enqueue(okCallback);
            return true;
        }

        return false;
    }

    protected void onResult(boolean success, String error) {
        if (executorListener != null) {
            executorListener.onResult(false, error);
        }
    }

}

package com.pipedog.hermes.request;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.pipedog.hermes.manager.IRequestFilter;
import com.pipedog.hermes.manager.NetworkManager;
import com.pipedog.hermes.request.interfaces.IDownloadSettings;
import com.pipedog.hermes.request.interfaces.IMultipartBody;
import com.pipedog.hermes.request.interfaces.RequestSettings;
import com.pipedog.hermes.request.internal.HermesMultipartBody;
import com.pipedog.hermes.request.listener.IDownloadListener;
import com.pipedog.hermes.request.listener.IResultListener;
import com.pipedog.hermes.request.listener.IUploadListener;
import com.pipedog.hermes.enums.CachePolicy;
import com.pipedog.hermes.enums.RequestType;
import com.pipedog.hermes.enums.SerializerType;
import com.pipedog.hermes.utils.RequestIDGenerator;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liang
 * @time 2022/05/23
 * @desc 请求实体包装
 */
public class Request implements RequestSettings {

    // PRIVATE VARIABLES

    private Object extra;
    private RequestType requestType = RequestType.POST;
    private String baseUrl;
    private String urlPath;
    private Map<String, Object> parameters = new HashMap<>();
    private CachePolicy cachePolicy = CachePolicy.RELOAD_IGNORE_CACHE_DATA;
    private Map<String, String> requestHeaders = new HashMap<>();
    private SerializerType serializerType = SerializerType.HTTP;
    private int autoRetryTimes = 1;
    private boolean callbackOnMainThread = false;
    private Class<?> responseClass;

    private AtomicBoolean executing = new AtomicBoolean(false);
    private String requestID;
    private IResultListener resultListener;
    private IDownloadListener downloadListener;
    private IUploadListener uploadListener;
    private HermesMultipartBody multipartBody;
    private IDownloadSettings downloadSettings;
    private LifecycleEventObserverImpl lifecycleObserver;


    // PUBLIC STATIC METHODS

    /**
     * 取消请求
     */
    public static void cancelRequests(IRequestFilter filter) {
        NetworkManager.getInstance().cancelRequests(filter);
    }


    // CONSTRUCTORS

    /**
     * Request 构造接口
     */
    public static interface Builder {
        void onBuild(RequestSettings settings);
    }

    /**
     * Request 构造方法
     */
    public static Request build(Builder builder) {
        Request request = new Request();
        builder.onBuild(request);
        return request;
    }

    /**
     * 强制使用 build(Builder) 函数代替
     */
    private Request() {
    }


    // PUBLIC METHODS

    /**
     * 设置普通 JSON 数据请求监听
     */
    public Request setResultListener(IResultListener listener) {
        resultListener = listener;
        return this;
    }

    /**
     * 设置下载监听
     * https://juejin.cn/post/6844903970201305095
     */
    public Request setDownloadListener(IDownloadListener listener) {
        downloadListener = listener;
        return this;
    }

    /**
     * 设置上传监听
     */
    public Request setUploadListener(IUploadListener listener) {
        uploadListener = listener;
        return this;
    }

    /**
     * 设置下载相关配置
     */
    public Request setDownloadSettings(IDownloadSettings settings) {
        downloadSettings = settings;
        return this;
    }

    /**
     * 添加上传数据
     */
    public Request setMultipartBody(IMultipartBody.Builder builder) {
        multipartBody = new HermesMultipartBody();
        builder.onBuild(multipartBody);
        return this;
    }

    /**
     * 发送请求
     */
    public Request send() {
        NetworkManager.getInstance().addRequest(this);
        return this;
    }

    /**
     * 取消请求
     */
    public void cancel() {
        NetworkManager.getInstance().cancelRequest(this);
    }

    /**
     * 请求实例销毁
     */
    public void destory() {
        if (resultListener != null) {
            resultListener = null;
        }
        if (downloadListener != null) {
            downloadListener = null;
        }
        if (uploadListener != null) {
            uploadListener = null;
        }
        if (multipartBody != null) {
            multipartBody = null;
        }
        if (downloadSettings != null) {
            downloadSettings = null;
        }
        if (lifecycleObserver != null) {
            lifecycleObserver = null;
        }
    }


    // SETTER METHODS

    @Override
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new RuntimeException("The argument `parameters` can not be null!");
        }
        this.parameters = parameters;
    }

    @Override
    public void setCachePolicy(CachePolicy cachePolicy) {
        this.cachePolicy = cachePolicy;
    }

    @Override
    public void setRequestHeaders(Map<String, String> requestHeaders) {
        if (requestHeaders == null) {
            throw new RuntimeException("The argument `requestHeaders` can not be null!");
        }
        this.requestHeaders = requestHeaders;
    }

    @Override
    public void setSerializerType(SerializerType serializerType) {
        this.serializerType = serializerType;
    }

    @Override
    public void setAutoRetryTimes(int autoRetryTimes) {
        this.autoRetryTimes = autoRetryTimes;
    }

    @Override
    public void setCallbackOnMainThread(boolean callbackOnMainThread) {
        this.callbackOnMainThread = callbackOnMainThread;
    }

    @Override
    public void setResponseClass(Class responseClass) {
        this.responseClass = responseClass;
    }

    @Override
    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public void setExecuting(boolean executing) {
        this.executing.set(executing);
    }

    @Override
    public void bindLifecycle(Lifecycle lifecycle) {
        if (lifecycle == null) {
            return;
        }

        lifecycleObserver = new LifecycleEventObserverImpl(this);
        lifecycle.addObserver(lifecycleObserver);
    }


    // GETTER METHODS

    public RequestType getRequestType() {
        return requestType;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public CachePolicy getCachePolicy() {
        return cachePolicy;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public SerializerType getSerializerType() {
        return serializerType;
    }

    public int getAutoRetryTimes() {
        return autoRetryTimes;
    }

    public boolean isCallbackOnMainThread() {
        return callbackOnMainThread;
    }

    public Class getResponseClass() {
        return responseClass;
    }

    public Object getExtra() {
        return extra;
    }

    public boolean isExecuting() {
        return executing.get();
    }

    public String getRequestID() {
        if (requestID == null) {
            requestID = RequestIDGenerator.getRequestID();
        }
        return requestID;
    }

    public IResultListener getResultListener() {
        return resultListener;
    }

    public IDownloadListener getDownloadListener() {
        return downloadListener;
    }

    public IUploadListener getUploadListener() {
        return uploadListener;
    }

    public HermesMultipartBody getFormData() {
        return multipartBody;
    }

    public IDownloadSettings getDownloadSettings() {
        return downloadSettings;
    }


    // PRIVATE STATIC CLASSES

    private static class LifecycleEventObserverImpl implements LifecycleEventObserver {
        private WeakReference<Request> weakReference;

        public LifecycleEventObserverImpl(Request request) {
            this.weakReference = new WeakReference<>(request);
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            Request request = weakReference.get();
            if (request == null) { return; }

            switch (event) {
                case ON_DESTROY: {
                    request.destory();
                } break;
                default: break;
            }
        }
    }

}

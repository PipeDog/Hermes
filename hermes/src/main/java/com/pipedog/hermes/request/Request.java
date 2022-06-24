package com.pipedog.hermes.request;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.pipedog.hermes.manager.NetworkManager;
import com.pipedog.hermes.request.interfaces.IDownloadSettings;
import com.pipedog.hermes.request.interfaces.IMultipartBody;
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
public class Request {

    private Object extra;
    private RequestType requestType = RequestType.POST;
    private String baseUrl;
    private String urlPath;
    private Map<String, Object> parameters = new HashMap<>();
    private CachePolicy cachePolicy = CachePolicy.RELOAD_IGNORE_CACHE_DATA;
    private Map<String, String> requestHeaders = new HashMap<>();
    private SerializerType serializerType = SerializerType.HTTP;
    private int autoRetryTimes = 1;
    private boolean callbackOnMainThread = true;
    private Class<?> responseClass;

    private AtomicBoolean executing = new AtomicBoolean(false);
    private String requestID;
    private IResultListener resultListener;
    private IDownloadListener downloadListener;
    private IUploadListener uploadListener;
    private HermesMultipartBody multipartBody;
    private IDownloadSettings downloadSettings;
    private LifecycleObserverImpl lifecycleObserver;


    // CONSTRUCTORS

    public static Request build(BuildBlock block) {
        Builder builder = new Builder();
        return block.onBuild(builder);
    }

    private Request(
            Object extra, RequestType requestType, String baseUrl,
            String urlPath, Map<String, Object> parameters, CachePolicy cachePolicy,
            Map<String, String> requestHeaders, SerializerType serializerType, int autoRetryTimes,
            boolean callbackOnMainThread, Class<?> responseClass, Lifecycle lifecycle) {
        this.extra = extra;
        this.requestType = requestType;
        this.baseUrl = baseUrl;
        this.urlPath = urlPath;
        this.parameters = parameters;
        this.cachePolicy = cachePolicy;
        this.requestHeaders = requestHeaders;
        this.serializerType = serializerType;
        this.autoRetryTimes = autoRetryTimes;
        this.callbackOnMainThread = callbackOnMainThread;
        this.responseClass = responseClass;

        if (lifecycle != null) {
            this.lifecycleObserver = new LifecycleObserverImpl(this);
            lifecycle.addObserver(this.lifecycleObserver);
        }
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

    public void setExecuting(boolean executing) {
        this.executing.set(executing);
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


    // CUSTOM CLASSES && INTERFACES

    private static class LifecycleObserverImpl implements LifecycleObserver {
        private WeakReference<Request> weakReference;

        public LifecycleObserverImpl(Request request) {
            this.weakReference = new WeakReference<>(request);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestory() {
            Request request = weakReference.get();
            if (request != null) { request.destory(); }
        }
    }

    public static class Builder {

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
        private WeakReference<Lifecycle> lifecycle;

        public Builder() {
        }

        public Builder extra(Object extra) {
            this.extra = extra;
            return this;
        }

        public Builder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder urlPath(String urlPath) {
            this.urlPath = urlPath;
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder cachePolicy(CachePolicy cachePolicy) {
            this.cachePolicy = cachePolicy;
            return this;
        }

        public Builder requestHeaders(Map<String, String> requestHeaders) {
            this.requestHeaders = requestHeaders;
            return this;
        }

        public Builder serializerType(SerializerType serializerType) {
            this.serializerType = serializerType;
            return this;
        }

        public Builder autoRetryTimes(int autoRetryTimes) {
            this.autoRetryTimes = autoRetryTimes;
            return this;
        }

        public Builder callbackOnMainThread(boolean callbackOnMainThread) {
            this.callbackOnMainThread = callbackOnMainThread;
            return this;
        }

        public Builder responseClass(Class<?> responseClass) {
            this.responseClass = responseClass;
            return this;
        }

        public Builder lifecycle(Lifecycle lifecycle) {
            this.lifecycle = new WeakReference<>(lifecycle);
            return this;
        }

        public Request build() {
            return new Request(
                    extra, requestType, baseUrl, urlPath,
                    parameters, cachePolicy, requestHeaders, serializerType,
                    autoRetryTimes, callbackOnMainThread, responseClass,
                    lifecycle == null ? null : lifecycle.get());
        }

    }

    public static interface BuildBlock {
        Request onBuild(Builder builder);
    }

}

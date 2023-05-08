package com.pipedog.hermes.request;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.pipedog.hermes.manager.Hermes;
import com.pipedog.hermes.enums.CachePolicy;
import com.pipedog.hermes.enums.RequestType;
import com.pipedog.hermes.enums.SerializerType;
import com.pipedog.hermes.response.Callback;
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

    private String baseUrl;
    private String urlPath;
    private Map<String, String> headers;
    private Map<String, Object> parameters;
    private MultipartFormData multipartFormData;
    private String targetPath;
    private RequestType requestType;
    private SerializerType serializerType;

    private CachePolicy cachePolicy;
    private Class<?> responseClass;
    private int autoRetryTimes;
    private boolean callbackOnMainThread;
    private Object extra;

    private Callback callback;
    private WeakReference<Lifecycle> lifecycle;
    private LifecycleObserverImpl lifecycleObserver;
    private volatile AtomicBoolean executing = new AtomicBoolean(false);
    private String requestID;


    // CONSTRUCTORS

    private Request(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.urlPath = builder.urlPath;
        this.headers = builder.headers;
        this.parameters = builder.parameters;
        this.multipartFormData = builder.multipartFormData;
        this.targetPath = builder.targetPath;
        this.requestType = builder.requestType;
        this.serializerType = builder.serializerType;

        this.cachePolicy = builder.cachePolicy;
        this.responseClass = builder.responseClass;
        this.autoRetryTimes = builder.autoRetryTimes;
        this.callbackOnMainThread = builder.callbackOnMainThread;
        this.extra = builder.extra;

        this.lifecycle = builder.lifecycle;
        if (this.lifecycle != null) {
            Lifecycle realLifecycle = lifecycle.get();
            if (realLifecycle != null) {
                this.lifecycleObserver = new LifecycleObserverImpl(this);
                realLifecycle.addObserver(this.lifecycleObserver);
            }
        }
    }


    // PUBLIC METHODS

    /**
     * 发送请求
     */
    public <T> Request call(Callback<T> callback) {
        this.callback = callback;
        Hermes.getInstance().addRequest(this);
        return this;
    }

    /**
     * 取消请求
     */
    public void cancel() {
        Hermes.getInstance().cancelRequest(this);
    }


    // SETTER METHODS

    public void setExecuting(boolean executing) {
        this.executing.set(executing);
    }


    // GETTER METHODS

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public MultipartFormData getMultipartFormData() {
        return multipartFormData;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public SerializerType getSerializerType() {
        return serializerType;
    }

    public CachePolicy getCachePolicy() {
        return cachePolicy;
    }

    public Class<?> getResponseClass() {
        return responseClass;
    }

    public int getAutoRetryTimes() {
        return autoRetryTimes;
    }

    public boolean isCallbackOnMainThread() {
        return callbackOnMainThread;
    }

    public Object getExtra() {
        return extra;
    }

    public Callback getCallback() {
        return callback;
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


    // CUSTOM CLASSES && INTERFACES

    private static class LifecycleObserverImpl implements LifecycleObserver {
        private WeakReference<Request> weakReference;

        public LifecycleObserverImpl(Request request) {
            this.weakReference = new WeakReference<>(request);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestory() {
            Request request = weakReference.get();
            if (request != null) {
                request.cancel();
            }
        }
    }

    public static class Builder {
        private String baseUrl = "";
        private String urlPath = "";
        private Map<String, String> headers = new HashMap<>();
        private Map<String, Object> parameters = new HashMap<>();
        private MultipartFormData multipartFormData;
        private String targetPath;
        private RequestType requestType = RequestType.POST;
        private SerializerType serializerType = SerializerType.HTTP;

        private CachePolicy cachePolicy = CachePolicy.RELOAD_IGNORE_CACHE_DATA;
        private Class<?> responseClass;
        private int autoRetryTimes = 1;
        private boolean callbackOnMainThread = true;
        private Object extra;
        private WeakReference<Lifecycle> lifecycle;

        public Builder() {
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder urlPath(String urlPath) {
            this.urlPath = urlPath;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder multipartFormData(MultipartFormData multipartFormData) {
            this.multipartFormData = multipartFormData;
            return this;
        }

        public Builder targetPath(String targetPath) {
            this.targetPath = targetPath;
            return this;
        }

        public Builder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder serializerType(SerializerType serializerType) {
            this.serializerType = serializerType;
            return this;
        }

        public Builder cachePolicy(CachePolicy cachePolicy) {
            this.cachePolicy = cachePolicy;
            return this;
        }

        public Builder responseClass(Class<?> responseClass) {
            this.responseClass = responseClass;
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

        public Builder extra(Object extra) {
            this.extra = extra;
            return this;
        }

        public Builder lifecycle(Lifecycle lifecycle) {
            this.lifecycle = new WeakReference<>(lifecycle);
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }

}

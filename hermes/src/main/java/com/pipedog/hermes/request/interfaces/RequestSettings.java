package com.pipedog.hermes.request.interfaces;

import android.content.Context;

import com.pipedog.hermes.enums.CachePolicy;
import com.pipedog.hermes.enums.RequestType;
import com.pipedog.hermes.enums.SerializerType;

import java.util.Map;

/**
 * @author liang
 * @time 2022/05/24
 * @desc Request 构造器协议
 */
public interface RequestSettings {

    /**
     * 设置请求类型
     */
    void setRequestType(RequestType requestType);

    /**
     * 设置 baseUrl，支持的格式如：
     *  https://www.pipedog.cn
     *  https://www.pipedog.cn/study/detail
     */
    void setBaseUrl(String baseUrl);

    /**
     * 设置 url 路径，如：
     *  /study/detail
     */
    void setUrlPath(String urlPath);

    /**
     * 设置 query 或 body 参数
     */
    void setParameters(Map<String, Object> parameters);

    /**
     * 设置缓存模式
     */
    void setCachePolicy(CachePolicy cachePolicy);

    /**
     * 设置请求 header
     */
    void setRequestHeaders(Map<String, String> requestHeaders);

    /**
     * 设置序列化模式，主要针对 header 中的 Content-Type 字段
     */
    void setSerializerType(SerializerType serializerType);

    /**
     * 设置自动重试次数
     */
    void setAutoRetryTimes(int autoRetryTimes);

    /**
     * 设置是否在主线程进行回调
     */
    void setCallbackOnMainThread(boolean callbackOnMainThread);

    /**
     * 设置响应数据类型
     */
    void setResponseClass(Class responseClass);

    /**
     * 扩展字段，可以在这里设置一些附加内容
     */
    void setExtra(Object extra);

}

package com.pipedog.hermes.utils;

import com.pipedog.hermes.log.Logger;
import com.pipedog.hermes.request.Request;

import java.util.Map;
import java.util.TreeMap;

import okhttp3.HttpUrl;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 请求工具包装
 */
public class RequestUtils {

    /**
     * 类型为确定
     */
    public static final int REQUEST_MODE_NOT_DETERMINE = -1;

    /**
     * 普通 json 数据请求
     */
    public static final int REQUEST_MODE_GENERAL = 0;

    /**
     * 上传请求
     */
    public static final int REQUEST_MODE_UPLOAD = 1;

    /**
     * 下载请求
     */
    public static final int REQUEST_MODE_DOWNLOAD = 2;

    /**
     * 获取请求模式（上传、下载、普通 json 请求）
     */
    public static int getRequestMode(Request request) {
        if (request == null) {
            return REQUEST_MODE_NOT_DETERMINE;
        }
        if (request.getResultListener() != null) {
            return REQUEST_MODE_GENERAL;
        }
        if (request.getUploadListener() != null) {
            return REQUEST_MODE_UPLOAD;
        }
        if (request.getDownloadListener() != null) {
            return REQUEST_MODE_DOWNLOAD;
        }
        return REQUEST_MODE_NOT_DETERMINE;
    }

    /**
     * 获取 Request 的完整 url
     */
    public static String getFullUrl(Request request) {
        String url = request.getBaseUrl() + request.getUrlPath();
        Map<String, Object> parameters = request.getParameters();
        return getFullUrl(url, parameters);
    }

    /**
     * 将 query 参数拼接到 url 后边，形成完整的 url
     */
    public static String getFullUrl(String url, Map<String, Object> parameters) {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();

        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                builder.removeAllQueryParameters(entry.getKey());

                Object value = entry.getValue();

                if (!(value instanceof String)) {
                    // eg:
                    //  jsonString  => {"code":0,"data":{"currentPage":1,"list":["Amy","Bob","Tom"]},"message":"success"}
                    //  toString    => {code=0, data={currentPage=1, list=[Amy, Bob, Tom]}, message=success}
                    value = value.toString();
                }

                builder.addQueryParameter(entry.getKey(), (String) value);
            }
        }

        String fullUrl = builder.build().toString();
        Logger.info("request : %s", fullUrl);
        return fullUrl;
    }

    public static String getCacheID(Request request) {
        String url = request.getBaseUrl() + request.getUrlPath();

        if (request.getParameters() != null && request.getParameters().size() > 0) {
            TreeMap<String, Object> orderedParameters = new TreeMap<>(request.getParameters());
            String parametersString = orderedParameters.toString();
            url += ("&params=" + parametersString);
        }

        if (request.getRequestHeaders() != null && request.getRequestHeaders().size() > 0) {
            TreeMap<String, String> orderedHeaders = new TreeMap<>(request.getRequestHeaders());
            String headersString = orderedHeaders.toString();
            url += ("&headers=" + headersString);
        }

        String cacheId = SecretUtils.getMD5Result(url);
        Logger.info("url : %s, cacheId : %s.", url, cacheId);
        return cacheId;
    }

}

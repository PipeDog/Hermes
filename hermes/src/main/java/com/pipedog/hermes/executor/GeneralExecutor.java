package com.pipedog.hermes.executor;

import com.pipedog.hermes.enums.CachePolicy;
import com.pipedog.hermes.executor.base.AbstractExecutor;
import com.pipedog.hermes.log.Logger;
import com.pipedog.hermes.request.Request;
import com.pipedog.hermes.enums.RequestType;
import com.pipedog.hermes.enums.SerializerType;
import com.pipedog.hermes.response.ResultResponse;
import com.pipedog.hermes.response.internal.ResultResponseImpl;
import com.pipedog.hermes.utils.AssertHandler;
import com.pipedog.hermes.utils.JsonUtils;
import com.pipedog.hermes.utils.RequestUtils;
import com.pipedog.hermes.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 普通拉取 json 数据的执行器
 */
public class GeneralExecutor extends AbstractExecutor {

    public GeneralExecutor(OkHttpClient okHttpClient, Request request) {
        super(okHttpClient, request);
    }

    @Override
    public void execute() {
        if (isExecuted()) {
            return;
        }

        boolean continueRequesting = callbackCacheDataIfNeeded();
        if (!continueRequesting) {
            return;
        }

        okhttp3.Request okRequest = null;

        if (request.getRequestType() == RequestType.GET) {
            okRequest = createGetRequest();
        } else if (request.getRequestType() == RequestType.POST) {
            okRequest = createPostRequest();
        } else {
            throw new RuntimeException("Invalid requestType!");
        }

        okCall = okHttpClient.newCall(okRequest);
        okCallback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                executeOnCallbackThread(() -> {
                    onRequestFailure(e, null);
                    onResult(false, e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                handleResponse(response);
            }
        };

        okCall.enqueue(okCallback);
    }


    // PRIVATE METHODS

    private okhttp3.Request createGetRequest() {
        // [GET] fullUrl = baseUrl + urlPath + parameters
        String fullUrl = RequestUtils.getFullUrl(request);
        fullUrl = UrlUtils.getEncodedUrl(fullUrl);
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder().get().url(fullUrl);

        // headers
        Map<String, String> allHeaders = request.getRequestHeaders();
        for (Map.Entry<String, String> entry : allHeaders.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        okhttp3.Request okRequest = builder.build();
        return okRequest;
    }

    private okhttp3.Request createPostRequest() {
        // [POST] fullUrl = baseUrl + urlPath
        String fullUrl = request.getBaseUrl() + request.getUrlPath();
        fullUrl = UrlUtils.getEncodedUrl(fullUrl);
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder().url(fullUrl);

        // headers
        Map<String, String> allHeaders = request.getRequestHeaders();
        for (Map.Entry<String, String> entry : allHeaders.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        // application/x-www-form-urlencoded
        if (request.getSerializerType() == SerializerType.HTTP) {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            if (allHeaders.get("Content-Type") != null) {
                mediaType = MediaType.parse(allHeaders.get("Content-Type"));
            }

            // body eg:
            //      {code=0, data={currentPage=1, list=[Amy, Bob, Tom]}, message=success}
            RequestBody requestBody = RequestBody.Companion.create(request.getParameters().toString(), mediaType);
            okhttp3.Request okRequest = builder.post(requestBody).build();
            return okRequest;
        }

        // application/json; charset=utf-8
        else if (request.getSerializerType() == SerializerType.JSON) {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            if (allHeaders.get("Content-Type") != null) {
                mediaType = MediaType.parse(allHeaders.get("Content-Type"));
            }

            // body eg:
            //      {"code":0,"data":{"currentPage":1,"list":["Amy","Bob","Tom"]},"message":"success"}
            String jsonString = JsonUtils.toJSONString(request.getParameters());
            RequestBody requestBody = RequestBody.Companion.create(jsonString, mediaType);
            okhttp3.Request okRequest = builder.post(requestBody).build();
            return okRequest;
        }

        else {
            throw new RuntimeException("Invalid serializerType!");
        }
    }

    private void handleResponse(Response response) {
        ResponseBody responseBody = response.body();
        String responseString = null;
        try {
            responseString = responseBody.string();
        } catch (Exception e) {
            if (autoRetryIfNeeded()) {
                return;
            }

            executeOnCallbackThread(() -> {
                onRequestFailure(e, null);
                onResult(false, "Request success but parse failed.");
            });
            return;
        }

        final String finalResponseString = responseString;
        int code = response.code();
        String message = response.message();

        // statusCode != 200, failure
        if (response.code() != HTTP_STATUS_OK) {
            if (autoRetryIfNeeded()) {
                return;
            }

            executeOnCallbackThread(() -> {
                onRequestFailure(null, new ResultResponseImpl(
                        code, message, gson.fromJson(finalResponseString, Object.class)));
                onResult(false, "Request failed");
            });
            return;
        }

        // statusCode == 200, success
        String cacheId = RequestUtils.getCacheID(request);
        if (!cacheStorage.saveCache(cacheId, responseString)) {
            Logger.error("Save cache failed, cacheId = %s.", cacheId);
        }

        Object entity = null;
        if (request.getResponseClass() == null) {
            entity = gson.fromJson(responseString, Object.class);
        } else {
            entity = gson.fromJson(responseString, request.getResponseClass());
            AssertHandler.handle(entity != null, "Entity should not be null!");
        }

        final Object finalEntity = entity;
        executeOnCallbackThread(() -> {
            onRequestSuccess(new ResultResponseImpl(code, message, finalEntity));
            onResult(true, "Request success");
        });
    }

    private boolean callbackCacheDataIfNeeded() {
        boolean continueRequesting = true;
        String cacheId = RequestUtils.getCacheID(request);
        CachePolicy cachePolicy = request.getCachePolicy();

        if (cachePolicy == CachePolicy.RELOAD_IGNORE_CACHE_DATA) {
            return continueRequesting;
        }

        String responseString = cacheStorage.getCache(cacheId);
        Object entity = null;
        if (request.getResponseClass() == null) {
            entity = gson.fromJson(responseString, Object.class);
        } else {
            entity = gson.fromJson(responseString, request.getResponseClass());
            AssertHandler.handle(entity != null, "Entity should not be null!");
        }

        if (cachePolicy == CachePolicy.RETURN_CACHE_DATA_THEN_LOAD) {
            if (entity != null) {
                callbackCacheData(entity);
            }
        } else if (cachePolicy == CachePolicy.RETURN_CACHE_DATA_ELSE_LOAD) {
            if (entity != null) {
                callbackCacheData(entity);
                continueRequesting = false;
            }
        } else if (cachePolicy == CachePolicy.RETURN_CACHE_DATA_DONT_LOAD) {
            callbackCacheData(entity);
            continueRequesting = false;
        } else {
            throw new RuntimeException("Invalid cachePolicy");
        }

        return continueRequesting;
    }

    private void callbackCacheData(Object body) {
        executeOnCallbackThread(() -> {
            onRequestSuccess(new ResultResponseImpl(HTTP_STATUS_OK, "success", body));
        });
    }

    private void onRequestSuccess(ResultResponse response) {
        if (request.getResultListener() != null) {
            request.getResultListener().onSuccess(response);
        }
    }

    private void onRequestFailure(Exception e, ResultResponse response) {
        if (request.getResultListener() != null) {
            request.getResultListener().onFailure(e, response);
        }
    }

}

package com.pipedog.hermes.executor;

import com.pipedog.hermes.executor.base.AbstractExecutor;
import com.pipedog.hermes.request.Request;
import com.pipedog.hermes.enums.SerializerType;
import com.pipedog.hermes.response.ProgressCallback;
import com.pipedog.hermes.response.internal.RealResponse;
import com.pipedog.hermes.utils.AssertionHandler;
import com.pipedog.hermes.utils.JsonUtils;
import com.pipedog.hermes.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 文件上传的执行器
 */
public class UploadExecutor extends AbstractExecutor {

    public UploadExecutor(OkHttpClient okHttpClient, Request request) {
        super(okHttpClient, request);
    }

    @Override
    public void execute() {
        if (isExecuted()) {
            return;
        }

        okhttp3.Request okRequest = createUploadRequest();
        okCall = okHttpClient.newCall(okRequest);
        okCallback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (autoRetryIfNeeded()) {
                    return;
                }

                onRequestFailure(e, null);
                onResult(false, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                handleResponse(response);
            }
        };

        okCall.enqueue(okCallback);
    }


    // PRIVATE METHODS

    private okhttp3.Request createUploadRequest() {
        // [POST] fullUrl = baseUrl + urlPath
        String fullUrl = request.getBaseUrl() + request.getUrlPath();
        fullUrl = UrlUtils.getEncodedUrl(fullUrl);
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder().url(fullUrl);

        // headers
        Map<String, String> allHeaders = request.getHeaders();
        for (Map.Entry<String, String> entry : allHeaders.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        // body
        RequestBody fullBody = null;

        // body - file
        MultipartBody.Builder bodyBuilder = request.getMultipartFormData().getBuilder();

        // body - parameters
        //  application/x-www-form-urlencoded
        if (request.getSerializerType() == SerializerType.HTTP) {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            if (allHeaders.get("Content-Type") != null) {
                mediaType = MediaType.parse(allHeaders.get("Content-Type"));
            }

            // body eg:
            //      {code=0, data={currentPage=1, list=[Amy, Bob, Tom]}, message=success}
            RequestBody parametersBody = RequestBody.Companion.create(request.getParameters().toString(), mediaType);
            bodyBuilder.addPart(parametersBody);
            fullBody = bodyBuilder.build();
        }

        //  application/json; charset=utf-8
        else if (request.getSerializerType() == SerializerType.JSON) {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            if (allHeaders.get("Content-Type") != null) {
                mediaType = MediaType.parse(allHeaders.get("Content-Type"));
            }

            // body eg:
            //      {"code":0,"data":{"currentPage":1,"list":["Amy","Bob","Tom"]},"message":"success"}
            String jsonString = JsonUtils.toJSONString(request.getParameters());
            RequestBody parametersBody = RequestBody.Companion.create(jsonString, mediaType);
            bodyBuilder.addPart(parametersBody);
            fullBody = bodyBuilder.build();
        }

        else {
            throw new RuntimeException("Invalid serializerType!");
        }

        // callback upload progress if needed
        boolean throwProgress = (request.getCallback() instanceof ProgressCallback);
        if (throwProgress) {
            fullBody = new HermesRequestBody(
                    fullBody, new HermesRequestBody.IProgressListener() {
                @Override
                public void onProgress(long currentLength, long totalLength) {
                    onRequestProgress(currentLength, totalLength);
                }
            });
        }

        okhttp3.Request okRequest = builder.post(fullBody).build();
        return okRequest;
    }

    private void handleResponse(okhttp3.Response response) {
        ResponseBody responseBody = response.body();
        String responseString = null;
        try {
            responseString = responseBody.string();
        } catch (Exception e) {
            if (autoRetryIfNeeded()) {
                return;
            }

            onRequestFailure(e, null);
            onResult(false, "Request success but parse failed.");
            return;
        }

        int code = response.code();
        String message = response.message();

        // statusCode != 200, failure
        if (response.code() != HTTP_STATUS_OK) {
            if (autoRetryIfNeeded()) {
                return;
            }

            onRequestFailure(null, new RealResponse(
                    code, message, gson.fromJson(responseString, Object.class)));
            onResult(false, "Request failed");
            return;
        }

        // statusCode == 200, success
        Object entity = null;
        if (request.getResponseClass() == null) {
            entity = gson.fromJson(responseString, Object.class);
        } else {
            entity = gson.fromJson(responseString, request.getResponseClass());
            AssertionHandler.handle(entity != null, "Entity should not be null!");
        }

        onRequestSuccess(new RealResponse(code, message, entity));
        onResult(true, "Request success");
    }


    // PRIVATE CLASSES

    /**
     * 支持进度回调的 MultipartBody 封装
     */
    private static class HermesRequestBody extends RequestBody {

        private interface IProgressListener {
            void onProgress(long currentLength, long totalLength);
        }

        private RequestBody requestBody;
        private IProgressListener listener;
        private long currentLength;
        private long totalLength;

        public HermesRequestBody(RequestBody requestBody, IProgressListener listener) {
            this.requestBody = requestBody;
            this.listener = listener;
        }

        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        @NotNull
        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        @Override
        public void writeTo(@NotNull BufferedSink sink) throws IOException {

            ForwardingSink forwardingSink = new ForwardingSink(sink) {
                @Override
                public void write(@NotNull Buffer source, long byteCount) throws IOException {
                    currentLength += byteCount;
                    if (listener != null) {
                        listener.onProgress(currentLength, totalLength);
                    }
                    super.write(source, byteCount);
                }
            };

            BufferedSink bufferedSink = Okio.buffer(forwardingSink);
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        }
    }

}

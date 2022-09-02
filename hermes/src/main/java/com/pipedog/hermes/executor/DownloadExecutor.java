package com.pipedog.hermes.executor;

import android.text.TextUtils;

import com.pipedog.hermes.executor.base.AbstractExecutor;
import com.pipedog.hermes.request.Request;
import com.pipedog.hermes.response.ProgressCallback;
import com.pipedog.hermes.response.internal.RealResponse;
import com.pipedog.hermes.utils.AssertionHandler;
import com.pipedog.hermes.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 文件下载的执行器
 */
public class DownloadExecutor extends AbstractExecutor {

    public DownloadExecutor(OkHttpClient okHttpClient, Request request) {
        super(okHttpClient, request);
    }

    @Override
    public void execute() {
        if (isExecuted()) {
            return;
        }

        okhttp3.Request okRequest = new okhttp3.Request.Builder().get().url(getUrl()).build();
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

    private String getUrl() {
        String url = this.request.getBaseUrl() + this.request.getUrlPath();
        String encodedUrl = UrlUtils.getEncodedUrl(url);
        return encodedUrl;
    }

    private boolean createTargetDirectoryIfNeeded() {
        String targetPath = request.getTargetPath();
        if (TextUtils.isEmpty(targetPath)) {
            AssertionHandler.handle(false, "Invalid `targetPath`.");
            return false;
        }

        File file = new File(targetPath);
        String directoryPath = file.getParentFile().getAbsolutePath();

        File directoryFile = new File(directoryPath);
        directoryFile.delete();
        return directoryFile.mkdirs();
    }

    private void handleResponse(okhttp3.Response response) {
        if (response.code() != HTTP_STATUS_OK) {
            if (autoRetryIfNeeded()) {
                return;
            }

            onRequestFailure(null, new RealResponse(response.code(), response.message(), null));
            onResult(false, "Download failed!");
            return;
        }

        // 构建下载目录
        createTargetDirectoryIfNeeded();

        // 构建完整文件
        String targetPath = request.getTargetPath();
        File file = new File(targetPath);

        // 下载数据写入文件
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[2048]; // 2KB

        try {
            ResponseBody responseBody = response.body();
            inputStream = responseBody.byteStream();
            fileOutputStream = new FileOutputStream(file);

            boolean throwProgress = (request.getCallback() instanceof ProgressCallback);
            int readLength = 0;
            long currentLength = 0;
            long totalLength = responseBody.contentLength();

            // read() returns the number of bytes read, or -1 if this source is exhausted.
            while ((readLength = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, readLength);
                currentLength += readLength;

                if (throwProgress) {
                    onRequestProgress(currentLength, totalLength);
                }
            }

            fileOutputStream.flush();

            // 下载完成
            onRequestSuccess(new RealResponse(response.code(), response.message(), targetPath));
            onResult(true, "Download success!");
        } catch (Exception e) {
            e.printStackTrace();

            if (autoRetryIfNeeded()) {
                return;
            }

            onRequestFailure(e, new RealResponse(1000, "Write download data to failed!", null));
            onResult(false, "Write download data to failed!");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

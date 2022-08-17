package com.pipedog.hermes.executor;

import com.pipedog.hermes.executor.base.AbstractExecutor;
import com.pipedog.hermes.request.interfaces.IDownloadSettings;
import com.pipedog.hermes.request.Request;
import com.pipedog.hermes.response.IResponse;
import com.pipedog.hermes.response.RealResponse;
import com.pipedog.hermes.utils.AssertHandler;
import com.pipedog.hermes.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
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

                executeOnCallbackThread(() -> {
                    onDownloadFailure(e, null);
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

    private String getUrl() {
        String url = this.request.getBaseUrl() + this.request.getUrlPath();
        String encodedUrl = UrlUtils.getEncodedUrl(url);
        return encodedUrl;
    }

    private String getDestinationDirectoryPath() {
        checkDownloadSettings();

        IDownloadSettings settings = request.getDownloadSettings();
        String fullPath = settings.getDestinationFullPath();
        File file = new File(fullPath);
        String directoryPath = file.getParentFile().getAbsolutePath();

        if (directoryPath == null || directoryPath.length() == 0) {
            AssertHandler.handle(false, "Can not found directoryPath for download file!");
        }

        return directoryPath;
    }

    private String getDestinationFullPath() {
        checkDownloadSettings();

        IDownloadSettings settings = request.getDownloadSettings();
        String fullPath = settings.getDestinationFullPath();

        if (fullPath == null || fullPath.length() == 0) {
            AssertHandler.handle(false, "Can not found fullPath for download file!");
        }

        return fullPath;
    }

    private void checkDownloadSettings() {
        IDownloadSettings settings = request.getDownloadSettings();
        if (settings == null) {
            // VARIABLES
            throw new RuntimeException("The instance impl `IDownloadSettings` lost!");
        }
    }

    private void handleResponse(Response response) {
        if (response.code() != HTTP_STATUS_OK) {
            if (autoRetryIfNeeded()) {
                return;
            }

            executeOnCallbackThread(() -> {
                onDownloadFailure(null,
                        new RealResponse(response.code(), response.message(), null));
                onResult(false, "Download failed!");
            });
            return;
        }

        // 构建下载目录
        String dirPath = getDestinationDirectoryPath();
        File dirFile = new File(dirPath);
        dirFile.delete();
        dirFile.mkdirs();

        // 构建完整文件
        String fullPath = getDestinationFullPath();
        File file = new File(fullPath);

        // 下载数据写入文件
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[2048]; // 2KB

        try {
            ResponseBody responseBody = response.body();
            inputStream = responseBody.byteStream();
            fileOutputStream = new FileOutputStream(file);

            int readLength = 0;
            long currentLength = 0;
            long totalLength = responseBody.contentLength();

            // read() returns the number of bytes read, or -1 if this source is exhausted.
            while ((readLength = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, readLength);
                currentLength += readLength;

                final long finalCurrentLength = currentLength;
                final long finalTotalLength = totalLength;

                // 进度更新回调
                executeOnCallbackThread(() -> {
                    onDownloadProgress(finalCurrentLength, finalTotalLength);
                });
            }

            fileOutputStream.flush();

            // 下载完成
            executeOnCallbackThread(() -> {
                onDownloadSuccess(new RealResponse(response.code(), response.message(), fullPath));
                onResult(true, "Download success!");
            });
        } catch (Exception e) {
            e.printStackTrace();

            if (autoRetryIfNeeded()) {
                return;
            }

            executeOnCallbackThread(() -> {
                onDownloadFailure(e,
                        new RealResponse(1000, "Write download data to failed!", null));
                onResult(false, "Write download data to failed!");
            });
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

    private void onDownloadProgress(long currentLength, long totalLength) {
        if (request.getCallback() != null) {
            request.getCallback().onProgress(currentLength, totalLength);
        }
    }

    private void onDownloadFailure(Exception e, IResponse response) {
        if (request.getCallback() != null) {
            request.getCallback().onFailure(e, response);
        }
    }

    private void onDownloadSuccess(IResponse response) {
        if (request.getCallback() != null) {
            request.getCallback().onSuccess(response);
        }
    }

}

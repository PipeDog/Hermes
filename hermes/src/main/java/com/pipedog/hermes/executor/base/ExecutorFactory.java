package com.pipedog.hermes.executor.base;

import com.pipedog.hermes.executor.DownloadExecutor;
import com.pipedog.hermes.executor.GenericExecutor;
import com.pipedog.hermes.executor.UploadExecutor;
import com.pipedog.hermes.request.Request;
import com.pipedog.hermes.utils.RequestUtils;

import okhttp3.OkHttpClient;

/**
 * @author liang
 * @time 2022/05/25
 * @desc 执行器工厂
 */
public class ExecutorFactory {

    public ExecutorFactory() {

    }

    public AbstractExecutor getExecutor(OkHttpClient okHttpClient, Request request) {
        int requestMode = RequestUtils.getRequestMode(request);

        if (requestMode == RequestUtils.REQUEST_MODE_GENERAL) {
            return new GenericExecutor(okHttpClient, request);
        }
        if (requestMode == RequestUtils.REQUEST_MODE_UPLOAD) {
            return new UploadExecutor(okHttpClient, request);
        }
        if (requestMode == RequestUtils.REQUEST_MODE_DOWNLOAD) {
            return new DownloadExecutor(okHttpClient, request);
        }

        return null;
    }

}

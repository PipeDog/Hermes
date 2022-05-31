package com.pipedog.hermes.request.listener;

import com.pipedog.hermes.response.DownloadResponse;

import org.jetbrains.annotations.Nullable;

/**
 * @author liang
 * @time 2022/05/23
 * @desc 下载回调
 */
public interface IDownloadListener {

    /**
     * 下载进度回调
     * @param currentLength 当前下载数据字节数
     * @param totalLength 下载数据总字节数
     */
    void onProgress(long currentLength, long totalLength);

    /**
     * 下载成功
     * @param 响应实体
     */
    void onSuccess(DownloadResponse response);

    /**
     * 下载失败回调
     * @param e 响应异常实例
     * @param response 响应实体
     */
    void onFailure(@Nullable Exception e, @Nullable DownloadResponse response);

}

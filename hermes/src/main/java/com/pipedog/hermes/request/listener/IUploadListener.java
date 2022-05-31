package com.pipedog.hermes.request.listener;

import com.pipedog.hermes.response.UploadResponse;

import org.jetbrains.annotations.Nullable;

/**
 * @author liang
 * @time 2022/05/23
 * @desc 上传回调
 */
public interface IUploadListener<T> {

    /**
     * 上传进度回调
     * @param currentLength 当前下载数据字节数
     * @param totalLength 下载数据总字节数
     */
    void onProgress(long currentLength, long totalLength);

    /**
     * 上传成功
     * @param 响应实体
     */
    void onSuccess(UploadResponse<T> response);

    /**
     * 上传失败回调
     * @param e 响应异常实例
     * @param response 响应实体
     */
    void onFailure(@Nullable Exception e, @Nullable UploadResponse response);

}

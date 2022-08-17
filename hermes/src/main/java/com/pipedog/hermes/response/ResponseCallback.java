package com.pipedog.hermes.response;

import androidx.annotation.Nullable;

public interface ResponseCallback<T> {

    /**
     * 进度回调
     * @param currentLength 当前获取/上传数据字节数
     * @param totalLength 数据总字节数
     */
    default void onProgress(long currentLength, long totalLength) {

    }

    /**
     * 请求成功回调
     * @param response 响应实体
     */
    void onSuccess(Response<T> response);

    /**
     * 请求失败回调
     * @param e 响应异常实例
     * @param response 响应实体
     */
    void onFailure(@Nullable Exception e, @Nullable Response<T> response);

}

package com.pipedog.hermes.response;

public interface ProgressCallback<T> extends ResponseCallback<T> {

    /**
     * 进度回调
     * @param currentLength 当前获取/上传数据字节数
     * @param totalLength 数据总字节数
     */
    void onProgress(long currentLength, long totalLength);

}

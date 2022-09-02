package com.pipedog.hermes.response;

/**
 * @author liang
 * @time 2022/09/02
 * @desc 请求结果回调，支持进度回调，上传或下载时如果需要监听进度可以使用这个接口
 */
public interface ProgressCallback<T> extends Callback<T> {

    /**
     * 进度回调
     * @param currentLength 当前获取/上传数据字节数
     * @param totalLength 数据总字节数
     */
    void onProgress(long currentLength, long totalLength);

}

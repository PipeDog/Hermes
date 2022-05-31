package com.pipedog.hermes.request.internal;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 进度监听
 */
public interface IProgressListener {
    void onProgress(long currentLength, long totalLength);
}

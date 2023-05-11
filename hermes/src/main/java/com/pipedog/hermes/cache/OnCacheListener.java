package com.pipedog.hermes.cache;

import java.io.Serializable;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 缓存读写回调
 */
public interface OnCacheListener<T extends Serializable> {

    /**
     * 缓存成功
     * @param result 缓存结果
     */
    void onCacheSuccess(T result);

    /**
     * 缓存失败
     * @param code 错误码
     * @param message 错误描述
     */
    void onCacheFailed(int code, String message);

}

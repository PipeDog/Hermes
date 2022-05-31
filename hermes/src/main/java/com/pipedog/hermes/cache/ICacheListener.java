package com.pipedog.hermes.cache;

import java.io.Serializable;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 缓存读写回调
 */
public interface ICacheListener<T extends Serializable> {

    /**
     * 缓存成功
     * @param result
     */
    void onCacheSuccess(T result);

    /**
     * 缓存失败
     * @param code
     * @param msg
     */
    void onCacheFail(String code, String msg);

}

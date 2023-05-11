package com.pipedog.hermes.cache.memory;

import java.io.Serializable;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 内存缓存接口定义
 */
public interface IMemoryCache {

    /**
     * 保存缓存
     * @param key
     * @param value
     * @param <T>
     */
    <T extends Serializable> void saveMemoryCache(String key, T value);

    /**
     * 获取缓存
     * @param key
     * @return
     */
    <T extends Serializable> T getMemoryCache(String key);

    /**
     * 删除缓存
     * @param key
     */
    void deleteMemoryCache(String key);

    /**
     * 清空所有缓存
     */
    void clear();

}

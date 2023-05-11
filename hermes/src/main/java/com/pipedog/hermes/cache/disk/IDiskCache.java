package com.pipedog.hermes.cache.disk;

import java.io.Serializable;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 磁盘缓存接口定义
 */
public interface IDiskCache {

    /**
     * 保存缓存
     * @param key       key
     * @param value     Serializable
     * @return true 成功 false 失败
     */
    <T extends Serializable> boolean saveSerializable(String key, T value);

    /**
     * 获取缓存
     * @param key   key
     * @param <T>   Serializable对象
     * @return
     */
    <T extends Serializable> T getSerializable(String key);

    /**
     * 删除缓存
     * @param key
     */
    void deleteCache(String key);

    /**
     * 清空所有缓存
     */
    void clear();

}

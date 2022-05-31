package com.pipedog.hermes.cache;

import java.io.Serializable;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 数据缓存接口定义
 */
public interface ICacheStorage {

    /**
     * 保存缓存
     * @param key
     * @param value
     * @param listener
     * @param <T>
     */
    <T extends Serializable> void saveCache(String key, T value, ICacheListener listener);

    /**
     * 保存缓存
     * @param key       key
     * @param value     Serializable
     * @return true 成功 false 失败
     */
    <T extends Serializable> boolean saveCache(String key, T value);

    /**
     * 获取缓存
     * @param key
     * @param listener
     * @return
     */
    void getCache(String key, ICacheListener listener);

    /**
     * 获取缓存
     * @param key   key
     * @param <T>   Serializable对象
     * @return
     */
    <T extends Serializable> T getCache(String key);

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

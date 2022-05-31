package com.pipedog.hermes.cache.engine;

import java.io.Serializable;

import com.pipedog.hermes.cache.ICacheListener;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 缓存引擎接口定义
 */
public interface ICacheEngine {

    /**
     * 保存缓存
     * @param key
     * @param value
     * @param listener
     * @param <T>
     */
    <T extends Serializable> void saveCache(String key, T value, ICacheListener<T> listener);

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
    <T extends Serializable>  void getCache(String key, ICacheListener<T> listener);

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

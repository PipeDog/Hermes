package com.pipedog.hermes.cache.memory;

import android.util.LruCache;

import java.io.Serializable;

import com.pipedog.hermes.cache.utils.ObjectSizeCalculator;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 内存缓存实现
 */
public class MemoryCache implements IMemoryCache {

    private static final int MAX_SIZE = 10 * 1024 * 1024; // 10MB
    private LruCache<String, Object> mLruCache;

    public MemoryCache() {
        mLruCache = new LruCache<String, Object>(MAX_SIZE) {
            @Override
            protected int sizeOf(String key, Object value) {
                return 0;
            }
        };
    }

    @Override
    public <T extends Serializable> void saveMemoryCache(String key, T value) {
        mLruCache.put(key, value);
    }

    @Override
    public <T extends Serializable> T getMemoryCache(String key) {
        return (T) mLruCache.get(key);
    }

    @Override
    public void deleteMemoryCache(String key) {
        mLruCache.remove(key);
    }

    @Override
    public void clear() {
        mLruCache.evictAll();
    }

}
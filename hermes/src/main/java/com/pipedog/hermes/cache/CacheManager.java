package com.pipedog.hermes.cache;

import android.content.Context;

import java.io.Serializable;

import com.pipedog.hermes.cache.engine.CacheEngine;
import com.pipedog.hermes.cache.engine.ICacheEngine;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 缓存管理器
 */
public class CacheManager implements ICacheStorage {

    private static Context sContext;
    private ICacheEngine mCacheEngine;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public CacheManager(String cacheDirPath) {
        if (sContext == null) {
            throw new RuntimeException("Call init(Context) first!");
        }
        mCacheEngine = new CacheEngine(sContext, cacheDirPath);
    }

    @Override
    public <T extends Serializable> void saveCache(String key, T value, ICacheListener listener) {
        mCacheEngine.saveCache(key,value, listener);
    }

    @Override
    public <T extends Serializable> boolean saveCache(String key, T value) {
        return mCacheEngine.saveCache(key, value);
    }

    @Override
    public void getCache(String key, ICacheListener listener) {
        mCacheEngine.getCache(key, listener);
    }

    @Override
    public <T extends Serializable> T getCache(String key) {
        return mCacheEngine.getCache(key);
    }

    @Override
    public void deleteCache(String key) {
        mCacheEngine.deleteCache(key);
    }

    @Override
    public void clear() {
        mCacheEngine.clear();
    }

}
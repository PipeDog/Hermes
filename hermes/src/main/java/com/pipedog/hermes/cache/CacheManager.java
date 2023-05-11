package com.pipedog.hermes.cache;

import android.content.Context;

import java.io.File;
import java.io.Serializable;

import com.pipedog.hermes.cache.engine.CacheEngine;
import com.pipedog.hermes.cache.engine.ICacheEngine;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 缓存管理器
 */
public class CacheManager implements ICacheStorage {

    private ICacheEngine mCacheEngine;

    public CacheManager(Context context) {
        String cacheDirPath = context.getExternalCacheDir().getAbsoluteFile() + File.separator + "hermes";
        mCacheEngine = new CacheEngine(context.getApplicationContext(), cacheDirPath);
    }

    public CacheManager(Context context, String cacheDirPath) {
        mCacheEngine = new CacheEngine(context.getApplicationContext(), cacheDirPath);
    }

    @Override
    public <T extends Serializable> void save(String key, T value, OnCacheListener listener) {
        mCacheEngine.saveCache(key,value, listener);
    }

    @Override
    public <T extends Serializable> boolean save(String key, T value) {
        return mCacheEngine.saveCache(key, value);
    }

    @Override
    public void get(String key, OnCacheListener listener) {
        mCacheEngine.getCache(key, listener);
    }

    @Override
    public <T extends Serializable> T get(String key) {
        return mCacheEngine.getCache(key);
    }

    @Override
    public void delete(String key) {
        mCacheEngine.deleteCache(key);
    }

    @Override
    public void clear() {
        mCacheEngine.clear();
    }

}
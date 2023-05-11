package com.pipedog.hermes.cache.engine;

import android.content.Context;

import java.io.Serializable;

import com.pipedog.hermes.cache.OnCacheListener;
import com.pipedog.hermes.cache.disk.DiskCache;
import com.pipedog.hermes.cache.disk.IDiskCache;
import com.pipedog.hermes.cache.memory.IMemoryCache;
import com.pipedog.hermes.cache.memory.MemoryCache;
import com.pipedog.hermes.cache.utils.ThreadUtils;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 缓存引擎，加入线程切换逻辑
 */
public class CacheEngine implements ICacheEngine {

    private IDiskCache mDiskCache;
    private IMemoryCache mMemoryCache;

    public CacheEngine(Context context, String cacheDirPath) {
        mDiskCache = new DiskCache(context, cacheDirPath);
        mMemoryCache = new MemoryCache();
    }

    @Override
    public <T extends Serializable> void saveCache(String key, T value, OnCacheListener<T> listener) {
        ThreadUtils.runOnIOThread(new Runnable() {
            @Override
            public void run() {
                boolean result = saveCache(key, value);
                if (result) {
                    onCacheSuccess(value, listener);
                } else {
                    onCacheFailed(listener, 1000, "Save cache failed!");
                }
            }
        });
    }

    @Override
    public <T extends Serializable> boolean saveCache(String key, T value) {
        boolean result = mDiskCache.saveSerializable(key, value);
        if (!result) {
            return false;
        }

        mMemoryCache.saveMemoryCache(key, value);
        return true;
    }

    @Override
    public <T extends Serializable> void getCache(String key, OnCacheListener<T> listener) {
        ThreadUtils.runOnIOThread(new Runnable() {
            @Override
            public void run() {
                T result = getCache(key);
                if (result != null) {
                    onCacheSuccess(result, listener);
                } else {
                    onCacheFailed(listener, 1001, "Get cache failed!");
                }
            }
        });
    }

    @Override
    public <T extends Serializable> T getCache(String key) {
        T result = mMemoryCache.getMemoryCache(key);
        if (result != null) {
            return result;
        }

        result = mDiskCache.getSerializable(key);
        return result;
    }

    @Override
    public void deleteCache(String key) {
        mDiskCache.deleteCache(key);
        mMemoryCache.deleteMemoryCache(key);
    }

    @Override
    public void clear() {
        mDiskCache.clear();
        mMemoryCache.clear();
    }


    // PRIVATE METHODS

    private <T extends Serializable> void onCacheSuccess(T t, OnCacheListener<T> listener) {
        if (listener == null) {
            return;
        }

        ThreadUtils.runOnUIThread(() -> {
            listener.onCacheSuccess(t);
        });
    }

    private <T extends Serializable> void onCacheFailed(OnCacheListener<T> listener, int code, String message) {
        if (listener == null) {
            return;
        }

        ThreadUtils.runOnUIThread(() -> {
            listener.onCacheFailed(code, message);
        });
    }

}
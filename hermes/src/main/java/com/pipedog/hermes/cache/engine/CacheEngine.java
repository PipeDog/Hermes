package com.pipedog.hermes.cache.engine;

import android.content.Context;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.pipedog.hermes.cache.ICacheListener;
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
    public <T extends Serializable> void saveCache(String key, T value, ICacheListener<T> listener) {
        ThreadUtils.runOnIOThread(new Runnable() {
            @Override
            public void run() {
                boolean result = mDiskCache.saveSerializable(key, value);
                if (result) {
                    mMemoryCache.saveMemoryCache(key, value);
                    cacheSuccess(value, listener);
                } else {
                    cacheFail(listener);
                }
            }
        });
    }

    @Override
    public <T extends Serializable> boolean saveCache(String key, T value) {
        final Semaphore semaphore = new Semaphore(0);
        final AtomicBoolean result = new AtomicBoolean(false);

        ThreadUtils.runOnIOThread(new Runnable() {
            @Override
            public void run() {
                result.set(mDiskCache.saveSerializable(key, value));
                if (result.get()) {
                    mMemoryCache.saveMemoryCache(key, value);
                }
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.get();
    }

    @Override
    public <T extends Serializable> void getCache(String key, ICacheListener<T> listener) {
        T result = mMemoryCache.getMemoryCache(key);
        if (result != null) {
            listener.onCacheSuccess(result);
            return;
        }
        getCacheFromDisk(key, listener);
    }

    @Override
    public <T extends Serializable> T getCache(String key) {
        final Semaphore semaphore = new Semaphore(0);
        final AtomicReference<T> value = new AtomicReference<>();

        ThreadUtils.runOnIOThread(new Runnable() {
            @Override
            public void run() {
                value.set(mMemoryCache.getMemoryCache(key));
                if (value.get() == null) {
                    value.set(mDiskCache.getSerializable(key));
                }
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value.get();
    }

    private <T extends Serializable> void getCacheFromDisk(String key, ICacheListener<T> listener) {
        ThreadUtils.runOnIOThread(new Runnable() {
            @Override
            public void run() {
                T result = mDiskCache.getSerializable(key);
                if (result != null) {
                    cacheSuccess(result, listener);
                } else {
                    cacheFail(listener);
                }
            }
        });
    }

    private <T extends Serializable> void cacheSuccess(T t, ICacheListener<T> listener) {
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                listener.onCacheSuccess(t);
            }
        });
    }

    private <T extends Serializable> void cacheFail(ICacheListener<T> listener) {
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                listener.onCacheFail("0", "");
            }
        });
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

}
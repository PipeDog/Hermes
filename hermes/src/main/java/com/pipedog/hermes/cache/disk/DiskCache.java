package com.pipedog.hermes.cache.disk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.pipedog.hermes.cache.disk.lrucache.DiskLruCache;
import com.pipedog.hermes.cache.utils.SecretUtils;
import com.pipedog.hermes.log.Logger;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 磁盘缓存
 */
public class DiskCache implements IDiskCache {

    private File directory;
    private DiskLruCache mDiskLruCache;
    private DiskLruCache.Editor mEditor = null;
    private DiskLruCache.Snapshot mSnapshot = null;

    private final int valueCount = 1;
    // 100MB 的缓存大小
    private final long maxSize = 100 * 1024 * 1024;

    public DiskCache(Context context, String cacheDirPath) {
        try {
            directory = new File(cacheDirPath);
            mDiskLruCache = DiskLruCache.open(directory, getAppVersion(context), valueCount, maxSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends Serializable> boolean saveSerializable(String key, T value) {
        return putSerializableToLocal(key, value);
    }

    @Override
    public <T extends Serializable> T getSerializable(String key) {
        return getSerializableFromLocal(key);
    }

    @Override
    public void deleteCache(String key) {
        putSerializableToLocal(key, (Serializable) new Null());
    }

    @Override
    public void clear() {
        try {
            mDiskLruCache.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.toString());
        }
    }

    /**
     * 序列化对象写入
     * @param key    cache key
     * @param object 待缓存的序列化对象
     */
    private boolean putSerializableToLocal(String key, Serializable object) {
        boolean result = false;
        ObjectOutputStream oos = null;
        DiskLruCache.Editor editor = null;
        try {
            editor = edit(key);
            if (editor == null) {
                return false;
            }
            oos = new ObjectOutputStream(editor.newOutputStream(0));
            oos.writeObject(object);
            oos.flush();
            editor.commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (editor != null)
                    editor.abort();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取 序列化对象
     * @param key cache key
     * @param <T> 对象类型
     * @return 读取到的序列化对象
     */
    private <T> T getSerializableFromLocal(String key) {
        T object = null;
        ObjectInputStream ois = null;

        InputStream in = getCacheInputStream(key);
        if (in == null) {
            return null;
        }

        try {
            ois = new ObjectInputStream(in);
            object = (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object instanceof Null ? null : object;
    }

    /**
     * 获取 缓存数据的 InputStream
     * @param key cache key
     */
    private InputStream getCacheInputStream(String key) {
        key = SecretUtils.getMD5Result(key);
        InputStream in;
        DiskLruCache.Snapshot snapshot = snapshot(key);
        if (snapshot == null) {
            return null;
        }
        in = snapshot.getInputStream(0);
        return in;
    }

    /**
     * 获取缓存 editor
     * @param key 缓存的key
     */
    private DiskLruCache.Editor edit(String key) throws IOException {
        key = SecretUtils.getMD5Result(key); // 存取的 key
        if (mDiskLruCache != null) {
            mEditor = mDiskLruCache.edit(key);
        }
        return mEditor;
    }

    /**
     * 根据 key 获取缓存缩略
     * @param key 缓存的key
     * @return Snapshot
     */
    private DiskLruCache.Snapshot snapshot(String key) {
        if (mDiskLruCache != null) {
            try {
                mSnapshot = mDiskLruCache.get(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mSnapshot;
    }

    /**
     * 获取应用的版本号
     * @return 应用版本号
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 空对象处理（不能直接使用 Object，因为要实现 Serializable 接口）
     */
    private static class Null implements Serializable {
        public Null() {
        }
    }

}
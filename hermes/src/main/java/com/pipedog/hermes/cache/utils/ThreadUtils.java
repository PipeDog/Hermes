package com.pipedog.hermes.cache.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 线程工具简单包装
 */
public class ThreadUtils {

    private static final long KEEP_ALIVE_TIME_MS = TimeUnit.SECONDS.toMillis(10);

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(0,
            Integer.MAX_VALUE,
            KEEP_ALIVE_TIME_MS,
            TimeUnit.MILLISECONDS,
            new SynchronousQueue<Runnable>());

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 运行在 UI 线程
     * @param runnable
     */
    public static void runOnUIThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    /**
     * 运行在子线程
     * @param runnable
     */
    public static void runOnIOThread(Runnable runnable) {
        executor.execute(runnable);
    }

}

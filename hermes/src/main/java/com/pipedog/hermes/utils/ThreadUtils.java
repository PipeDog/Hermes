package com.pipedog.hermes.utils;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 线程工具
 */
public class ThreadUtils {

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 在主线程执行任务
     * @param r 将要被执行的任务代码
     */
    public static void runInMainThread(@NotNull Runnable r) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            r.run();
        } else {
            mainHandler.post(r);
        }
    }

}

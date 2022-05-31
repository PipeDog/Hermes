package com.pipedog.hermes.utils;


import java.util.logging.Logger;

/**
 * @author liang
 * @time 2022/05/26
 * @desc 模拟断言
 */
public class AssertHandler {

    //    private static boolean mDebug = true;
    private static boolean mDebug = false;

    /**
     * 可以通过修改 debug 的值来开关断言功能
     * @param debug 当 debug 为 true 时启用断言功能
     */
    public static void setDebug(boolean debug) {
        mDebug = debug;
    }

    /**
     * 处理断言
     * @param condition 断言条件，debug 模式下当 condition 为 false 时会触发断言
     */
    public static void handle(boolean condition, String message) {
        if (mDebug && !condition) {
            throw new RuntimeException(message);
        } else {
            Logger.getGlobal().warning("[danger] ⚠️" + message);
        }
    }

}

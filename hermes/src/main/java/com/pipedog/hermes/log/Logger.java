package com.pipedog.hermes.log;

import java.util.Formatter;

/**
 * @author liang
 * @time 2022/05/30
 * @desc 日志
 */
public class Logger {

    private static final java.util.logging.Logger sLogger = java.util.logging.Logger.getLogger("Hermes");

    public static void debug(String format, Object... args) {
        String msg = new Formatter().format(format, args).toString();
        debug(msg);
    }

    public static void info(String format, Object... args) {
        String msg = new Formatter().format(format, args).toString();
        info(msg);
    }

    public static void warning(String format, Object... args) {
        String msg = new Formatter().format(format, args).toString();
        warning(msg);
    }

    public static void error(String format, Object... args) {
        String msg = new Formatter().format(format, args).toString();
        error(msg);
    }

    public static void debug(String msg) {
        sLogger.info("[Hermes|debug] " + msg);
    }

    public static void info(String msg) {
        sLogger.info("[Hermes|info] " + msg);
    }

    public static void warning(String msg) {
        sLogger.warning("[Hermes|warn] " + msg);
    }

    public static void error(String msg) {
        sLogger.severe("[Hermes|error] " + msg);
    }

}

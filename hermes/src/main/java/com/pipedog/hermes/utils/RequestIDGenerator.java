package com.pipedog.hermes.utils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liang
 * @time 2022/05/23
 * @desc 请求 id 生成器
 */
public class RequestIDGenerator {

    private static AtomicLong counter = new AtomicLong(1);

    /**
     * 获取 requestID
     */
    public static String getRequestID() {
        Date date = new Date();
        String requestId = String.format("[%s]::[%d]", date.toString(), counter.getAndIncrement());
        return requestId;
    }

}

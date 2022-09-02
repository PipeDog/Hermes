package com.pipedog.hermes.response;

/**
 * @author liang
 * @time 2022/09/02
 * @desc 响应数据接口定义
 */
public interface Response<T> {
    /**
     * HTTP 状态码
     */
    int code();

    /**
     * 信息描述
     */
    String message();

    /**
     * 响应实体
     */
    T body();
}

package com.pipedog.hermes.response;

import okhttp3.Response;

/**
 * @author liang
 * @time 2022/05/23
 * @desc 响应体接口定义
 */
public interface ResultResponse<T> {

    /**
     * HTTP 状态码
     */
    public int code();

    /**
     * 信息描述
     */
    public String message();

    /**
     * 响应实体
     */
    public T body();

}

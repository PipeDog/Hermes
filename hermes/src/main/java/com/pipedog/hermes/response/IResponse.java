package com.pipedog.hermes.response;

public interface IResponse<T> {
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

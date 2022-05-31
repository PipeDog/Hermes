package com.pipedog.hermes.response.internal;

import com.pipedog.hermes.response.ResultResponse;

/**
 * @author liang
 * @time 2022/05/23
 * @desc 响应体实现
 */
public class ResultResponseImpl<T> implements ResultResponse<T> {

    /**
     * HTTP 状态码
     */
    private int code;

    /**
     * 信息描述
     */
    private String message;

    /**
     * 响应实体
     */
    private T body;


    // CONSTRUCTORS

    public ResultResponseImpl(int code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public T body() {
        return this.body;
    }

}

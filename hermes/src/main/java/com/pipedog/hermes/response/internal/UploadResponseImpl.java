package com.pipedog.hermes.response.internal;

import com.pipedog.hermes.response.UploadResponse;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 上传响应体实现
 */
public class UploadResponseImpl<T> implements UploadResponse<T> {

    /**
     * HTTP 状态码
     */
    public int code;

    /**
     * 信息描述
     */
    public String message;

    /**
     * 响应实体
     */
    public T body;


    // CONSTRUCTORS

    public UploadResponseImpl(int code, String message, T body) {
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

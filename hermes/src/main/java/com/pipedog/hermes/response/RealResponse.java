package com.pipedog.hermes.response;

public class RealResponse<T> implements Response<T> {

    /**
     * HTTP 状态码
     */
    private final int code;

    /**
     * 信息描述
     */
    private final String message;

    /**
     * 响应实体
     */
    private final T body;

    public RealResponse(int code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }


    // OVERRIDE METHODS FOR `IResponse`

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public T body() {
        return body;
    }

}

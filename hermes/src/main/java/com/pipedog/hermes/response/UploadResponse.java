package com.pipedog.hermes.response;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 上传响应接口定义
 */
public interface UploadResponse<T> {

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

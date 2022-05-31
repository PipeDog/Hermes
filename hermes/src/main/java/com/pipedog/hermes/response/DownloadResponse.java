package com.pipedog.hermes.response;

/**
 * @author liang
 * @time 2022/05/27
 * @desc 下载响应体接口定义
 */
public interface DownloadResponse {

    /**
     * HTTP 状态码
     */
    public int code();

    /**
     * 信息描述
     */
    public String message();

    /**
     * 下载文件路径
     */
    public String filePath();

}

package com.pipedog.hermes.response.internal;

import com.pipedog.hermes.response.DownloadResponse;

/**
 * @author liang
 * @time 2022/05/27
 * @desc 下载响应体实现
 */
public class DownloadResponseImpl implements DownloadResponse {

    /**
     * HTTP 状态码
     */
    private int code;

    /**
     * 信息描述
     */
    private String message;

    /**
     * 下载文件路径
     */
    private String filePath;


    // CONSTRUCTORS

    public DownloadResponseImpl(int code, String message, String filePath) {
        this.code = code;
        this.message = message;
        this.filePath = filePath;
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
    public String filePath() {
        return this.filePath;
    }

}

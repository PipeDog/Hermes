package com.pipedog.hermes.request.interfaces;

/**
 * @author liang
 * @time 2022/05/25
 * @desc 下载配置（如下载路径）
 */
public interface DownloadSettings {

    /**
     * 下载的文件完整路径
     */
    String getDestinationFullPath();

}

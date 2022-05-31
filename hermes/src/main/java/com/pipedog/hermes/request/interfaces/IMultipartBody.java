package com.pipedog.hermes.request.interfaces;

import java.io.File;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 数据上传的表单接口定义
 */
public interface IMultipartBody {

    /**
     * FormData 构造接口
     */
    static interface Builder {
        void onBuild(IMultipartBody multipartBody);
    }

    /**
     * 表单数据拼接
     * @param name 字段名
     * @param value 字段值
     */
    IMultipartBody addFormData(String name, String value);

    /**
     * 表单数据拼接
     * @param data 要编码并拼接到表单数据的数据流
     * @param name data 的字段名称
     * @param filename data 的文件名
     * @param mimeType 资源媒体类型标识，常见类型列表地址：
     *                 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
     */
    IMultipartBody addFormData(byte[] data, String name, String filename, String mimeType);

    /**
     * 表单数据拼接
     * @param file 要上传的文件
     * @param name file 的字段名称
     * @param filename file 的文件名
     * @param mimeType 资源媒体类型标识，常见类型列表地址：
     *                 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
     */
    IMultipartBody addFormData(File file, String name, String filename, String mimeType);

}

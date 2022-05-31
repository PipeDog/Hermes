package com.pipedog.hermes.enums;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 数据序列化方式枚举定义
 */
public enum SerializerType {

    /**
     * 默认 Content-Type 为 application/x-www-form-urlencoded
     */
    HTTP,

    /**
     * 默认 Content-Type 为 application/json
     */
    JSON,

}

package com.pipedog.hermes.enums;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 缓存策略枚举定义
 */
public enum CachePolicy {

    /**
     * 忽略缓存数据，本次拉取的数据也不进行缓存
     */
    RELOAD_IGNORE_CACHE_DATA,

    /**
     * 返回缓存数据后发送请求
     */
    RETURN_CACHE_DATA_THEN_LOAD,

    /**
     * 有缓存数据则直接返回缓存数据（不发送请求）；没有缓存数据则发送请求进行数据拉取
     */
    RETURN_CACHE_DATA_ELSE_LOAD,

    /**
     * 仅返回缓存数据（没有则返回空），不进行数据拉取
     */
    RETURN_CACHE_DATA_DONT_LOAD,

}

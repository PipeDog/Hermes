package com.pipedog.hermes.response;

import androidx.annotation.Nullable;

/**
 * @author liang
 * @time 2022/09/02
 * @desc 请求结果回调
 */
public interface Callback<T> {

    /**
     * 请求成功回调
     * @param response 响应实体
     */
    void onSuccess(Response<T> response);

    /**
     * 请求失败回调
     * @param e 响应异常实例
     * @param response 响应实体
     */
    void onFailure(@Nullable Exception e, @Nullable Response<T> response);

}

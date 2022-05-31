package com.pipedog.hermes.request.listener;

import com.pipedog.hermes.response.ResultResponse;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author liang
 * @time 2022/05/23
 * @desc 请求回调
 */
public interface IResultListener<T> {
    void onSuccess(ResultResponse<T> response);
    void onFailure(@Nullable Exception e, @Nullable ResultResponse response);
}

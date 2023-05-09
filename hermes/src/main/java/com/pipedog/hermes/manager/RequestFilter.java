package com.pipedog.hermes.manager;

import com.pipedog.hermes.request.Request;

/**
 * @author liang
 * @time 2022/05/24
 * @desc Request 过滤接口
 */
public interface RequestFilter {
    boolean onFilter(Request request);
}

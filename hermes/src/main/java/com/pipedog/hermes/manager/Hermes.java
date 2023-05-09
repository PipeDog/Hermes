package com.pipedog.hermes.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.pipedog.hermes.cache.CacheManager;
import com.pipedog.hermes.cache.ICacheStorage;
import com.pipedog.hermes.executor.base.AbstractExecutor;
import com.pipedog.hermes.executor.base.ExecutorFactory;
import com.pipedog.hermes.request.Request;
import com.pipedog.hermes.utils.AssertionHandler;
import com.pipedog.hermes.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 网络管理器
 */
public class Hermes {

    public static class Registry {
        private OkHttpClient httpClient = new OkHttpClient();
        private ICacheStorage cacheStorage;
        private Gson gson = JsonUtils.getGson();

        public Registry(Context context) {
            this.cacheStorage = new CacheManager(context.getApplicationContext());
        }

        public Registry httpClient(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Registry cacheStorage(ICacheStorage cacheStorage) {
            this.cacheStorage = cacheStorage;
            return this;
        }

        public Registry gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public void register() {
            sInstance = new Hermes(this);
        }
    }

    private static Hermes sInstance;

    public static Hermes global() {
        if (sInstance == null) {
            throw new NullPointerException("sInstance == null");
        }
        return sInstance;
    }


    private OkHttpClient httpClient;
    private ICacheStorage cacheStorage;
    private Gson gson;

    private ExecutorFactory executorFactory = new ExecutorFactory();
    private Map<String, Request> requestTable = new HashMap<>();
    private Map<String, AbstractExecutor> executorTable = new HashMap<>();;
    // 因为 okhttp 拥有自己的线程管理，这里无需过多干涉 Request 的任务分配，因此这里
    // 仅采用了单线程线程池，来保证 requestTable 以及 executorTable 的线程安全问题
    private ExecutorService serialExecutorService = Executors.newSingleThreadExecutor();

    private Hermes(Registry registry) {
        this.httpClient = registry.httpClient;
        this.cacheStorage = registry.cacheStorage;
        this.gson = registry.gson;
    }


    // PUBLIC METHODS

    public void addRequest(Request request) {
        serialExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                realAddRequest(request);
            }
        });
    }

    public void cancelRequest(Request request) {
        serialExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                realCancelRequest(request);
            }
        });
    }

    /**
     * 注意：
     *      1、{@link Hermes#cancelRequests(RequestFilter)}
     *      2、{@link Hermes#cancelAllRequests()}
     *  两个方法最终都是调用到 {@link Hermes#cancelRequest(Request)}，已经被打到单线程按
     *  照串行顺序执行，且已经对 requestTable 进行了 copy 操作，这里无需再考虑线程安全问题
     */
    public void cancelRequests(RequestFilter filter) {
        if (filter == null) {
            return;
        }

        Map<String, Request> copyTable = new HashMap<>(requestTable);
        for (Request req : copyTable.values()) {
            if (filter.onFilter(req)) {
                req.cancel();
            }
        }
    }

    public void cancelAllRequests() {
        Map<String, Request> copyTable = new HashMap<>(requestTable);
        for (Request req : copyTable.values()) {
            req.cancel();
        }
    }


    // PRIVATE METHODS

    private void realAddRequest(Request request) {
        if (request == null || request.getRequestID() == null) {
            AssertionHandler.handle(false, "Invalid argument `request`, check it!");
            return;
        }

        AbstractExecutor executor = executorTable.get(request.getRequestID());
        if (executor != null) {
            executor.cancel();
        }

        executor = executorFactory.getExecutor(httpClient, request);
        if (executor == null) {
            return;
        }

        requestTable.put(request.getRequestID(), request);
        executorTable.put(request.getRequestID(), executor);

        executor.setGson(gson);
        executor.setCacheStorage(cacheStorage);
        executor.setExecutorListener(new AbstractExecutor.ExecutorListener() {
            @Override
            public void onResult(boolean success, String error) {
                serialExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        request.setExecuting(false);
                        requestTable.remove(request.getRequestID());
                        executorTable.remove(request.getRequestID());
                    }
                });
            }
        });

        request.setExecuting(true);
        executor.execute();
    }

    private void realCancelRequest(Request request) {
        if (request == null || request.getRequestID() == null) {
            return;
        }

        AbstractExecutor executor = executorTable.get(request.getRequestID());
        if (!executor.isExecuted() || executor.isCanceled()) {
            return;
        }

        executor.cancel();
    }

}

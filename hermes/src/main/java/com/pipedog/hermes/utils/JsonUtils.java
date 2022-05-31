package com.pipedog.hermes.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liang
 * @time 2022/05/26
 * @desc JSON 工具
 */
public class JsonUtils {

    private static Gson sGson;

    /**
     * 获取 gson 实例
     */
    public static Gson getGson() {
        if (sGson != null) {
            return sGson;
        }

        sGson = new GsonBuilder().registerTypeAdapter(
                new TypeToken<Map<String, Object>>(){}.getType(),
                new NumberTypeAdapter())
                .create();

        return sGson;
    }

    /**
     * Map 转 json string
     */
    public static String toJSONString(Map<String, Object> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    /**
     * Map 转 JSONObject
     */
    public static JSONObject toJSONObject(Map<String, Object> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject;
    }

}

package com.pipedog.hermes.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liang
 * @time 2022/05/27
 * @desc Gson 适配器，用来解决 json 字符串转 Map 时整型变浮点型的问题
 */
public class NumberTypeAdapter extends TypeAdapter<Object> {

    private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

    @Override
    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY: {
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;
            }

            case BEGIN_OBJECT: {
                Map<String, Object> map = new LinkedTreeMap<>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;
            }

            case STRING: {
                return in.nextString();
            }

            case NUMBER: {
                // Number 类型，整型与浮点型转换逻辑处理

                // 包含小数点，返回浮点型
                String numberString = in.nextString();
                if (numberString.contains(".")) {
                    return Double.parseDouble(numberString);
                }

                double dbNum = in.nextDouble();

                // 数字超过 long 的最大值，返回浮点类型
                if (dbNum > Long.MAX_VALUE) {
                    return dbNum;
                }

                // 判断数字是否为整数值
                long lngNum = (long) dbNum;
                if (dbNum == lngNum) {
                    try {
                        return (int) lngNum;
                    } catch (Exception e) {
                        return lngNum;
                    }
                } else {
                    return dbNum;
                }
            }

            case BOOLEAN: {
                return in.nextBoolean();
            }

            case NULL: {
                in.nextNull();
                return null;
            }

            default: {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        delegate.write(out, value);
    }

}

package com.pipedog.hermes.request;

import android.text.TextUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author liang
 * @time 2022/09/02
 * @desc request body 包装
 */
public class MultipartFormData {

    private final MultipartBody.Builder builder
            = new MultipartBody.Builder().setType(MultipartBody.FORM);

    public MultipartFormData addFormDataPart(String name, String value) {
        builder.addFormDataPart(name, value);
        return this;
    }

    public MultipartFormData addFormData(byte[] data, String name, String filename, String mimeType) {
        MediaType mediaType = MediaType.parse(
                TextUtils.isEmpty(mimeType) ? "multipart/form-data" : mimeType);
        RequestBody body = RequestBody.create(data, mediaType);
        builder.addFormDataPart(name, filename, body);
        return this;
    }

    public MultipartFormData addFormData(File file, String name, String filename, String mimeType) {
        MediaType mediaType = MediaType.parse(
                TextUtils.isEmpty(mimeType) ? "multipart/form-data" : mimeType);
        RequestBody body = RequestBody.create(file, mediaType);
        builder.addFormDataPart(name, filename, body);
        return this;
    }


    // INTERNAL METHODS

    public MultipartBody.Builder getBuilder() {
        return builder;
    }

}

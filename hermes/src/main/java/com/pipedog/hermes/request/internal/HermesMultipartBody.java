package com.pipedog.hermes.request.internal;

import com.pipedog.hermes.request.interfaces.IMultipartBody;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author liang
 * @time 2022/05/24
 * @desc 数据上传功能包装
 */
public class HermesMultipartBody implements IMultipartBody {

    private MultipartBody.Builder builder;

    public HermesMultipartBody() {
        this.builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    }

    public MultipartBody.Builder getBuilder() {
        return this.builder;
    }


    // OVERRIDE METHODS FOR `IMultipartBody`

    @Override
    public IMultipartBody addFormData(String name, String value) {
        builder.addFormDataPart(name, value);
        return this;
    }

    @Override
    public IMultipartBody addFormData(byte[] data, String name, String filename, String mimeType) {
        MediaType mediaType = MediaType.parse(
                mimeType != null ? mimeType : "application/octet-stream");
        RequestBody body = RequestBody.create(data, mediaType);
        builder.addFormDataPart(name, filename, body);
        return this;
    }

    @Override
    public IMultipartBody addFormData(File file, String name, String filename, String mimeType) {
        MediaType mediaType = MediaType.parse(
                mimeType != null ? mimeType : "application/octet-stream");
        RequestBody body = RequestBody.create(file, mediaType);
        builder.addFormDataPart(name, filename, body);
        return this;
    }

}

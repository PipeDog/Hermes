package com.pipedog.hermes.demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pipedog.hermes.R;

import androidx.annotation.NonNull;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.pipedog.hermes.cache.CacheManager;
import com.pipedog.hermes.enums.CachePolicy;
import com.pipedog.hermes.request.interfaces.DownloadSettings;
import com.pipedog.hermes.request.interfaces.MultipartBody;
import com.pipedog.hermes.enums.RequestType;
import com.pipedog.hermes.enums.SerializerType;
import com.pipedog.hermes.request.Request;
import com.pipedog.hermes.response.ProgressCallback;
import com.pipedog.hermes.response.ResponseCallback;
import com.pipedog.hermes.response.Response;
import com.pipedog.hermes.utils.JsonUtils;
import com.pipedog.hermes.utils.UrlUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 下载测试地址
//https://upload-images.jianshu.io/upload_images/5809200-a99419bb94924e6d.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
//https://upload-images.jianshu.io/upload_images/5809200-736bc3917fe92142.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
//https://upload-images.jianshu.io/upload_images/5809200-7fe8c323e533f656.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
//https://upload-images.jianshu.io/upload_images/5809200-c12521fbde6c705b.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
//https://upload-images.jianshu.io/upload_images/5809200-caf66b935fd00e18.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
//https://upload-images.jianshu.io/upload_images/5809200-48dd99da471ffa3f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
//https://upload-images.jianshu.io/upload_images/5809200-4de5440a56bff58f.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
//https://upload-images.jianshu.io/upload_images/5809200-03bbbd715c24750e.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240


public class MainActivity extends AppCompatActivity {

    private TextView tvDownloadProgress;
    private ImageView ivDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CacheManager.init(this.getApplicationContext());

        tvDownloadProgress = findViewById(R.id.tv_download_progress);
        ivDownload = findViewById(R.id.iv_download);

        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);

        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("currentPage", 1);

        List<String> list = new ArrayList<>();
        list.add("Amy");
        list.add("Bob");
        list.add("Tom");
        innerMap.put("list", list);

        map.put("message", "success");
        map.put("data", innerMap);

        toJsonString(map);
        toString(map);

//        jsonRequest();
        download();
    }


    private void toJsonString(Map<String, Object> map) {
        String jsonString = JsonUtils.toJSONString(map);
        System.out.println("jsonString => " + jsonString);
    }

    private void toString(Map<String, Object> map) {
        String string = map.toString();
        System.out.println("string => " + string);
    }


    // json

    // json

    private void jsonRequest() {
//        new ResponseCallback<String>() {
//            @Override
//            public void onSuccess(IResponse<String> response) {
//
//            }
//
//            @Override
//            public void onFailure(@Nullable Exception e, @Nullable IResponse<String> response) {
//                response.getBody()
//            }
//        }

    }

    // upload

    /*

        Map<String, String> headers = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();

        new Request.Builder()
            .baseUrl("https://www.baidu.com")
            .urlPath("/path")
            .headers(headers)
            .parameters(parameters)
            .multipartBody()
            .build()
            .upload(new UploadCallback() {

            });
     */

    private void upload() {
//        new Request.Builder()
//                .multipartBody(new MultipartBody.Builder() {
//            @Override
//            public void onBuild(MultipartBody multipartBody) {
//
//            }
//        }).callbackOnMainThread(true)

    }

    // download

    private void download() {
        new Request.Builder()
                .baseUrl("https://upload-images.jianshu.io/upload_images/5809200-a99419bb94924e6d.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240")
                .targetPath(getFullPath())
                .build()
                .call(new ProgressCallback<String>() {
                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        System.out.println("currentLength = " + currentLength + ", totalLength = " + totalLength);

                        String progressText = (
                                "cur = " + currentLength + ", total = " + totalLength +
                                        ", progress = " + ((int)((currentLength * 100.0) / totalLength)));
                        Message msg = handler.obtainMessage(10, progressText);
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        System.out.println(response.body());
                        Message msg = handler.obtainMessage(100, response.body());
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(@Nullable Exception e, @Nullable Response<String> response) {
                        e.printStackTrace();
                    }
                });
    }

    public String getFullPath() {
        return getDownloadFullPath(this) + "/2.png";
    }

    public String getDownloadFullPath(Context context) {
        String dirName;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 29) {
            dirName = context.getExternalCacheDir().getPath();
            return dirName;
        } else if (currentapiVersion < 29) {
            dirName = context.getExternalCacheDir().getAbsolutePath();
            return dirName;
        }
        return "";
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 10) {
                String downloadProgress = (String) msg.obj;
                tvDownloadProgress.setText(downloadProgress);
            } else if (msg.what == 100) {
                try {
                    String filePath = (String) msg.obj;
                    File file = new File(filePath);
                    FileInputStream is = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    ivDownload.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
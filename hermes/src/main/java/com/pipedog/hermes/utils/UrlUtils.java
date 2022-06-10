package com.pipedog.hermes.utils;

import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author liang
 * @time 2022/05/28
 * @desc Url 工具封装
 */
public class UrlUtils {

    /**
     * 对 url 字符串进行统一编码（解决空格、中文字符、% 等特殊字符导致的问题）
     */
    public static String getEncodedUrl(String urlString) {
        Logger.getGlobal().warning("[Url-encode] input: " + urlString);

        if (urlString == null || urlString.length() == 0) {
            return "";
        }

        try {
            URL url = new URL(urlString);
            URI uri = new URI(
                    url.getProtocol(), url.getUserInfo(), url.getHost(),
                    url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            URL formatUrl = uri.toURL();
            String encodedUrlString = formatUrl.toString();

            Logger.getGlobal().warning("[Url-encode] output: " + encodedUrlString);
            return encodedUrlString;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}

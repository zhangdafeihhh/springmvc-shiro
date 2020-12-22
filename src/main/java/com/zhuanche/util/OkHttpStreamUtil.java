package com.zhuanche.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author kjeakiry
 */
@Slf4j
public class OkHttpStreamUtil {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient() {{
        new Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }};


    public static InputStream execute(String url) {
        Request okHttpRequest = new Request.Builder().url(url).build();
        return getInputStream(okHttpRequest);
    }

    public static byte[] executeForBytes(String url) {
        Request okHttpRequest = new Request.Builder().url(url).build();
        return getResultBytes(okHttpRequest);
    }

    public static InputStream postJson(String url, String json) {
        RequestBody body = RequestBody.create( MediaType.parse("application/json; charset=utf-8"), json);
        Request okHttpRequest = new Request.Builder().url(url).post(body).build();
        return getInputStream(okHttpRequest);
    }

    private static InputStream getInputStream(Request okHttpRequest) {
        Call call = OK_HTTP_CLIENT.newCall(okHttpRequest);
        InputStream inputStream = null;
        try {
            Response okHttpResponse = call.execute();
            if (okHttpResponse != null && okHttpResponse.isSuccessful() && Objects.nonNull(okHttpResponse.body())) {
                inputStream = okHttpResponse.body().byteStream();
            }
        } catch (Exception e) {
            log.error("get byteStream error, url= " + okHttpRequest.url(), e);
        }
        return inputStream;
    }

    private static byte[] getResultBytes(Request okHttpRequest) {
        Call call = OK_HTTP_CLIENT.newCall(okHttpRequest);
        try {
            Response okHttpResponse = call.execute();
            if (okHttpResponse != null && okHttpResponse.isSuccessful() && Objects.nonNull(okHttpResponse.body())) {
                return okHttpResponse.body().bytes();
            }
        } catch (Exception e) {
            log.error("get byteStream error, url= " + okHttpRequest.url(), e);
        }
        return null;
    }
}

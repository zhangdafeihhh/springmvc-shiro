package com.zhuanche.common.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sq.common.okhttp.OkHttpUtil;
import com.zhuanche.http.MpOkHttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author fanht
 * @Description
 * @Date 2019/4/13 下午6:34
 * @Version 1.0
 */
@Component
public class DingdingAlarmUtil {

    private static Logger logger = LoggerFactory.getLogger(DingdingAlarmUtil.class);

    /**
     * 发送钉钉消息
     */
    public static void sendDingdingAlerm(String message, String dingding_token_url){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype","text");
        JSONObject content = new JSONObject();
        content.put("content",message);
        jsonObject.put("text",content);
        JSONObject isAtAll = new JSONObject();
        isAtAll.put("atMobiles",false);
        JSONObject atMobiles = new JSONObject();
        /*JSONArray jsonArray = new JSONArray();
        jsonArray.add("13552448009");
        atMobiles.put("atMobiles",jsonArray);*/
        jsonObject.put("at",isAtAll);
        jsonObject.put("at",atMobiles);

        OkHttpUtil.getIntance().doPostJsonAsync(dingding_token_url, null, jsonObject.toJSONString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.info("钉钉消息发送失败！失败信息:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.info("钉钉消息发送成功!" + response.toString());

            }
        });

/*        MpOkHttpUtil.okHttpPostJsonAsync(dingding_token_url, jsonObject.toJSONString(), 0, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                logger.info("钉钉消息发送失败！失败信息:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.info("钉钉消息发送成功!" + response.toString());
            }
        });*/
    }
}

package com.zhuanche.serv.third;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.http.MpOkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kjeakiry
 */
@Slf4j
@Component
public class MpConfigClient {
    private static final String PUSH_MAIL_SINGLE_DRIVER_PUSH = "/pushMail/singleDriverPush";
    private static Map<String, Object> headerParams = new HashMap<String, Object>() {{
        put("Content-Type", "application/json;charset=UTF-8");
    }};

    @Value("${punish.single.forward.url}")
    private String punishForwardUrl;
    @Value("${config.url}")
    private String configUrl;


    /**
     * 发送站内信
     *
     * @param title title
     * @param nopsis nospis
     * @param content content
     * @param driverId 司机ID
     * @param phone 电话
     */
    public void singleDriverPush(String title, String nopsis, String content, Integer driverId, String phone) {

        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(11);
        paramMap.put("newTitle", title);
        paramMap.put("synopsis", nopsis);
        paramMap.put("content", content);
        paramMap.put("screenType", 1);
        paramMap.put("newType", 1);
        paramMap.put("createBy", 222);
        String forward = punishForwardUrl.replace("{driverId}", driverId + "").replace("{phone}", phone);
        paramMap.put("url", forward);
        paramMap.put("isImportant", 0);
        paramMap.put("speech", 0);
        paramMap.put("carType", 1);
        paramMap.put("driverId", driverId);
        try {
            JSONObject result = MpOkHttpUtil.okHttpGetBackJson(configUrl + PUSH_MAIL_SINGLE_DRIVER_PUSH, paramMap, 3000, "加盟商司机修改申请审核", headerParams);
            log.info("执行结果,result=" + ToStringBuilder.reflectionToString(result));
        } catch (Exception e) {
            log.info("发站内信出错  error:" + e);
        }
    }
}

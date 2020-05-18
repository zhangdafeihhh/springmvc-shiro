package com.zhuanche.serv.third;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.entity.driver.DriverPunish;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.TreeMap;

/**
 * 调用客服后台接口
 * @author kjeakiry
 */
@Slf4j
@Component
public class CsApiClient {

    @Value("${cs.api.url}")
    private String csApiUrl;

    public static final String DRIVER_PUNISH_KEFU_URL_NEW = "/api/driver/punishment/repealnew";
    public static final String DRIVER_PUNISH_KEFU_UPDATE_URL = "/api/threeParty/updateStatus";

    /**
     * 请求客服系统.对司机进行处罚或者解除停运
     */
    public void kefuCancelPunishNew(String businessId, Integer repealResult) {
        TreeMap<String, Object> map = new TreeMap<>();
        map.put("aId", 107);
        map.put("reduceId", businessId);
        map.put("repealResult", repealResult);
        map.put("userId", 1);
        map.put("userName", "sysAdmin");
        try {
            String sign = Md5Util.createSignByBase64(map, "vwQ5L3Gc");
            map.put("sign", sign);
            log.info("sign=" + sign);
            log.info("请求客服系统，对司机进行处罚或者解除停运：businessId:" + businessId + ",repealResult:" + repealResult);
            JSONObject result = MpOkHttpUtil.okHttpGetBackJson(csApiUrl + DRIVER_PUNISH_KEFU_URL_NEW, map, 1, "司机处罚或解除停运");
            log.info("请求客服系统，执行结果,result={}", JSONObject.toJSONString(result));
        } catch (Exception e) {
            log.error("调用客服取消订单处罚异常.", e);
        }
    }

    /**
     * 通知客服系统处罚记录状态变更
     */
    public void notifyKefuUpdate(DriverPunish punishEntity, Integer status) {
        try {
            TreeMap<String, Object> map = new TreeMap<>();
            map.put("orderNo", punishEntity.getOrderNo());
            //稽查状态(3已过期; 4审核通过；6审核拒绝；7已驳回)
            map.put("status", status);
            //yyyy-MM-dd HH:mm:ss,申诉时间
            map.put("appealTime", DateUtil.getTimeString(punishEntity.getAppealDate()));
            log.info("通知客服系统，司机处罚变更param:" + map);
            JSONObject result = MpOkHttpUtil.okHttpGetBackJson(csApiUrl + DRIVER_PUNISH_KEFU_UPDATE_URL, map, 1, "处罚记录状态变更");
            log.info("通知客服系统，司机处罚变更，执行结果,result=" + JSONObject.toJSONString(result));
        } catch (Exception e) {
            log.error("通知客服系统司机处罚变更,异常.", e);
        }
    }

}

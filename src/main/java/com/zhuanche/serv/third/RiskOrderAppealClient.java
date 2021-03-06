package com.zhuanche.serv.third;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.entity.driver.DriverPunish;
import com.zhuanche.http.MpOkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 调用风控接口
 * @author kjeakiry
 */
@Slf4j
@Component
public class RiskOrderAppealClient {


    @Value("${risk.order.complain.url}")
    private String riskOrderComplainUrl;

    /**  http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=23141554 **/
    private static final String DRIVER_PUNISH_FK_URL = "/admin/orderincontrol/updateStatus.do";

    /**
     * 请求风控系统.订单状态处理接口
     * status 1 :未申诉；2: 待处理、3 :申诉成功、 4申诉驳回
     */
    public void updateStatus(DriverPunish punishEntity, int status) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(6);
        map.put("orderNo", punishEntity.getOrderNo());
        map.put("driverId", punishEntity.getDriverId());
        map.put("driverPhone", punishEntity.getPhone());
        map.put("appealStatus", status);
        map.put("appealCommitBy", "sysAdmin");
        map.put("appealCommitTime", System.currentTimeMillis());
        try {
            JSONObject resultJson = MpOkHttpUtil.okHttpPostBackJson(riskOrderComplainUrl + DRIVER_PUNISH_FK_URL, map, 1, "风控撤销订单处罚");
            log.info("申诉处理调用风控更新状态，param:{},result={}", JSONObject.toJSONString(resultJson), JSONObject.toJSONString(resultJson));
        } catch (Exception e) {
            log.error("调用风控取消订单处罚异常.", e);
        }
    }
}

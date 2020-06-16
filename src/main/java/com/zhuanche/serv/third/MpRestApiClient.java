package com.zhuanche.serv.third;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.entity.driver.appraisa.UpdateAppraisalVo;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.http.MpOkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kjeakiry
 */
@Slf4j
@Component
public class MpRestApiClient {

    public static final String API_PUNISH_SEND_DRIVER_SCORE_MSG = "/api/punish/sendDriverScoreMsg";
    public static final String APPRAISAL_API_SET_APPRAISAL = "/appraisalApi/setAppraisal";

    @Value("${mp.restapi.url}")
    private String mpRestApiUrl;

    /**
     * 发送差评处罚信息到mp-restapi,进行扣分处理
     */
    public void sendDriverScoreMsg(CarBizCustomerAppraisalDTO customerAppraisalEntity) {
        String url = mpRestApiUrl + API_PUNISH_SEND_DRIVER_SCORE_MSG;
        Map<String, Object> map = new HashMap<>(8);
        map.put("orderNo", customerAppraisalEntity.getOrderNo());
        map.put("driverId", customerAppraisalEntity.getDriverId());
        map.put("evaluate", customerAppraisalEntity.getEvaluate());
        map.put("evaluateScore", customerAppraisalEntity.getEvaluateScore());
        map.put("memo", customerAppraisalEntity.getMemo());
        JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, map, 1, "发送差评处罚消息");
        log.info("发送差评处罚消息 sendDriverScoreMsg result:{}", JSONObject.toJSONString(result));
    }

    public void updateAppraisalStatus(UpdateAppraisalVo vo) {
        String url = mpRestApiUrl + APPRAISAL_API_SET_APPRAISAL;
        net.sf.json.JSONObject param = net.sf.json.JSONObject.fromObject(vo);
        com.alibaba.fastjson.JSONObject result = null;
        try {
            result = HttpClientUtil.buildPostRequest(url)
                    .addHeader("Content-Type", ContentType.APPLICATION_JSON)
                    .setBody(param.toString()).setReadTimeOut(16000)
                    .executeToJson();
        } catch (Exception e) {
            log.error("updateAppraisalStatus异常", e);
        }
        log.info("updateAppraisalStatus param:{},result:{}", param.toString(), JSONObject.toJSONString(result));
    }
}

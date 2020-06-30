package com.zhuanche.serv.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.entity.driver.DriverPunish;
import com.zhuanche.entity.driver.appraisa.UpdateAppraisalVo;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.http.MpOkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
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
    /** 无结果*/
    public static final String RESULT_NOT_RESULT = "1001";
    /** 请求成功*/
    public static final String RESULT_SUCCESS = "0";
    public static final String CODE = "code";

    @Value("${mp.restapi.url}")
    private String mpRestApiUrl;


    @Value("${mp.rest.url}")
    private String mpRestUrl;

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

    /**
     * 查询处罚配置
     * @param punishEntity
     * @return
     */
    public Map<String, String> getPunishConfig(DriverPunish punishEntity) {
        Map<String, String> dataMap = new HashMap<>();
        long timestamp = System.currentTimeMillis();
        try {
            // 获取特殊配置 参数现在是写死的
            Integer punishType = punishEntity.getPunishType();
            Integer cityId = punishEntity.getCityId();
            Integer cooperationType = punishEntity.getCooperationType();
            String reqUrl = mpRestUrl + "/api/v1/punishTypeSpecial/getConfig?punishType=" + punishType + "&cityCode=" + cityId + "&couperationType=" + cooperationType;
            String confResult = HttpClientUtil.buildGetRequest(reqUrl, 1).setReadTimeOut(3000).setConnectTimeOut(3000).execute();
            log.info("查询策略配置特殊配置 reqUrl:{},result:{}", reqUrl, confResult);
            // 判断结果
            JSONObject jsonResult = JSONObject.parseObject(confResult);

            // 没有查询到特殊配置
            if (RESULT_NOT_RESULT.equals(String.valueOf(jsonResult.get(CODE)))) {
                log.info("没有查询到特殊配置信息,开始查询基础配置.timestamp={}", timestamp);
                String baseUrl = mpRestUrl + "/api/v1/punishTypeBase/getConfigById?configId=" + punishType;
                String baseConf = HttpClientUtil.buildGetRequest(baseUrl, 1).setReadTimeOut(3000).setConnectTimeOut(3000).execute();
                // 判断结果
                JSONObject baseJson = JSONObject.parseObject(baseConf);
                if (!RESULT_SUCCESS.equals(String.valueOf(baseJson.get(CODE)))) {
                    log.info("获取基础配置信息也为空.timestamp={}", timestamp);
                    return dataMap;
                }

                // 解析
                dataMap = JSONObject.parseObject(baseJson.get("data").toString(), Map.class);
                log.info("查询策略配置普通配置 baseUrl:{},baseConf:{}", baseUrl, baseConf);
                return dataMap;
            }

            // 特殊结果
            if (!RESULT_SUCCESS.equals(String.valueOf(jsonResult.get("code")))) {
                log.info("获取信息为空.");
                return dataMap;
            }
            // 解析
            dataMap = JSONObject.parseObject(jsonResult.get("data").toString(), Map.class);
            log.info(JSON.toJSONString(dataMap));

        } catch (HttpException e) {
            log.error("获取申诉特殊配置异常.", e);
        }
        return dataMap;
    }
}

package com.zhuanche.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.util.MyRestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller()
@RequestMapping(value = "/monitor/driverorder")
public class DriverOrderInfoController {

    private static final Logger logger = LoggerFactory.getLogger(DriverOrderInfoController.class);


    @Value("${bigdata.saas.data.url}")
    private String  bigDataSaasUrl;

    /**
     * 大数据的 wiki :
     *          http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21044281
     *  对外输出wiki:
     *          http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21044937
     * @param allianceId  加盟商ID
     * @param vehiclePlate  车牌号
     *  @param driverPhone  司机手机号
     * @param onboardDate   实际上车日期 yyyy-MM-dd 格式字符串
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/indate", method = { RequestMethod.POST,RequestMethod.GET })
    public AjaxResponse driverOrderbBtween(
            @RequestParam(value = "allianceId", required = true,defaultValue = "0")int allianceId,
            @RequestParam(value = "vehiclePlate", required = true)String vehiclePlate,
            @RequestParam(value = "driverPhone", required = true,defaultValue = "")String driverPhone,
            @RequestParam(value = "onboardDate", required = true)String onboardDate) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("allianceId",allianceId);
        param.put("vehiclePlate",vehiclePlate);
        param.put("onboardDate",onboardDate);
        param.put("driverPhone",driverPhone);

        logger.info("监控-指标汇总查询接口-请求参数" + JSON.toJSONString(param));
        String url = "/singleVehicleMonitor/statistic";
        try {

            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
           headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(param), headers);
            RestTemplate restTemplate = new RestTemplate();
            JSONObject responseObject = restTemplate.postForObject(bigDataSaasUrl+url, formEntity, JSONObject.class);
            if (responseObject != null) {
                Integer code = responseObject.getInteger("code");
                if (code == 0) {
                    //gps有返回
                    JSONObject gpgData = responseObject.getJSONObject("result");
                    return AjaxResponse.success(gpgData);
                }else {
                    logger.error("监控-指标汇总查询接口-请求参数" + JSON.toJSONString(param)+",返回结果为："+responseObject.toJSONString());
                }
            }
            return AjaxResponse.success(null);
        } catch (Exception e) {
            logger.error("监控-指标汇总查询接口-请求参数" + JSON.toJSONString(param)+",请求url："+(bigDataSaasUrl+url),e);
            return AjaxResponse.fail(RestErrorCode.MONITOR_DRIVERO_ORDER_FAIL,null);

        }
    }

}

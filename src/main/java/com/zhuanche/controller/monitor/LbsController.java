package com.zhuanche.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.controller.risk.RiskOrderComplainController;
import com.zhuanche.entity.risk.RiskCarManagerOrderComplainEntity;
import com.zhuanche.util.MyRestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller()
@RequestMapping(value = "/monitor/gps")
public class LbsController {

    private static final Logger logger = LoggerFactory.getLogger(LbsController.class);

    @Autowired
    @Qualifier("lbsDriverGpsRestTemplate")
    private MyRestTemplate lbsDriverGpsRestTemplate;

    @ResponseBody
    @RequestMapping(value = "/getGpsByDriver", method = { RequestMethod.POST,RequestMethod.GET })
    public AjaxResponse getGpsByDriver(
            @RequestParam(value = "driverId", required = false,defaultValue = "0")int driverId,
            @RequestParam(value = "startTime", required = true)String startTime,
            @RequestParam(value = "endTime", required = true)String endTime,
              ModelMap model) {
        JSONObject param = new JSONObject();
        param.put("driverId",driverId);
        param.put("startTime",startTime);
        param.put("endTime",endTime);
        logger.info("监控-查看车辆GPS轨迹-请求参数" + JSON.toJSONString(param));
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("driverId", driverId);
            paramMap.put("startTime", startTime);
            paramMap.put("endTime", endTime);
            paramMap.put("platform", 20);


            ResponseEntity<String> responseEntity = lbsDriverGpsRestTemplate.getForEntity("/hbase/queryGpsByDriver",
                    String.class, paramMap);
            if(HttpStatus.OK .equals(responseEntity.getStatusCode() )){
                String result =  responseEntity.getBody();
                if (StringUtils.isNotEmpty(result)) {
                    JSONObject responseObject = JSONObject.parseObject(result);

                    Integer code = responseObject.getInteger("code");
                    if (code == 0) {
                        //gps有返回
                        JSONObject gpgData = responseObject.getJSONObject("data");
                        return AjaxResponse.success(gpgData);
                    }else {
                        logger.error("监控-查看车辆GPS轨迹-返回状态码为："+responseEntity.getStatusCode().toString()+";请求参数" + JSON.toJSONString(param)+",返回结果为："+responseObject.toJSONString());
                    }
                }
            }else{
                logger.error("监控-查看车辆GPS轨迹-返回状态码为："+responseEntity.getStatusCode().toString()+";请求参数" + JSON.toJSONString(param));
            }
            return AjaxResponse.success(null);
        } catch (Exception e) {
            logger.error("监控-查看车辆GPS轨迹-请求参数" + JSON.toJSONString(param),e);
            return AjaxResponse.fail(RestErrorCode.MONITOR_GPS_FAIL,null);

        }
    }

}

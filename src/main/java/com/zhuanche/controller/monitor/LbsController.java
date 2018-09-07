package com.zhuanche.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.controller.risk.RiskOrderComplainController;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.risk.RiskCarManagerOrderComplainEntity;
import com.zhuanche.serv.CarBizDriverInfoService;
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

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;

    /**
     * lbs wiki :
     *          http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=3213570
     * @param driverId
     * @param startTime
     * @param endTime
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getGpsByDriver", method = { RequestMethod.POST,RequestMethod.GET })
    public AjaxResponse getGpsByDriver(
            @RequestParam(value = "driverPhone", required = true,defaultValue = "")String driverPhone,
            @RequestParam(value = "startTime", required = true)String startTime,
            @RequestParam(value = "endTime", required = true)String endTime,
              ModelMap model) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("startTime",startTime);
        paramMap.put("endTime",endTime);
        paramMap.put("platform", 20);
        logger.info("监控-查看车辆GPS轨迹-请求参数" + JSON.toJSONString(paramMap));
        try {
            CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPhone(driverPhone);
            Integer driverId = null;
            if(carBizDriverInfo == null){
                return AjaxResponse.fail(RestErrorCode.MONITOR_GPS_DRIVER_NOT_EXIST,null);
            }else{
                driverId = carBizDriverInfo.getDriverId();
            }
            paramMap.put("driverId",driverId);

            ResponseEntity<String> responseEntity = lbsDriverGpsRestTemplate.getForEntity("/hbase/queryGpsByDriver?driverId="+driverId+"&startTime="+startTime+"&endTime="+endTime+"&platform=20",
                    String.class, new HashMap<>());
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
                        logger.error("监控-查看车辆GPS轨迹-返回状态码为："+responseEntity.getStatusCode().toString()+";请求参数" + JSON.toJSONString(paramMap)+",返回结果为："+responseObject.toJSONString());
                    }
                }
            }else{
                logger.error("监控-查看车辆GPS轨迹-返回状态码为："+responseEntity.getStatusCode().toString()+";请求参数" + JSON.toJSONString(paramMap));
            }
            return AjaxResponse.fail(RestErrorCode.MONITOR_GPS_FAIL,"nodata");
        } catch (Exception e) {
            logger.error("监控-查看车辆GPS轨迹-请求参数" + JSON.toJSONString(paramMap),e);
            return AjaxResponse.fail(RestErrorCode.MONITOR_GPS_FAIL,null);

        }
    }

}

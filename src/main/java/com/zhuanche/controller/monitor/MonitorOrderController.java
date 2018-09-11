package com.zhuanche.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.serv.CarBizDriverInfoService;
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
@RequestMapping(value = "/monitor/order")
public class MonitorOrderController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorOrderController.class);

//    @Value("${bigdata.saas.data.url}")
    private String  exOrderDataSaasUrl;

    /**
     * 定单组 wiki :
     *         http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21047769
     * 输出wiki:
     *          http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21048019
     * @param pageNo
     * @param pageSize
     * @param supplierId        加盟商id
     * @param licensePlates     车牌号
     * @param driverPhone     司机手机号
     * @param beginCreateDate     订单创建时间，开始时间
     * @param endCreateDate     订单创建时间，结束时间
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/page", method = { RequestMethod.POST,RequestMethod.GET })
    public AjaxResponse getGpsByDriver(
            @RequestParam(value = "pageNo", required = false,defaultValue = "0")Integer pageNo,
            @RequestParam(value = "pageSize", required = false,defaultValue = "20")Integer pageSize,
            @RequestParam(value = "supplierId", required = true)String supplierId,
            @RequestParam(value = "licensePlates", required = true)String licensePlates,
            @RequestParam(value = "driverPhone", required = true)Integer driverPhone,
            @RequestParam(value = "beginCreateDate", required = true)String beginCreateDate,
            @RequestParam(value = "endCreateDate", required = true)String endCreateDate,

              ModelMap model) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pageNo",pageNo);
        param.put("pageSize",pageSize);
        param.put("supplierId",supplierId);
        param.put("licensePlates", licensePlates);
        param.put("driverPhone", driverPhone);
        param.put("beginCreateDate", beginCreateDate);
        param.put("endCreateDate", endCreateDate);
        logger.info("监控-查询司机订单列表-请求参数" + JSON.toJSONString(param));
        try {

            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(param), headers);
            RestTemplate restTemplate = new RestTemplate();
            JSONObject responseObject = restTemplate.postForObject(exOrderDataSaasUrl+"/order/v1/search", formEntity, JSONObject.class);
            if (responseObject != null) {
                Integer code = responseObject.getInteger("code");
                if (code == 0) {
                    //es  order 有返回
                    JSONObject pageData = responseObject.getJSONObject("data");
                    JSONArray pageList = pageData.getJSONArray("data");
                    JSONArray newPageList = new JSONArray();
                    if(pageList != null && pageList.size() >=1){
                        JSONObject item = null;
                        for(int i=0;i <= pageList.size();i++){
                            item = pageList.getJSONObject(i);

                            JSONObject newItem = new JSONObject();
                            newItem.put("orderNo",item.getString("orderNo"));
                            newItem.put("tencentCreateTime",item.getString("tencentCreateTime"));
                            newItem.put("status",item.getString("status"));

                            newPageList.add(newItem);
                        }
                    }
                    pageData.put("data",newPageList);
                    return AjaxResponse.success(pageData);
                }else {
                    logger.error("监控-查询司机订单列表-请求参数" + JSON.toJSONString(param)+",返回结果为："+responseObject.toJSONString());
                }
            }
            return AjaxResponse.success(null);
        } catch (Exception e) {
            logger.error("监控-查询司机订单列表-请求参数" + JSON.toJSONString(param),e);
            return AjaxResponse.fail(RestErrorCode.MONITOR_GPS_FAIL,null);

        }
    }

}

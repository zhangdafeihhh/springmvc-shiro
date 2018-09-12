package com.zhuanche.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller()
@RequestMapping(value = "/monitor/order")
public class MonitorOrderController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorOrderController.class);

    @Value("${order.saas.es.url}")
    private String  esOrderDataSaasUrl;

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
    @RequestMapping(value = "/page", method = { RequestMethod.POST })
    public AjaxResponse getGpsByDriver(
            @RequestParam(value = "pageNo", required = false,defaultValue = "1")Integer pageNo,
            @RequestParam(value = "pageSize", required = false,defaultValue = "20")Integer pageSize,
            @RequestParam(value = "supplierId", required = true)String supplierId,
            @RequestParam(value = "licensePlates", required = true)String licensePlates,
            @RequestParam(value = "driverPhone", required = true)String driverPhone,
            @RequestParam(value = "beginCreateDate", required = true)long beginCreateDate,
            @RequestParam(value = "endCreateDate", required = true)long endCreateDate,
            HttpServletRequest request,
              ModelMap model) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pageNo",pageNo);
        param.put("pageSize",pageSize);
        param.put("supplierId",supplierId);
        param.put("licensePlates", licensePlates);
        param.put("driverPhone", driverPhone);
        param.put("beginCreateDate", beginCreateDate);
        param.put("endCreateDate", endCreateDate);
        param.put("transId", UUID.randomUUID().toString().replace("-", "").toLowerCase());

        logger.info("监控-查询司机订单列表-请求参数" + JSON.toJSONString(param));
        try {

            Date begign = new Date(beginCreateDate);
            Date end = new Date(endCreateDate);
            String beginString = DateUtils.formatDate(begign,"yyyy-MM-dd HH:mm:ss");
            String endString = DateUtils.formatDate(end,"yyyy-MM-dd HH:mm:ss");

            MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
            postParameters.add("pageNo", pageNo+"");
            postParameters.add("pageSize", pageSize+"");
            postParameters.add("supplierId", supplierId+"");
            postParameters.add("licensePlates", licensePlates);
            postParameters.add("driverPhone", driverPhone);
            postParameters.add("beginCreateDate", beginString);
            postParameters.add("endCreateDate", endString);
            postParameters.add("transId", param.get("transId"));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity<MultiValueMap<String, Object>> r = new HttpEntity<>(postParameters, headers);
            RestTemplate restTemplate = new RestTemplate();
            JSONObject responseObject= restTemplate.postForObject(esOrderDataSaasUrl+"/order/v1/search", r, JSONObject.class);

            if (responseObject != null) {
                Integer code = responseObject.getInteger("code");
                if (code == 0) {
                    //es  order 有返回
                    JSONObject pageData = responseObject.getJSONObject("data");
                    JSONArray pageList = pageData.getJSONArray("data");
                    JSONArray newPageList = new JSONArray();
                    if(pageList != null && pageList.size() >=1){
                        JSONObject item = null;
                        for(int i=0;i < pageList.size();i++){
                            item = pageList.getJSONObject(i);

                            JSONObject newItem = new JSONObject();
                            newItem.put("orderId",item.getString("orderId"));
                            newItem.put("orderNo",item.getString("orderNo"));
                            newItem.put("createDate",item.getString("createDate"));
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
            return AjaxResponse.fail(RestErrorCode.MONITOR_DRIVERO_ORDER_FAIL,null);

        }
    }

}

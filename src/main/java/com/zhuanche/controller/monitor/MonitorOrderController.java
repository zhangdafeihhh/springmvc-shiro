package com.zhuanche.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
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
     * @param licensePlates     车牌号
     * @param beginCreateDate     订单创建时间，开始时间
     * @param endCreateDate     订单创建时间，结束时间
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/page", method = { RequestMethod.POST })
    public AjaxResponse getGpsByDriver(
            @RequestParam(value = "pageNo", required = false,defaultValue = "1")Integer pageNo,
            @Verify(param = "pageSize",rule = "max(50)")@RequestParam(value = "pageSize", required = false,defaultValue = "20")Integer pageSize,

            @RequestParam(value = "licensePlates", required = true)String licensePlates,
            @RequestParam(value = "beginCreateDate", required = true)long beginCreateDate,
            @RequestParam(value = "endCreateDate", required = true)long endCreateDate,
            HttpServletRequest request,
              ModelMap model) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pageNo",pageNo);
        param.put("pageSize",pageSize);

        param.put("licensePlates", licensePlates);

        param.put("beginCreateDate", beginCreateDate);
        param.put("endCreateDate", endCreateDate);
        param.put("transId", UUID.randomUUID().toString().replace("-", "").toLowerCase());

        logger.info("监控-查询司机订单列表-请求参数" + JSON.toJSONString(param));
        String url = esOrderDataSaasUrl+"/order/v1/search";
        try {

            Date begign = new Date(beginCreateDate);
            Date end = new Date(endCreateDate);
            String beginString = DateUtils.formatDate(begign,"yyyy-MM-dd HH:mm:ss");
            String endString = DateUtils.formatDate(end,"yyyy-MM-dd HH:mm:ss");

            MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
            postParameters.add("pageNo", pageNo+"");
            postParameters.add("pageSize", pageSize+"");

            postParameters.add("licensePlates", licensePlates);

            postParameters.add("beginCreateDate", beginString);
            postParameters.add("endCreateDate", endString);
            postParameters.add("transId", param.get("transId"));

            logger.info("监控-查询司机订单列表-请求参数postParameters=" + JSON.toJSONString(postParameters));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity<MultiValueMap<String, Object>> r = new HttpEntity<>(postParameters, headers);
            RestTemplate restTemplate = new RestTemplate();
            JSONObject responseObject= restTemplate.postForObject(url, r, JSONObject.class);
            logger.info("监控-查询司机订单列表-返回结果为=" + (responseObject == null?"null":responseObject.toJSONString()));
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
                            newItem.put("status",transStatus(item.getString("status")));

                            newPageList.add(newItem);
                        }
                    }
                    pageData.put("data",newPageList);
                    return AjaxResponse.success(pageData);
                }else {
                    logger.error("监控-查询司机订单列表-请求参数" + JSON.toJSONString(param)+",返回结果为："+responseObject.toJSONString() +",url="+url);
                }
            }
            return AjaxResponse.success(null);
        } catch (Exception e) {
            logger.error("监控-查询司机订单列表-url="+url+";请求参数" + JSON.toJSONString(param),e);
            return AjaxResponse.fail(RestErrorCode.MONITOR_DRIVERO_ORDER_FAIL,null);
        }
    }
    private String transStatus(String status){
        if(StringUtils.isEmpty(status)){
            return status;
            //状态 10预定中 ；13订单池； 14待支付 ；15待服务； 20司机已出发；25司机已到达；30服务中；40待结算；42支付中；43扣款中；44后付；45已结算；50已完成；55订单异议；60已取消',
        }else if("10".equals(status)){
            status = "预定中";
        }else if("13".equals(status)){
            status = "订单池";
        }else if("14".equals(status)){
            status = "待支付";
        }else if("15".equals(status)){
            status = "待服务";
        }else if("20".equals(status)){
            status = "司机已出发";
        }else if("25".equals(status)){
            status = "司机已到达";
        }else if("30".equals(status)){
            status = "服务中";
        }else if("40".equals(status)){
            status = "待结算";
        }else if("42".equals(status)){
            status = "支付中";
        }else if("43".equals(status)){
            status = "扣款中";
        }else if("44".equals(status)){
            status = "后付";
        }else if("45".equals(status)){
            status = "已结算";
        }else if("50".equals(status)){
            status = "已完成";
        }else if("55".equals(status)){
            status = "订单异议";
        }else if("60".equals(status)){
            status = "已取消";
        }
        return status;
    }

}

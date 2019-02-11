package com.zhuanche.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.CAR_RUNNING_ORDER_LIST;

@Controller()
@RequestMapping(value = "/monitor/order")
public class MonitorOrderController {

    private static final Logger logger = LoggerFactory.getLogger(MonitorOrderController.class);

    @Value("${order.saas.es.url}")
    private String  esOrderDataSaasUrl;

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;


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
    @RequestFunction(menu = CAR_RUNNING_ORDER_LIST)
    public AjaxResponse getGpsByDriver(
            @RequestParam(value = "pageNo", required = false,defaultValue = "1")Integer pageNo,
            @Verify(param = "pageSize",rule = "max(50)")@RequestParam(value = "pageSize", required = false,defaultValue = "20")Integer pageSize,
            @RequestParam(value = "driverId", required = true,defaultValue = "")String driverId,
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

            if(WebSessionUtil.isSupperAdmin() == false){// 如果是普通管理员
                SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
                Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
                Set<Integer> cityIds  = currentLoginUser.getCityIds();
                Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息

                CarBizDriverInfo carBizDriverInfo = carBizDriverInfoService.selectByPrimaryKey(Integer.parseInt(driverId));
                if(carBizDriverInfo != null){
                    if(cityIds != null && cityIds.size()>=1 && !cityIds.contains(carBizDriverInfo.getServiceCity())){
                        //缺少授权
                        logger.info("监控-指标汇总查询接口-缺少城市授权-请求参数" + JSON.toJSONString(param)+",已授权城市为："+(cityIds==null?"null":JSON.toJSONString(cityIds))+",司机所属城市为："+carBizDriverInfo.getServiceCity());
                        return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED,null);
                    }
                    if(supplierIds != null && supplierIds.size()>=1  && !supplierIds.contains(carBizDriverInfo.getSupplierId())){
                        //缺少授权
                        logger.info("监控-指标汇总查询接口-缺少供应商授权-请求参数" + JSON.toJSONString(param)+",已授权供应商id为："+(supplierIds==null?"null":JSON.toJSONString(supplierIds))+",司机所属供应商id为："+carBizDriverInfo.getSupplierId());
                        return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED,null);
                    }
                }
            }

            Date begign = new Date(beginCreateDate);
            Date end = new Date(endCreateDate);
            String beginString = DateUtils.formatDate(begign,"yyyy-MM-dd HH:mm:ss");
            String endString = DateUtils.formatDate(end,"yyyy-MM-dd HH:mm:ss");

            MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
            postParameters.add("pageNo", pageNo+"");
            postParameters.add("pageSize", pageSize+"");

            postParameters.add("licensePlates", licensePlates);
            postParameters.add("driverId", driverId);
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

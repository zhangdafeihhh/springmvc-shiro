package com.zhuanche.controller.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.zhuanche.common.enums.MenuEnum.CAR_RUNNING_DRIVER_INDATE;

@Controller()
@RequestMapping(value = "/monitor/driverorder")
public class DriverOrderInfoController {

    private static final Logger logger = LoggerFactory.getLogger(DriverOrderInfoController.class);


    @Value("${bigdata.saas.data.url}")
    private String  bigDataSaasUrl;

    @Autowired
    private CarBizDriverInfoService carBizDriverInfoService;

    /**
     * 大数据的 wiki :
     *          http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21044281
     *  对外输出wiki:
     *          http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21044937
     *
     * @param vehiclePlate  车牌号
     *  @param driverId  司机手机号
     * @param onboardDate   实际上车日期 yyyy-MM-dd 格式字符串
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/indate", method = { RequestMethod.POST,RequestMethod.GET })
    @RequestFunction(menu = CAR_RUNNING_DRIVER_INDATE)
    public AjaxResponse driverOrderbBtween(

            @RequestParam(value = "vehiclePlate", required = true)String vehiclePlate,
            @RequestParam(value = "driverId", required = true,defaultValue = "")String driverId,
            @RequestParam(value = "onboardDate", required = true)String onboardDate) {
        Map<String, Object> param = new HashMap<String, Object>();

        param.put("vehiclePlate",vehiclePlate);
        param.put("onboardDate",onboardDate);
        param.put("driverId",driverId);

        logger.info("监控-指标汇总查询接口-请求参数" + JSON.toJSONString(param));
        String url = "/singleVehicleMonitor/statistic";
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
                    if(supplierIds != null && supplierIds.size()>=1 && !supplierIds.contains(carBizDriverInfo.getSupplierId())){
                        //缺少授权
                        logger.info("监控-指标汇总查询接口-缺少供应商授权-请求参数" + JSON.toJSONString(param)+",已授权供应商id为："+(supplierIds==null?"null":JSON.toJSONString(supplierIds))+",司机所属供应商id为："+carBizDriverInfo.getSupplierId());
                        return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED,null);
                    }
                }
            }

            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
           headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(param), headers);
            RestTemplate restTemplate = new RestTemplate();
            JSONObject responseObject = restTemplate.postForObject(bigDataSaasUrl+url, formEntity, JSONObject.class);
            logger.info("监控-指标汇总查询接口-httpUrl:"+bigDataSaasUrl+url+";返回结果为=" + (responseObject == null?"null":responseObject.toJSONString()));
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

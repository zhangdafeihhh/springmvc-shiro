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
import com.zhuanche.util.MyRestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.zhuanche.common.enums.MenuEnum.CAR_RUNNING_TRAIL;

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
     * 输出wiki:
     *          http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21044880

     * @param startTime
     * @param endTime
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getGpsByDriver", method = { RequestMethod.POST,RequestMethod.GET })
	@RequiresPermissions(value = { "CarMonitor_look" } )
    @RequestFunction(menu = CAR_RUNNING_TRAIL)
    public AjaxResponse getGpsByDriver(
            @RequestParam(value = "driverId", required = true,defaultValue = "")String driverId,
            @RequestParam(value = "startTime", required = true)String startTime,
            @RequestParam(value = "endTime", required = true)String endTime,
              ModelMap model) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("driverId",driverId);
        paramMap.put("startTime",startTime);
        paramMap.put("endTime",endTime);
        paramMap.put("platform", 20);
        logger.info("监控-查看车辆GPS轨迹-请求参数" + JSON.toJSONString(paramMap));
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
                        logger.info("监控-指标汇总查询接口-缺少城市授权-请求参数" + JSON.toJSONString(paramMap)+",已授权城市为："+(cityIds==null?"null":JSON.toJSONString(cityIds))+",司机所属城市为："+carBizDriverInfo.getServiceCity());
                        return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED,null);
                    }
                    if(supplierIds != null && supplierIds.size()>=1 && !supplierIds.contains(carBizDriverInfo.getSupplierId())){
                        //缺少授权
                        logger.info("监控-指标汇总查询接口-缺少供应商授权-请求参数" + JSON.toJSONString(paramMap)+",已授权供应商id为："+(supplierIds==null?"null":JSON.toJSONString(supplierIds))+",司机所属供应商id为："+carBizDriverInfo.getSupplierId());
                        return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED,null);
                    }
                }
            }
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

package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;
import com.zhuanche.serv.intercity.DriverInfoInterCityService;
import com.zhuanche.serv.intercity.InterDriverLineRelService;
import com.zhuanche.serv.intercity.MainOrderInterService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author fanht
 * @Description 城际拼车设置司机和线路关联关系
 * @Date 2020/11/3 上午10:05
 * @Version 1.0
 */
@Controller
@RequestMapping("/interDriverLineRel")
@Log4j
public class InterDriverLineRelController {


    @Autowired
    private InterDriverLineRelService driverLineRelService;
    /**
     * 根据userId查询
     * @param userId
     * @return
     */
    @RequestMapping("/queryDetail")
    @ResponseBody
    public AjaxResponse queryDetail(@Verify(param = "userId",rule = "required") Integer userId){
        log.info("根据userId查询线路和司机关系入参:" + userId);
        try {
            return driverLineRelService.queryDetail(userId);
        } catch (Exception e) {
            log.error("查询异常",e);
        }
        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }

    @RequestMapping("/addOrUpdateDriverLineRel")
    @ResponseBody
    public AjaxResponse addOrUpdateDriverLineRel(Integer id,String driverIds,String lineIds){
        log.info("更改or添加城际拼车司机和线路关系入参" + JSONObject.toJSON(id));
        try {
            int code =  driverLineRelService.addOrUpdateDriverLineRel(id,driverIds,lineIds);
            if(code  > 0){
                return AjaxResponse.success(null);
            }
        } catch (Exception e) {
            log.error("更改or添加异常",e);
        }
        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }
}

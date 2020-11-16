package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.intercity.InterDriverLineRelService;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author fanht
 * @Description 城际拼车设置司机和线路关联关系
 * @Date 2020/11/3 上午10:05
 * @Version 1.0
 */
@Controller
@RequestMapping("/interDriverLineRel")
public class InterDriverLineRelController {


    private Logger log = LoggerFactory.getLogger(this.getClass());

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
    public AjaxResponse addOrUpdateDriverLineRel(Integer id,
                                                 @Verify(param = "driverIds",rule = "required|maxLength(2000)") String driverIds,
                                                 @Verify(param = "lineIds",rule = "required|maxLength(2000)") String lineIds,
                                                 @Verify(param = "userId",rule = "required") Integer userId){
        log.info("更改or添加城际拼车司机和线路关系入参" + JSONObject.toJSON(id));
        try {
            int code =  driverLineRelService.addOrUpdateDriverLineRel(id,driverIds,lineIds,userId);
            if(code  > 0){
                return AjaxResponse.success(null);
            }
        } catch (Exception e) {
            log.error("更改or添加异常",e);
        }
        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }


    /**
     * 根据userId查询
     * @param userId
     * @return
     */
    @RequestMapping("/queryAllLineAndDriver")
    @ResponseBody
    public AjaxResponse queryAllLineAndDriver(@Verify(param = "userId",rule = "required") Integer userId){
        log.info("根据userId查询所有的线路和司机关系入参:" + userId);
        try {
            return AjaxResponse.success(driverLineRelService.queryAllLineAndDriver(userId));
        } catch (Exception e) {
            log.error("查询异常",e);
        }
        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }
}

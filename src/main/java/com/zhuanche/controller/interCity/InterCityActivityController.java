package com.zhuanche.controller.interCity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.serv.interCity.InterCityActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author fanht
 * @Description 城际拼车立减/优惠活动
 * @Date 2020/4/20 下午2:24
 * @Version 1.0
 */
@Controller
@RequestMapping("/discount")
public class InterCityActivityController {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterCityActivityService activityService;

    @RequestMapping(value = "/queryList")
    @ResponseBody
    public AjaxResponse queryList(Integer groupId,
                                  Integer disCountStatus,
                                  Integer ruleId){
        logger.info("查询城际拼车立减优惠活动入参:{},{},{}",groupId,disCountStatus,ruleId);

        JSONArray jsonArray = activityService.queryList(groupId,disCountStatus,ruleId);

        AjaxResponse ajaxResponse = AjaxResponse.success(jsonArray);

        return ajaxResponse;
    }
    @RequestMapping(value = "/getDetail")
    @ResponseBody
    public AjaxResponse getDetail(Integer id){
        logger.info("查询城际拼车getDetail立减优惠活动入参:{}",id);

        JSONObject jsonObject = activityService.getDetail(id);

        AjaxResponse ajaxResponse = AjaxResponse.success(jsonObject);

        return ajaxResponse;
    }

    @RequestMapping(value = "/saveOrUpdate")
    @ResponseBody
    public AjaxResponse saveOrUpdate(Integer discountId,
                                     Integer strategyId,
                                     Integer discountType,
                                     String discountAmount,
                                     String discountStartTime,
                                     String discountEndTime,
                                     Integer discountStatus){
        logger.info("查询城际拼车立减优惠活动saveOrUpdate入参:{},{},{},{},{},{}",discountId,strategyId,
                discountType,discountAmount,discountStartTime, discountEndTime,discountStatus);

        Integer code = 0;
        try {
         code =   activityService.saveOrUpdate(discountId,strategyId,discountType,discountAmount,
                   discountStartTime,discountEndTime,discountStatus);
        } catch (Exception e) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        AjaxResponse ajaxResponse = AjaxResponse.success(code);

        return ajaxResponse;
    }


    @RequestMapping(value = "/queryRule")
    @ResponseBody
    public AjaxResponse queryRule(){


        JSONArray jsonArray = activityService.queryRule();
        AjaxResponse ajaxResponse = AjaxResponse.success(jsonArray);

        return ajaxResponse;
    }


}

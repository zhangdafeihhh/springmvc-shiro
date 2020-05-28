package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.serv.intercity.InterCityActivityService;
import com.zhuanche.util.collectionutil.TransportUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

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
                                  Integer discountStatus,
                                  Integer ruleId,
                                  @Verify(param = "pageNo",rule="required|min(0)")Integer pageNo,
                                  @Verify(param = "pageSize",rule="required|min(10)")Integer pageSize){
        logger.info("查询城际拼车立减优惠活动入参:{},{},{}",groupId,discountStatus,ruleId);

        PageDTO pageDTO = activityService.queryList(groupId,discountStatus,ruleId,pageNo,pageSize);

        AjaxResponse ajaxResponse = AjaxResponse.success(pageDTO);

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

    /**
     *
     * @param discountId
     * @param strategyId
     * @param discountType
     * @param discountAmount
     * @param discountStartTime
     * @param discountEndTime
     * @param discountStatus
     * @param allDiscountType
     * @param allDiscountAmount
     * @param carShardRulePrice 拼车规则价格
     * @param carPackRulePrice 包车规则价格
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate")
    @ResponseBody
    public AjaxResponse saveOrUpdate(Integer discountId,
                                     Integer strategyId,
                                     Integer discountType,
                                     String discountAmount,
                                     String discountStartTime,
                                     String discountEndTime,
                                     Integer discountStatus,
                                     Integer allDiscountType,
                                     String allDiscountAmount,
                                     String carShardRulePrice,
                                     String carPackRulePrice){
        logger.info("查询城际拼车立减优惠活动saveOrUpdate入参:{},{},{},{},{},{},{},{}",discountId,strategyId,
                discountType,discountAmount,discountStartTime, discountEndTime,discountStatus,
                allDiscountType,allDiscountAmount);
        /**拼车设置的立减价格不能大于已有的价格最小值 */
        if(IntegerEnum.DISCOUNT_TYPE_ZERO.equals(discountType)){
         String[] strArr =   carShardRulePrice.split(Constants.SEPERATER);
         Double minPrice = TransportUtils.getDoubleMinValue(strArr);
          if(Double.valueOf(discountAmount)>=minPrice){
              logger.info("设置的拼车价格过大");
              return AjaxResponse.fail(RestErrorCode.DISCOUNT_MAX);
          }
        }

        if(IntegerEnum.ALL_DISCOUNT_TYPE_ZERO.equals(discountType)){
            String[] strArr =   carPackRulePrice.split(Constants.SEPERATER);
            Double minPrice = TransportUtils.getDoubleMinValue(strArr);
            if(Double.valueOf(allDiscountAmount)>=minPrice){
                logger.info("设置的包车价格过大");
                return AjaxResponse.fail(RestErrorCode.ALL_DISCOUNT_MAX);
            }
        }

        Integer code = 1;
        try {
         code =   activityService.saveOrUpdate(discountId,strategyId,discountType,discountAmount,
                   discountStartTime,discountEndTime,discountStatus,allDiscountType,allDiscountAmount);
        } catch (Exception e) {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        AjaxResponse ajaxResponse;
        if(code == 0){
            ajaxResponse = AjaxResponse.success(code);
        }else {
            ajaxResponse = AjaxResponse.fail(code);
        }

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

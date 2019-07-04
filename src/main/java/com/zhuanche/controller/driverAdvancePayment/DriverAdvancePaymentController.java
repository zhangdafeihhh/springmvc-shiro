package com.zhuanche.controller.driverAdvancePayment;

import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.serv.mdbcarmanage.DriverAdvancePaymentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 司机垫付列表查询
 * Created by ghg on 2019/7/1.
 */
@Controller
@RequestMapping(value = "/driver")
public class DriverAdvancePaymentController {
    private static Logger logger = LoggerFactory.getLogger(DriverAdvancePaymentController.class);
    @Autowired
    private DriverAdvancePaymentService driverAdvancePaymentService;


    /**
     * 查询司机垫付列表
     *
     * @param page
     * @param pageSize
     * @param driverId
     * @param tradeOrderNo
     * @param status
     * @param startTimeStr
     * @param endTimeStr
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/advancePaymentList")
    public AjaxResponse advancePaymentList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "driverId", required = true) Long driverId,
                                            String tradeOrderNo, Integer status,
                                           @RequestParam(value = "startTimeStr", required = true) String startTimeStr,
                                           @RequestParam(value = "endTimeStr", required = true) String endTimeStr,
                                            Integer total) {
        logger.info("DriverAdvancePaymentController--查询司机垫付列表列表");
        try {
            Map<String, Object> params = Maps.newHashMap();
            params.put("driverId", driverId);
            params.put("startTimeStr", startTimeStr + " 00:00:00");
            params.put("endTimeStr", endTimeStr + " 23:59:59");
            if (StringUtils.isNotBlank(tradeOrderNo)) {
                params.put("tradeOrderNo", tradeOrderNo);
            }
            if(page >1 && total==null){
                return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
            }
            if (status != null) {
                params.put("status", status);
            }
            if(total!= null && total != 0){
                params.put("totalPageSize",total);
            }
            params.put("pageNo", page);
            params.put("pageSize", pageSize);
            AjaxResponse response = driverAdvancePaymentService.queryAdvancePaymentList(params);
            return response;
        } catch (Exception e) {
            logger.error("查询车辆审核列表参数:page--{},pageSize--{},driverId--{},tradeOrderNo--{},status--{}" +
                    "startTimeStr--{},endTimeStr--{}", page, pageSize, driverId, tradeOrderNo, status, startTimeStr, endTimeStr, e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }


}

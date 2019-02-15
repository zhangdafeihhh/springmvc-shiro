package com.zhuanche.controller.system;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.system.OperationLogService;
import com.zhuanche.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/operation")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;


    @RequestMapping("/daily")
    @ResponseBody
    public AjaxResponse getUserDailyOperationCount(@Verify(param = "startDate",rule = "required") String startDate,
                                                   @Verify(param = "endDate",rule = "required")String endDate,
                                                   @RequestParam(defaultValue = "20") int top){
        Date start = DateUtils.parse(startDate + START_END_SUFFIX, FORMAT_STR, Date.class);
        Date end = DateUtils.parse(endDate + END_END_SUFFIX, FORMAT_STR, Date.class);
        return operationLogService.getUserDailyOperationCount(start, end, top);
    }


    @RequestMapping("/menu")
    @ResponseBody
    public AjaxResponse getMenuOperationCount(@Verify(param = "startDate",rule = "required") String startDate,
                                              @Verify(param = "endDate",rule = "required") String endDate,
                                              @RequestParam(defaultValue = "20") int top){
        return operationLogService.getMenuOperationCount(
                DateUtils.parse(startDate + START_END_SUFFIX, FORMAT_STR, Date.class),
                DateUtils.parse(endDate + END_END_SUFFIX, FORMAT_STR, Date.class),
                top);
    }

    @RequestMapping("/user")
    @ResponseBody
    public AjaxResponse getUserOperationCount(@Verify(param = "startDate",rule = "required") String startDate,
                                              @Verify(param = "endDate",rule = "required") String endDate,
                                              @RequestParam(defaultValue = "20") int top){
        Date start = DateUtils.parse(startDate + START_END_SUFFIX, FORMAT_STR, Date.class);
        Date end = DateUtils.parse(endDate + END_END_SUFFIX, FORMAT_STR, Date.class);
        return operationLogService.getUserOperationCount(start, end, top);
    }

    private static final String START_END_SUFFIX = " 00:00:00";
    private static final String END_END_SUFFIX = " 23:59:59";
    private static final String FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

}

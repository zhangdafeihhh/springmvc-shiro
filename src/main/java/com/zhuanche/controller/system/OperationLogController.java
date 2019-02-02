package com.zhuanche.controller.system;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.system.OperationLogService;
import com.zhuanche.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
                                                   @Verify(param = "endDate",rule = "required")String endDate){
        Date start = DateUtils.parse(startDate, "yyyy-MM-dd", Date.class);
        Date end = DateUtils.parse(endDate, "yyyy-MM-dd", Date.class);
        return operationLogService.getUserDailyOperationCount(start, end);
    }


    @RequestMapping("/menu")
    @ResponseBody
    public AjaxResponse getMenuOperationCount(@Verify(param = "startDate",rule = "required") Date startDate,
                                              @Verify(param = "endDate",rule = "required")Date endDate){
        return operationLogService.getMenuOperationCount(startDate, endDate);
    }

    @RequestMapping("/user")
    @ResponseBody
    public AjaxResponse getUserOperationCount(String startDate, String endDate){
        System.out.println(startDate + endDate);
        Date start = DateUtils.parse(startDate, "yyyy-MM-dd", Date.class);
        Date end = DateUtils.parse(endDate, "yyyy-MM-dd", Date.class);
        return operationLogService.getUserOperationCount(start, end);
    }

}

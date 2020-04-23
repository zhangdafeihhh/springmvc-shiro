package com.zhuanche.controller.biDriverMeasureDay;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.driverMeasureDay.BiDriverMeasureDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:qxx
 * @Date:2020/4/23
 * @Description:
 */

@RequestMapping("/driverMeasureDay")
@RestController
public class BiDriverMeasureDayController extends BaseController {


    @Autowired
    private BiDriverMeasureDayService biDriverMeasureDayService;


    @RequestMapping("/count")
    public AjaxResponse count(@Verify(param = "startDate", rule = "required") String startDate,
                              @Verify(param = "endDate", rule = "required") String endDate, String allianceId, String motorcadeId){
        try {
            String result = biDriverMeasureDayService.getResponsibleComplaintRate(startDate,endDate, allianceId);
            return AjaxResponse.success(result);
        }
        catch (Exception e){
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
}

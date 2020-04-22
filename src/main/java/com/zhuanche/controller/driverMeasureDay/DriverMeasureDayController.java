package com.zhuanche.controller.driverMeasureDay;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.controller.driverPunish.DriverPunishController;
import com.zhuanche.entity.bigdata.BiDriverMeasureDay;
import com.zhuanche.serv.driverMeasureDay.BiDriverMeasureDayService;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author:qxx
 * @Date:2020/4/22
 * @Description:
 */

@RequestMapping("/driverMeasureDay")
@RestController
public class DriverMeasureDayController {

    private static final Logger log = LoggerFactory.getLogger(DriverMeasureDayController.class);

    @Autowired
    private BiDriverMeasureDayService biDriverMeasureDayService;

    @RequestMapping("/getRatio")
    public AjaxResponse getRatio(Integer supplierId){
        log.info("查询数据,参数为--{}", supplierId);
        try {
            BiDriverMeasureDay params = new BiDriverMeasureDay();
            if(supplierId !=null){
                params.setSupplierId(supplierId);
            }
            //权限：
            String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
            params.setSupplierIds(suppliers);
            List<BiDriverMeasureDay> list = biDriverMeasureDayService.getRecordList(params);
            Double result = biDriverMeasureDayService.count(list);
            return AjaxResponse.success(result);
        }
        catch (Exception e){
            log.error("查询数据出现异常-{}", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

}

package com.zhuanche.controller.driverPunish;

import com.github.pagehelper.PageInfo;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.entity.driver.DriverAppealRecord;
import com.zhuanche.entity.driver.DriverPunishDto;
import com.zhuanche.serv.driverPunish.DriverPunishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:qxx
 * @Date:2020/4/9
 * @Description:
 */


@RestController
@RequestMapping("/driverPunish")
public class DriverPunishController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(DriverPunishController.class);

    @Autowired
    private DriverPunishService driverPunishService;


    @RequestMapping("/getDriverPunishList")
    public AjaxResponse getDriverPunishList(DriverPunishDto params){
        try {
            log.info("查询列表,参数为--{}", params.toString());
            PageInfo<DriverPunishDto> page = driverPunishService.selectList(params);
            PageDTO pageDTO = new PageDTO(params.getPage(), params.getPagesize(), page.getTotal(), page.getList());
            return AjaxResponse.success(pageDTO);
        }catch (Exception e){
            log.error("查询列表出现异常-{}", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }

    @RequestMapping("/getDriverPunishDetail")
    public AjaxResponse getDriverPunishDetail(Integer punishId){
        try {
            Map<String, Object> data = new HashMap<>();

            log.info("查询详情,punishId为--{}", punishId);
            DriverPunishDto driverPunish = driverPunishService.getDetail(punishId);
            data.put("driverPunish", driverPunish);
            List<DriverAppealRecord> rocordList = driverPunishService.selectDriverAppealRocordByPunishId(punishId);
            if(rocordList==null){
                rocordList = new ArrayList<DriverAppealRecord>();
            }
            data.put("rocordList", rocordList);
            return AjaxResponse.success(data);
        }catch (Exception e){
            log.error("查询列表出现异常-{}", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
    }
}

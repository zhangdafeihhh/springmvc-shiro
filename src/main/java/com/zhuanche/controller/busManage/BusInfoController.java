package com.zhuanche.controller.busManage;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.serv.busManage.BusInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: mp-manage
 * @description: 巴士车辆管理
 * @author: niuzilian
 * @create: 2018-11-22 14:22
 **/
@RestController
@RequestMapping("/busInfo")
public class BusInfoController {
    private static Logger logger = LoggerFactory.getLogger(BusInfoController.class);
    @Autowired
    private BusInfoService busInfoService;

    public AjaxResponse queryList(){
        return null;
    }
}

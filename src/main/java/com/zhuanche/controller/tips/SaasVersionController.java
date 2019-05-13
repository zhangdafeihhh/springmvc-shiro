package com.zhuanche.controller.tips;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.mdbcarmanage.CarBizSaasVersion;
import com.zhuanche.serv.mdbcarmanage.service.CarBizSaasVersionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @Author: nysspring@163.com
 * @Description: saas 系统版本更新记录
 * @Date: 19:08 2019/5/13
 */
@Controller
@RequestMapping("/saasVersion")
public class SaasVersionController {

    @Autowired
    private CarBizSaasVersionService carBizSaasVersionService;




    @RequestMapping(value = "/createVersionRecord",method = RequestMethod.POST)
    @RequiresPermissions(value = {"SupplierTipsCreate"})
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.MASTER)
    })
    public AjaxResponse createVersionRecord(){

        CarBizSaasVersion record = new CarBizSaasVersion();
        record.setCreateUserid(1);
        record.setStatus(1);
        record.setVersion("V1.8");
        record.setVersionSummary("版本简介");
        record.setVersionDetail("版本详情");
        record.setVersionTakeEffectDate(new Date());

        int i = carBizSaasVersionService.saveCarBizSaasVersion(record);

        return AjaxResponse.success(i);
    }



}

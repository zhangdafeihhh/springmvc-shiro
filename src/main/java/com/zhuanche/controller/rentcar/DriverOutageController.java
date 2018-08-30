package com.zhuanche.controller.rentcar;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.rentcar.DriverOutageDTO;
import com.zhuanche.serv.common.DataPermissionHelper;
import com.zhuanche.serv.rentcar.DriverOutageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/driverOutage")
public class DriverOutageController {

    @Autowired
    private DriverOutageService driverOutageService;

    @Autowired
    private DataPermissionHelper dataPermissionHelper;

    private static final Logger logger = LoggerFactory.getLogger(DriverOutageController.class);


    /**
     * 获取临时停运司机列表
     * @param cityId    城市id
     * @param supplierId    供应商id
     * @param carGroupId    服务类型
     * @param outageSource  停运来源:1系统停运 2人工停运
     * @param driverName    司机姓名
     * @param driverPhone   手机号
     * @param startDateBegin    停运开始时间
     * @param startDateEnd      停运结束时间
     * @param removeStatus      解除状态：1：已执行 2：执行中 3：待执行 4：撤销(未执行解除)
     * @return
     */
    @GetMapping(value = "/queryList")
    public AjaxResponse queryList(String cityId,
                                  String supplierId,
                                  String carGroupId,
                                  String outageSource,
                                  String driverName,
                                  String driverPhone,
                                  String startDateBegin,
                                  String startDateEnd,
                                  String removeStatus){
        logger.info("【司机停运】司机停运列表数据:queryList");
        List<DriverOutageDTO> rows = new ArrayList<DriverOutageDTO>();
        //权限
       //TODO 获取当前登录用户名
        String loginName = "";
        Set<Integer> cities = dataPermissionHelper.havePermOfCityIds(loginName);//城市id
        Set<Integer> supplierIds = dataPermissionHelper.havePermOfSupplierIds(loginName);//供应商id

        //查数量
        total = this.driverOutageService.queryForInt(params);
        if(total==0){
            return this.gridJsonFormate(rows, total);
        }
        //查数据
        rows = this.driverOutageService.queryForListObject(params);
        return AjaxResponse.success(rows);
    }

}

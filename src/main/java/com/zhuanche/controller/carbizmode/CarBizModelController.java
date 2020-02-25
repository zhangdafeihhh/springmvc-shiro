package com.zhuanche.controller.carbizmode;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.driver.DriverVehicle;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.serv.financial.DriverVehicleService;
import com.zhuanche.serv.rentcar.CarBizModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@RestController("CarBizModelController")
@RequestMapping(value = "/model")
public class CarBizModelController {


    private static final Logger logger = LoggerFactory.getLogger(CarBizModelController.class);

    @Autowired
    private CarBizModelService carBizModelService;

    @Autowired
    private DriverVehicleService driverVehicleService;

    @RequestMapping(value = "/findbybrandid")
    public AjaxResponse findbybrandid(@Verify(param = "brandId", rule = "required|min(1)")Integer brandId) {
        try{
            List<DriverVehicle> driverVehicles=driverVehicleService.queryDriverVehicleList(brandId);
            List<CarBizModel> modelList = null;
            if(driverVehicles != null && driverVehicles.size() >= 1){
                Set<Integer > modelIds = new HashSet<>();
                for(DriverVehicle item :driverVehicles){
                    if(item.getModelId() != null){
                        modelIds.add(Integer.parseInt(item.getModelId()+""));
                    }
                }

                if(modelIds.size() >= 1){
                    modelList =  carBizModelService.findByIdSet(modelIds);
                }
            }else {
                modelList = new ArrayList<>();
            }
            return AjaxResponse.success(modelList);
        }catch (Exception e){
            logger.error("根据品牌查询关联的车型异常，参数为：brandId="+brandId,e);
            return AjaxResponse.failMsg(500,"服务端错误");
        }

    }
}

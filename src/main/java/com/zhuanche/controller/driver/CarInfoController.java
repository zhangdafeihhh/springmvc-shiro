package com.zhuanche.controller.driver;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.serv.CarBizCarInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/carInfo")
public class CarInfoController {

    private static final Logger logger = LoggerFactory.getLogger(CarInfoController.class);

    @Autowired
    private CarBizCarInfoService carBizCarInfoService;

    /**
     * 查询未绑定车牌号
     * @param cityId 城市ID，必填
     * @param supplierId 供应商ID，必填
     * @param driverId 司机ID
     * @param licensePlates 车牌号
     * @return
     */
    @RequestMapping(value = "/licensePlatesList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DataSourceMode.SLAVE)
    })
    public Object licensePlatesList(@Verify(param = "driverId", rule = "requird") Integer cityId,
                                    @Verify(param = "driverId", rule = "requird") Integer supplierId,
                                    Integer driverId, String licensePlates) {
        // 查询未绑定车牌号
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("cityId",cityId);
        map.put("supplierId",supplierId);
        map.put("driverId",driverId);
        map.put("licensePlates",licensePlates);
        List<CarBizCarInfoDTO> carList = carBizCarInfoService.licensePlatesNotDriverIdList(map);
        return carList;
    }
}

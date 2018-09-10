package com.zhuanche.controller.driver;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.CarBizCooperationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/carBizCarGroup")
public class CarBizCarGroupController {

    @Autowired
    private CarBizCarGroupService carBizCarGroupService;

    @Autowired
    private CarBizCooperationTypeService carBizCooperationTypeService;

    /**
     * 查询服务类型
     * @param type 1:专车、2:大巴车
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCarGroupList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse queryCarGroupList(Integer type) {
        List<CarBizCarGroup> carBizCarGroups = carBizCarGroupService.queryCarGroupList(type);
        return AjaxResponse.success(carBizCarGroups);
    }

    /**
     * 查询所有加盟类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCarBizCooperationTypeList")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)
    })
    public AjaxResponse queryCarBizCooperationTypeList() {
        List<CarBizCooperationType> carBizCooperationTypes = carBizCooperationTypeService.queryCarBizCooperationTypeList();
        return AjaxResponse.success(carBizCooperationTypes);
    }

}

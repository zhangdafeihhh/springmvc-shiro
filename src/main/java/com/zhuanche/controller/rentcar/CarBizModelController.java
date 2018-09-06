package com.zhuanche.controller.rentcar;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.rentcar.CarBizModel;
import com.zhuanche.serv.rentcar.CarBizModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("carBizModel")
public class CarBizModelController {

    @Autowired
    private CarBizModelService carBizModelService;

    @RequestMapping(value="/queryAllList")
    public AjaxResponse updateDriverOutages(){
        List<CarBizModel> carBizModels = carBizModelService.queryAllList();
        return AjaxResponse.success(carBizModels);
    }
}

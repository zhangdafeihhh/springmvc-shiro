package com.zhuanche.controller.supplier;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.driver.SupplierExtDto;
import com.zhuanche.entity.rentcar.CarBizSupplierQuery;
import com.zhuanche.serv.supplier.SupplierRecordService;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author fanht
 * @Description
 * @Date 2019/12/3 下午1:40
 * @Version 1.0
 */
@RequestMapping("/supplierRecord")
@Controller
public class SupplierRecordController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private SupplierRecordService recordService;

    /**
     *
     * @param supplierId
     * @param cityId
     * @param mainCityId
     * @param cooperationMode 合作模式 1 自主 2 非自主
     * @param gradeLevel
     * @param applierStatus 申请更新状态 1 申请中 2.已更新 3 驳回
     * @return
     */
    @RequestMapping("/recordList")
    @ResponseBody
    public AjaxResponse recordList(Integer supplierId,
                                   Integer cityId,
                                   Integer mainCityId,
                                   Integer cooperationMode,
                                   String gradeLevel,
                                   Integer applierStatus,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "30") Integer pageSize){
        SupplierExtDto supplierExtDto = new SupplierExtDto();
        if (mainCityId == null){
            Set<Integer> cityIds = WebSessionUtil.getCurrentLoginUser().getCityIds();
            supplierExtDto.setCityIds(cityIds);
        }else {
            Set<Integer> cityIds = new HashSet<>();
            cityIds.add(mainCityId);
            supplierExtDto.setCityIds(cityIds);
        }

        if(supplierId == null){
            Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
            supplierExtDto.setSupplierIds(supplierIds);
        }else {
            Set<Integer> supplierIds = new HashSet<>();
            supplierIds.add(supplierId);
            supplierExtDto.setSupplierIds(supplierIds);
        }


        return null;
    }
}

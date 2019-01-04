package com.zhuanche.controller.supplier;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import com.zhuanche.entity.rentcar.CarBizSupplierQuery;
import com.zhuanche.entity.rentcar.CarBizSupplierVo;
import com.zhuanche.serv.CarBizCooperationTypeService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequestMapping("/supplier")
@Controller
public class SupplierController {


    @Resource
    private CarBizSupplierService supplierService;

    @Resource
    private CarBizCooperationTypeService cooperationTypeService;

    @RequestMapping("/datalist")
    @ResponseBody
    public AjaxResponse getSupplierDataList(String supplierFullName, Integer supplierCity,
                                            Integer status, Integer cooperationType, Integer enterpriseType,
                                            @RequestParam(defaultValue = "0") Integer pageNum,
                                            @RequestParam(defaultValue = "30") Integer pageSize){
        CarBizSupplierQuery queryParam = new CarBizSupplierQuery();
        if (supplierCity == null){
            Set<Integer> cityIds = WebSessionUtil.getCurrentLoginUser().getCityIds();
            queryParam.setCityIds(cityIds);
        }else {
            Set<Integer> cityIds = new HashSet<>();
            cityIds.add(supplierCity);
            queryParam.setCityIds(cityIds);
        }
        Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
        queryParam.setSupplierIds(supplierIds);
        queryParam.setSupplierFullName(supplierFullName);
        queryParam.setSupplierCity(supplierCity);
        queryParam.setCooperationType(cooperationType);
        queryParam.setEnterpriseType(enterpriseType);
        queryParam.setStatus(status);
        List<CarBizSupplierVo> list;
        int total;
        Page p = PageHelper.startPage(pageNum, pageSize, true);
        try {
            list = supplierService.findSupplierListByPage(queryParam);
            supplierService.addExtInfo(list);
            total = (int)p.getTotal();
        } finally {
            PageHelper.clearPage();
        }
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, list);
        return AjaxResponse.success(pageDTO);
    }

    @RequestMapping("/addSupplier")
    @ResponseBody
    public AjaxResponse addSupplier(CarBizSupplierVo supplier){
        return supplierService.saveSupplierInfo(supplier);
    }

    @RequestMapping("/updateSupplier")
    @ResponseBody
    public AjaxResponse updateSupplier(CarBizSupplierVo supplier){
        return supplierService.saveSupplierInfo(supplier);
    }

    @RequestMapping("/cooperationType")
    @ResponseBody
    public AjaxResponse getCooperationType(){
        List<CarBizCooperationType> carBizCooperationTypes = cooperationTypeService.queryCarBizCooperationTypeList();
        return AjaxResponse.success(carBizCooperationTypes);
    }

    @RequestMapping("/querySupplierById")
    @ResponseBody
    public AjaxResponse getSupplierInfoById(Integer supplierId){
        return supplierService.querySupplierById(supplierId);
    }


}

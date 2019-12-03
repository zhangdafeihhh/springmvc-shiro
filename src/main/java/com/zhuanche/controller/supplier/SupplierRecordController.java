package com.zhuanche.controller.supplier;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.driver.SupplierExtDto;
import com.zhuanche.entity.rentcar.CarBizSupplierQuery;
import com.zhuanche.serv.supplier.SupplierRecordService;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.List;
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
        PageInfo<SupplierExtDto> pageDto = null;
        try {
            SupplierExtDto supplierExtDto = new SupplierExtDto();
            if (cityId == null){
                Set<Integer> cityIds = WebSessionUtil.getCurrentLoginUser().getCityIds();
                supplierExtDto.setCityIds(cityIds);
            }else {
                Set<Integer> cityIds = new HashSet<>();
                cityIds.add(cityId);
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

            supplierExtDto.setMainCityId(mainCityId);
            supplierExtDto.setCooperationMode(cooperationMode);
            supplierExtDto.setGardenPlanLevel(gradeLevel);
            supplierExtDto.setApplierStatus(applierStatus);

            Page page = PageHelper.startPage(pageNum,pageSize,true);
            List<SupplierExtDto> list =  recordService.extDtoList(supplierExtDto);

            pageDto = new PageInfo<>(list);
        } catch (Exception e) {
            logger.error("查询异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return  AjaxResponse.success(pageDto);
    }


    /**
     * 查询
     * @param supplierId
     * @return
     */
    @RequestMapping("/recordDetailEdit")
    @ResponseBody
    public AjaxResponse recordDetailEdit(@Verify(param = "supplierId",rule = "required") Integer supplierId){
        SupplierExtDto dto = new SupplierExtDto();
         try {
             dto = recordService.extDtoDetail(supplierId);
        } catch (Exception e) {
            logger.error("查询异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return  AjaxResponse.success(dto);
    }


    /**
     * 编辑
     * @param supplierId
     * @return
     */
    @RequestMapping("/recordEdit")
    @ResponseBody
    public AjaxResponse recordEdit(@Verify(param = "supplierId",rule = "required") Integer supplierId,
                                   @Verify(param = "contacts",rule = "required") String contacts,
                                   @Verify(param = "contactsPhone",rule = "required") String contactsPhone,
                                   @Verify(param = "email",rule = "required") String email,
                                   @Verify(param = "address",rule = "required") String address){
        SupplierExtDto dto = new SupplierExtDto();
        try {

            dto = recordService.extDtoDetail(supplierId);
        } catch (Exception e) {
            logger.error("查询异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return  AjaxResponse.success(dto);
    }


    /**
     * 详情
     * @param id
     * @return
     */
    @RequestMapping("/supplierRecodeDetail")
    @ResponseBody
    public AjaxResponse supplierRecodeDetail(@Verify(param = "supplierId",rule = "required") Integer id){
        SupplierExtDto dto = new SupplierExtDto();
        try {

            dto = recordService.extDtoDetail(id);
        } catch (Exception e) {
            logger.error("查询异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return  AjaxResponse.success(dto);
    }
}

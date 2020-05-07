package com.zhuanche.controller.supplier;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.supplier.SupplierCooperationAgreementDTO;
import com.zhuanche.dto.rentcar.CityDto;
import com.zhuanche.entity.driver.*;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.CarBizSupplierQuery;
import com.zhuanche.entity.rentcar.CarBizSupplierVo;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.supplier.SupplierRecordService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
import mapper.driver.ex.*;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.util.*;

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

    @Autowired
    private CarBizSupplierExMapper supplierExMapper;

    @Autowired
    private SupplierAccountApplyExMapper applyExMapper;

    @Autowired
    private SupplierExperienceExMapper experienceExMapper;

    @Autowired
    private SupplierCooperationAgreementExMapper agreementExMapper;

    @Autowired
    private SupplierLevelExMapper supplierLevelexMapper;

    @Autowired
    private CarBizSupplierService supplierService;

    @Autowired
    private SupplierCheckFailExMapper exMapper;


    @Autowired
    private CarBizCityExMapper carBizCityExMapper;
    /**
     *
     * @param cityId
     * @param mainCityId
     * @param cooperationMode 合作模式 1 自主 2 非自主
     * @param gradeLevel
     * @param applierStatus 申请更新状态 1 申请中 2.已更新 3 驳回
     * @return
     */
    @RequestMapping("/recordList")
    @ResponseBody
    public AjaxResponse recordList(Integer cityId,
                                   Integer mainCityId,
                                   Integer cooperationMode,
                                   String gradeLevel,
                                   Integer applierStatus,
                                   String supplierName,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "30") Integer pageSize){
        PageInfo<SupplierExtDto> pageDto = null;
        try {
            SupplierExtDto supplierExtDto = new SupplierExtDto();

            if(StringUtils.isNotEmpty(supplierName) || cityId  != null){
                Set<Integer> set = new HashSet<>();
                 if(cityId != null){
                     set.add(cityId);
                 }
                 List<CarBizSupplier> queryByCityOrSupplierName = supplierExMapper.queryByCityOrSupplierName(set,supplierName);
                 if(CollectionUtils.isNotEmpty(queryByCityOrSupplierName)){
                     Set<Integer> setSupplier = new HashSet<>();
                     for(CarBizSupplier supplier : queryByCityOrSupplierName){
                         setSupplier.add(supplier.getSupplierId());
                     }
                     supplierExtDto.setSupplierIds(setSupplier);
                 }else {
                     //如果根据供应商查询出来为空
                     Set<Integer> setSupplier = new HashSet<>();
                     setSupplier.add(0);
                     supplierExtDto.setSupplierIds(setSupplier);
                 }
            }else {
                Set<Integer> supplierIds = WebSessionUtil.getCurrentLoginUser().getSupplierIds();
                supplierExtDto.setSupplierIds(supplierIds);
            }

            supplierExtDto.setMainCityId(mainCityId);
            supplierExtDto.setCooperationMode(cooperationMode);
            supplierExtDto.setGardenPlanLevel(gradeLevel);
            supplierExtDto.setApplierStatus(applierStatus);

            List<CityDto> cityDtoList =  carBizCityExMapper.selectAllCity();
            Map<Integer,String> cityMap = Maps.newHashMap();
            for(CityDto cityDto : cityDtoList){
                cityMap.put(cityDto.getCityId(),cityDto.getCityName());
            }
            Page page = PageHelper.startPage(pageNum,pageSize,true);
            List<SupplierExtDto> list =  recordService.extDtoList(supplierExtDto);

            List<Integer> listSuppliers = new ArrayList<>();
            list.forEach(dto ->{
                listSuppliers.add(dto.getSupplierId());
            });

            List<SupplierCooperationAgreement> agreementList = agreementExMapper.querySupplierIds(listSuppliers);

            Map<Integer,Object> maps = Maps.newHashMap();

            agreementList.forEach(agr ->{
                maps.put(agr.getSupplierId(),agr);
            });

            for(SupplierExtDto dto : list){
                dto.setSupplierFullName(dto.getSupplierShortName());
                if(dto.getMainCityId() > 0){
                    dto.setMainCityName(cityMap.get(dto.getMainCityId()));
                 }
                 SupplierCooperationAgreement agreement = (SupplierCooperationAgreement) maps.get(dto.getSupplierId());
                if(agreement != null){
                    dto.setAgreementStartTime(agreement.getAgreementStartTime() != null ? DateUtils.formatDate(agreement.getAgreementStartTime()):"");
                    dto.setAgreementEndTime(agreement.getAgreementEndTime()     != null ? DateUtils.formatDate(agreement.getAgreementEndTime()) : "");
                    dto.setCarNumber(StringUtil.isEmpty(agreement.getCarNumber() )?  0 : Integer.valueOf(agreement.getCarNumber() ));
                    dto.setLimitLowMonthWater(agreement.getLowLimitMonthWater());
                }
            }
            //获取城市名称、供应商名称
            CarBizSupplierQuery queryParam = new CarBizSupplierQuery();
            Set<Integer> setSuppliers = new HashSet<>();
            for(SupplierExtDto dto : list){
                setSuppliers.add(dto.getSupplierId());
            }
            queryParam.setSupplierIds(setSuppliers);
            List<CarBizSupplierVo> voList = supplierService.findSupplierListByPage(queryParam);
            Map<Integer,CarBizSupplierVo> mapVo = Maps.newHashMap();
            for(CarBizSupplierVo vo : voList){
                mapVo.put(vo.getSupplierId(),vo);
            }
            for(SupplierExtDto dto : list){
                if(null != mapVo.get(dto.getSupplierId()) ){
                    CarBizSupplierVo vo = mapVo.get(dto.getSupplierId());
                    dto.setSupplierName(vo.getSupplierFullName());
                    dto.setCityName(vo.getSupplierCityName());
                }
            }
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
         CarBizSupplierVo supplierVo = new CarBizSupplierVo();
         try {
             supplierVo = supplierExMapper.querySupplierById(supplierId);
             //查询邮箱
             SupplierExtDto dto = recordService.extDtoDetail(supplierId);
             if(dto != null ){
                 supplierVo.setEmail(dto.getEmail());
                 supplierVo.setCustomerPhone(dto.getCustomerPhone());
                 supplierVo.setCustomerLineNumber(dto.getCustomerLineNumber());
             }
         } catch (Exception e) {
            logger.error("查询异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return  AjaxResponse.success(supplierVo);
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
                                   @Verify(param = "address",rule = "required") String address,
                                   String customerPhone,
                                   String customerLineNumber){
        SupplierExtDto dto = new SupplierExtDto();
        try {
            dto.setSupplierId(supplierId);
            dto.setEmail(email);
            dto.setCustomerPhone(customerPhone==null?"":customerPhone);
            dto.setCustomerLineNumber(customerLineNumber);
            int code = recordService.editExtDto(dto);
            CarBizSupplierVo supplier = new CarBizSupplierVo();
            supplier.setContacts(contacts);
            supplier.setContactsPhone(contactsPhone);
            supplier.setAddress(address);
            supplier.setSupplierId(supplierId);
            supplierExMapper.updateByPrimaryKeySelective(supplier);

        } catch (Exception e) {
            logger.error("更新异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return  AjaxResponse.success(null);
    }


    /**
     * 详情
     * @param id
     * @return
     */
    @RequestMapping("/supplierRecodeDetail")
    @ResponseBody
    public AjaxResponse supplierRecodeDetail(@Verify(param = "id",rule = "required") Integer id,
                                             @Verify(param = "supplierId",rule = "required")Integer supplierId){
        CarBizSupplierVo supplier = supplierExMapper.querySupplierById(supplierId);

        try {
            SupplierExtDto dto = new SupplierExtDto();
            dto = recordService.extDtoDetail(supplierId);
            //根据supplierId 获取城市、合作商 负责人、联系电话
            if(dto != null){
                supplier.setMainCityName(dto.getMainCityName());
                if(dto.getCooperationMode() == 1 ){
                    supplier.setCooperationName(SupplierExtDto.modelEnum.COOPERATEION_FREE.getName());
                }else if(dto.getCooperationMode() == 2){
                    supplier.setCooperationName(SupplierExtDto.modelEnum.COOPERATEION_UNFREE.getName());
                }else {
                    supplier.setCooperationName("");
                }
                supplier.setGardenPlanLevel(dto.getGardenPlanLevel());
                supplier.setStatus(dto.getStatus() == null ? 0 : Integer.valueOf(dto.getStatus()));
                supplier.setFirstSignTime(dto.getFirstSignTime()== null ? "" : DateUtils.formatDate(dto.getFirstSignTime(),"yyyy-MM-dd hh:mm:ss"));
                supplier.setMarginAmount(StringUtil.isEmpty(dto.getAmountDeposit())  ? 0.00 : Double.valueOf(dto.getAmountDeposit()));
                supplier.setEmail(dto.getEmail());
                supplier.setSupplierShortName(dto.getSupplierShortName());
                supplier.setSupplierName(dto.getSupplierShortName());

                List<CityDto> cityDtoList =  carBizCityExMapper.selectAllCity();
                Map<Integer,String> cityMap = Maps.newHashMap();
                for(CityDto cityDto : cityDtoList){
                    cityMap.put(cityDto.getCityId(),cityDto.getCityName());
                }
                if(dto.getMainCityId() > 0 ){
                    supplier.setMainCityName(cityMap.get(dto.getMainCityId()));
                }
                //花园计划等级
                SupplierLevel level = supplierLevelexMapper.findSupplierLevelScoreBySupplierId(supplierId);
                if(level != null && StringUtils.isNotEmpty(level.getGradeLevel())){
                    supplier.setGardenPlanLevel(level.getGradeLevel());
                }
            }

            SupplierAccountApply supplierAccountApply = applyExMapper.selectApplyBySupplierId(supplierId);
              if(supplierAccountApply != null &&  supplierAccountApply.getId()>0 ){
                supplier.setApplyList(supplierAccountApply);
            }else {
                  supplierAccountApply = new SupplierAccountApply();
                supplierAccountApply.setBankName(dto.getBankName());
                supplierAccountApply.setBankAccount(dto.getBankAccount());
                supplierAccountApply.setSettlementAccount(dto.getSettlementAccount());
                supplierAccountApply.setBankIdentify(dto.getBankIdentify());
                supplierAccountApply.setSettlementFullName(dto.getSettlementFullName());
                supplier.setApplyList(supplierAccountApply);
            }

            List<SupplierExperience> experienceList = experienceExMapper.selectAllBySupplierId(supplierId);

            supplier.setExperienceList(experienceList);

            List<SupplierCooperationAgreement> agreementList = agreementExMapper.selectAllBySupplierId(supplierId);
            supplier.setCooperationAgreementList(agreementList);

            SupplierLevel params = new SupplierLevel();
            params.setSupplierId(supplierId);
            List<SupplierLevel> list =  supplierLevelexMapper.findPage(params);
            supplier.setLevelList(list);


        } catch (Exception e) {
            logger.error("查询异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        return  AjaxResponse.success(supplier);
    }


    /**
     * 账号信息
     * @param supplierId
     * @return
     */
    @RequestMapping("/bankCountDetail")
    @ResponseBody
    public AjaxResponse bankCountDetail(@Verify(param = "supplierId",rule = "required") Integer supplierId){
        SupplierExtDto dto = new SupplierExtDto();
        try {
            CarBizSupplierVo vo = supplierExMapper.querySupplierById(supplierId);
            SupplierAccountApply apply = applyExMapper.selectApplyStatusBySupplierId(supplierId);
            dto = recordService.extDtoDetail(supplierId);
            if(apply != null){
                dto.setBankAccount(apply.getBankAccount());
                dto.setBankName(apply.getBankName());
                dto.setBankIdentify(apply.getBankIdentify());
                dto.setSettlementAccount(apply.getSettlementAccount());
                dto.setBankPicUrl(apply.getBankPicUrl());
                dto.setOfficalSealUrl(apply.getOfficalSealUrl());
                dto.setSettlementFullName(apply.getSettlementFullName());
                dto.setAccountApplyId(apply.getId());
            }
            if(vo != null){
                dto.setSupplierFullName(vo.getSupplierFullName());
                dto.setSupplierName(vo.getSupplierFullName());
                dto.setCityId(vo.getSupplierCity());
            }


            List<SupplierCheckFail> list = exMapper.failList(supplierId);
            if(CollectionUtils.isNotEmpty(list)){
                dto.setList(list);
            }
          } catch (Exception e) {
            logger.error("查询异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }

        return  AjaxResponse.success(dto);
    }



    /**
     * 账号信息
     * @param
     * @return
     */
    @RequestMapping("/bankCountEdit")
    @ResponseBody
    public AjaxResponse bankCountEdit(Long id,
                                      @Verify(param = "supplierId",rule = "required") Integer supplierId,
                                      @Verify(param = "bankName",rule = "required") String bankName,
                                      @Verify(param = "bankAccount",rule = "required") String bankAccount,
                                      @Verify(param = "settlementFullName",rule = "required") String  settlementFullName,
                                      @Verify(param = "bankIdentify",rule = "required") String bankIdentify,
                                      @Verify(param = "bankPicUrl",rule = "required") String bankPicUrl,
                                      @Verify(param = "officalSealUrl",rule = "required") String officalSealUrl){
        SupplierAccountApply accountApply = new SupplierAccountApply();
        try {
            accountApply.setSupplierId(supplierId);
            accountApply.setBankName(bankName);
            accountApply.setBankAccount(bankAccount);
            accountApply.setSettlementFullName(settlementFullName);
            accountApply.setBankIdentify(bankIdentify);
            accountApply.setBankPicUrl(bankPicUrl);
            accountApply.setId(id);
            accountApply.setOfficalSealUrl(officalSealUrl);
            accountApply.setStatus((byte) 1);

            int code  = 0;
            if(id != null &&  id > 0){
              code =  applyExMapper.updateByPrimaryKey(accountApply);
            }else {
              CarBizSupplierVo vo =  supplierExMapper.querySupplierById(supplierId);
              if(vo != null){
                  SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
                  accountApply.setCityId(vo.getSupplierCity());
                  accountApply.setSettlementFullName(settlementFullName);
                  accountApply.setUpdateBy(loginUser.getId());
                  accountApply.setUpdateName(loginUser.getName());
                  accountApply.setUpdateDate(new Date());
                  accountApply.setCreateBy(loginUser.getId());
                  accountApply.setCreateName(loginUser.getName());
                  accountApply.setCreateDate(new Date());
                  code = applyExMapper.insert(accountApply);
              }
            }


            if(code > 0){
                SupplierExtDto dto = new SupplierExtDto();
                dto.setSupplierId(supplierId);
                dto.setApplierStatus(1);
                recordService.editExtStatus(dto);
            }

        } catch (Exception e) {
            logger.error("更新异常" + e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        return  AjaxResponse.success(null);
    }
}

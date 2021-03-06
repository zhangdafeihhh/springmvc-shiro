package com.zhuanche.serv.supplier;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.supplier.SupplierAccountApplyDTO;
import com.zhuanche.entity.driver.SupplierAccountApply;
import com.zhuanche.entity.driver.SupplierCheckFail;
import com.zhuanche.entity.driver.SupplierExt;
import com.zhuanche.entity.driver.SupplierExtDto;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.driver.SupplierAccountApplyMapper;
import mapper.driver.SupplierExtDtoMapper;
import mapper.driver.ex.SupplierAccountApplyExMapper;
import mapper.driver.ex.SupplierExtDtoExMapper;
import mapper.rentcar.CarBizCityMapper;
import mapper.rentcar.CarBizSupplierMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SupplierAccountApplyService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierAccountApplyService.class);

    @Autowired
    private SupplierAccountApplyExMapper supplierAccountApplyExMapper;

    @Autowired
    private SupplierExtDtoExMapper supplierExtDtoExMapper;

    @Autowired
    private SupplierAccountApplyMapper supplierAccountApplyMapper;

    @Autowired
    private CarBizCityMapper carBizCityMapper;

    @Autowired
    private CarBizSupplierMapper carBizSupplierMapper;

    @Autowired
    private SupplierExtDtoMapper supplierExtDtoMapper;

    @Autowired
    private SupplierCheckFailService failService;

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public List<SupplierAccountApply> selectApplyAllBySupplierId(Integer supplierId){
        return supplierAccountApplyExMapper.selectApplyAllBySupplierId(supplierId);
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public SupplierAccountApply selectApplyBySupplierId(Integer supplierId){
        return supplierAccountApplyExMapper.selectApplyBySupplierId(supplierId);
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.MASTER )
    } )
    public AjaxResponse addSupplierAccountApply(SupplierAccountApply params){
        int i = supplierAccountApplyMapper.insertSelective(params);
        if(i>0){
            return AjaxResponse.success(null);
        } else {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public AjaxResponse updateSupplierAccountApplyStatua(Long id, Integer cityId, Integer supplierId,
                                                          String bankAccount,
                                                         String bankName, String bankIdentify,
                                                         String settlementFullName,String bankPicUrl,
                                                         String officalSealUrl,Integer status,String remark){


        SupplierAccountApply apply = supplierAccountApplyMapper.selectByPrimaryKey(id);
        if(apply==null){
            return AjaxResponse.fail(RestErrorCode.SUPPLIER_ACCOUNT_APPLY_UPDATE);
        }

        //??????????????????????????????????????????
        SupplierAccountApply params = new SupplierAccountApply();
        params.setId(id);
        params.setCityId(cityId);
        params.setSupplierId(supplierId);
/*
        params.setSettlementAccount(settlementAccount);
*/
        params.setBankAccount(bankAccount);
        params.setBankName(bankName);
        params.setBankIdentify(bankIdentify);
        params.setSettlementFullName(settlementFullName);
        params.setStatus((byte)status.intValue());
        SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
        params.setUpdateBy(currentLoginUser.getId());
        params.setUpdateName(currentLoginUser.getName());
        params.setBankPicUrl(bankPicUrl);
        params.setOfficalSealUrl(officalSealUrl);
        //????????????
        DynamicRoutingDataSource.setMasterSlave("driver-DataSource", DataSourceMode.MASTER);
        int i = supplierAccountApplyMapper.updateByPrimaryKeySelective(params);

        if(i>0){
            //????????????????????????????????????
            SupplierExtDto supplierExtDto = new SupplierExtDto();
            supplierExtDto.setSupplierId(supplierId);
            supplierExtDto.setApplierStatus(status);

            int count = supplierExtDtoExMapper.selectCountBySupplierId(supplierId);
            logger.info("????????? ???????????????????????? ??????????????????????????????={}", count);

            //?????????????????????????????????
            if(!status.equals(SupplierExtDto.opeStatus.REJERECT.getCode())){
                supplierExtDto.setBankAccount(bankAccount);
                supplierExtDto.setBankName(bankName);
                supplierExtDto.setBankIdentify(bankIdentify);
                supplierExtDto.setSettlementFullName(settlementFullName);
            }

            if (count == 0) {
                supplierExtDtoMapper.insertSelective(supplierExtDto);
                logger.info("??????????????????????????? : supplierExtInfo {}", JSON.toJSONString(supplierExtDto));
            } else {

                supplierExtDtoExMapper.updateBySupplierId(supplierExtDto);
                logger.info("??????????????????????????? : supplierExtInfo {}", JSON.toJSONString(supplierExtDto));
            }

            try {
                SupplierCheckFail fail = new SupplierCheckFail();
                fail.setStatus(status);
                fail.setRemark(remark);
                fail.setSupplierId(supplierId);
                fail.setCreateTime(new Date());
                fail.setUpdateTime(new Date());
                failService.insert(fail);
            } catch (Exception e) {
                logger.info("????????????" + e);
            }

            return AjaxResponse.success(null);
        } else {
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE ),
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public List<SupplierAccountApplyDTO> queryApplyList(Integer cityId, Integer supplierId, Integer status) {
        // ??????????????????SSOLoginUser
        Set<Integer> permOfCity        = WebSessionUtil.getCurrentLoginUser().getCityIds(); //??????????????????????????????????????????ID
        Set<Integer> permOfSupplier    = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); //?????????????????????????????????????????????ID

        List<SupplierAccountApply> applyList = supplierAccountApplyExMapper.queryApplyList(cityId, supplierId, status, permOfCity, permOfSupplier);

        List<SupplierAccountApplyDTO> applyDTOS = Lists.newArrayList();

        applyList.forEach( apply -> {
            if (apply != null){
                SupplierAccountApplyDTO applyDTO = new SupplierAccountApplyDTO();
                BeanUtils.copyProperties(apply, applyDTO);
                CarBizCity carBizCity = carBizCityMapper.selectByPrimaryKey(apply.getCityId());
                if(carBizCity!=null){
                    applyDTO.setCityName(carBizCity.getCityName());
                }
                CarBizSupplier carBizSupplier = carBizSupplierMapper.selectByPrimaryKey(apply.getSupplierId());
                if(carBizSupplier!=null){
                    applyDTO.setSupplierName(carBizSupplier.getSupplierFullName());
                }
                applyDTOS.add(applyDTO);
            }
        });
        return applyDTOS;
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public SupplierAccountApply selectApplyStatusBySupplierId(Integer supplierId){
        return supplierAccountApplyExMapper.selectApplyStatusBySupplierId(supplierId);
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="driver-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    public SupplierAccountApply selectByPrimaryKey(Long id){
        return supplierAccountApplyMapper.selectByPrimaryKey(id);
    }
}
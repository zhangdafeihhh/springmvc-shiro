package com.zhuanche.serv.mdbcarmanage;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.mdbcarmanage.DriverInfoUpdateApplyDTO;
import com.zhuanche.entity.driver.DriverBrand;
import com.zhuanche.entity.driver.DriverVehicle;
import com.zhuanche.entity.mdbcarmanage.DriverInfoUpdateApply;
import com.zhuanche.serv.financial.DriverBrandService;
import com.zhuanche.serv.financial.DriverVehicleService;
import mapper.mdbcarmanage.DriverInfoUpdateApplyMapper;
import mapper.mdbcarmanage.ex.DriverInfoUpdateApplyExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverInfoUpdateService {

    @Autowired
    private DriverInfoUpdateApplyMapper driverInfoUpdateApplyMapper;


    @Autowired
    private DriverInfoUpdateApplyExMapper driverInfoUpdateApplyExMapper;

    @Autowired
    private DriverVehicleService driverVehicleService;

    @Autowired
    private DriverBrandService driverBrandService;
    /**
     * 司机\车辆修改申请信息列表
     * @param driverInfoUpdateApplyDTO
     * @return
     */
    public List<DriverInfoUpdateApplyDTO> queryDriverInfoUpdateList(DriverInfoUpdateApplyDTO driverInfoUpdateApplyDTO){
        return driverInfoUpdateApplyExMapper.queryDriverInfoUpdateList(driverInfoUpdateApplyDTO);
    }

    /**
     * 根据ID查询信息
     * @param id
     * @return
     */
    public DriverInfoUpdateApply selectByPrimaryKey(Integer id){
        DriverInfoUpdateApply entity =  driverInfoUpdateApplyMapper.selectByPrimaryKey(id);
        packObj(entity );
        return entity;
    }

    public void packObj(DriverInfoUpdateApply driverInfoUpdateApply ){
        if(driverInfoUpdateApply != null){
            if(driverInfoUpdateApply.getCarModelId() != null){
                DriverVehicle driverVehicle = driverVehicleService.queryByModelId(driverInfoUpdateApply.getCarModelId());
                if(driverVehicle != null){
                    Long brandId =   driverVehicle.getBrandId();
                    driverInfoUpdateApply.setNewBrandId(brandId);
                    if(brandId != null){
                        DriverBrand driverBrand = driverBrandService.getDriverBrandByPrimaryKey(brandId);
                        if(driverBrand != null){
                            driverInfoUpdateApply.setNewBrandName(driverBrand.getBrandName());
                        }
                    }
                }
            }
            if(driverInfoUpdateApply.getCarModelIdNew() != null){
                DriverVehicle driverVehicle2 = driverVehicleService.queryByModelId(driverInfoUpdateApply.getCarModelIdNew());
                if(driverVehicle2 != null){
                    Long brandId2 =   driverVehicle2.getBrandId();
                    driverInfoUpdateApply.setNewBrandIdNew(brandId2);
                    if(brandId2 != null){
                        DriverBrand driverBrand2 = driverBrandService.getDriverBrandByPrimaryKey(brandId2);
                        if(driverBrand2 != null){
                            driverInfoUpdateApply.setNewBrandNameNew(driverBrand2.getBrandName());
                        }
                    }
                }
            }
        }
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public int insertSelective(DriverInfoUpdateApply driverInfoUpdateApply){
        return driverInfoUpdateApplyMapper.insertSelective(driverInfoUpdateApply);
    }
}

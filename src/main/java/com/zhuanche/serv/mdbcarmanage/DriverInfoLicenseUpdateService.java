package com.zhuanche.serv.mdbcarmanage;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.mdbcarmanage.DriverInfoLicenseUpdateApplyDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoLicenseUpdateApply;
import mapper.mdbcarmanage.DriverInfoLicenseUpdateApplyMapper;
import mapper.mdbcarmanage.ex.DriverInfoLicenseUpdateApplyExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverInfoLicenseUpdateService {

    @Autowired
    private DriverInfoLicenseUpdateApplyMapper DriverInfoLicenseUpdateApplyMapper;


    @Autowired
    private DriverInfoLicenseUpdateApplyExMapper DriverInfoLicenseUpdateApplyExMapper;

    /**
     * 司机\车辆修改申请信息列表
     * @param licenseUpdateApplyDTO
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<DriverInfoLicenseUpdateApplyDTO> queryDriverInfoLicenseUpdateList(DriverInfoLicenseUpdateApplyDTO licenseUpdateApplyDTO){
        return DriverInfoLicenseUpdateApplyExMapper.queryDriverInfoLicenseUpdateList(licenseUpdateApplyDTO);
    }

    /**
     * 根据ID查询信息
     * @param id
     * @return
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public DriverInfoLicenseUpdateApply selectByPrimaryKey(Integer id){
        return DriverInfoLicenseUpdateApplyMapper.selectByPrimaryKey(id);
    }
}

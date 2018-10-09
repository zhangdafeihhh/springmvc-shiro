package com.zhuanche.serv.mdbcarmanage;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.mdbcarmanage.DriverInfoUpdateApplyDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoUpdateApply;
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
        return driverInfoUpdateApplyMapper.selectByPrimaryKey(id);
    }

    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
    public int insertSelective(DriverInfoUpdateApply driverInfoUpdateApply){
        return driverInfoUpdateApplyMapper.insertSelective(driverInfoUpdateApply);
    }
}

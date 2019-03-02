package com.zhuanche.serv;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.driver.TwoLevelCooperationDto;
import com.zhuanche.entity.rentcar.CarBizCooperationType;
import mapper.driver.ex.TwoLevelCooperationExMapper;
import mapper.rentcar.CarBizCooperationTypeMapper;
import mapper.rentcar.ex.CarBizCooperationTypeExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarBizCooperationTypeService {

    @Autowired
    private CarBizCooperationTypeMapper carBizCooperationTypeMapper;

    @Autowired
    private CarBizCooperationTypeExMapper carBizCooperationTypeExMapper;

    @Autowired
    private TwoLevelCooperationExMapper twoLevelCooperationExMapper;

    /**
     * 查找加盟类型名称
     * @param id
     * @return
     */
    public CarBizCooperationType selectByPrimaryKey(Integer id){
        return carBizCooperationTypeMapper.selectByPrimaryKey(id);
    }

/*    *//**
     * 根据groupId查询
     * @param carBizCarGroup
     * @return
     *//*
    public CarBizCooperationType queryForObject(CarBizCooperationType carBizCarGroup){
        return carBizCooperationTypeExMapper.queryForObject(carBizCarGroup);
    }*/


    /**
     * 查询所有加盟类型
     * @return
     */
    public List<CarBizCooperationType> queryCarBizCooperationTypeList(){
        return carBizCooperationTypeExMapper.queryCarBizCooperationTypeList();
    }


    @MasterSlaveConfigs(
            configs = {
                    @MasterSlaveConfig(databaseTag = "driver-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
            }
    )
    public List<TwoLevelCooperationDto> queryTwoLevelCooperationType(Integer cooperationId) {
        return twoLevelCooperationExMapper.getTwoLevelCooperationTypeByCooperationId(cooperationId);

    }
}

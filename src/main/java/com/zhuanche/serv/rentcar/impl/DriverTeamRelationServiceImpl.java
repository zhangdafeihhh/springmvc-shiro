package com.zhuanche.serv.rentcar.impl;

import com.zhuanche.dto.driver.DriverTeamRelationEntity;
import com.zhuanche.serv.rentcar.IDriverTeamRelationService;
import mapper.mdbcarmanage.ex.CarRelateTeamExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverTeamRelationServiceImpl implements IDriverTeamRelationService<DriverTeamRelationEntity> {

    @Autowired
    private CarRelateTeamExMapper carRelateTeamExMapper;

    @Override
    public List<DriverTeamRelationEntity> selectDriverIdsNoLimit(DriverTeamRelationEntity params) {
        return carRelateTeamExMapper.queryForListObjectNoLimit(params);
    }

    @Override
    public String pingDriverIds(List<DriverTeamRelationEntity> params) {
        String driverId = "";
        if(params!=null&&params.size()>0){
            int j=0;
            for(int i=0;i<params.size();i++){
                if(!"".equals(params.get(i))&&params.get(i)!=null&&!"".equals(params.get(i).getDriverId())&&params.get(i).getDriverId()!=null){
                    if(j==0){
                        driverId = "'"+params.get(i).getDriverId()+"'";
                    }else{
                        driverId +=",'"+params.get(i).getDriverId()+"'";
                    }
                    j++	;
                }
            }
        }
        return driverId;
    }

    @Override
    public DriverTeamRelationEntity selectDriverInfo(DriverTeamRelationEntity params) {
        return this.carRelateTeamExMapper.queryForObject(params);
    }

    @Override
    public DriverTeamRelationEntity queryForObjectGroup(DriverTeamRelationEntity params) {
        return this.carRelateTeamExMapper.queryForObjectGroup(params);
    }

}

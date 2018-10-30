package com.zhuanche.serv.rentcar.impl;

import com.zhuanche.dto.driver.DriverTeamRelationEntity;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDetailDTO;
import com.zhuanche.entity.rentcar.DriverEntity;
import com.zhuanche.serv.rentcar.IDriverService;
import com.zhuanche.serv.rentcar.IDriverTeamRelationService;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizDriverInfoDetailExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverServiceImpl implements IDriverService<DriverEntity> {
    private static Logger logger = Logger.getLogger(DriverServiceImpl.class);


    @Autowired
    private CarBizDriverInfoMapper carBizDriverInfoMapper;

    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

    @Autowired
    private IDriverTeamRelationService driverTeamRelationService;

    @Autowired
    private CarBizDriverInfoDetailExMapper carBizDriverInfoDetailExMapper;


    @Override
    public int selectDriverByKeyCountAddCooperation(DriverVoEntity params) {
        return carBizDriverInfoExMapper.selectDriverByKeyCountAddCooperation(params);
    }

    @Override
    public List<DriverVoEntity> selectDriverByKeyAddCooperation(DriverVoEntity params) {
        //查询司机信息
        List<DriverVoEntity> driverEntity = carBizDriverInfoExMapper.selectDriverByKeyAddCooperation(params);
        //
        for(int i=0;i<driverEntity.size();i++){
            DriverTeamRelationEntity params2 = new DriverTeamRelationEntity();
            params2.setDriverId(driverEntity.get(i).getDriverId());
            DriverTeamRelationEntity driverTeamRelationEntity = (DriverTeamRelationEntity) driverTeamRelationService.selectDriverInfo(params2);
            if(driverTeamRelationEntity != null){
                driverEntity.get(i).setTeamid(driverTeamRelationEntity.getTeamId());
                driverEntity.get(i).setTeamName(driverTeamRelationEntity.getTeamName());
            }
//            DriverTeamRelationEntity driverTeamRelationEntity2 = (DriverTeamRelationEntity) this.driverTeamRelationService.queryForObjectGroup(params2);
//            if(!"".equals(driverTeamRelationEntity2)&&driverTeamRelationEntity2!=null){
//                driverEntity.get(i).setGroupId(driverTeamRelationEntity2.getGroupId());
//                driverEntity.get(i).setGroupName(driverTeamRelationEntity2.getGroupName());
//            }
//            CarBizDriverInfoDetailDTO d = carBizDriverInfoDetailExMapper.selectByDriverId(Integer.parseInt(driverEntity.get(i).getDriverId()));
//
//            if(d!=null&&d.getExt1()!=null){
//                driverEntity.get(i).setExt1(d.getExt1());
//            }else{
//                driverEntity.get(i).setExt1(2);
//            }
        }
        return driverEntity;
    }


}

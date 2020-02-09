package com.zhuanche.serv.rentcar.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.driver.DriverTeamRelationEntity;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.entity.rentcar.DriverEntity;
import com.zhuanche.serv.rentcar.IDriverService;
import com.zhuanche.serv.rentcar.IDriverTeamRelationService;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizDriverInfoDetailExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public PageInfo<DriverVoEntity> findPageDriver(DriverVoEntity params) {
        logger.info("查询司机信息，参数为"+(params == null ?"null": JSON.toJSONString(params)));
        PageHelper.startPage(params.getPage(), params.getPagesize(), true);
        List<DriverVoEntity> list = carBizDriverInfoExMapper.selectDriverByKeyAddCooperation(params);
        PageInfo<DriverVoEntity> pageInfo = new PageInfo<>(list);
        list = pageInfo.getList();
        if(list != null){
            Set<String> driverIdSet = new HashSet<>();
            for(DriverVoEntity item : list){
                driverIdSet.add(item.getDriverId());
            }
            //批量查询司机与车队的关系对象
            List<DriverTeamRelationEntity> driverTeamRelationEntityList = driverTeamRelationService.selectByDriverIdSet(driverIdSet);
            //本地遍历，缓存
            Map<String,DriverTeamRelationEntity> cacheDriverTeamRelationEntity = new HashMap<>();
            if(driverTeamRelationEntityList != null){
                for(DriverTeamRelationEntity item :driverTeamRelationEntityList){
                    cacheDriverTeamRelationEntity.put(item.getDriverId(),item);
                }
            }
            //遍历赋值
            DriverTeamRelationEntity tempDriverTeamRelationEntity = null;
            for(DriverVoEntity item : list){
                tempDriverTeamRelationEntity = cacheDriverTeamRelationEntity.get(item.getDriverId());
                if(tempDriverTeamRelationEntity != null){
                    item.setTeamid(tempDriverTeamRelationEntity.getTeamId());
                    item.setTeamName(tempDriverTeamRelationEntity.getTeamName());
                }
            }
        }
        return  pageInfo;
    }

    @Override
    public List<Integer> queryDriverIdsByName(String name) {
        return carBizDriverInfoExMapper.queryDriverIdsByName(name);
    }

    @Override
    public List<Integer> queryDriverIdsByPhone(String phone) {
        return carBizDriverInfoExMapper.queryDriverIdsByPhone(phone);
    }

}

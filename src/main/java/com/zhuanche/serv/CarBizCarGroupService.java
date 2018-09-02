package com.zhuanche.serv;

import com.zhuanche.entity.rentcar.CarBizCarGroup;
import mapper.rentcar.CarBizCarGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarBizCarGroupService {

    @Autowired
    private CarBizCarGroupMapper carBizCarGroupMapper;

    public CarBizCarGroup selectByPrimaryKey(Integer groupId){
        return carBizCarGroupMapper.selectByPrimaryKey(groupId);
    }

}

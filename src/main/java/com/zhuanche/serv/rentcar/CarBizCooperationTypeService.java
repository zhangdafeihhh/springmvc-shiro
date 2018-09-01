package com.zhuanche.serv.rentcar;

import com.zhuanche.entity.rentcar.CarBizCooperationType;
import mapper.rentcar.ex.CarBizCooperationTypeExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarBizCooperationTypeService {

    @Autowired
    private CarBizCooperationTypeExMapper carBizCooperationTypeExMapper;


    /**
     * 根据groupId查询
     * @param carBizCarGroup
     * @return
     */
    public CarBizCooperationType queryForObject(CarBizCooperationType carBizCarGroup){
        return carBizCooperationTypeExMapper.queryForObject(carBizCarGroup);
    }
}
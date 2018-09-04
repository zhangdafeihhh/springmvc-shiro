package com.zhuanche.serv;

import com.zhuanche.entity.rentcar.CarBizCooperationType;
import mapper.rentcar.CarBizCooperationTypeMapper;
import mapper.rentcar.ex.CarBizCooperationTypeExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarBizCooperationTypeService {

    @Autowired
    private CarBizCooperationTypeMapper carBizCooperationTypeMapper;

    /*@Autowired
    private CarBizCooperationTypeExMapper carBizCooperationTypeExMapper;*/

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
}

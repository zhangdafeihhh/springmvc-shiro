package com.zhuanche.serv;

import com.zhuanche.entity.rentcar.CarBizCooperationType;
import mapper.rentcar.CarBizCooperationTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarBizCooperationTypeService {

    @Autowired
    private CarBizCooperationTypeMapper carBizCooperationTypeMapper;

    /**
     * 查找加盟类型名称
     * @param id
     * @return
     */
    public CarBizCooperationType selectByPrimaryKey(Integer id){
        return carBizCooperationTypeMapper.selectByPrimaryKey(id);
    }
}

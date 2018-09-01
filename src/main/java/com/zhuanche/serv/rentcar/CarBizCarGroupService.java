package com.zhuanche.serv.rentcar;

import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizCity;
import mapper.rentcar.CarBizCityMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CarBizCarGroupService {
	@Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;


    /**
     * 根据groupId查询
     * @param carBizCarGroup
     * @return
     */
    public CarBizCarGroup queryForObject(CarBizCarGroup carBizCarGroup){
        return carBizCarGroupExMapper.queryForObject(carBizCarGroup);
    }
}
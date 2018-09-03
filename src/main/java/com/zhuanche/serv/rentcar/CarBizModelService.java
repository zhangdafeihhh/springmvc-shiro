package com.zhuanche.serv.rentcar;

import com.zhuanche.entity.rentcar.CarBizModel;
import mapper.rentcar.CarBizModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author wzq
 */
@Service
public class CarBizModelService {
	@Autowired
	private CarBizModelMapper carBizModelMapper;


    public CarBizModel selectByPrimaryKey(Integer modelId){
	    return carBizModelMapper.selectByPrimaryKey(modelId);
    }
}
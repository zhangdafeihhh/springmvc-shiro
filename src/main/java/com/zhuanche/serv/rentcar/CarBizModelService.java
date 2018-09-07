package com.zhuanche.serv.rentcar;

import com.zhuanche.entity.rentcar.CarBizModel;
import mapper.rentcar.CarBizModelMapper;
import mapper.rentcar.ex.CarBizModelExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author wzq
 */
@Service
public class CarBizModelService {
	@Autowired
	private CarBizModelMapper carBizModelMapper;

    @Autowired
    private CarBizModelExMapper carBizModelExMapper;


    public CarBizModel selectByPrimaryKey(Integer modelId){
	    return carBizModelMapper.selectByPrimaryKey(modelId);
    }

    /**
     * 查询有效车型
     * @return
     */
    public List<CarBizModel> queryCarBizModelNoPage(){
        return carBizModelExMapper.queryCarBizModelNoPage();
    }
}
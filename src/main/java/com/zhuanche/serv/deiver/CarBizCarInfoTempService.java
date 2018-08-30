package com.zhuanche.serv.deiver;

import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import mapper.mdbcarmanage.ex.CarBizCarInfoTempExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class CarBizCarInfoTempService {
    @Autowired
    private CarBizCarInfoTempExMapper carBizCarInfoTempExMapper;

    /**
     * 分页查询
     * @param params
     * @return
     */
    public List<CarBizCarInfoTemp> queryForPageObject(Map<String,Object> params){
        return carBizCarInfoTempExMapper.queryForPageObject(params);
    }
}
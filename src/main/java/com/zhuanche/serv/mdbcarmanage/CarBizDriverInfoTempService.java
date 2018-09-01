package com.zhuanche.serv.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarBizDriverInfoTemp;
import mapper.mdbcarmanage.ex.CarBizDriverInfoTempExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author wzq
 */
@Service
public class CarBizDriverInfoTempService {
	@Autowired
	private CarBizDriverInfoTempExMapper carBizDriverInfoTempExMapper;


    /**
     * 根据车牌号查询
     * @param licensePlates
     * @return
     */
    public CarBizDriverInfoTemp getDriverByLincesePlates(String licensePlates){
        return carBizDriverInfoTempExMapper.getDriverByLincesePlates(licensePlates);
    }

    /**
     * 更新
     * @param driverVoEntity
     * @return
     */
    public int update(CarBizDriverInfoTemp driverVoEntity){
        return carBizDriverInfoTempExMapper.update(driverVoEntity);
    }

    /**
     * 根据条件分页查询
     * @param driverVoEntity
     * @return
     */
    public List<CarBizDriverInfoTemp> queryForPageObject(CarBizDriverInfoTemp driverVoEntity){
        return carBizDriverInfoTempExMapper.queryForPageObject(driverVoEntity);
    }
}
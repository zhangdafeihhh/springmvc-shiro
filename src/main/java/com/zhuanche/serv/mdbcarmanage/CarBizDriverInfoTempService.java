package com.zhuanche.serv.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverVoEntity;
import com.zhuanche.entity.rentcar.CarBizModel;
import mapper.mdbcarmanage.ex.CarBizDriverInfoTempExMapper;
import mapper.rentcar.CarBizModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
    public DriverVoEntity getDriverByLincesePlates(String licensePlates){
        return carBizDriverInfoTempExMapper.getDriverByLincesePlates(licensePlates);
    }

    /**
     * 更新
     * @param driverVoEntity
     * @return
     */
    public int update(DriverVoEntity driverVoEntity){
        return carBizDriverInfoTempExMapper.update(driverVoEntity);
    }
}
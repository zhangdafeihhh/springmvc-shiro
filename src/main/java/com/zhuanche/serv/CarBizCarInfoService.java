package com.zhuanche.serv;

import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CarBizCarInfoService {


    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

    /**
     * 根据车牌号查询车型信息
     * @param licensePlates
     * @return
     */
    public CarBizCarInfoDTO selectModelByLicensePlates(String licensePlates){
        return carBizCarInfoExMapper.selectModelByLicensePlates(licensePlates);
    }

    /**
     * 查询未绑定车牌号
     * @param map
     * @return
     */
     public List<CarBizCarInfoDTO> licensePlatesNotDriverIdList(Map<String, Object> map){
         return carBizCarInfoExMapper.licensePlatesNotDriverIdList(map);
     }

    /**
     * 更新 车辆信息
     * @param licensePlates
     * @return
     */
    public int updateCarLicensePlates(String licensePlates, Integer driverId){
        return carBizCarInfoExMapper.updateCarLicensePlates(licensePlates, driverId);
    }

    /**
     * 查询车牌号是否存在
     * @param licensePlates
     * @return
     */
    public Boolean checkLicensePlates(String licensePlates){
        int count = carBizCarInfoExMapper.checkLicensePlates(licensePlates);
        if(count>0){
            return true;
        }
        return false;
    }

    /**
     * 查询城市，供应商，车牌号是否一致
     * @param licensePlates
     * @return
     */
    public Boolean validateCityAndSupplier(Integer cityId, Integer supplierId, String licensePlates){
        int count = carBizCarInfoExMapper.validateCityAndSupplier(cityId, supplierId, licensePlates);
        if(count>0){
            return true;
        }
        return false;
    }

}

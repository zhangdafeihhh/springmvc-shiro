package com.zhuanche.serv;

import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import org.apache.ibatis.annotations.Param;
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

}

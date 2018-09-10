package com.zhuanche.serv;

import com.zhuanche.dto.rentcar.CarBizDriverInfoDetailDTO;
import com.zhuanche.entity.rentcar.CarBizDriverInfoDetail;
import mapper.rentcar.CarBizDriverInfoDetailMapper;
import mapper.rentcar.ex.CarBizDriverInfoDetailExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarBizDriverInfoDetailService {

    @Autowired
    private CarBizDriverInfoDetailMapper carBizDriverInfoDetailMapper;

    @Autowired
    private CarBizDriverInfoDetailExMapper carBizDriverInfoDetailExMapper;

    /**
     * 检查银行卡号是否存在
     * @param bankCardNumber
     * @param driverId
     * @return
     */
    public Boolean checkBankCardBank(String bankCardNumber, Integer driverId){
        int count = carBizDriverInfoDetailExMapper.checkBankCardBank(bankCardNumber, driverId);
        if(count>0){
            return true;
        }
        return false;
    }

    /**
     * 保存司机扩展表信息
     * @param params
     * @return
     */
    public int insertSelective(CarBizDriverInfoDetail params){
        return carBizDriverInfoDetailMapper.insertSelective(params);
    }

    /**
     * 保存司机扩展表信息
     * @param params
     * @return
     */
    public int updateByPrimaryKeySelective(CarBizDriverInfoDetail params){
        return carBizDriverInfoDetailMapper.updateByPrimaryKeySelective(params);
    }

    /**
     * 根据主键ID查询司机扩展表
     * @param id
     * @return
     */
    public CarBizDriverInfoDetail selectByPrimaryKey(Integer id){
        return carBizDriverInfoDetailMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据司机ID查询司机扩展表
     * @param driverId
     * @return
     */
    public CarBizDriverInfoDetailDTO selectByDriverId(Integer driverId){
        return carBizDriverInfoDetailExMapper.selectByDriverId(driverId);
    }

}

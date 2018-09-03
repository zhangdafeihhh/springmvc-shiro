package com.zhuanche.serv;

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
     * @param bankCardBank
     * @param driverId
     * @return
     */
    public Boolean checkBankCardBank(String bankCardBank, Integer driverId){
        int count = carBizDriverInfoDetailExMapper.checkBankCardBank(bankCardBank, driverId);
        if(count>0){
            return false;
        }
        return true;
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
     * 根据司机ID查询司机扩展表
     * @param driverId
     * @return
     */
    public CarBizDriverInfoDetail selectByPrimaryKey(Integer driverId){
        return carBizDriverInfoDetailMapper.selectByPrimaryKey(driverId);
    }

}

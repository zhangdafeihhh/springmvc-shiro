package com.zhuanche.serv;

import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import mapper.rentcar.ex.CarBizCustomerAppraisalExMapper;
import mapper.rentcar.ex.CarBizCustomerAppraisalStatisticsExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerAppraisalService {

    @Autowired
    private CarBizCustomerAppraisalExMapper carBizCustomerAppraisalExMapper;

    @Autowired
    private CarBizCustomerAppraisalStatisticsExMapper carBizCustomerAppraisalStatisticsExMapper;

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    public List<CarBizCustomerAppraisalDTO> queryCustomerAppraisalList(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {
        return carBizCustomerAppraisalExMapper.queryCustomerAppraisalList(carBizCustomerAppraisalDTO);
    }

    /**
     * 查询司机评分
     * @param carBizCustomerAppraisalStatisticsDTO
     * @return
     */
    public List<CarBizCustomerAppraisalStatisticsDTO> queryCustomerAppraisalStatisticsList(CarBizCustomerAppraisalStatisticsDTO carBizCustomerAppraisalStatisticsDTO) {
        return carBizCustomerAppraisalStatisticsExMapper.queryCustomerAppraisalStatisticsList(carBizCustomerAppraisalStatisticsDTO);
    }

    /**
     * 查询订单评分信息
     * @param carBizCustomerAppraisalDTO
     * @return
     */
    public List<CarBizCustomerAppraisalDTO> queryDriverAppraisalDetail(CarBizCustomerAppraisalDTO carBizCustomerAppraisalDTO) {
        return carBizCustomerAppraisalExMapper.queryDriverAppraisalDetail(carBizCustomerAppraisalDTO);
    }

}

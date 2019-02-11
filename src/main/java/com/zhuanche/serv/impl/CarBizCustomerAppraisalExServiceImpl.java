package com.zhuanche.serv.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;
import com.zhuanche.serv.CarBizCustomerAppraisalExService;
import mapper.rentcar.ex.CarBizCustomerAppraisalExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarBizCustomerAppraisalExServiceImpl implements CarBizCustomerAppraisalExService {

    private static final Logger logger = LoggerFactory.getLogger(CarBizCustomerAppraisalExServiceImpl.class);

    @Autowired
    private CarBizCustomerAppraisalExMapper carBizCustomerAppraisalExMapper;

    @Override
    public PageInfo<CarBizCustomerAppraisal> findPageByparam(CarBizCustomerAppraisalParams params) {
        logger.info("查询订单评分，参数为："+(params==null?"null": JSON.toJSONString(params)));
        PageHelper.startPage(params.getPage(), params.getPageSize(), true);
        List<CarBizCustomerAppraisal> list  = carBizCustomerAppraisalExMapper.queryForListObject(params);
        PageInfo<CarBizCustomerAppraisal> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}

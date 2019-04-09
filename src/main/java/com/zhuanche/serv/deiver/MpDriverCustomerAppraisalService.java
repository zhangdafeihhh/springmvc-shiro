package com.zhuanche.serv.deiver;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalDTO;
import com.zhuanche.dto.rentcar.CarBizCustomerAppraisalStatisticsDTO;
import com.zhuanche.entity.driver.MpCarBizCustomerAppraisal;
import com.zhuanche.entity.driver.MpCustomerAppraisalParams;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;
import com.zhuanche.serv.impl.CarBizCustomerAppraisalExServiceImpl;
import mapper.driver.ex.CustomerAppraisalExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MpDriverCustomerAppraisalService {

    private static final Logger logger = LoggerFactory.getLogger(MpDriverCustomerAppraisalService.class);

    @Autowired
    CustomerAppraisalExMapper customerAppraisalExMapper;

    public PageInfo<MpCarBizCustomerAppraisal> findPageByparam(MpCustomerAppraisalParams params) {
        logger.info("查询订单评分，参数为："+(params==null?"null": JSON.toJSONString(params)));
        PageHelper.startPage(params.getPage(), params.getPageSize(), true);
        String createDateBegin = params.getCreateDateBegin();
        String createDateEnd = params.getCreateDateEnd();
        String orderFinishTimeBegin = params.getOrderFinishTimeBegin();
        String orderFinishTimeEnd = params.getOrderFinishTimeEnd();
        if (StringUtils.isNotEmpty(createDateBegin)){
            params.setCreateDateBegin(createDateBegin + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(createDateEnd)){
            params.setCreateDateEnd(createDateEnd + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(orderFinishTimeBegin)){
            params.setOrderFinishTimeBegin(orderFinishTimeBegin + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(orderFinishTimeEnd)){
            params.setOrderFinishTimeEnd(orderFinishTimeEnd + " 23:59:59");
        }
        List<MpCarBizCustomerAppraisal> list  = customerAppraisalExMapper.queryForListObject(params);
        PageInfo<MpCarBizCustomerAppraisal> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

}

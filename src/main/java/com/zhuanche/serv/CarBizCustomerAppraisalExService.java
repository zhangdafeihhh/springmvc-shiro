package com.zhuanche.serv;

import com.github.pagehelper.PageInfo;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalParams;

public interface CarBizCustomerAppraisalExService {

    public PageInfo<CarBizCustomerAppraisal> findPageByparam(CarBizCustomerAppraisalParams params);

}

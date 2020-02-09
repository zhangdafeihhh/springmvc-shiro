package com.zhuanche.serv.rentcar;

import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;

import java.util.List;

public interface IDriverService <DriverEntity> {

    PageInfo<DriverVoEntity> findPageDriver(DriverVoEntity params);

    List<Integer> queryDriverIdsByName(String name);

    List<Integer> queryDriverIdsByPhone(String phone);
}

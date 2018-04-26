package com.zhuanche.service.driver;

import com.zhuanche.entity.driver.DriverBigdataDistrict;

import java.util.List;

public interface DriverBigdataDistrictService {

    /**
     * 根据城市ID，和时间，查询大数据商圈
     * @param cityId
     * @return
     */
    List<DriverBigdataDistrict> findListByCityId(Integer cityId);

}

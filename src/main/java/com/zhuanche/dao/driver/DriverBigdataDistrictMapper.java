package com.zhuanche.dao.driver;

import com.zhuanche.entity.driver.DriverBigdataDistrict;

import java.util.List;

public interface DriverBigdataDistrictMapper {

    List<DriverBigdataDistrict> findListByCityId(DriverBigdataDistrict params);
}
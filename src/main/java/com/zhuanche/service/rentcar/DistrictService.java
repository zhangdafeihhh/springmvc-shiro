package com.zhuanche.service.rentcar;

import com.zhuanche.entity.rentcar.District;

import java.util.List;

public interface DistrictService {

    /**
     * 根据城市ID，查询默认商圈
     * @param cityId
     * @return
     */
    List<District> findListByCityId(Integer cityId);
}

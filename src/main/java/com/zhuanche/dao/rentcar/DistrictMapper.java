package com.zhuanche.dao.rentcar;

import com.zhuanche.entity.rentcar.District;

import java.util.List;

public interface DistrictMapper {

    List<District> findListByCityId(Integer cityId);
}
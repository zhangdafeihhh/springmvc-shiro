package com.zhuanche.service.driver;

import com.zhuanche.entity.driver.DriverCityWhite;

import java.util.List;

public interface DriverCityWhiteService {

    /**
     * 查询所有有效的白名单城市
     * @return
     */
    List<DriverCityWhite> findList();

}

package com.zhuanche.service.impl.driver;

import com.zhuanche.dao.driver.DriverCityWhiteMapper;
import com.zhuanche.entity.driver.DriverCityWhite;
import com.zhuanche.service.driver.DriverCityWhiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("driverCityWhiteService")
public class DriverCityWhiteServiceImpl implements DriverCityWhiteService {

    @Autowired
    private DriverCityWhiteMapper driverCityWhiteMapper;

    @Override
    public List<DriverCityWhite> findList() {
        return driverCityWhiteMapper.findList();
    }
}

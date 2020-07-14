package com.zhuanche.serv.intercity.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.mdbcarmanage.InterDriverTeamRelDto;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.serv.intercity.DriverInfoInterCityService;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/14 上午10:27
 * @Version 1.0
 */
@Service
public class DriverInfoInterCityServiceImpl implements DriverInfoInterCityService{

    @Autowired
    private DriverInfoInterCityExMapper exMapper;

    @Override
    public PageDTO queryDriverRelTeam(Integer pageSize,
                                      Integer pageNo,
                                      DriverInfoInterCity driverInfoInterCity,
                                      Integer teamId) {
        Page page = PageHelper.startPage(pageNo, pageSize, true);

        List<InterDriverTeamRelDto> dtoList;
        int total = 0;
        try {
            dtoList = exMapper.queryDriverRelTeam(driverInfoInterCity.getCityId(), driverInfoInterCity.getSupplierId(),
                    driverInfoInterCity.getDriverName(), driverInfoInterCity.getDriverPhone(),
                    driverInfoInterCity.getLicensePlates(), teamId);
        } finally {
            total = (int) page.getTotal();
            PageHelper.clearPage();

        }

        PageDTO pageDTO = new PageDTO(pageNo, pageSize, total, dtoList);

        return pageDTO;
    }
}

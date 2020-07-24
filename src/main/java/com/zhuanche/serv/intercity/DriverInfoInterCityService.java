package com.zhuanche.serv.intercity;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.mdbcarmanage.InterDriverTeamRelDto;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/14 上午10:26
 * @Version 1.0
 */
public interface DriverInfoInterCityService {
    PageDTO queryDriverRelTeam(Integer pageSize,
                               Integer pageNum,
                               DriverInfoInterCity driverInfoInterCity,
                               Integer teamId);

    AjaxResponse queryDriverByTeam(Integer teamId);

    AjaxResponse queryDriverByParam(String queryParam);
}


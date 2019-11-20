package com.zhuanche.serv.transportMonitor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.transportMonitor.IndexMonitorDriverStatisticsDto;

import java.util.Set;

public interface DriverMonitoringService {
    IndexMonitorDriverStatisticsDto queryIndexMonitorDriverStatistics(Integer cityId, Set<Integer> supplierIds, Set<Integer> teamIds);


    JSONObject getBizdistrict(Integer cityId);

    JSONObject getHotspotDistrict(Integer cityId);



    JSONArray trajectory(Integer cityId, String supplierIds, String carTeamIds);

    JSONObject driverInfo(Integer driverId);

    JSONArray efficiency(
            Integer cityId,
            String supplierIds,
            String carTeamIds
    );

    JSONArray abnormity(
            Integer cityId,
            String supplierIds,
            String carTeamIds,
            Integer freeTime,
            Integer finishedOrder,
            Integer finishedAmount
    );
}

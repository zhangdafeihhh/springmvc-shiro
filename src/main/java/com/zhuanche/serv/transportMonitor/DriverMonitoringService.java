package com.zhuanche.serv.transportMonitor;

import com.zhuanche.dto.transportMonitor.IndexMonitorDriverStatisticsDto;

import java.util.Set;

public interface DriverMonitoringService {
    IndexMonitorDriverStatisticsDto queryIndexMonitorDriverStatistics(Integer cityId, Set<Integer> supplierIds, Set<Integer> teamIds);
}

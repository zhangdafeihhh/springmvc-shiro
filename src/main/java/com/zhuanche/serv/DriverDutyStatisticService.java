package com.zhuanche.serv;

import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.rentcar.DriverDutyStatisticDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatistic;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatisticParams;

import java.util.List;

public interface DriverDutyStatisticService {

    /**
     * 考勤报告-日报
     */
    public PageInfo<DriverDutyStatistic> queryDriverDayDutyList(DriverDutyStatisticParams params);


    /**
     * 考勤报告-月报
     */
    public PageInfo<DriverDutyStatistic> queryDriverMonthDutyList(DriverDutyStatisticParams params);


    public  List<DriverDutyStatisticDTO> selectSuppierNameAndCityName(List<DriverDutyStatistic> rows);
}

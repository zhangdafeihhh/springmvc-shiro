package com.zhuanche.serv;

import com.github.pagehelper.PageInfo;
import com.zhuanche.dto.DriverDailyReportDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;

import java.text.ParseException;
import java.util.List;

public interface DriverDailyReportExService {

    /**
     * 查询日报
     * @param params
     * @return
     */
    public PageInfo<DriverDailyReport> findDayDriverDailyReportByparam(DriverDailyReportParams params);

    /**
     * 查询周报
     * @param params
     * @return
     */
    public PageInfo<DriverDailyReport> findWeekDriverDailyReportByparam(DriverDailyReportParams params,String statDateStart,  String statDateEnd );


    public List<DriverDailyReportDTO> selectSuppierNameAndCityNameDays(List<DriverDailyReport> rows,
                                                                       Integer reportType,
                                                                       String statDateStart,
                                                                       String statDateEnd) throws ParseException ;
}

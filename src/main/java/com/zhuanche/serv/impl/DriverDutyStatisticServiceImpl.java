package com.zhuanche.serv.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.rentcar.DriverDutyStatisticDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatistic;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatisticParams;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.serv.DriverDutyStatisticService;
import com.zhuanche.util.BeanUtil;
import mapper.mdbcarmanage.ex.DriverDutyStatisticExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DriverDutyStatisticServiceImpl implements DriverDutyStatisticService {
    private static final Logger logger = LoggerFactory.getLogger(DriverDutyStatisticServiceImpl.class);


    @Autowired
    private DriverDutyStatisticExMapper driverDutyStatisticExMapper;

    @Autowired
    private CarBizCityExMapper carBizCityExMapper;

    @Override
    public PageInfo<DriverDutyStatistic> queryDriverDayDutyList(DriverDutyStatisticParams params) {
        logger.info("查询考勤报告，日报，参数为："+(params==null?"null": JSON.toJSONString(params)));
        PageHelper.startPage(params.getPage(), params.getPageSize(), true);
        List<DriverDutyStatistic> list  = driverDutyStatisticExMapper.queryForListObject(params);
        PageInfo<DriverDutyStatistic> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public PageInfo<DriverDutyStatistic> queryDriverMonthDutyList(DriverDutyStatisticParams params) {
        logger.info("查询考勤报告，月报，参数为："+(params==null?"null": JSON.toJSONString(params)));
        PageHelper.startPage(params.getPage(), params.getPageSize(), true);
        List<DriverDutyStatistic> list  = driverDutyStatisticExMapper.queryDriverMonthDutyList(params);
        PageInfo<DriverDutyStatistic> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * <p>Title: selectSuppierNameAndCityNameDays</p>
     * <p>Description: 转换</p>
     * @param rows
     * @return
     * return: List<DriverDailyReportDTO>
     */
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<DriverDutyStatisticDTO> selectSuppierNameAndCityName(List<DriverDutyStatistic> rows){
        List<DriverDutyStatisticDTO> list = null;
        //不为空进行转换并查询城市名称和供应商名称
        if(rows!=null&&rows.size()>0){
            Set<Integer> s = new HashSet();
            for(DriverDutyStatistic driverDutyStatistic : rows){
                s.add(driverDutyStatistic.getCityid());
            }
            //查询供应商名称，一次查询出来避免多次读库
            List<CarBizCity> cityList = carBizCityExMapper.queryNameByIds(s);
            Map<String,CarBizCity> cacheCity = new HashMap();
            if(cityList != null){
                for(CarBizCity city :cityList){
                    cacheCity.put("c_"+city.getCityId(),city);
                }
            }
            CarBizCity temp = null;
            for (DriverDutyStatistic entity: rows) {
                temp = cacheCity.get("c_"+entity.getCityid());
                if(temp != null){
                    entity.setCityName(temp.getCityName());
                }
            }
            list = BeanUtil.copyList(rows, DriverDutyStatisticDTO.class);
        }
        return list;
    }
}

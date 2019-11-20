package com.zhuanche.controller.transportMonitor;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.transportMonitor.IndexMonitorDriverStatisticsDto;
import com.zhuanche.serv.transportMonitor.DriverMonitoringService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * 运力监控
 */
@RestController
@RequestMapping(value = "/api/driverMonitoring")
public class DriverMonitoringController {
    @Autowired
    private DriverMonitoringService driverMonitoringService;
    /**查询商圈
     * @param cityId
     * @return
     */
    @RequestMapping(value = "/area")
    public AjaxResponse area(@Verify(param = "cityId", rule = "required|min(1)") Integer cityId){
        return null;
    }

    /**
     * 查询蜂巢
     * @param cityId
     * @return
     */
    @RequestMapping(value = "/beehive")
    public AjaxResponse beehive(@Verify(param = "cityId", rule = "required|min(1)") Integer cityId){
        return null;
    }

    /**司机实时位置
     * @param cityId
     * @param driverStatus
     * @param carType
     * @param driverType
     * @param supplierId
     * @param carTeam
     * @return
     */
    @RequestMapping(value = "/trajectory")
    public AjaxResponse trajectory(
            @Verify(param = "cityId", rule = "required|min(1)") Integer cityId,
            Integer driverStatus,
            Integer carType,
            Integer driverType,
            Integer supplierId,
            Integer carTeam
    ){
        return null;
    }

    /**司机信息
     * @param driverId
     * @return
     */
    @RequestMapping(value = "/driverInfo")
    public AjaxResponse driverInfo(@Verify(param = "driverId", rule = "required|min(1)") Integer driverId){
        return null;
    }

    /**低效司机列表
     * @param cityId
     * @param supplierId
     * @param teamId
     * @return
     */
    @RequestMapping(value = "/efficiency")
    public AjaxResponse efficiency(
            @Verify(param = "cityId", rule = "required|min(1)") Integer cityId,
            Integer supplierId,
            Integer teamId
    ){
        return null;
    }

    /**空闲司机列表
     * @param cityId
     * @param supplierId
     * @param teamId
     * @param freeTime
     * @param finishedOrder
     * @param finishedAmount
     * @return
     */
    @RequestMapping(value = "/abnormity")
    public AjaxResponse abnormity(
            @Verify(param = "cityId", rule = "required|min(1)") Integer cityId,
            Integer supplierId,
            Integer teamId,
            Integer freeTime,
            Integer finishedOrder,
            Integer finishedAmount
    ){
        return null;
    }


    /**
     * @param cityId
     * @param supplierId
     * @param teamId
     * @return
     */
    @RequestMapping(value = "/transportStatistics")
    public AjaxResponse transportStatistics(
            @Verify(param = "cityId", rule = "required|min(1)") Integer cityId,
            Integer supplierId,
            Integer teamId
            //String currentTime
    ){
        Set<Integer> supplierIds = getSupplierIds(supplierId);
        Set<Integer> teamIds = getTeamIds(teamId);
        IndexMonitorDriverStatisticsDto indexMonitorDriverStatisticsDto=driverMonitoringService.queryIndexMonitorDriverStatistics(cityId,supplierIds,teamIds);
        return AjaxResponse.success(indexMonitorDriverStatisticsDto);
    }

    /**
     * 获取可以查看供应商集合
     * @param supplierId
     * @return
     */
    public Set<Integer> getSupplierIds(Integer supplierId){
        String supplierIds="";
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();

        Set <Integer> supplierIdSet=new HashSet <>();

        if(supplierId != null){
            supplierIdSet.add(supplierId);
        }else{
            supplierIdSet = user.getSupplierIds();
        }

       /* if(supplierIdSet.size()>0){
            supplierIds = StringUtils.join(supplierIdSet, ",");
        }*/
        return supplierIdSet;
    }

    /**
     * 获取可以查询车队集合
     * @param teamId
     * @return
     */
    public Set<Integer> getTeamIds(Integer teamId){
        String teamIds="";
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Set <Integer> teamIdSet=new HashSet <>();

        if(teamId != null){
            teamIdSet.add(teamId);
        }else{
            teamIdSet = user.getTeamIds();
        }
        /*if(teamIdSet.size()>0){
            teamIds = StringUtils.join(teamIdSet, ",");
        }*/
        return teamIdSet;
    }
}

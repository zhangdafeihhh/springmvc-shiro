package com.zhuanche.controller.transportMonitor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.le.config.dict.Dicts;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.transportMonitor.IndexMonitorDriverStatisticsDto;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.transportMonitor.DriverMonitoringService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 运力监控
 */
@RestController
@RequestMapping(value = "/api/driverMonitoring")
public class DriverMonitoringController {
    private Logger logger = LoggerFactory.getLogger(DriverMonitoringController.class);
    @Autowired
    private DriverMonitoringService driverMonitoringService;

    @Autowired
    private RedisTemplate <String, Serializable > redisTemplate;
    /**查询商圈
     * @param cityId
     * @return
     */
    @RequestMapping(value = "/getBizdistrict")
    public AjaxResponse getBizdistrict(@Verify(param = "cityId", rule = "required|min(1)") Integer cityId){
        JSONObject data = driverMonitoringService.getBizdistrict(cityId);
        if(null != data){
            return AjaxResponse.success(data);
        }
        return AjaxResponse.fail(-1,"查询失败");
    }

    /**
     * 查询蜂巢
     * @param cityId
     * @return
     */
    @RequestMapping(value = "/getHotspotDistrict")
    public AjaxResponse getHotspotDistrict(@Verify(param = "cityId", rule = "required|min(1)") Integer cityId){
        JSONObject data = driverMonitoringService.getHotspotDistrict(cityId);
        if(null != data){
            return AjaxResponse.success(data);
        }
        return AjaxResponse.fail(-1,"查询失败");
    }

    /**司机实时位置
     * @param cityId
     * @param supplierId
     * @param teamId
     * @return
     */
    @RequestMapping(value = "/trajectory")
    public AjaxResponse trajectory(
            @Verify(param = "cityId", rule = "required|min(1)") Integer cityId,
            Integer supplierId,
            Integer teamId,
            Integer carType,
            Integer driverStatus,
            String licensePlates
    ){
        String supplierIds = getSupplierIdsStr(supplierId);
        String carTteamIds = getTeamIdsStr(teamId);
        JSONArray data = driverMonitoringService.trajectory(cityId, supplierIds, carTteamIds, carType, driverStatus, licensePlates);
        if(null != data){
            return AjaxResponse.success(data);
        }
        return AjaxResponse.fail(-1,"查询失败");
    }

    /**司机信息
     * @param driverId
     * @return
     */
    @RequestMapping(value = "/driverInfo")
    public AjaxResponse driverInfo(@Verify(param = "driverId", rule = "required|min(1)") Integer driverId){
        JSONObject data = driverMonitoringService.driverInfo(driverId);
        if(null != data){
            return AjaxResponse.success(data);
        }
        return AjaxResponse.fail(-1,"查询失败");
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
        String supplierIds = getSupplierIdsStr(supplierId);
        String carTteamIds = getTeamIdsStr(teamId);
        JSONArray data = driverMonitoringService.efficiency(cityId, supplierIds, carTteamIds);
        if(null != data){
            return AjaxResponse.success(data);
        }
        return AjaxResponse.fail(-1,"查询失败");
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
        String supplierIds = getSupplierIdsStr(supplierId);
        String carTteamIds = getTeamIdsStr(teamId);
        JSONArray data = driverMonitoringService.abnormity(cityId, supplierIds, carTteamIds, freeTime, finishedOrder, finishedAmount);
        if(null != data){
            return AjaxResponse.success(data);
        }
        return AjaxResponse.fail(-1,"查询失败");
    }


    /**司机运力统计数据查询
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
        logger.info("--查询司机运力数据入参--cityId--{}--,--supplierId--{}--,--teamId--{}--",cityId,supplierId,teamId);
        String supplierIds = getSupplierIdsStr(supplierId);
        String teamIds = getTeamIdsStr(teamId);
        //IndexMonitorDriverStatisticsDto indexMonitorDriverStatisticsDto=driverMonitoringService.queryIndexMonitorDriverStatistics(cityId,supplierIds,teamIds);
        AjaxResponse ajaxResponse=driverMonitoringService.getTransportStatics(cityId,supplierIds,teamIds);
        return ajaxResponse;
    }

    /**圈外空闲司机消息提醒
     * @param cityId
     * @param supplierId
     * @param teamId
     * @return
     */
    @RequestMapping(value = "/outsideDriverSendMsg")
    public AjaxResponse outsideDriverSendMsg(
            @Verify(param = "cityId", rule = "required|min(1)") Integer cityId,
            Integer supplierId,
            Integer teamId
    ){
        logger.info("--给圈外空闲司机发送消息入参--cityId--{}--,--supplierId--{}--,--teamId--{}--",cityId,supplierId,teamId);

        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Serializable redisValue = redisTemplate.opsForValue().get("sendMsg_key_" + user.getLoginName());
        if(redisValue!=null){
            return AjaxResponse.fail(RestErrorCode.SEND_MSG_LOCK);
        }

        String redis_sendMsg_count = "sendMsg_count_"+user.getLoginName();
        long score = System.currentTimeMillis();
        //zset内部是按分数来排序的，这里用当前时间做分数
        redisTemplate.opsForZSet().add(redis_sendMsg_count, String.valueOf(score), score);
        //统计60分钟内发送消息次数
        int statistics = 60;
        redisTemplate.expire(redis_sendMsg_count, statistics, TimeUnit.MINUTES);

        //统计用户60分钟内发送消息次数
        long max = score;
        long min = max - (statistics * 60 * 1000);
        long count = redisTemplate.opsForZSet().count(redis_sendMsg_count, min, max);

        int countLimit = 3;
        logger.info("用户"+user.getLoginName()+"在"+statistics+"分钟内第"+count+"次进行发送消息操作");
        if(count  > countLimit) {
            logger.info("用户"+user.getLoginName()+"在"+statistics+"次进行发送消息操作"+count+"次,超过限制"+countLimit+",需要等待"+statistics+"分钟");
            return AjaxResponse.fail(RestErrorCode.SEND_MSG_COUNT,statistics);
        }
        String supplierIds = getSupplierIdsStr(supplierId);
        String teamIds = getTeamIdsStr(teamId);
        boolean b=driverMonitoringService.sendPushMsg(cityId,supplierIds,teamIds);
        if(b){
            redisTemplate.opsForValue().set("sendMsg_key_" + user.getLoginName(), user.getLoginName(), 60 * 10, TimeUnit.SECONDS);
        }
        return AjaxResponse.success(b);
    }


    /**
     * @return
     */
    @RequestMapping(value = "/auth")
    public AjaxResponse auth(){
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Set<Integer> userCityIds = user.getCityIds();
        if(userCityIds.isEmpty()){
            return AjaxResponse.success(true);
        }
        Set<String> authCityIdSet = getAuthCityId();
        for (String cityId : authCityIdSet) {
           if(userCityIds.contains(Integer.valueOf(cityId))){
               userCityIds.remove(Integer.valueOf(cityId));
           }
        }
        if(userCityIds.isEmpty()){
            return AjaxResponse.success(false);
        }
        return AjaxResponse.success(true);
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
        return teamIdSet;
    }


    /**
     * 获取可以查看供应商集合
     * @param supplierId
     * @return
     */
    public String getSupplierIdsStr(Integer supplierId){
        String supplierIds="";
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();

        Set <Integer> supplierIdSet=new HashSet <>();

        if(supplierId != null){
            supplierIdSet.add(supplierId);
        }else{
            supplierIdSet = user.getSupplierIds();
        }

        if(supplierIdSet!=null && supplierIdSet.size()>0){
            supplierIds = StringUtils.join(supplierIdSet, ",");
        }
        return supplierIds;
    }

    /**
     * 获取可以查询车队集合
     * @param teamId
     * @return
     */
    public String getTeamIdsStr(Integer teamId){
        String teamIds="";
        SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
        Set <Integer> teamIdSet=new HashSet <>();

        if(teamId != null){
            teamIdSet.add(teamId);
        }else{
            teamIdSet = user.getTeamIds();
        }
        if(teamIdSet!=null && teamIdSet.size()>0){
            teamIds = StringUtils.join(teamIdSet, ",");
        }
        return teamIds;
    }


    public Set<String> getAuthCityId(){
        String authCityIdStr = Dicts.getString("driverMonitoring_authCityIdStr", "44,66,79,82,84,107,119,72,93,94,101,67,78,95,71,111,113,81,109,80,83");
        String[] strArray = authCityIdStr.split(",");
        List<String> strList =  java.util.Arrays.asList(strArray);
        Set<String> authCityIdSet = new HashSet<>(strList);
        return authCityIdSet;
    }
}

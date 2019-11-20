package com.zhuanche.serv.transportMonitor.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.transportMonitor.IndexMonitorDriverStatisticsDto;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.SaasIndexMonitorDriverStatistics;
import com.zhuanche.serv.transportMonitor.DriverMonitoringService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DriverMonitoringServiceImpl implements DriverMonitoringService {

    @Resource(name = "driverMongoTemplate")
    private MongoTemplate driverMongoTemplate;

    String  shangquanApiUrl="http://pre-inside-bigdata-athena.01zhuanche.com/api/inside/driverMonitoring/areaNew";

    String  fengchaoApiUrl="http://pre-inside-bigdata-athena.01zhuanche.com/api/inside/driverMonitoring/beehiveNew";

    String  trajectoryApiUrl="http://pre-inside-bigdata-athena.01zhuanche.com/api/inside/saasCenter/trajectory";

    String  driverInfoApiUrl="http://pre-inside-bigdata-athena.01zhuanche.com/api/inside/saasCenter/driverInfo";

    String  efficiencyApiUrl="http://pre-inside-bigdata-athena.01zhuanche.com/api/inside/saasCenter/efficiency";

    String  abnormityApiUrl="http://pre-inside-bigdata-athena.01zhuanche.com/api/inside/saasCenter/abnormity";


    /**
     * @param cityId
     * @param supplierIds
     * @param teamIds
     * @return
     */
    @Override
    public IndexMonitorDriverStatisticsDto queryIndexMonitorDriverStatistics(Integer cityId, Set <Integer> supplierIds, Set<Integer> teamIds) {
        Query query = new Query().limit(1);
        query.addCriteria(Criteria.where("cityId").is(cityId));
        if (supplierIds!=null && supplierIds.size()>0){
            query.addCriteria(Criteria.where("driverTeamId").in(supplierIds));
        }
        if (teamIds!=null && teamIds.size()>0){
            query.addCriteria(Criteria.where("supplierId").in(teamIds));
        }
        query.with(new Sort(new Order(Direction.DESC,"minTime")));

        query.skip(0);
        IndexMonitorDriverStatisticsDto dto =null;
        List<SaasIndexMonitorDriverStatistics> list=driverMongoTemplate.find(query, SaasIndexMonitorDriverStatistics.class);
        if (list!=null && list.size()>0){
            SaasIndexMonitorDriverStatistics saasIndexMonitorDriverStatistics = list.get(0);
            String minTime = saasIndexMonitorDriverStatistics.getMinTime();
            List <AggregationOperation> operations = new ArrayList<>();

            Criteria c = Criteria.where("cityId").is(cityId).and("minTime").is(minTime);
            if (supplierIds!=null && supplierIds.size()>0){
                c.and("supplierId").in(supplierIds);
            }
            if (teamIds!=null && teamIds.size()>0){
                c.and("driverTeamId").in(teamIds);
            }
            operations.add(Aggregation.match(c));
            operations.add(Aggregation.group("cityId")
                    .max("minTime").as("minTime")
                    .sum("inCycleTotalOnlineCnt").as("inCycleTotalOnlineCnt")
                    .sum("inCycleTotalServingCnt").as("inCycleTotalServingCnt")
                    .sum("inCycleTotalFreeCnt").as("inCycleTotalFreeCnt")
                    .sum("inCycleTotalOfflineCnt").as("inCycleTotalOfflineCnt")
                    .sum("outCycleTotalOnlineCnt").as("outCycleTotalOnlineCnt")
                    .sum("outCycleTotalServingCnt").as("outCycleTotalServingCnt")
                    .sum("outCycleTotalFreeCnt").as("outCycleTotalFreeCnt")
                    .sum("outCycleTotalOfflineCnt").as("outCycleTotalOfflineCnt")
                    .sum("totalOnlineCnt").as("totalOnlineCnt")
                    .sum("totalServingCnt").as("totalServingCnt")
                    .sum("totalFreeCnt").as("totalFreeCnt")
                    .sum("totalOfflineCnt").as("totalOfflineCnt")
            );
            operations.add(Aggregation.sort(Sort.Direction.DESC, "minTime"));
            Aggregation aggregation = Aggregation.newAggregation(operations);
            AggregationResults <IndexMonitorDriverStatisticsDto> g = driverMongoTemplate.aggregate(aggregation, SaasIndexMonitorDriverStatistics.class, IndexMonitorDriverStatisticsDto.class);
            List <IndexMonitorDriverStatisticsDto> listIndexMonitorDriverStatistics = g.getMappedResults();
            if (listIndexMonitorDriverStatistics!=null && listIndexMonitorDriverStatistics.size()>0){
                dto = listIndexMonitorDriverStatistics.get(0);
                /**上线司机 运力在圈率=*/
                dto.setOnLineInCycleRate(getRate(dto.getInCycleTotalOnlineCnt(),dto.getTotalOnlineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                /**服务中司机 运力在圈率*/
                dto.setServingInCycleRate(getRate(dto.getInCycleTotalServingCnt(),dto.getTotalServingCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                /**空闲司机 运力在圈率*/
                dto.setFreeInCycleRate(getRate(dto.getInCycleTotalFreeCnt(),dto.getTotalFreeCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                /**线下听单司机 运力在圈率*/
                dto.setOffLineInCycleRate(getRate(dto.getInCycleTotalOfflineCnt(),dto.getTotalOfflineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                /**全城运营率*/
                dto.setCityOperatingRate(getRate(dto.getInCycleTotalServingCnt(),dto.getTotalOnlineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                /**圈内运营率*/
                dto.setWithinCircleOperatingRate(getRate(dto.getTotalServingCnt(),dto.getInCycleTotalOnlineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
                /**圈外运营率*/
                dto.setOutSideCircleOperatingRate(getRate(dto.getOutCycleTotalServingCnt(),dto.getOutCycleTotalOnlineCnt()).setScale(3,BigDecimal.ROUND_DOWN));
            }
        }
        return dto;
    }

    @Override
    public JSONObject getBizdistrict(Integer cityId) {
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(shangquanApiUrl+"?cityId="+cityId,1,"");
        return result;
    }

    @Override
    public JSONObject getHotspotDistrict(Integer cityId) {
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(fengchaoApiUrl+"?cityId="+cityId,1,"");
        int status = result.getInteger("status");
        if(status==1){
            return result.getJSONObject("info");
        }
        return null;
    }

    @Override
    public JSONArray trajectory(Integer cityId, String supplierIds, String carTeamIds) {
//        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(trajectoryApiUrl+"?cityId="+cityId+"&supplierIds="+supplierIds+"&carTeamIds="+carTeamIds,1,"");
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(trajectoryApiUrl+"?cityId="+cityId,1,"");
        int status = result.getInteger("status");
        if(status==1){
            return result.getJSONArray("info");
        }
        return null;
    }

    @Override
    public JSONObject driverInfo(Integer driverId) {
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(driverInfoApiUrl+"?driverId="+driverId,1,"");
        int status = result.getInteger("status");
        if(status==1){
            return result.getJSONObject("info");
        }
        return null;
    }

    @Override
    public JSONArray efficiency(Integer cityId, String supplierIds, String carTeamIds) {
//        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(efficiencyApiUrl+"?cityId="+cityId+"&supplierIds="+supplierIds+"&carTeamIds="+carTeamIds,1,"");
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(efficiencyApiUrl+"?cityId="+cityId,1,"");
        if(null == result.get("status")){
            return result.getJSONArray("data");
        }
        return null;
    }

    @Override
    public JSONArray abnormity(Integer cityId, String supplierIds, String carTeamIds, Integer freeTime, Integer finishedOrder, Integer finishedAmount) {
//        String params = "?cityId="+cityId+"&supplierIds="+supplierIds+"&carTeamIds="+carTeamIds+"&freeTime="+freeTime+"&finishedOrder"+freeTime+"&finishedAmount";
        String params = "?cityId="+cityId;
        JSONObject result = MpOkHttpUtil.okHttpGetBackJson(abnormityApiUrl+params,1,"");
        if(null == result.get("status")){
            return result.getJSONArray("data");
        }
        return null;
    }

    /**
     * 计算除法
     * @param dividend
     * @param divisor
     * @return
     */
    public BigDecimal getRate(Integer dividend,Integer divisor){
        if (dividend==null || dividend==0 || divisor ==0 ||divisor==null){
            return BigDecimal.ZERO;
        }
        BigDecimal rate = new BigDecimal(dividend/divisor);
        return rate;
    }

}

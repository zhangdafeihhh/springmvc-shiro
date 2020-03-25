package com.zhuanche.serv.bigdata;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndex;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndexList;
import com.zhuanche.entity.bigdata.QueryTermDriverAnaly;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.bigdata.ex.BiDriverDisinfectMeasureDayExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BiDriverMeasureDayService {

    private static Logger logger = LoggerFactory.getLogger(BiDriverMeasureDayService.class);

    @Autowired
    private BiDriverDisinfectMeasureDayExMapper biDriverDisinfectMeasureDayExMapper;

    public List<DriverOperAnalyIndex> query(QueryTermDriverAnaly queryTerm) {
        return biDriverDisinfectMeasureDayExMapper.query(queryTerm);
    }

    public DriverOperAnalyIndexList trend(QueryTermDriverAnaly queryTerm) {

        DriverOperAnalyIndexList doai = new DriverOperAnalyIndexList();

        List<DriverOperAnalyIndex> driverOperAnalyIndexLists = biDriverDisinfectMeasureDayExMapper.trend(queryTerm);
        if(driverOperAnalyIndexLists!=null && driverOperAnalyIndexLists.size()>0) {
            List<Map> onlineDriverAmountList = Lists.newArrayList();
            List<Map> onlineRateList = Lists.newArrayList();
            List<Map> serviceDriverAmountList = Lists.newArrayList();
            List<Map> serviceRateList = Lists.newArrayList();
            List<Map> distributeOrderAmountList = Lists.newArrayList();
            List<Map> completeOrderAmountList = Lists.newArrayList();
            List<Map> bindOrderAmountPerVehicleList = Lists.newArrayList();
            List<Map> completeOrderPerVehicleList = Lists.newArrayList();
            List<Map> incomePerVehicleList = Lists.newArrayList();
            List<Map> pricePerOIrderList = Lists.newArrayList();
            List<Map> totalDriverNumList = Lists.newArrayList();
            List<Map> disinfectDriverCntList = Lists.newArrayList();
            List<Map> noDisinfectDriverCntList = Lists.newArrayList();
            List<Map> disinfectDriverCntRateList = Lists.newArrayList();
            for (DriverOperAnalyIndex driverOperAnalyIndex : driverOperAnalyIndexLists) {
                // 上线司机数
                Map<String, String> onlineDriverAmountMap = Maps.newHashMap();
                onlineDriverAmountMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getOnlineDriverAmount());
                // 上线率
                Map<String, String> onlineRateMap = Maps.newHashMap();
                onlineRateMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getOnlineRate());
                // 运营司机数
                Map<String, String> serviceDriverAmountMap = Maps.newHashMap();
                serviceDriverAmountMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getServiceDriverAmount());
                // 运营率
                Map<String, String> serviceRateMap = Maps.newHashMap();
                serviceRateMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getServiceRate());
                // 派单量
                Map<String, String> distributeOrderAmountMap = Maps.newHashMap();
                distributeOrderAmountMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getDistributeOrderAmount());
                // 完单率
                Map<String, String> completeOrderAmountMap = Maps.newHashMap();
                completeOrderAmountMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getCompleteOrderAmount());
                // 车均绑单量
                Map<String, String> bindOrderAmountPerVehicleMap = Maps.newHashMap();
                bindOrderAmountPerVehicleMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getBindOrderAmountPerVehicle());
                // 车均完单量
                Map<String, String> completeOrderPerVehicleMap = Maps.newHashMap();
                completeOrderPerVehicleMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getCompleteOrderPerVehicle());
                // 车均流水
                Map<String, String> incomePerVehicleMap = Maps.newHashMap();
                incomePerVehicleMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getIncomePerVehicle());
                // 单均价
                Map<String, String> pricePerOIrderMap = Maps.newHashMap();
                pricePerOIrderMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getPricePerOIrder());
                // 司机总数
                Map<String, String> totalDriverNumMap = Maps.newHashMap();
                totalDriverNumMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getTotalDriverNum());
                // 已消毒司机数
                Map<String, String> disinfectDriverCntMap = Maps.newHashMap();
                disinfectDriverCntMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getDisinfectDriverCnt());
                // 未消毒司机数
                Map<String, String> noDisinfectDriverCntMap = Maps.newHashMap();
                noDisinfectDriverCntMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getNoDisinfectDriverCnt());
                // 消毒率
                Map<String, String> disinfectDriverCntRateMap = Maps.newHashMap();
                disinfectDriverCntRateMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getDisinfectDriverCntRate());

                onlineDriverAmountList.add(onlineDriverAmountMap);
                onlineRateList.add(onlineRateMap);
                serviceDriverAmountList.add(serviceDriverAmountMap);
                serviceRateList.add(serviceRateMap);
                distributeOrderAmountList.add(distributeOrderAmountMap);
                completeOrderAmountList.add(completeOrderAmountMap);
                bindOrderAmountPerVehicleList.add(bindOrderAmountPerVehicleMap);
                completeOrderPerVehicleList.add(completeOrderPerVehicleMap);
                incomePerVehicleList.add(incomePerVehicleMap);
                pricePerOIrderList.add(pricePerOIrderMap);
                totalDriverNumList.add(totalDriverNumMap);
                disinfectDriverCntList.add(disinfectDriverCntMap);
                noDisinfectDriverCntList.add(noDisinfectDriverCntMap);
                disinfectDriverCntRateList.add(disinfectDriverCntRateMap);
            }
            doai.setOnlineDriverAmount(onlineDriverAmountList);
            doai.setOnlineRate(onlineRateList);
            doai.setServiceDriverAmount(serviceDriverAmountList);
            doai.setServiceRate(serviceRateList);
            doai.setDistributeOrderAmount(distributeOrderAmountList);
            doai.setCompleteOrderAmount(completeOrderAmountList);
            doai.setBindOrderAmountPerVehicle(bindOrderAmountPerVehicleList);
            doai.setCompleteOrderPerVehicle(completeOrderPerVehicleList);
            doai.setIncomePerVehicle(incomePerVehicleList);
            doai.setPricePerOIrder(pricePerOIrderList);
            doai.setTotalDriverNum(totalDriverNumList);
            doai.setDisinfectDriverCnt(disinfectDriverCntList);
            doai.setNoDisinfectDriverCnt(noDisinfectDriverCntList);
            doai.setDisinfectDriverCntRate(disinfectDriverCntRateList);
        }
        return  doai;
    }

    /**
     * 数据权限设置
     * @param cityId
     * @param supplier
     * @param teamId
     * @return
     */
    public Map<String, Object> getCurrentLoginUserParamMap(Long cityId, String supplier, String teamId){
        // 数据权限设置
        // 非超级管理员可以管理的所有城市ID
        Set<Integer> cityIdsForAuth = new HashSet<Integer>();
        // 非超级管理员可以管理的所有供应商ID
        Set<Integer> supplierIdsForAuth = new HashSet<Integer>();
        // 非超级管理员可以管理的可见的车队信息
        Set<Integer> teamIdsForAuth = new HashSet<Integer>();

        // 查询参数设置
        Set<Long> cityIds = Sets.newHashSet();
        Set<String> supplierIds = Sets.newHashSet();
        Set<String> teamIds = Sets.newHashSet();

        logger.info("非超级管理员:"+ WebSessionUtil.isSupperAdmin()+"cityId:"+cityId+",supplier:"+supplier+",teamId:"+teamId);
        if (!WebSessionUtil.isSupperAdmin()) {
            // 非超级管理员
            // 获取当前登录用户信息
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
            logger.info("获取当前登录用户信息:"+currentLoginUser);
            if(null != currentLoginUser){
                // 获取用户可见的城市ID
                cityIdsForAuth = currentLoginUser.getCityIds();
                // 获取用户可见的供应商信息
                supplierIdsForAuth = currentLoginUser.getSupplierIds();
                // 获取用户可见的车队信息
                teamIdsForAuth = currentLoginUser.getTeamIds();
            }else{
                logger.info("获取当前登录用户信息null");
                return null;
            }
            if (cityIdsForAuth.size() > 0 && cityId != null && !cityIdsForAuth.contains(Integer.valueOf(String.valueOf(cityId)))) {
                logger.info("cityIdsForAuth="+(cityIdsForAuth==null?"null": JSON.toJSONString(cityIdsForAuth))
                        +";cityId="+cityId);
                return null;
            }
            if (supplierIdsForAuth.size() > 0 && StringUtils.isNotBlank(supplier) && !supplierIdsForAuth.contains(Integer.valueOf(supplier))) {
                logger.info("supplierIdsForAuth="+(supplierIdsForAuth==null?"null":JSON.toJSONString(supplierIdsForAuth))
                        +";supplier="+supplier);
                return null;
            }

            if (teamIdsForAuth.size() > 0 && StringUtils.isNotBlank(teamId) && !teamIdsForAuth.contains(Integer.valueOf(teamId))) {
                logger.info("teamIdsForAuth="+(teamIdsForAuth==null?"null":JSON.toJSONString(teamIdsForAuth))
                        +";teamId="+teamId);
                return null;
            }
            // 城市权限
            if(cityIdsForAuth != null && cityIdsForAuth.size() >0){
                for (Integer cityid : cityIdsForAuth) {
                    cityIds.add(cityid.longValue());
                }
            }
            // 供应商权限
            if(supplierIdsForAuth != null && supplierIdsForAuth.size() >0 ){
                for (Integer supplierId : supplierIdsForAuth) {
                    supplierIds.add(String.valueOf(supplierId));
                }
            }
            // 车队权限
            if(teamIdsForAuth != null && teamIdsForAuth.size() >0 ){
                for (Integer tId : teamIdsForAuth) {
                    teamIds.add(String.valueOf(tId));
                }
            }

        }else{
            if(StringUtils.isNotEmpty(supplier)){
                supplierIds.add(supplier);
            }
            if(StringUtils.isNotEmpty(teamId)){
                teamIds.add(teamId);
            }
            if(cityId != null && cityId >= 1){
                cityIds.add(cityId);
            }
        }
        Map<String, Object> paramMap = Maps.newHashMap();
        if(!cityIds.isEmpty()){
            //可见城市ID
            paramMap.put("visibleCityIds", cityIds);
        }
        if(!supplierIds.isEmpty()){
            // 可见加盟商ID
            paramMap.put("visibleAllianceIds", supplierIds);
        }
        if(!teamIds.isEmpty()){
            // 可见车队ID
            paramMap.put("visibleMotorcadeIds", teamIds);
        }
        return paramMap;
    }
}

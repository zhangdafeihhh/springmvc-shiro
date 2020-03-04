package com.zhuanche.serv.bigdata;

import com.zhuanche.controller.rentcar.CarInfoController;
import com.zhuanche.entity.bigdata.*;
import mapper.bigdata.ex.BiSaasCiDeviceDayExMapper;
import mapper.bigdata.ex.CarMeasureDayExMapper;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AllianceIndexService{
    @Autowired
    private CarMeasureDayExMapper carMeasureDayExMapper;
    @Autowired
    private BiSaasCiDeviceDayExMapper biSaasCiDeviceDayExMapper;
    private static Logger logger = LoggerFactory.getLogger(AllianceIndexService.class);
    /**
     * 查询车均在线时长
     * @param saasIndexQuery
     * @return
     */
    public List<Map> getCarOnlineDuration(SAASIndexQuery saasIndexQuery){
        try {
            List<Map> result = new ArrayList();
            List<StatisticSection> statisticSections = carMeasureDayExMapper.getCarOnlineDuration(saasIndexQuery);
            if (!CollectionUtils.isEmpty(statisticSections)){
                for(StatisticSection statisticSection : statisticSections){
                    Map map = new HashMap<>(2);
                    map.put(statisticSection.getDate(),statisticSection.getValue());
                    result.add(map);
                }
            }
            return result;
        }catch (Exception e){
            logger.error("车均在线报错", e);
            return new ArrayList<>();
        }

    }


    /**
     * 查询车辆运营统计
     * @param saasIndexQuery
     * @return
     */
    public List<Map> getCarOperateStatistics(SAASIndexQuery saasIndexQuery){
        try {
            List<Map> result = new ArrayList();
            List<StatisticSection> statisticSections = carMeasureDayExMapper.getCarOperateStatistics(saasIndexQuery);
            if (!CollectionUtils.isEmpty(statisticSections)){
                for(StatisticSection statisticSection : statisticSections){
                    Map map = new HashMap<>(2);
                    map.put(statisticSection.getDate(),statisticSection.getValue());
                    result.add(map);
                }
            }
            return result;
        }catch (Exception e){
            logger.error("车辆运营统计报错", e);
            return new ArrayList<>();
        }
    }


    /**
     * 查询订单数量统计
     * @param saasIndexQuery
     * @return
     */
    public List<Map> getOrderNumStatistic(SAASIndexQuery saasIndexQuery){
        try {
            List<Map> result = new ArrayList();
            List<StatisticSection> statisticSections = carMeasureDayExMapper.getOrderNumStatistic(saasIndexQuery);
            if (!CollectionUtils.isEmpty(statisticSections)){
                for(StatisticSection statisticSection : statisticSections){
                    Map map = new HashMap<>(2);
                    map.put(statisticSection.getDate(),statisticSection.getValue());
                    result.add(map);
                }
            }
            return result;
        }catch (Exception e){
            logger.error("订单数量统计报错", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查询订单CI预测数量统计
     * @param saasIndexQuery
     * @return
     */
    public List<Map> getCiOrderNumStatistic(SAASIndexQuery saasIndexQuery){
        try {
            List<Map> result = new ArrayList();
            Integer supplierDriverCount = biSaasCiDeviceDayExMapper.getInstallCiDrierNum(saasIndexQuery);
            if(supplierDriverCount>0){//查询加盟商下是否有安装ci的司机  有
                List<CiOrderStatisticSection> statisticSections = biSaasCiDeviceDayExMapper.getCiOrderNumStatistic(saasIndexQuery);
                List<StatisticSection> statisticSectionsOld = carMeasureDayExMapper.getOrderNumStatistic(saasIndexQuery);
                if(statisticSections.size()!=statisticSectionsOld.size()){
                    if (!CollectionUtils.isEmpty(statisticSections)){
                        for(CiOrderStatisticSection s : statisticSections){
                            Map map = new HashMap<>(2);
                            map.put(s.getDate(),s.getValue());
                            result.add(map);
                        }
                    }
                    return result;
                }
                if (!CollectionUtils.isEmpty(statisticSections)){
                    for(int i=0;i<statisticSections.size();i++){
                        Map map = new HashMap<>(2);
                        if(statisticSections.get(i)!=null && statisticSectionsOld.get(i)!=null){
                            CiOrderStatisticSection statisticSection = statisticSections.get(i);
                            if(statisticSection.getValue()!=null && statisticSectionsOld.get(i).getValue()!=null){
                                if(Integer.parseInt(statisticSection.getValue())>Integer.parseInt(statisticSectionsOld.get(i).getValue())){//当加盟商有安装全途宝设备，且安装设备的司机信息要大于或者等于加盟商的数据 ，则用安装全途宝设备司机信息去计算；
                                    map.put(statisticSection.getDate(),statisticSection.getValue());
                                }else{//当加盟商有安装全途宝设备，且安装设备的司机信息要小于加盟商数据 ，则取所有加盟商安装全途宝的数据；
                                    CiOrderAllStatisticSection c = biSaasCiDeviceDayExMapper.getAllCiOrderNumStatistic(statisticSection.getDate2());
                                    if(c!=null){
                                        BigDecimal orderNum = new BigDecimal(c.getOrderNum());
                                        BigDecimal driverNum = new BigDecimal(c.getDriverNum());
                                        map.put(statisticSection.getDate(),orderNum.divide(driverNum, 0, BigDecimal.ROUND_HALF_UP).intValue()*statisticSection.getDriverNum());
                                    }
                                }
                            }else{
                                map.put(statisticSections.get(i).getDate(),statisticSections.get(i).getValue());
                            }
                        }else{
                            map.put(statisticSections.get(i).getDate(),statisticSections.get(i).getValue());
                        }
                        result.add(map);
                    }
                }
            }else{////查询加盟商下是否有安装ci的司机  无安装ci的司机，去全部加盟商
                Map map = new HashMap<>(2);
                List<CiOrderStatisticSection> statisticSectionsAll = biSaasCiDeviceDayExMapper.getCiOrderNumStatistic(saasIndexQuery);
                if (!CollectionUtils.isEmpty(statisticSectionsAll)){
                    for(CiOrderStatisticSection all : statisticSectionsAll){
                        CiOrderAllStatisticSection cAll = biSaasCiDeviceDayExMapper.getAllCiOrderNumStatistic(all.getDate2());
                        BigDecimal orderNum = new BigDecimal(cAll.getOrderNum());
                        BigDecimal driverNum = new BigDecimal(cAll.getDriverNum());
                        map.put(all.getDate(),orderNum.divide(driverNum, 0, BigDecimal.ROUND_HALF_UP).intValue()*all.getDriverNum());
                    }
                }
            }
            return result;
        }catch (Exception e){
            logger.error("查询订单CI预测数量统计", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查询CI预测数量统计百分比
     * @param startDate
     * @param endDate
     * @param allianceId
     * @param motorcadeId
     * @param visibleAllianceIds
     * @param visibleMotocadeIds
     * @param dateDiff
     * @return
     */
    public List<SAASCoreIndexPercentDto> getCiCoreIndexStatistic(SAASIndexQuery saasIndexQuery,String startDate, String endDate,String allianceId,String motorcadeId,List<String> visibleAllianceIds,List<String> visibleMotocadeIds,long dateDiff){
        List<SAASCoreIndexDto> saasCoreIndexDtoList = carMeasureDayExMapper.getCoreIndexStatistic(startDate,endDate,allianceId,motorcadeId,visibleAllianceIds,visibleMotocadeIds,dateDiff);
        Integer supplierDriverCount = biSaasCiDeviceDayExMapper.getInstallCiDrierNum(saasIndexQuery);
        List<SAASCoreIndexPercentDto> list = biSaasCiDeviceDayExMapper.getCiCoreIndexStatistic(startDate,endDate,allianceId,motorcadeId,visibleAllianceIds,visibleMotocadeIds,dateDiff);
        List<SAASAllCoreIndexPercentDto> allList = biSaasCiDeviceDayExMapper.getCiAllCoreIndexStatistic(startDate,endDate);
        if(supplierDriverCount>0) {//查询加盟商下是否有安装ci的司机  有
            if (!CollectionUtils.isEmpty(list)) {
                for (SAASCoreIndexPercentDto p : list) {
                    //完成总量
                    BigDecimal ciFactEndNumb = new BigDecimal(p.getCiFactEndNum());
                    BigDecimal ciDriverNumb = new BigDecimal(p.getCiDriverNum());
                    BigDecimal driverNumb  = new BigDecimal(p.getDriverNum());
                    BigDecimal completeOrderAmountb = new BigDecimal(p.getCompleteOrderAmount());
                    BigDecimal orderTotalFlag =  ciFactEndNumb.divide(ciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb);
                    BigDecimal orderTotalSub =  ciFactEndNumb.divide(ciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb).subtract(completeOrderAmountb);
                    if(orderTotalFlag.subtract(completeOrderAmountb).compareTo(BigDecimal.ZERO)==1){
                        p.setCompleteOrderAmountPerecnt(orderTotalSub.multiply(new BigDecimal(100)).divide(completeOrderAmountb, 0, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }else{
                        BigDecimal eciFactEndNumb = new BigDecimal(allList.get(0).getCiFactEndNum());
                        BigDecimal eciDriverNumb = new BigDecimal(allList.get(0).getCiDriverNum());
                        orderTotalSub =  eciFactEndNumb.divide(eciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb).subtract(completeOrderAmountb);
                        p.setCompleteOrderAmountPerecnt(orderTotalSub.multiply(new BigDecimal(100)).divide(completeOrderAmountb, 0, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }
                    //司机端总流水
                    BigDecimal ciFactOverAmountb = new BigDecimal(p.getCiFactOverAmount());
                    BigDecimal factOverAmountb = new BigDecimal(p.getFactOverAmount());
                    BigDecimal amountTotalFlag =  ciFactOverAmountb.divide(ciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb);
                    BigDecimal amountTotalSub =  ciFactOverAmountb.divide(ciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb).subtract(factOverAmountb);
                    if(amountTotalFlag.subtract(factOverAmountb).compareTo(BigDecimal.ZERO)==1){
                        p.setIncomeAmountPercent(amountTotalSub.multiply(new BigDecimal(100)).divide(factOverAmountb, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }else{
                        BigDecimal eciFactOverAmountb = new BigDecimal(allList.get(0).getCiFactOverAmount());
                        BigDecimal eciDriverNumb2 = new BigDecimal(allList.get(0).getCiDriverNum());
                        amountTotalSub =  eciFactOverAmountb.divide(eciDriverNumb2, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb).subtract(factOverAmountb);
                        p.setIncomeAmountPercent(amountTotalSub.multiply(new BigDecimal(100)).divide(factOverAmountb, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }
                    //车均订单
                    BigDecimal ciOrderNumb = new BigDecimal(p.getCiOrderNum());
                    BigDecimal orderPerVehicle = new BigDecimal(saasCoreIndexDtoList.get(0).getOrderPerVehicle());
                    BigDecimal vehTotalFlag =  ciOrderNumb.divide(ciDriverNumb, 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal vehTotalSub = vehTotalFlag.subtract(orderPerVehicle);
                    if(vehTotalFlag.compareTo(orderPerVehicle)==1){
                        p.setOrderPerVehiclePercent(vehTotalSub.multiply(new BigDecimal(100)).divide(orderPerVehicle, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }else{
                        BigDecimal eciOrderNumb = new BigDecimal(allList.get(0).getCiOrderNum());
                        BigDecimal eciDriverNumb3 = new BigDecimal(allList.get(0).getCiDriverNum());
                        vehTotalFlag = eciOrderNumb.divide(eciDriverNumb3, 2, BigDecimal.ROUND_HALF_UP);
                        vehTotalSub = vehTotalFlag.subtract(orderPerVehicle);
                        p.setOrderPerVehiclePercent(vehTotalSub.multiply(new BigDecimal(100)).divide(orderPerVehicle, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }

                    //车均流水
                    BigDecimal ciIncomeFactOverAmountb = new BigDecimal(p.getCiFactOverAmount());
                    BigDecimal incomePerVehicle = new BigDecimal(saasCoreIndexDtoList.get(0).getIncomePerVehicle());
                    BigDecimal incomeFlag = ciIncomeFactOverAmountb.divide(ciDriverNumb, 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal incomeSub = incomeFlag.subtract(incomePerVehicle);
                    if(incomeFlag.compareTo(incomePerVehicle)==1){
                        p.setIncomePerVehiclePercent(incomeSub.multiply(new BigDecimal(100)).divide(incomePerVehicle, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }else{
                        BigDecimal eciIncomeFactOverAmountb = new BigDecimal(allList.get(0).getCiFactOverAmount());
                        BigDecimal eciDriverNumb4 = new BigDecimal(allList.get(0).getCiDriverNum());
                        incomeFlag = eciIncomeFactOverAmountb.divide(eciDriverNumb4, 2, BigDecimal.ROUND_HALF_UP);
                        incomeSub =  incomeFlag.subtract(incomePerVehicle);
                        p.setIncomePerVehiclePercent(incomeSub.multiply(new BigDecimal(100)).divide(incomePerVehicle, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }

                    //差评单量
                    BigDecimal badEvaluateAllNum = new BigDecimal(saasCoreIndexDtoList.get(0).getBadEvaluateAllNum());
                    BigDecimal ciBadEvaluateCountAllb = new BigDecimal(p.getCiBadEvaluateAllNum());
                    BigDecimal ciBadEvaluateAllFlag = ciBadEvaluateCountAllb.divide(ciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb);
                    BigDecimal ciBadEvaluateAllSub  = ciBadEvaluateAllFlag.subtract(badEvaluateAllNum);
                    if(ciBadEvaluateAllFlag.compareTo(badEvaluateAllNum)==-1){
                        p.setBadEvaluateAllNumPercent(ciBadEvaluateAllSub.multiply(new BigDecimal(100)).divide(badEvaluateAllNum, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }else{
                        BigDecimal ebadEvaluateAllNum =   new BigDecimal(allList.get(0).getCiBadEvaluateAllNum());
                        BigDecimal eciDriverNumb5 = new BigDecimal(allList.get(0).getCiDriverNum());
                        ciBadEvaluateAllFlag = ebadEvaluateAllNum.divide(eciDriverNumb5, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb);
                        ciBadEvaluateAllSub =  ciBadEvaluateAllFlag.subtract(badEvaluateAllNum);
                        p.setBadEvaluateAllNumPercent(ciBadEvaluateAllSub.multiply(new BigDecimal(100)).divide(badEvaluateAllNum, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }

                    //有效差评单量
                    BigDecimal badEvaluateNum = new BigDecimal(saasCoreIndexDtoList.get(0).getBadEvaluateNum());
                    BigDecimal ciBadEvaluateCountb = new BigDecimal(p.getCiBadEvaluateNum());
                    BigDecimal ciBadEvaluateFlag = ciBadEvaluateCountb.divide(ciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb);
                    BigDecimal ciBadEvaluateSub  = ciBadEvaluateFlag.subtract(badEvaluateNum);
                    if(ciBadEvaluateFlag.compareTo(badEvaluateNum)==-1){
                        p.setBadEvaluateNumPercent(ciBadEvaluateSub.multiply(new BigDecimal(100)).divide(badEvaluateNum, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }else{
                        BigDecimal ebadEvaluateNum =   new BigDecimal(allList.get(0).getCiBadEvaluateNum());
                        BigDecimal eciDriverNumb6 = new BigDecimal(allList.get(0).getCiDriverNum());
                        ciBadEvaluateFlag = ebadEvaluateNum.divide(eciDriverNumb6, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb);
                        ciBadEvaluateSub =  ciBadEvaluateFlag.subtract(badEvaluateNum);
                        p.setBadEvaluateNumPercent(ciBadEvaluateSub.multiply(new BigDecimal(100)).divide(badEvaluateNum, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }

                    //差评率
                    BigDecimal badEvaluateNoChannelRate = new BigDecimal(p.getCriticismRate());
                    BigDecimal ciBadEvaluateNoChannel = new BigDecimal(p.getCiOrderCntNotChannel());
                    BigDecimal badEvaluateNoChannelFlag = ciBadEvaluateCountb.divide(ciBadEvaluateNoChannel, 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal badEvaluateNoChannelSub = badEvaluateNoChannelFlag.subtract(badEvaluateNoChannelRate);
                    if(badEvaluateNoChannelFlag.compareTo(badEvaluateNoChannelRate)==-1){
                        p.setCriticismRatePercent(badEvaluateNoChannelSub.multiply(new BigDecimal(100)).divide(badEvaluateNoChannelRate, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }else{
                        BigDecimal ebadEvaluateNum2 =   new BigDecimal(allList.get(0).getCiBadEvaluateNum());
                        BigDecimal eciBadEvaluateNoChannel = new BigDecimal(allList.get(0).getCiOrderCntNotChannel());
                        badEvaluateNoChannelFlag = ebadEvaluateNum2.divide(eciBadEvaluateNoChannel, 2, BigDecimal.ROUND_HALF_UP);
                        badEvaluateNoChannelSub = badEvaluateNoChannelFlag.subtract(badEvaluateNoChannelRate);
                        p.setCriticismRatePercent(badEvaluateNoChannelSub.multiply(new BigDecimal(100)).divide(badEvaluateNoChannelRate, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                    }
                }
            }
        }else{////查询加盟商下是否有安装ci的司机  无安装ci的司机，去全部加盟商
            if (!CollectionUtils.isEmpty(list)) {
                for (SAASCoreIndexPercentDto p : list) {
                    //完成总量
                    BigDecimal driverNumb  = new BigDecimal(p.getDriverNum());
                    BigDecimal completeOrderAmountb = new BigDecimal(p.getCompleteOrderAmount());
                    BigDecimal eciFactEndNumb = new BigDecimal(allList.get(0).getCiFactEndNum());
                    BigDecimal eciDriverNumb = new BigDecimal(allList.get(0).getCiDriverNum());
                    BigDecimal orderTotalSub =  eciFactEndNumb.divide(eciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb).subtract(completeOrderAmountb);
                    p.setCompleteOrderAmountPerecnt(orderTotalSub.multiply(new BigDecimal(100)).divide(completeOrderAmountb, 0, BigDecimal.ROUND_HALF_UP).toString()+"%");

                    //司机端总流水

                    BigDecimal eciFactOverAmountb = new BigDecimal(allList.get(0).getCiFactOverAmount());
                    BigDecimal factOverAmountb = new BigDecimal(p.getFactOverAmount());
                    BigDecimal amountTotalSub =  eciFactOverAmountb.divide(eciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb).subtract(factOverAmountb);
                    p.setIncomeAmountPercent(amountTotalSub.multiply(new BigDecimal(100)).divide(factOverAmountb, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");

                    //车均订单
                    BigDecimal orderPerVehicle = new BigDecimal(saasCoreIndexDtoList.get(0).getOrderPerVehicle());
                    BigDecimal eciOrderNumb = new BigDecimal(allList.get(0).getCiOrderNum());
                    BigDecimal vehTotalFlag = eciOrderNumb.divide(eciDriverNumb, 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal vehTotalSub = vehTotalFlag.subtract(orderPerVehicle);
                    p.setOrderPerVehiclePercent(vehTotalSub.multiply(new BigDecimal(100)).divide(orderPerVehicle, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");


                    //车均流水
                    BigDecimal incomePerVehicle = new BigDecimal(saasCoreIndexDtoList.get(0).getIncomePerVehicle());
                    BigDecimal eciIncomeFactOverAmountb = new BigDecimal(allList.get(0).getCiFactOverAmount());
                    BigDecimal incomeFlag = eciIncomeFactOverAmountb.divide(eciDriverNumb, 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal incomeSub =  incomeFlag.subtract(incomePerVehicle);
                        p.setIncomePerVehiclePercent(incomeSub.multiply(new BigDecimal(100)).divide(incomePerVehicle, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");


                    //差评单量
                    BigDecimal badEvaluateAllNum = new BigDecimal(saasCoreIndexDtoList.get(0).getBadEvaluateAllNum());
                    BigDecimal ebadEvaluateAllNum =   new BigDecimal(allList.get(0).getCiBadEvaluateAllNum());
                    BigDecimal ciBadEvaluateAllFlag = ebadEvaluateAllNum.divide(eciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb);
                    BigDecimal ciBadEvaluateAllSub =  ciBadEvaluateAllFlag.subtract(badEvaluateAllNum);
                    p.setBadEvaluateAllNumPercent(ciBadEvaluateAllSub.multiply(new BigDecimal(100)).divide(badEvaluateAllNum, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");


                    //有效差评单量
                    BigDecimal badEvaluateNum = new BigDecimal(saasCoreIndexDtoList.get(0).getBadEvaluateNum());
                    BigDecimal ebadEvaluateNum =   new BigDecimal(allList.get(0).getCiBadEvaluateNum());
                    BigDecimal ciBadEvaluateFlag = ebadEvaluateNum.divide(eciDriverNumb, 2, BigDecimal.ROUND_HALF_UP).multiply(driverNumb);
                    BigDecimal ciBadEvaluateSub =  ciBadEvaluateFlag.subtract(badEvaluateNum);
                    p.setBadEvaluateNumPercent(ciBadEvaluateSub.multiply(new BigDecimal(100)).divide(badEvaluateNum, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");


                    //差评率
                    BigDecimal badEvaluateNoChannelRate = new BigDecimal(p.getCriticismRate());
                    BigDecimal ebadEvaluateNum2 =   new BigDecimal(allList.get(0).getCiBadEvaluateNum());
                    BigDecimal eciBadEvaluateNoChannel = new BigDecimal(allList.get(0).getCiOrderCntNotChannel());
                    BigDecimal badEvaluateNoChannelFlag = ebadEvaluateNum2.divide(eciBadEvaluateNoChannel, 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal badEvaluateNoChannelSub = badEvaluateNoChannelFlag.subtract(badEvaluateNoChannelRate);
                    p.setCriticismRatePercent(badEvaluateNoChannelSub.multiply(new BigDecimal(100)).divide(badEvaluateNoChannelRate, 2, BigDecimal.ROUND_HALF_UP).toString()+"%");
                }
            }
        }
        return list;

    }

    /**
     * 查询服务差评率
     * @param saasIndexQuery
     * @return
     */
    public List<Map> getServiceNegativeRate(SAASIndexQuery saasIndexQuery){
        try {
            List<Map> result = new ArrayList();
            List<StatisticSection> statisticSections = carMeasureDayExMapper.getServiceNegativeRate(saasIndexQuery);
            if (!CollectionUtils.isEmpty(statisticSections)){
                for(StatisticSection statisticSection : statisticSections){
                    Map map = new HashMap<>(2);
                    map.put(statisticSection.getDate(),statisticSection.getValue());
                    result.add(map);
                }
            }
            return result;
        }catch (Exception e){
            logger.error("查询服务差评率", e);
            return new ArrayList<>();
        }
    }
}

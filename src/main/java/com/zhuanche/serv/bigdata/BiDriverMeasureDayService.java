package com.zhuanche.serv.bigdata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndex;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndexList;
import com.zhuanche.entity.bigdata.QueryTermDriverAnaly;
import mapper.bigdata.ex.BiDriverDisinfectMeasureDayExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
}

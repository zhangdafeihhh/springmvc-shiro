package com.zhuanche.serv.bigdata;

import com.zhuanche.entity.bigdata.DriverOperAnalyIndex;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndexList;
import com.zhuanche.entity.bigdata.QueryTermDriverAnaly;
import mapper.bigdata.ex.BiDriverDisinfectMeasureDayExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
            List<Map> onlineDriverAmountList = new ArrayList<>();
            List<Map> onlineRateList = new ArrayList<>();
            List<Map> serviceDriverAmountList = new ArrayList<>();
            List<Map> serviceRateList = new ArrayList<>();
            List<Map> distributeOrderAmountList = new ArrayList<>();
            List<Map> completeOrderAmountList = new ArrayList<>();
            List<Map> bindOrderAmountPerVehicleList = new ArrayList<>();
            List<Map> completeOrderPerVehicleList = new ArrayList<>();
            List<Map> incomePerVehicleList = new ArrayList<>();
            List<Map> pricePerOIrderList = new ArrayList<>();
            List<Map> totalDriverNumList = new ArrayList<>();
            List<Map> disinfectDriverCntList = new ArrayList<>();
            List<Map> noDisinfectDriverCntList = new ArrayList<>();
            List<Map> disinfectDriverCntRateList = new ArrayList<>();
            for (DriverOperAnalyIndex driverOperAnalyIndex : driverOperAnalyIndexLists) {
                Map<String, String> onlineDriverAmountMap = new HashMap<>();
                onlineDriverAmountMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getOnlineDriverAmount());//上线司机数
                Map<String, String> onlineRateMap = new HashMap<>();
                onlineRateMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getOnlineRate());//上线率
                Map<String, String> serviceDriverAmountMap = new HashMap<>();
                serviceDriverAmountMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getServiceDriverAmount());//运营司机数
                Map<String, String> serviceRateMap = new HashMap<>();
                serviceRateMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getServiceRate());//运营率
                Map<String, String> distributeOrderAmountMap = new HashMap<>();
                distributeOrderAmountMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getDistributeOrderAmount());//派单量
                Map<String, String> completeOrderAmountMap = new HashMap<>();
                completeOrderAmountMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getCompleteOrderAmount());//完单率
                Map<String, String> bindOrderAmountPerVehicleMap = new HashMap<>();
                bindOrderAmountPerVehicleMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getBindOrderAmountPerVehicle());//车均绑单量
                Map<String, String> completeOrderPerVehicleMap = new HashMap<>();
                completeOrderPerVehicleMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getCompleteOrderPerVehicle());//车均完单量
                Map<String, String> incomePerVehicleMap = new HashMap<>();
                incomePerVehicleMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getIncomePerVehicle());//车均流水
                Map<String, String> pricePerOIrderMap = new HashMap<>();
                pricePerOIrderMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getPricePerOIrder());//单均价
                Map<String, String> totalDriverNumMap = new HashMap<>();
                totalDriverNumMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getTotalDriverNum());//司机总数
                Map<String, String> disinfectDriverCntMap = new HashMap<>();
                disinfectDriverCntMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getDisinfectDriverCnt());//已消毒司机数
                Map<String, String> noDisinfectDriverCntMap = new HashMap<>();
                noDisinfectDriverCntMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getNoDisinfectDriverCnt());//未消毒司机数
                Map<String, String> disinfectDriverCntRateMap = new HashMap<>();
                disinfectDriverCntRateMap.put(driverOperAnalyIndex.getDemenItemName(), driverOperAnalyIndex.getDisinfectDriverCntRate());//消毒率
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

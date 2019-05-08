package com.zhuanche.serv.bigdata;

import com.zhuanche.controller.rentcar.CarInfoController;
import com.zhuanche.entity.bigdata.SAASIndexQuery;
import com.zhuanche.entity.bigdata.StatisticSection;
import mapper.bigdata.ex.CarMeasureDayExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AllianceIndexService{
    @Autowired
    private CarMeasureDayExMapper carMeasureDayExMapper;
    private static Logger logger = LoggerFactory.getLogger(AllianceIndexService.class);
    /**
     * 查询车均在线时长
     * @param saasIndexQuery
     * @return
     */
    public List<Map> getCarOnlineDuration(SAASIndexQuery saasIndexQuery){
        try {
            Integer maxId = carMeasureDayExMapper.getCarOnlineDurationMaxId(saasIndexQuery);
            if(maxId!=null){
                logger.info("车均在线时长最大id:"+maxId);
                saasIndexQuery.setMaxId(maxId);
            }
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

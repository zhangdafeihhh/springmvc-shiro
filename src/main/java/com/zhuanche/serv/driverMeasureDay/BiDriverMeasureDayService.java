package com.zhuanche.serv.driverMeasureDay;

import com.zhuanche.entity.bigdata.BiDriverMeasureDay;
import mapper.bigdata.BiDriverMeasureDayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author:qxx
 * @Date:2020/4/22
 * @Description:
 */
@Service
public class BiDriverMeasureDayService {

    @Autowired
    private BiDriverMeasureDayMapper biDriverMeasureDayMapper;


    public List<BiDriverMeasureDay> getRecordList(BiDriverMeasureDay params){
        return biDriverMeasureDayMapper.getRecordList(params);
    }

    public Double count(List<BiDriverMeasureDay> lists){
        if(lists != null && lists.size() >0){
            Integer finishClOrderNumCount = 0;
            Integer responsibleComplaintNumCount = 0;
            for(BiDriverMeasureDay b: lists){
                finishClOrderNumCount+=b.getFinishClOrderNum();
                responsibleComplaintNumCount+=b.getResponsibleComplaintNum();
            }
            if(finishClOrderNumCount==0){
                return 0.00;
            }
            return div(responsibleComplaintNumCount, finishClOrderNumCount,4);
        }
        return 0.00;
    }

    public static double div(Integer v1,Integer v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Integer.toString(v1));
        BigDecimal b2 = new BigDecimal(Integer.toString(v2));

        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}

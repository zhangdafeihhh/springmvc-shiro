package com.zhuanche.serv.driverMeasureDay;

import com.zhuanche.entity.bigdata.BiDriverMeasureDay;
import com.zhuanche.shiro.session.WebSessionUtil;
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

    public String getResponsibleComplaintRate(String startDate, String endDate,  String allianceId){
        //BiDriverMeasureDay params = new BiDriverMeasureDay();
        //params.setStartDate(startDate);
        //params.setEndDate(endDate);
        //params.setSupplierId(Integer.valueOf(allianceId));
        //String suppliers = WebSessionUtil.getCurrentLoginUser().getSupplierIds().toString();
        //params.setSupplierIds(suppliers);
        //
        //Integer numerator = biDriverMeasureDayMapper.countNumerator(params);
        //Integer denominator = biDriverMeasureDayMapper.countDenominator(params);
        //if(denominator != 0){
        //    Double rate = div(numerator, denominator, 4);
        //    rate = rate*100;
        //    String result = rate.toString()+"%";
        //    return result;
        //}
        return "0";
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

package com.zhuanche.serv.driverMeasureDay;

import com.zhuanche.dto.IndexBiDriverMeasureDto;
import com.zhuanche.dto.bigdata.BiDriverMeasureDayDto;
import com.zhuanche.entity.bigdata.BiDriverMeasureDay;
import com.zhuanche.shiro.session.WebSessionUtil;
import mapper.bigdata.BiDriverMeasureDayMapper;
import mapper.bigdata.ex.BiDriverMeasureDayExtMapper;
import org.apache.commons.lang3.StringUtils;
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
public class DriverMeasureDayService {

    @Autowired
    private BiDriverMeasureDayMapper driverMeasureDayMapper;
    @Autowired
    private BiDriverMeasureDayExtMapper biDriverMeasureDayExtMapper;

    public String getResponsibleComplaintRate(String startDate, String endDate,  String allianceId){
        BiDriverMeasureDayDto params = new BiDriverMeasureDayDto();
        params.setStartDate(startDate);
        params.setEndDate(endDate);
        if(allianceId != null && !("").equals(allianceId)){
            params.setSupplierId(Integer.valueOf(allianceId));
        }
        if(WebSessionUtil.isSupperAdmin() == false){// 如果是普通管理员
            //String suppliers = WebSessionUtil.getCurrentLoginUser().getSupplierIds().toString();
            String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds().toArray(), ",");
            params.setSupplierIds(suppliers);
        }else{
            params.setSupplierIds("");
        }
        IndexBiDriverMeasureDto indexBiDriverMeasureDto = biDriverMeasureDayExtMapper.findForStatistics(params);

        Integer numerator =indexBiDriverMeasureDto.getResponsibleComplaintNum();// driverMeasureDayMapper.countNumerator(params);
        Integer denominator = indexBiDriverMeasureDto.getFinishClOrderNum();// driverMeasureDayMapper.countDenominator(params);
        if(denominator != null && denominator != 0){
            Double rate = div(numerator, denominator, 4);
            rate = rate*100;
            BigDecimal rate1 = new BigDecimal(rate);
            rate = rate1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            String result = rate.toString()+"%";
            return result;
        }
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

    public IndexBiDriverMeasureDto findForStatistics(String startDate, String endDate, String allianceId) {
        BiDriverMeasureDayDto params = new BiDriverMeasureDayDto();
        params.setStartDate(startDate);
        params.setEndDate(endDate);
        if(allianceId != null && !("").equals(allianceId)){
            params.setSupplierId(Integer.valueOf(allianceId));
        }
        if(WebSessionUtil.isSupperAdmin() == false){// 如果是普通管理员
            //String suppliers = WebSessionUtil.getCurrentLoginUser().getSupplierIds().toString();
            String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds().toArray(), ",");
            params.setSupplierIds(suppliers);
        }else{
            params.setSupplierIds("");
        }
        IndexBiDriverMeasureDto indexBiDriverMeasureDto = biDriverMeasureDayExtMapper.findForStatistics(params);
        return indexBiDriverMeasureDto;

    }
}

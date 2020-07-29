package com.zhuanche.serv.bigdata;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.bigdata.BiNewcityDriverReportDayDto;
import com.zhuanche.entity.bigdata.BiNewcityDriverReportDay;
import mapper.bigdata.ex.BiNewcityDriverReportDayExMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/20 下午1:25
 * @Version 1.0
 */
@Service
public class BiNewcityDriverReportDayService {


    @Autowired
    private BiNewcityDriverReportDayExMapper exMapper;

    public PageDTO pageInfoList(BiNewcityDriverReportDay reportDay,
                                Integer pageNo,
                                Integer pageNum,Integer dataType,
                                String dataBeginDate,
                                String dataEndDate,
                                Integer sort,
                                Integer amountSort){

        Page page = PageHelper.startPage(pageNo,pageNum,true);

        int count = 0;

        List<BiNewcityDriverReportDay> dtoList = exMapper.queryFlowList(reportDay.getCityId(),
                reportDay.getSupplierId(),reportDay.getDriverName(),reportDay.getDriverPhone(), reportDay.getLicensePlates(),dataType,dataBeginDate,dataEndDate,sort,amountSort);


        count = (int) page.getTotal();


        return new PageDTO(pageNo,pageNum,count,dtoList);
    }


    /**查询汇总数据*/
    public BiNewcityDriverReportDayDto flowTotal(BiNewcityDriverReportDay reportDay, Integer dataType,
                                String dataBeginDate,
                                String dataEndDate){

        BiNewcityDriverReportDayDto dto =  exMapper.queryFlowTotal(reportDay.getCityId(),
                reportDay.getSupplierId(),reportDay.getDriverName(),reportDay.getDriverPhone(), reportDay.getLicensePlates(),dataType,dataBeginDate,dataEndDate);

        dto.setBindOrderNumTotal(dto.getBindOrderNumTotal() == null ? 0 : dto.getBindOrderNumTotal());

        dto.setSettleOrderNumTotal(dto.getSettleOrderNumTotal() == null ? 0 : dto.getSettleOrderNumTotal());

        dto.setTotalAmountTotal(dto.getTotalAmountTotal() == null ? BigDecimal.ZERO : dto.getTotalAmountTotal());

        return  dto;
     }
}

package com.zhuanche.serv.statisticalAnalysis;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.zhuanche.dto.rentcar.CompleteOrderDTO;

public interface CompleteOrderService {
    /*
     * 导出
     */
    Workbook exportExcelCompleteOrder(List<CompleteOrderDTO> list , String path) throws Exception;
}

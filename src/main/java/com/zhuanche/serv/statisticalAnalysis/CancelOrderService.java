package com.zhuanche.serv.statisticalAnalysis;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.zhuanche.dto.rentcar.CancelOrderDTO;

public interface CancelOrderService {
    /*
     * 导出
     */
    Workbook exportExcelCancelOrder(List<CancelOrderDTO> list , String path) throws Exception;
}

package com.zhuanche.serv.statisticalAnalysis;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import com.zhuanche.dto.rentcar.DriverEvaluateDTO;

public interface DriverEvaluateService {
    /*
     * 导出
     */
    Workbook exportExcelDriverEvaluate(List<DriverEvaluateDTO> list , String path) throws Exception;
}

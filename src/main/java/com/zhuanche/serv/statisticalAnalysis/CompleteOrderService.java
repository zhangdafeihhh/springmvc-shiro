package com.zhuanche.serv.statisticalAnalysis;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.zhuanche.dto.rentcar.CompleteOrderDTO;

public interface CompleteOrderService {
    /*
     * 查询分页列表，所需list 和 total
     */
	Map<String, Object> queryForPageObject(Map<String, Object> paramMap);
    /*
     * 导出
     */
    Workbook exportExcelCompleteOrder(List<CompleteOrderDTO> list , String path) throws Exception;
}
